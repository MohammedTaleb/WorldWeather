package com.example.ma.awa;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Mohammed_Aqrabawi on 9/21/2018.
 */

public class DataBase extends SQLiteOpenHelper {
	private static final String DataBase_Name = "AWA.db";
	public static final String City = "Cities";
	final private static Integer VERSION = 1;

	public DataBase(Context context) {
		super(context, DataBase_Name, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		sqLiteDatabase.execSQL("Create table if not exists Cities(City text primary key) ");


	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

	}

	public Boolean insert(String ct) {

		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("City", ct);


		Long rsl =
				db.insert(City, null, values);

		if (rsl == -1)
			return false;
		else
			return true;

	}

	public ArrayList<String> readCity() {
		ArrayList<String> L1 = new ArrayList<String>();
		SQLiteDatabase db = getReadableDatabase();
		db.beginTransaction();
		try {


			String selectQ = "select * From " + City;
			Cursor c = db.rawQuery(selectQ, null);
			if (c.getCount() > 0) {
				while (c.moveToNext()) {
					String ct = c.getString(c.getColumnIndex("City"));
					L1.add(ct);
				}
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			db.close();
		}
		return L1;
	}

	public Integer delCity(String ct) {
		SQLiteDatabase db;
		db = getWritableDatabase();
		return db.delete(City, "City = ?", new String[]{ct});
	}
}