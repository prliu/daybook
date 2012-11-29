package tw.com.sunnybay.daybook.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import tw.com.sunnybay.daybook.R;
import tw.com.sunnybay.daybook.db.DaybookDBHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

public class ExcelExporter extends Thread {

	Context context = null;
	Calendar calendar = null;
	File file = null;

	public ExcelExporter(Context context, Calendar calendar, File file) {
		this.context = context;
		this.calendar = calendar;
		this.file = new File(file, "daybook.xls");
	}

	@Override
	public void run() {
		FileOutputStream out = null;

		DaybookDBHelper helper = new DaybookDBHelper(context);
		SQLiteDatabase db = helper.getReadableDatabase();

		/*
		 * Get item list of this month.
		 */
		String sql = String.format(
				"SELECT _DATE, _ITEM, _AMOUNT, _NOTE FROM TICK\n"
						+ "WHERE _DATE LIKE '%d-%02d%%'\n"
						+ "ORDER BY _DATE DESC", calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONDAY) + 1);

		Workbook wb = new HSSFWorkbook();

		Sheet sheet = wb.createSheet();
		Row row = sheet.createRow(0);
		row.createCell(0).setCellValue(context.getString(R.string.date));
		row.createCell(1).setCellValue(context.getString(R.string.title));
		row.createCell(2).setCellValue(context.getString(R.string.amount));
		row.createCell(3).setCellValue(context.getString(R.string.note));

		try {
			out = new FileOutputStream(file);

			Cursor cursor = db.rawQuery(sql, null);
			cursor.moveToFirst();
			int i = 1;
			while (!cursor.isAfterLast()) {
				row = sheet.createRow(i);
				row.createCell(0).setCellValue(cursor.getString(0));
				row.createCell(1).setCellValue(cursor.getString(1));
				row.createCell(2).setCellValue(cursor.getString(2));
				row.createCell(3).setCellValue(cursor.getString(3));
				i++;
				cursor.moveToNext();
			}
			cursor.close();

			wb.write(out);
			out.flush();
			out.close();

			Toast.makeText(context, context.getString(R.string.done),
					Toast.LENGTH_SHORT).show();

		} catch (IOException e) {
			Log.d(null, e.getMessage());
		} finally {
			try {
				if (out != null) {
					out.close();
					out = null;
				}
			} catch (IOException e) {
				Log.d(null, e.getMessage());
			}

			Toast.makeText(context, context.getString(R.string.done),
					Toast.LENGTH_SHORT).show();
		}

	}

}
