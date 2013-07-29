package eu.ciambella.smsbackup.ui.activities;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import eu.ciambella.smsbackup.R;
import eu.ciambella.smsbackup.SMSBackupApplication;
import eu.ciambella.smsbackup.task.BackupTask;

public class BackupActivity extends FragmentActivity implements OnClickListener, LoaderCallbacks<Cursor> {

	static final boolean DEBUG_MODE = SMSBackupApplication.DEBUG_MODE;
	static final String TAG = BackupActivity.class.getSimpleName();
	
	static final int LOADER_COUNT_SMS = 8623;
	static final int LOADER_COUNT_MMS = 8721;
	static final int LOADER_COUNT_CALL = 7321;
	
	static final String SMS_URI = "content://sms";
	static final String MMS_URI = "content://mms-sms/conversations/";
	static final String CALL_URI = "content://call_log/calls";
	
	private TextView mTxtStatSMS;
	private TextView mTxtStatMMS;
	private TextView mTxtStatCall;
	private ProgressBar mProgress;
	private Button mSubmit;
	
	private CheckBox mCheckSMS;
	private CheckBox mCheckMMS;
	private CheckBox mCheckCall;
	
	private int mNumberBackupSMS = 0;
	private int mNumberBackupMMS = 0;
	private int mNumberBackupCall = 0;
	private int mNumberResult = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Set layout
		setContentView(R.layout.activity_backup);
		
		// Get views
		mTxtStatSMS = (TextView) findViewById(R.id.backup_stats_sms);
		mTxtStatMMS = (TextView) findViewById(R.id.backup_stats_mms);
		mTxtStatCall = (TextView) findViewById(R.id.backup_stats_call);
		mCheckSMS = (CheckBox) findViewById(R.id.backup_check_sms);
		mCheckMMS = (CheckBox) findViewById(R.id.backup_check_mms);
		mCheckCall = (CheckBox) findViewById(R.id.backup_check_call);
		mProgress = (ProgressBar) findViewById(R.id.progress);
		mSubmit = (Button) findViewById(R.id.button_start_process);
		
		// Set listener
		mSubmit.setOnClickListener(this);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		mProgress.setVisibility(View.VISIBLE);
		mSubmit.setEnabled(false);
		
		mNumberResult = 0;
		final LoaderManager lm = getSupportLoaderManager();
		lm.restartLoader(LOADER_COUNT_CALL, null, this);
		lm.restartLoader(LOADER_COUNT_SMS, null, this);
		lm.restartLoader(LOADER_COUNT_MMS, null, this);
	}
	
	private void startProcess() {
		if (DEBUG_MODE) {
			Log.i(TAG, "Start process backup");
		}
		BackupTask backup = new BackupTask(this);
		int nbrSelect = 0;
		if (mCheckSMS.isChecked()) {
			if (DEBUG_MODE) {
				Log.d(TAG, "=> Backup SMS");
			}
			backup.enableBackupSMS(mNumberBackupSMS);
			nbrSelect++;
		}
		if (mCheckMMS.isChecked()) {
			if (DEBUG_MODE) {
				Log.d(TAG, "=> Backup MMS");
			}
			backup.enableBackupMMS(mNumberBackupMMS);
			nbrSelect++;
		}
		if (mCheckCall.isChecked()) {
			if (DEBUG_MODE) {
				Log.d(TAG, "=> Backup Call History");
			}
			backup.enableBackupCallHistory(mNumberBackupCall);
			nbrSelect++;
		}
		if (nbrSelect == 0) {
			Toast.makeText(this, "Sellectionnez au moins une option !", Toast.LENGTH_LONG).show(); // TODO string
			backup.cancel(true);
		} else {
			backup.execute();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_start_process:
			if (mSubmit.isEnabled()) {
				startProcess();
			}
			break;
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		switch (id) {
		case LOADER_COUNT_SMS:
			return new CursorLoader(this, Uri.parse(SMS_URI), null, null, null, null);
		case LOADER_COUNT_MMS:
			return new CursorLoader(this, Uri.parse(MMS_URI), null, null, null, null);
		case LOADER_COUNT_CALL:
			return new CursorLoader(this, Uri.parse(CALL_URI), null, null, null, null);
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (cursor == null) {
			return;
		}
		switch (loader.getId()) {
		case LOADER_COUNT_SMS:
			mNumberBackupSMS = cursor.getCount();
			mTxtStatSMS.setText("("+ cursor.getCount() +")");
			mNumberResult++;
			break;
		case LOADER_COUNT_MMS:
			mNumberBackupMMS = cursor.getCount();
			mTxtStatMMS.setText("("+ cursor.getCount() +")");
			mNumberResult++;
			break;
		case LOADER_COUNT_CALL:
			mNumberBackupCall = cursor.getCount();
			mTxtStatCall.setText("("+ cursor.getCount() +")");
			mNumberResult++;
			break;
		}
		if (!cursor.isClosed()) {
			cursor.close();
		}
		if (mNumberResult >= 3) {
			mProgress.setVisibility(View.GONE);
			mSubmit.setEnabled(true);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}
	
}
