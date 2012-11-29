package tw.com.sunnybay.daybook;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;

public class AboutDialog extends Dialog {

	private Context context = null;

	public AboutDialog(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.dialog_about);

		try {
			TextView version = (TextView) findViewById(R.id.textView2);
			String versionName = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
			version.setText("VER. " + versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		this.dismiss();
		return super.onTouchEvent(event);
	}

}
