package eu.ciambella.smsbackup.ui.activities;

import eu.ciambella.smsbackup.SMSBackupApplication;
import android.app.Activity;
import android.os.Bundle;

public class ParameterActivity extends Activity {

	static final String TAG = ParameterActivity.class.getSimpleName();
	static final boolean DEBUG_MODE = SMSBackupApplication.DEBUG_MODE;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
}
