package eu.ciambella.smsbackup;

import android.app.Application;
import android.util.Log;

import com.nullwire.trace.ExceptionHandler;

public class SMSBackupApplication extends Application {
	
	static final String TAG = SMSBackupApplication.class.getSimpleName();
	
	/**
	 * Activate the debug mode over all the application. Concretely, all
	 * exception who was occurs and innumerable application log was showing into
	 * Android Logcat. On enable this attribute, the application will be
	 * slightly slower. For the production mode, this attribute should not be
	 * enabled (false)
	 */
	public static final boolean DEBUG_MODE = true;

	/**
	 * Use this to force the application to don't use the connectivity of the
	 * device. For the production mode, this attribute should not be enabled
	 * (false)
	 */
	public static final boolean FORCE_OFFLINE = false;

	/**
	 * Parameter the Exception Handler module by specify there server address
	 * and that enable state
	 */
	static final String EXCEPTION_HANDLER_SERVER_URL = "";
	static final boolean EXCEPTION_HANDLER_ACTIVATE = false;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		if (DEBUG_MODE) {
			Log.i(TAG, "Application started !");
			Log.w(TAG, "DEBUG_MODE activate !");
			if (FORCE_OFFLINE) {
				Log.w(TAG, "FORCE_OFFLINE activated !");
			}
		}
		
		if (EXCEPTION_HANDLER_ACTIVATE) {
			if (DEBUG_MODE) {
				Log.w(TAG, "EXCEPTION_HANDLER_ACTIVATE activate !");
			}
			ExceptionHandler.register(this, EXCEPTION_HANDLER_SERVER_URL);
		}
		
	}
	
}
