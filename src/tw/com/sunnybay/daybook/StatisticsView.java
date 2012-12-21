package tw.com.sunnybay.daybook;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

public class StatisticsView extends View {

	private Paint paint = new Paint();
	private RectF oval;

	private boolean isPortrait = false;

	int[] colorTable = { 0xff8a56e2, 0xffcf56e2, 0xffe256ae, 0xffe25668,
			0xffe28956, 0xffe2cf56, 0xffaee256, 0xff68e256, 0xff46d279,
			0xff56aee2, 0xff5668e2 };

	private List<KeyValuePair<String, Float>> list = null;

	public StatisticsView(Context context) {
		super(context);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {

		int l;

		if (w > h) {
			l = h;
			isPortrait = false;
		} else {
			l = w;
			isPortrait = true;
		}

		oval = new RectF(0, 0, l, l);

		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		int index = 0;
		float angle = 0;
		boolean showDetail = true;

		float x, y;
		int step = 16;

		if (isPortrait) {
			x = step;
			y = oval.bottom + step;
		} else {
			x = oval.right + step;
			y = step;
		}

		paint.setTextSize(14f);

		Iterator<KeyValuePair<String, Float>> iter = list.iterator();
		while (iter.hasNext()) {
			KeyValuePair<String, Float> pair = iter.next();
			float percent = pair.value;

			if (index < colorTable.length) {
				paint.setColor(colorTable[index]);
			} else {
				showDetail = false;
			}

			float sweep = 360 * percent;
			if (percent < 0.005f) {
				showDetail = false;
			}
			
			if(showDetail) {
				canvas.drawText(pair.key, x, y, paint);
				canvas.drawText(String.format("%.2f%%", pair.value * 100),
						x + 100, y, paint);
				canvas.drawArc(oval, angle, sweep, true, paint);
				angle += sweep;
				index++;
				y += step;
			} else {
				// if the area is less than 0.5 percent, just add it into
				// 'other' type.
				paint.setColor(Color.LTGRAY);
				sweep = 360 - angle;
				canvas.drawText("Other", x, y, paint);
				canvas.drawText(String.format("%.2f%%", (sweep / 360) * 100),
						x + 100, y, paint);
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
