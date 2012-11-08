package com.lwoksoft.comiclister;

import java.util.ArrayList;
import java.util.List;
import android.location.GpsStatus.Listener;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class MainActivity extends Activity {

	public static class RowViewHolder {
		public RowViewHolder(TextView textView, CheckBox box) {
			super();
			this.textView = textView;
			this.box = box;
		}
		TextView textView;
		CheckBox box;
	}

	public static class MItems
	{
		String sTitel;
		public MItems(String sTitel) {
			super();
			this.sTitel = sTitel;
			fSelected = false;
		}
		public boolean fSelected;
		public void toggle() {
			fSelected = !fSelected;
		}
	}

	private static class MyCursorAdapter extends CursorAdapter {
		private LayoutInflater inflater;
		SQLiteDatabase db;
		public MyCursorAdapter(Context context, Cursor c, boolean autoRequery, SQLiteDatabase db) {
			super(context, c, autoRequery);
			// Cache the LayoutInflate to avoid asking for a new one each time.
			inflater = LayoutInflater.from(context);
			this.db = db;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// Because we use a ViewHolder, we avoid having to call
			// findViewById().
			RowViewHolder viewHolder = (RowViewHolder) view.getTag();
			CheckBox checkBox;
			TextView textView;

			checkBox = viewHolder.box;
			textView = viewHolder.textView;

			String sID = cursor.getString( cursor.getColumnIndex(DBProvider.COMIC_ID) );
			String titel = cursor.getString( cursor.getColumnIndex(DBProvider.COMIC_TITLE) );
			String checked = cursor.getString( cursor.getColumnIndex(DBProvider.COMIC_CHECKED) );

			// Tag the CheckBox with the Planet it is displaying, so that we can
			// access the planet in onClick() when the CheckBox is toggled.
			checkBox.setTag(sID);
			// Display planet data
			checkBox.setChecked( checked.equals("1") );
			textView.setText(titel);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			// The child views in each row.
			CheckBox checkBox;
			TextView textView;

			// Create a new row view
			View  convertView = inflater.inflate(R.layout.list_row, null);

			// Find the child views.
			textView = (TextView) convertView
					.findViewById(R.id.row_text_id);
			checkBox = (CheckBox) convertView.findViewById(R.id.row_checkbox_id);

			// Optimization: Tag the row with it's child views, so we don't
			// have to
			// call findViewById() later when we reuse the row.
			convertView.setTag(new RowViewHolder(textView, checkBox));
			// If CheckBox is toggled, update the planet it is tagged with.
			checkBox.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					CheckBox cb = (CheckBox) v;
					String sID = (String) cb.getTag();
					ContentValues val = new ContentValues();
					val.put(DBProvider.COMIC_CHECKED, cb.isChecked()?"1":"0");
					
					// planet.fSelected = cb.isChecked();
					db.update(DBProvider.CONTACTS_TABLE_NAME, val, 
							DBProvider.COMIC_ID + "=?", new String[] { sID });
				}
			});
			return convertView;
		}

	}

	/** Custom adapter for displaying an array of Planet objects. */
	private static class SelectArralAdapter extends ArrayAdapter<MItems> {
		private LayoutInflater inflater;

		public SelectArralAdapter(Context context, List<MItems> planetList) {
			super(context, R.layout.list_row, planetList); // , R.id.row_checkbox_id, planetList);
			// Cache the LayoutInflate to avoid asking for a new one each time.
			inflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// Planet to display
			MItems planet = (MItems) this.getItem(position);

			// The child views in each row.
			CheckBox checkBox;
			TextView textView;

			// Create a new row view
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.list_row, null);

				// Find the child views.
				textView = (TextView) convertView
						.findViewById(R.id.row_text_id);
				checkBox = (CheckBox) convertView.findViewById(R.id.row_checkbox_id);
				// Optimization: Tag the row with it's child views, so we don't
				// have to
				// call findViewById() later when we reuse the row.
				convertView.setTag(new RowViewHolder(textView, checkBox));
				// If CheckBox is toggled, update the planet it is tagged with.
				checkBox.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						CheckBox cb = (CheckBox) v;
						MItems planet = (MItems) cb.getTag();
						planet.fSelected = cb.isChecked();
					}
				});
			}
			// Reuse existing row view
			else {
				// Because we use a ViewHolder, we avoid having to call
				// findViewById().
				RowViewHolder viewHolder = (RowViewHolder) convertView
						.getTag();
				checkBox = viewHolder.box;
				textView = viewHolder.textView;
			}

			// Tag the CheckBox with the Planet it is displaying, so that we can
			// access the planet in onClick() when the CheckBox is toggled.
			checkBox.setTag(planet);
			// Display planet data
			checkBox.setChecked(planet.fSelected);
			textView.setText(planet.sTitel);
			return convertView;
		}
	}
	private ListView list;
	private ArrayAdapter<MItems> listAdapter;

	private MyCursorAdapter cursorAdapter;
	private SQLiteDatabase db;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		list = (ListView)findViewById(R.id.list);
		OnItemClickListener listener = new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View item, int pos,
					long id) {
				MItems it = listAdapter.getItem(pos);
				RowViewHolder holder = (RowViewHolder)item.getTag();
				it.toggle();
				holder.box.setChecked(it.fSelected);
			}

		};
		list.setOnItemClickListener(listener);
		ArrayList<MItems> dummys = new ArrayList<MainActivity.MItems>();
		for (int i = 0; i< 100; i++){
			dummys.add(new MItems("hello world"));
			dummys.add(new MItems("wir sind helden"));
		}
		//		listAdapter = new SelectArralAdapter(this, dummys);
		// list.setAdapter(listAdapter);
		DBProvider oDB = new DBProvider( getBaseContext() );
		        
        db = oDB.mOpenHelper.getWritableDatabase();
		
        Cursor c = db.query(DBProvider.CONTACTS_TABLE_NAME, 
        						 new String[] { DBProvider.COMIC_ID, DBProvider.COMIC_CHECKED, DBProvider.COMIC_TITLE }, 
        						 null, null, null, null, null );

		cursorAdapter = new MyCursorAdapter(this, c, true, db);
		list.setAdapter(cursorAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}


}
