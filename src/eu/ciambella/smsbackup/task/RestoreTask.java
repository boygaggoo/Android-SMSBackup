package eu.ciambella.smsbackup.task;

import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;

public class RestoreTask extends AsyncTask<Void, Void, Void> {

	static final String TAG = RestoreTask.class.getSimpleName();

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

	}

	@Override
	protected Void doInBackground(Void... params) {

//		Uri uri = Uri.parse("content://sms");
//
//		ContentValues cv = new ContentValues();
//		cv.put("address", "+33659077324");
//		cv.put("date", "1367659206905");
//		cv.put("type", 2);
//		cv.put("body", "mdr ptdr");
//
//		mContext.getContentResolver().insert(uri, cv);
//
//		ContentValues values = new ContentValues();
//		values.put("address", "+33672749401");
//		values.put("body", "Salut mec !");
//		mContext.getContentResolver().insert(Uri.parse("content://sms/inbox"), values);

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	}

}
