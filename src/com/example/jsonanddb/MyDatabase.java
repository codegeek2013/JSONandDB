package com.example.jsonanddb;

import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDatabase extends SQLiteOpenHelper{

	private static final int DATABASE_VERSION = 2;
    
    private static final String TABLE = "stops char(50) primary key";
    //private static final String DICTIONARY_TABLE_CREATE = "CREATE TABLE " + DICTIONARY_TABLE_NAME + " ("+TABLE +")";

    
    public MyDatabase(Context context) {
		super(context, "EnRoute_BUSSES", null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}
	//inserting values into database
	public void updateTable(String table_name,ArrayList<String> busses,SQLiteDatabase db)
	{
		String sql = "create table if not exists "+table_name+"("+TABLE+")";
		Log.d("VolleyDemo", sql);
		db.execSQL(sql);
		for(int i=0;i<busses.size();i++){
			
			sql = "insert into "+table_name+" values('"+busses.get(i).toString()+"')";
			Log.d("VolleyDemo", sql);
			try{
			db.execSQL(sql);
			}catch(Exception e){Log.d("VolleyDemo", "Exception");}
		}
	}
	
	/*public void finsih(SQLiteDatabase db)
	{
		db.execSQL("delete * from user_master");
	}*/

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	

}
