package tw.com.sunnybay.daybook;

import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class HelpActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String url = "file:///android_asset/manual/";
		String page = "index.html";
		Locale locale = Locale.getDefault();

		WebView view = new WebView(this);
		if (Locale.CHINESE.equals(locale)
				|| Locale.TRADITIONAL_CHINESE.equals(locale)
				|| Locale.SIMPLIFIED_CHINESE.equals(locale)
				|| Locale.CHINA.equals(locale) || Locale.TAIWAN.equals(locale)
				|| Locale.PRC.equals(locale))
			page = "index.zh_TW.html";

		view.loadUrl(url + page);
		setContentView(view);
	}

}
