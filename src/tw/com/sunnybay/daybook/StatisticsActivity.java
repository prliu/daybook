package tw.com.sunnybay.daybook;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tw.com.sunnybay.daybook.db.DaybookDBHelper;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class StatisticsActivity extends Activity {

	DaybookDBHelper helper = new DaybookDBHelper(this);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		long timeInMillis = intent.getLongExtra("date", -1);

		StatisticsView view = new StatisticsView(this);
		view.setKeyValuePair(getKeyValuePair(new Date(timeInMillis)));
		setContentView(view);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_statistics, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	private List<KeyValuePair<String, Float>> getKeyValuePair(Date date) {
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		SQLiteDatabase db = helper.getReadableDatabase();

		String sql = String.format(
				"SELECT SUM(_AMOUNT) FROM TICK WHERE _DATE LIKE '%d-%02d%%'",
				calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);

		int sum = 0;
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			sum = cursor.getInt(0);
		}
		cursor.close();

		sql = String.format(
				"SELECT _ITEM, SUM(_AMOUNT) AS _SUM FROM TICK\n" +
				"WHERE _DATE LIKE '%d-%02d%%'\n" +
				"GROUP BY _ITEM ORDER BY _SUM DESC",
				calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
		
		cursor = db.rawQuery(sql, null);
		cursor.moveToFirst();
		ArrayList<KeyValuePair<String, Float>> list = new ArrayList<KeyValuePair<String,Float>>();
		while(!cursor.isAfterLast()) {
			
			KeyValuePair<String, Float> pair = new KeyValuePair<String, Float>(cursor.getString(0), cursor.getFloat(1) / sum);
			list.add(pair);
			
			cursor.moveToNext();
		}
		cursor.close();
		
		db.close();

		return list;
	}

}
