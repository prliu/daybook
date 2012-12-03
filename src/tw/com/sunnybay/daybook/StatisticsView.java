package tw.com.sunnybay.daybook;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;

public class StatisticsView extends View {

	Paint paint = new Paint();
	RectF oval;

	int[] colorIndex = { 0xffff0000, 0xff00ff00, 0xff0000ff, 0xffffcc00,
			0xffff00cc, 0xffccff00, 0xff00ffcc, 0xffcc00ff, 0xff00ccff,
			0xffffff00, 0xffff00ff, 0xff00ffff, 0xffffffcc, 0xffffccff,
			0xffccffff, 0xffff9900, 0xffff0099, 0xff99ff00, 0xff00ff99,
			0xffffcc99, 0xffff99cc, 0xffccff99, 0xff99ffcc, 0xffff9999,
			0xff99ff99, 0xff9999ff, 0xffcc0000, 0xff00cc00, 0xff0000cc,
			0xffcc9900, 0xffcc0099, 0xff99cc00, 0xff00cc99, 0xff9900cc,
			0xff0099cc, 0xffcccc99, 0xffcc99cc, 0xff99cccc, 0xffcc9999,
			0xff99cc99, 0xff9999cc };

	private List<KeyValuePair<String, Float>> list = null;

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

		Iterator<KeyValuePair<String, Float>> iter = list.iterator();
		while (iter.hasNext()) {

			KeyValuePair<String, Float> pair = iter.next();
			float percent = pair.value;

			if (index < colorIndex.length) {
				paint.setColor(colorIndex[index]);
			} else {
				paint.setColor(Color.LTGRAY);
			}

			float sweep = 360 * percent;
			if (percent >= 0.005f) {
				canvas.drawArc(oval, angle, sweep, true, paint);
				angle += sweep;
				index++;
			} else {
				// if the area is less than 0.5 percent, just add it into
				// 'other' type.
				paint.setColor(Color.LTGRAY);
				sweep = 360 - angle;
				canvas.drawArc(oval, angle, sweep, true, paint);
				break;
			}

		}

		super.onDraw(canvas);
	}

	public void setKeyValuePair(List<KeyValuePair<String, Float>> list) {
		this.list = list;
	}
}
