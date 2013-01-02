package tw.com.sunnybay.daybook;

import java.util.Calendar;

import tw.com.sunnybay.daybook.db.DaybookDBHelper;
import android.os.Bundle;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

public class ItemFormActivity extends Activity {

	boolean isNew = true;
	long id = -1;

	DaybookDBHelper helper = new DaybookDBHelper(this);

	EditText fldDate, fldTitle, fldAmount, fldNote;
	RadioGroup radioGroup;

	protected DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {

			String dateStr = String.format("%d-%02d-%02d",
					Integer.valueOf(year), Integer.valueOf(monthOfYear) + 1,
					Integer.valueOf(dayOfMonth));

			fldDate.setText(dateStr);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_form);

		Intent intent = getIntent();

		String mode = intent.getStringExtra("mode");
		if ("add".equals(mode))
			setTitle(getString(R.string.add));
		else
			setTitle(getString(R.string.edit));

		initFields(intent);
		setListener();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	private void setListener() {

		fldDate.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				showDialog();
			}
		});

		Button btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				onCancelClicked(v);
			}
		});

		Button btnSave = (Button) findViewById(R.id.btnSave);
		btnSave.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				onSaveClicked(v);
			}
		});
	}

	private void onCancelClicked(View v) {

		setResult(RESULT_OK);
		finish();

	}

	private void onSaveClicked(View v) {

		SQLiteDatabase db = helper.getWritableDatabase();

		int amount = 0;

		try {
			amount = Integer.parseInt(fldAmount.getText().toString());
		} catch (NumberFormatException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}

		ContentValues values = new ContentValues();
		values.put("_DATE", fldDate.getText().toString());
		values.put("_ITEM", fldTitle.getText().toString());
		values.put("_AMOUNT", amount);
		values.put("_NOTE", fldNote.getText().toString());
		
		switch(radioGroup.getCheckedRadioButtonId()) {
		case R.id.radio1:
			values.put("_PAYMENT", DaybookDBHelper.PAYMENT_CREDIT_CARD);
			break;
		default:
			values.put("_PAYMENT", DaybookDBHelper.PAYMENT_CASH);
		}

		if (isNew) {
			db.insertOrThrow(DaybookDBHelper.TABLE_NAME, null, values);
		} else {
			values.put("_ID", id);
			db.replaceOrThrow(DaybookDBHelper.TABLE_NAME, null, values);
		}
		db.close();

		finish();

	}

	private void showDialog() {

		String dateStr = fldDate.getText().toString();
		String[] fields = dateStr.split("-");

		int year, month, day;
		if (fields.length < 3) {
			Calendar calendar = Calendar.getInstance();
			year = calendar.get(Calendar.YEAR);
			month = calendar.get(Calendar.MONTH);
			day = calendar.get(Calendar.DAY_OF_MONTH);
		} else {
			year = Integer.valueOf(fields[0]);
			month = Integer.valueOf(fields[1]) - 1;
			day = Integer.valueOf(fields[2]);
		}

		new DatePickerDialog(this, listener, year, month, day).show();
	}

	private void initFields(Intent intent) {

		fldDate = (EditText) findViewById(R.id.editText1);
		fldTitle = (EditText) findViewById(R.id.editText2);
		fldAmount = (EditText) findViewById(R.id.editText3);
		fldNote = (EditText) findViewById(R.id.editText4);

		radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);

		id = intent.getLongExtra("id", -1);

		if (id < 0) {

			// Set default date.
			Calendar calendar = Calendar.getInstance();
			String dateStr = String.format("%tY-%tm-%td", calendar, calendar,
					calendar);

			fldDate.setText(dateStr);

		} else {

			SQLiteDatabase db = helper.getReadableDatabase();
			String sql = String
					.format("SELECT _DATE, _ITEM, _AMOUNT, _NOTE, _PAYMENT FROM %s WHERE _ID=%d",
							DaybookDBHelper.TABLE_NAME, id);
			Cursor c = db.rawQuery(sql, null);
			c.moveToFirst();
			if (!c.isAfterLast()) {

				fldDate.setText(c.getString(0));
				fldTitle.setText(c.getString(1));
				fldAmount.setText(c.getString(2));
				fldNote.setText(c.getString(3));

				// Setup radio button group.
				switch (c.getInt(4)) {
				case DaybookDBHelper.PAYMENT_CREDIT_CARD:
					radioGroup.check(R.id.radio1);
					break;
				default:
					radioGroup.check(R.id.radio0);
				}
			}

			c.close();
			db.close();

			isNew = false;
		}

	}
}
