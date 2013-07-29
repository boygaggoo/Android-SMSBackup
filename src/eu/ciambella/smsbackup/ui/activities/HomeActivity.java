package eu.ciambella.smsbackup.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import eu.ciambella.smsbackup.R;
import eu.ciambella.smsbackup.SMSBackupApplication;

public class HomeActivity extends Activity implements OnClickListener {

	static final String TAG = HomeActivity.class.getSimpleName();
	static final boolean DEBUG_MODE = SMSBackupApplication.DEBUG_MODE;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Set layout
		setContentView(R.layout.activity_home);

		// Set listeners
		findViewById(R.id.button_backup).setOnClickListener(this);
		findViewById(R.id.button_parameter).setOnClickListener(this);
		findViewById(R.id.button_restore).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (DEBUG_MODE) {
			Log.i(TAG, "onClick !");
		}
		Intent intent = null;
		switch (v.getId()) {
		case R.id.button_backup:
			if (DEBUG_MODE) {
				Log.d(TAG, "Start Backup activity");
			}
			intent = new Intent(this, BackupActivity.class);
			break;
		case R.id.button_parameter:
			if (DEBUG_MODE) {
				Log.d(TAG, "Start Parameter activity");
			}
			intent = new Intent(this, ParameterActivity.class);
			break;
		case R.id.button_restore:
			if (DEBUG_MODE) {
				Log.d(TAG, "Start Restore activity");
			}
			intent = new Intent(this, RestoreActivity.class);
			break;
		}
		if (intent != null) {
			if (DEBUG_MODE) {
				Log.i(TAG, "Start activity");
			}
			startActivity(intent);
		}
	}

}
