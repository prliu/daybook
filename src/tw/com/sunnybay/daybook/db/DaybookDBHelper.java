package tw.com.sunnybay.daybook.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DaybookDBHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "cashflow.db";
	public static final String TABLE_NAME = "TICK";
	public static final int DATABASE_VERSION = 2;

	public DaybookDBHelper(Context context) {
		super(context, DaybookDBHelper.DATABASE_NAME, null,
				DaybookDBHelper.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		String sql = String
				.format("CREATE TABLE %s ("
						+ "_ID INTEGER PRIMARY KEY AUTOINCREMENT, _DATE DATE, _ITEM VARCHAR(50),"
						+ "_PAYMENT CHAR(1),_AMOUNT NUMBER, _NOTE VARCHAR(255))",
						DaybookDBHelper.TABLE_NAME);

		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		if (newVersion > oldVersion) {
			switch (oldVersion) {
			case 1:
				String sql = String.format(
						"ALTER TABLE %s ADD COLUMN _PAYMENT CHAR(1)",
						DaybookDBHelper.TABLE_NAME);
				db.execSQL(sql);
			}
		} else {
			onCreate(db);
		}

	}
}
