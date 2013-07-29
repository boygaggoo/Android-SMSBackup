package eu.ciambella.smsbackup.task;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import eu.ciambella.smsbackup.SMSBackupApplication;
import eu.ciambella.smsbackup.exceptions.CursorNullOrEmptyException;
import eu.ciambella.smsbackup.utils.FileUtils;

public class BackupTask extends AsyncTask<Void, Void, Void> {

	static final String TAG = BackupTask.class.getSimpleName();
	static final boolean DEBUG_MODE = SMSBackupApplication.DEBUG_MODE;

	private StringBuilder sb = new StringBuilder();
	private Context mContext;
	private ProgressDialog dialog;

	/*
	 * XML TAGS
	 */
	static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
	static final String XML_TAG_ITEM_BEGIN_TYPE_SMS = "<item type=\"sms\">";
	static final String XML_TAG_ITEM_BEGIN_TYPE_MMS = "<item type=\"mms\">";
	static final String XML_TAG_ITEM_BEGIN_TYPE_CALL = "<item type=\"call\">";
	static final String XML_TAG_ITEM_ENDING = "</item>";
	static final String XML_TAG_ITEMS_BEGIN = "<items>";
	static final String XML_TAG_ITEMS_ENDING = "</items>";
	static final String XML_TAG_BEGIN = "<";
	static final String XML_TAG_ENDING = ">";
	static final String XML_TAG_CLOSE = "</";
	static final String XML_CDATA_BEGIN = "<![CDATA[";
	static final String XML_CDATA_ENDING = "]]>";

	/*
	 * Activate backup state
	 */
	private boolean mEnableBackupSMS = false;
	private boolean mEnableBackupMMS = false;
	private boolean mEnabelBackupCall = false;
	
	/*
	 * Statistics
	 */
	private int mNumberBackupSMS = 0;
	private int mNumberBackupMMS = 0;
	private int mNumberBackupCall = 0;
	private int mNumberTotal = 0;
	
	/*
	 * Scheme URI
	 */
	static final String SMS_URI = "content://sms";
	static final String MMS_URI = "content://mms-sms/conversations";

	public BackupTask(Context context) {
		mContext = context;
	}

	public void enableBackupSMS(int nbrMsg) {
		mEnableBackupSMS = true;
		mNumberBackupSMS = nbrMsg;
		mNumberTotal += nbrMsg;
	}

	public void enableBackupMMS(int nbrMsg) {
		mEnableBackupMMS = true;
		mNumberBackupMMS = nbrMsg;
		mNumberTotal += nbrMsg;
	}

	public void enableBackupCallHistory(int nbrMsg) {
		mEnabelBackupCall = true;
		mNumberBackupCall = nbrMsg;
		mNumberTotal += nbrMsg;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		dialog = ProgressDialog.show(mContext, "Loading", "Loading");
		dialog.setCancelable(true);
		dialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				if (DEBUG_MODE) {
					Log.i(TAG, "Cancel task...");
				}
				cancel(true);
			}
		});
	}

	@Override
	protected Void doInBackground(Void... params) {
		// Initiate string builder
		sb.setLength(0);
		
		// Draw header
		sb.append(XML_HEADER);
		sb.append(XML_TAG_ITEMS_BEGIN);

		// Process backup
		if (mEnableBackupSMS) {
			backupProcessSMS();
		}
		if (mEnableBackupMMS) {
			backupProcessMMS();
		}
		if (mEnabelBackupCall) {
			backupProcessHistoryCall();
		}

		// Draw footer
		sb.append(XML_TAG_ITEMS_ENDING);
		
		// Save XML
		try {
			FileWriter fstream = new FileWriter(FileUtils.getBackupDirectory() +"/backup-"+ System.currentTimeMillis() +".xml");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(sb.toString());
			out.close();
		} catch (Exception e) {
			if (DEBUG_MODE) {
				Log.e(TAG, "Impossible to write content into file", e);
			}
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
	
		// Stop dialog progress
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	@SuppressLint("NewApi")
	private void backupCursor(Cursor cursor, String TAGBegin) {
		List<String> dataColName = new ArrayList<String>();
		List<Integer> dataColIndex = new ArrayList<Integer>();

		// Get columns name
		final int nbrCol = cursor.getColumnCount();
		for (int i = 0; i < nbrCol; i++) {
			final String colName = cursor.getColumnName(i);
			dataColName.add(colName);
			dataColIndex.add(cursor.getColumnIndex(colName));
		}

		// Build XML file
		do {
			sb.append(TAGBegin);
			for (int i = 0; i < nbrCol; i++) {
				final String colName = dataColName.get(i);
				sb.append(XML_TAG_BEGIN).append(colName).append(XML_TAG_ENDING);
				if (Build.VERSION.SDK_INT >= 11) {
					final int colType = cursor.getType(i);
					switch (colType) {
					case Cursor.FIELD_TYPE_STRING:
						sb.append(XML_CDATA_BEGIN);
						sb.append(cursor.getString(dataColIndex.get(i)));
						sb.append(XML_CDATA_ENDING);
						break;
					case Cursor.FIELD_TYPE_BLOB:
						sb.append(cursor.getBlob(dataColIndex.get(i)));
						break;
					case Cursor.FIELD_TYPE_FLOAT:
						sb.append(cursor.getFloat(dataColIndex.get(i)));
						break;
					case Cursor.FIELD_TYPE_INTEGER:
						sb.append(cursor.getInt(dataColIndex.get(i)));
						break;
					case Cursor.FIELD_TYPE_NULL:
						break;
					}
				} else {
					sb.append(XML_CDATA_BEGIN);
					sb.append(cursor.getString(dataColIndex.get(i)));
					sb.append(XML_CDATA_ENDING);
				}
				sb.append(XML_TAG_CLOSE).append(colName).append(XML_TAG_ENDING);
			}
			sb.append(XML_TAG_ITEM_ENDING);
		} while (cursor.moveToNext());
	}

	private void backupProcessSMS() {
		if (DEBUG_MODE) {
			Log.i(TAG, "Backup SMS process started");
		}
		Cursor cursor = mContext.getContentResolver().query(Uri.parse(SMS_URI), null, null, null, null);
		if (cursor == null) {
			throw new CursorNullOrEmptyException("Cursor is null");
		}
		if (!cursor.moveToFirst()) {
			cursor.close();
			throw new CursorNullOrEmptyException("Cursor is empty");
		}
		backupCursor(cursor, XML_TAG_ITEM_BEGIN_TYPE_SMS);
		if (cursor != null) {
			cursor.close();
		}
		if (DEBUG_MODE) {
			Log.i(TAG, "Backup SMS process ended");
		}
	}

	private void backupProcessMMS() {
		if (DEBUG_MODE) {
			Log.d(TAG, "Backup MMS process started");
		}
		Cursor cursor = mContext.getContentResolver().query(Uri.parse(MMS_URI), null, null, null, null);
		if (cursor == null) {
			throw new CursorNullOrEmptyException("Cursor is null");
		}
		if (!cursor.moveToFirst()) {
			cursor.close();
			throw new CursorNullOrEmptyException("Cursor is empty");
		}
		backupCursor(cursor, XML_TAG_ITEM_BEGIN_TYPE_MMS);
		if (cursor != null) {
			cursor.close();
		}
		if (DEBUG_MODE) {
			Log.d(TAG, "Backup MMS process ended");
		}
	}

	private void backupProcessHistoryCall() {

	}

}
