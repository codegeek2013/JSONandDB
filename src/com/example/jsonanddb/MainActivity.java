package com.example.jsonanddb;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements OnClickListener {

	private RequestQueue queue;
	private TextView txtDisplay;
	private Button update,show;
	private String url = "http://myexperiments.comuv.com/busroute.json";
	private final String tag = "JSONandDB";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ConnectivityManager con = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nifo = con.getActiveNetworkInfo();
		if(nifo != null && nifo.isConnected()){
			Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
			Log.d("VolleyDownloaded", "Connected");
		}
		findViewById(R.id.progress).setVisibility(View.GONE);
		txtDisplay = (TextView)findViewById(R.id.status);
		update = (Button)findViewById(R.id.update);
		update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				update();
			}
		});
		show = (Button)findViewById(R.id.show);
		show.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				show();
			}
		});
		
		
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	public void update()
	{
		findViewById(R.id.progress).setVisibility(View.VISIBLE);
		queue = Volley.newRequestQueue(getApplicationContext());
		JsonObjectRequest jsObjRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "Responding", Toast.LENGTH_LONG).show();
				txtDisplay.setText("Analyzing Response");
				parseJson(response);
				findViewById(R.id.progress).setVisibility(View.GONE);
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				if(error instanceof NoConnectionError)
					Toast.makeText(getApplicationContext(), "No Connection", Toast.LENGTH_LONG).show();;
			}
		});
		
		queue.add(jsObjRequest);
	}
	
	public void show(){
		MyDatabase mdb = new MyDatabase(getApplicationContext());
		SQLiteDatabase db  = mdb.getReadableDatabase();
		Log.d(tag, "Database Received");
		String sql = "select name from sqlite_master where type='table'";
		Cursor cur = db.rawQuery(sql,null);
		if(cur.moveToFirst())
		{
			String res = "";
			do{
				res = res + cur.getString(0).toString() + "\n";
			}while(cur.moveToNext());
			txtDisplay.setText(res);
		}
		db.close();
	}
	
	
	public void parseJson(JSONObject json)
	{
		MyDatabase mdb = new MyDatabase(getApplicationContext());
		SQLiteDatabase db  = mdb.getWritableDatabase();
		try {
			JSONArray bus_dtls = json.getJSONArray("buses");
			JSONObject route;
			ArrayList<String> stops = new ArrayList<String>();
			
			for(int i=0;i<bus_dtls.length();i++){
				route = bus_dtls.getJSONObject(i);
				Iterator<String> key = route.keys();
				String tkey="";
				while(key.hasNext()) //1 time
				{
					tkey = key.next();
					String[] stps = route.getString(tkey).split(",");
					for(String val: stps)
						stops.add(val);
				}
				txtDisplay.setText("Updating table "+tkey);
				mdb.updateTable(tkey, stops, db);
				stops.clear();
			}
		db.close();	
		txtDisplay.setText("DONE");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
