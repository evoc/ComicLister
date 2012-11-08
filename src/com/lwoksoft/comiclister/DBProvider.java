package com.lwoksoft.comiclister;

import java.util.Random;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBProvider {
	private static final String TAG = "DBProvider";

	public static final String DATABASE_NAME = "FirstTry.db";
	public static final int DATABASE_VERSION = 4;

	public static final String CONTACTS_TABLE_NAME = "contacts";
	public static final String COMIC_ID = "_id";
	public static final String COMIC_CHECKED = "phone";
	public static final String COMIC_TITLE = "name";

	public static final String EVENTS_TABLE_NAME = "events";
	public static final String EVENTS_ID_NAME = "_id";
	public static final String EVENTS_NAME_NAME = "name";

	public static final String CONTACT_EVENT_REL_NAME = "contact2Events";
	public static final String CONTACT_EVENT_REL_CONTACT_ID = "_idContact";
	public static final String CONTACT_EVENT_REL_EVENT_ID = "_idEvent";

	public DatabaseHelper mOpenHelper;

	public DBProvider(Context context) {
		mOpenHelper = new DatabaseHelper(context);

	}

	/**
	 * This class helps open, create, and upgrade the database file.
	 */
	public static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + CONTACTS_TABLE_NAME + " ("
					+ COMIC_ID + " INTEGER PRIMARY KEY,"
					+ COMIC_CHECKED + " TEXT,"
					+ COMIC_TITLE + " TEXT"
					//                    + "CREATED_DATE" + " INTEGER,"
					//                    + "MODIFIED_DATE" + " INTEGER"
					+ ");");

			db.execSQL("CREATE TABLE " + EVENTS_TABLE_NAME + " ("
					+ EVENTS_ID_NAME + " INTEGER PRIMARY KEY,"
					+ EVENTS_NAME_NAME + " TEXT,"
					+ "CREATED_DATE" + " INTEGER,"
					+ "MODIFIED_DATE" + " INTEGER"
					+ ");");

			db.execSQL("CREATE TABLE " + CONTACT_EVENT_REL_NAME + " ("
					+ CONTACT_EVENT_REL_CONTACT_ID + " INTEGER,"
					+ CONTACT_EVENT_REL_EVENT_ID + " INTEGER,"
					+ "CREATED_DATE" + " INTEGER,"
					+ "MODIFIED_DATE" + " INTEGER"
					+ ", PRIMARY KEY (" + CONTACT_EVENT_REL_CONTACT_ID + ", " + CONTACT_EVENT_REL_EVENT_ID + "));");
			Log.i(TAG, "Databases created (version " + DATABASE_VERSION + ")");

			for (int j=0; j<40; j++){
				int i = new Random().nextInt(2);
				String s = "insert into " + CONTACTS_TABLE_NAME + "( "+ COMIC_CHECKED + ","
						+ COMIC_TITLE + ") values(" + i + ", 'titel" + j + "' )";
				db.execSQL(s);           
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + CONTACTS_TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + EVENTS_TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + CONTACT_EVENT_REL_NAME);
			onCreate(db);
		}
	}

}
