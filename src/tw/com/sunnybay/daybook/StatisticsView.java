package tw.com.sunnybay.daybook;

import java.util.Iterator;
import java.util.List;
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

	// int[] colorIndex = { 0xff99cc99, 0xff99cccc, 0xffcccccc, 0xffccccff,
	// 0xffccff99, 0xffccffcc, 0xffccffff, 0xffff6666, 0xffff9966,
	// 0xffffcc99, 0xffffcccc, 0xffffff99, 0xffffffcc
	// };

	int[] colorTable = { 
			0xffff0000, 0xff00ff00, 0xff0000ff,
			0xffffcc00, 0xffff00cc, 0xffccff00, 0xff00ffcc,
			0xffcc00ff, 0xff00ccff
	};

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

			if (index < colorTable.length) {
				paint.setColor(colorTable[index]);
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

	public void setKeyValuePair(List<KeyValuePair<String, Float>> list) {
		this.list = list;
	}
}
