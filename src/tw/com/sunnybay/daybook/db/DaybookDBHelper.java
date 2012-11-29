package tw.com.sunnybay.daybook.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DaybookDBHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "cashflow.db";
	private static final int DATABASE_VERSION = 1;
	
	public DaybookDBHelper(Context context) {
		super(context, DaybookDBHelper.DATABASE_NAME, null,
				DaybookDBHelper.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		String sql = "CREATE TABLE TICK ("
				+ "_ID INTEGER PRIMARY KEY AUTOINCREMENT, _DATE DATE, _ITEM VARCHAR(50),"
				+ "_AMOUNT NUMBER, _NOTE VARCHAR(255))";

		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		
		String sql = "DROP TABLE TICK";
		arg0.execSQL(sql);
		onCreate(arg0);
	}
	
}
