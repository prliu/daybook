package tw.com.sunnybay.daybook.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import tw.com.sunnybay.daybook.R;
import tw.com.sunnybay.daybook.R.string;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class FileOperation extends Thread {

	public final static int FILE_OPERATION_DONE = 0;
	public final static int FILE_OPERATION_FAIL = -1;

	Context context = null;
	File in = null;
	File out = null;

	public FileOperation(Context context, File in, File out) {
		this.context = context;
		this.in = in;
		this.out = out;
	}

	@Override
	public synchronized void run() {

		byte[] buffer = new byte[1024];
		int count = 0;

		FileInputStream fis = null;
		FileOutputStream fos = null;

		try {
			fis = new FileInputStream(in);
			fos = new FileOutputStream(out);

			for (count = fis.read(buffer); count > 0; count = fis.read(buffer)) {
				fos.write(buffer, 0, count);
			}

			fos.flush();

			Toast.makeText(context, context.getString(R.string.done),
					Toast.LENGTH_LONG).show();

		} catch (IOException e) {

			e.printStackTrace();

			Message msg = new Message();
			msg.what = FileOperation.FILE_OPERATION_FAIL;

			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();

		} finally {
			try {
				if (fis != null) {
					fis.close();
					fis = null;
				}

				if (fos != null) {
					fos.close();
					fos = null;
				}
			} catch (IOException e) {
				Log.d(null, e.getMessage());
			}
		}

		super.run();
	}

}
