package tw.com.sunnybay.daybook;

import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

public class StatisticsView extends View {

	Paint paint = new Paint();
	RectF oval;
	
	int[] colorIndex = { 0xff99cc99, 0xff99cccc, 0xffcccccc, 0xffccccff,
			0xffccff99, 0xffccffcc, 0xffccffff, 0xffff6666, 0xffff9966,
			0xffffcc99, 0xffffcccc, 0xffffff99, 0xffffffcc, 0xffffffff };

	private Map<String, Float> map = null;

	public StatisticsView(Context context) {
		super(context);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {

		int l = w > h ? h : w;
		oval = new RectF(0, 0, l, l);

		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		int index = 0;
		float angle = 0;
		Iterator<String> keys = map.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			float percent = map.get(key);

			if (index < colorIndex.length) {
				paint.setColor(colorIndex[index]);
			} else {
				paint.setColor(Color.LTGRAY);
			}

			float sweep = 360 * percent;
			canvas.drawArc(oval, angle, sweep, true, paint);
			angle += sweep;
			index++;

		}

		super.onDraw(canvas);
	}

	public void setKeyValuePair(Map<String, Float> map) {
		this.map = map;
	}
}
