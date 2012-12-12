package tw.com.sunnybay.daybook;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

import tw.com.sunnybay.daybook.db.DaybookDBHelper;
import tw.com.sunnybay.daybook.io.ExcelExporter;
import tw.com.sunnybay.daybook.io.FileOperation;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private DaybookDBHelper helper = new DaybookDBHelper(this);
	private SQLiteDatabase database = null;

	private File app_dir = null;

	private long selectedId = -1;

	private Calendar calendar = Calendar.getInstance();

	private GestureDetector detector;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		app_dir = new File(Environment.getExternalStorageDirectory(),
				getString(R.string.app_name));
		if (!app_dir.exists()) {
			if (!app_dir.mkdir()) {
				String msg = String.format(
						getString(R.string.cannot_create_dir),
						app_dir.getName());
				Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
			}
		}

		ListView list = (ListView) findViewById(R.id.listView1);
		registerForContextMenu(list);

		setupList();
		setListener();

		detector = new GestureDetector(
				new GestureDetector.SimpleOnGestureListener() {

					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {

						float movment = e1.getX() - e2.getX();

						if (Math.abs(movment) > 150) {
							if (velocityX > 0) {
								calendar.add(Calendar.MONTH, -1);
								updateList();
							} else {
								calendar.add(Calendar.MONTH, +1);
								updateList();
							}
						}

						return super.onFling(e1, e2, velocityX, velocityY);
					}

				});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		// Detect sliding motions on the screen.
		if (detector.onTouchEvent(event))
			return true;

		return super.onTouchEvent(event);
	}

	@Override
	protected void onResume() {

		updateList();
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		boolean result = true;

		switch (item.getItemId()) {
		case R.id.menu_item_current:
			calendar = Calendar.getInstance();
			updateList();
			break;
		case R.id.menu_item_statistics:
			Intent intent = new Intent(this, StatisticsActivity.class);
			intent.putExtra("date", calendar.getTimeInMillis());
			startActivity(intent);
			break;
		case R.id.menu_subitem_import_db:
			importDB();
			break;
		case R.id.menu_subitem_export_db:
			exportDB();
			break;
		case R.id.menu_subitem_export_xls:
			exportXLS();
			break;
		case R.id.menu_item_about:
			about();
			break;
		default:
			result = super.onOptionsItemSelected(item);
		}

		return result;
	}

	@Override
	protected void onDestroy() {

		ListView list = (ListView) findViewById(R.id.listView1);
		SimpleCursorAdapter adapter = (SimpleCursorAdapter) list.getAdapter();
		adapter.getCursor().close();

		if (database != null) {
			database.close();
		}

		super.onDestroy();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, v, menuInfo);

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		selectedId = info.id;

		if (v.getId() == R.id.listView1) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.main_context_menu, menu);
		}

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.main_menu_daily_sum:
			showDailySum(selectedId);
			return true;
		case R.id.main_menu_delete:
			deleteItem(selectedId);
			return true;
		}

		return super.onContextItemSelected(item);
	}

	private void setListener() {

		// Construct Add Button
		Button btnAdd = (Button) findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				addItem(v);
			}
		});
	}

	private void addItem(View v) {
		Intent intent = new Intent(this, ItemFormActivity.class);
		intent.putExtra("mode", "add");
		startActivity(intent);
	}

	private void setMonthlyAmount() {

		// Set month
		String dateStr = String.format(Locale.getDefault(), "%tY-%tm",
				calendar, calendar);
		TextView txtMonth = (TextView) findViewById(R.id.txtMonth);
		txtMonth.setText(dateStr);

		String sql = String.format("SELECT SUM(_AMOUNT) AS _TOTAL FROM %s\n"
				+ "WHERE _DATE LIKE '%tY-%tm%%'", DaybookDBHelper.TABLE_NAME,
				calendar, calendar);

		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor c = db.rawQuery(sql, null);
		c.moveToFirst();
		if (!c.isAfterLast()) {

			TextView txtTotal = (TextView) findViewById(R.id.txtTotal);
			if (!c.isNull(0)) {
				txtTotal.setText(c.getString(0));
			} else {
				txtTotal.setText("0");
			}
		}
		c.close();
		db.close();

		c = null;
		db = null;
	}

	private void setupList() {

		ListView list = (ListView) findViewById(R.id.listView1);

		String[] from = new String[] { "_ITEM", "_DATE", "_AMOUNT" };
		int[] to = new int[] { R.id.txtItem, R.id.txtDate, R.id.txtPrice };
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.view_listitem, null, from, to);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				editItem(id);
			}

		});

		list.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				// Detect Sliding motions on the screen when the screen is
				// filled with list items.
				if (detector.onTouchEvent(event))
					return true;

				return false;
			}
		});

	}

	private Cursor getCursor() {

		database = helper.getReadableDatabase();

		/*
		 * Get item list of this month.
		 */
		String sql = String.format(
				"SELECT _ID _id, _DATE, _ITEM, _AMOUNT FROM %s\n"
						+ "WHERE _DATE LIKE '%tY-%tm%%'\n"
						+ "ORDER BY _DATE DESC", DaybookDBHelper.TABLE_NAME,
				calendar, calendar);

		return database.rawQuery(sql, null);

	}

	private void editItem(long id) {

		Intent intent = new Intent(this, ItemFormActivity.class);
		intent.putExtra("mode", "edit");
		intent.putExtra("id", id);
		startActivity(intent);

	}

	private void deleteItem(long id) {

		SQLiteDatabase db = helper.getWritableDatabase();

		String sql = String.format("DELETE FROM %s WHERE _ID = %d",
				DaybookDBHelper.TABLE_NAME, id);

		db.execSQL(sql);
		db.close();

		updateList();
	}

	private void updateList() {

		setMonthlyAmount();

		ListView list = (ListView) findViewById(R.id.listView1);
		SimpleCursorAdapter adapter = (SimpleCursorAdapter) list.getAdapter();
		adapter.changeCursor(getCursor());

	}

	private void showDailySum(long id) {

		SQLiteDatabase db = helper.getReadableDatabase();
		String sql = String.format("SELECT SUM(_AMOUNT) FROM %s WHERE _DATE=("
				+ "SELECT _DATE FROM TICK WHERE _ID=%d",
				DaybookDBHelper.TABLE_NAME, id);
		Cursor cursor = db.rawQuery(sql, null);

		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			Toast.makeText(this,
					getString(R.string.daily_sum) + ":" + cursor.getInt(0),
					Toast.LENGTH_LONG).show();
		}
		cursor.close();
		db.close();
	}

	private void exportDB() {

		File file = getDatabasePath(DaybookDBHelper.DATABASE_NAME);
		File external = new File(app_dir, DaybookDBHelper.DATABASE_NAME);

		FileOperation action = new FileOperation(this, file, external);
		runOnUiThread(action);
	}

	private void importDB() {

		new AlertDialog.Builder(this)
				.setTitle(R.string.import_db)
				.setMessage(R.string.are_you_sure)
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								File file = getDatabasePath(DaybookDBHelper.DATABASE_NAME);
								File external = new File(app_dir,
										DaybookDBHelper.DATABASE_NAME);

								FileOperation action = new FileOperation(
										MainActivity.this, external, file);
								runOnUiThread(action);

								updateList();
							}
						}).setNegativeButton(android.R.string.no, null).show();

	}

	private void exportXLS() {
		ExcelExporter action = new ExcelExporter(this, calendar, app_dir);
		runOnUiThread(action);
	}

	private void about() {
		Dialog dialog = new AboutDialog(this);
		dialog.setTitle(getString(R.string.about));
		dialog.show();

	}
}
