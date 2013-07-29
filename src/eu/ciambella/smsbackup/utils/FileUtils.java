package eu.ciambella.smsbackup.utils;

import java.io.File;

import android.os.Environment;

public class FileUtils {

	public static String getBackupDirectory() {
		final File externalFile = Environment.getExternalStorageDirectory();
		final File backupDirectory = new File(externalFile.getPath() + "/backups/");
		backupDirectory.mkdirs();
		return backupDirectory.getPath();
	}
	
	/**
	  * Check if the external memory is available on the mobile
	  * 
	  * @return true if the external memory is available and writable
	  */
	 public static boolean isExternalMemoryAvailable() {
		 boolean mExternalStorageAvailable = false;
		 boolean mExternalStorageWriteable = false;
		 String state = Environment.getExternalStorageState();

		 if (Environment.MEDIA_MOUNTED.equals(state)) {
			 // We can read and write the media
			 mExternalStorageAvailable = mExternalStorageWriteable = true;
		 } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			 // We can only read the media
			 mExternalStorageAvailable = true;
			 mExternalStorageWriteable = false;
		 } else {
			 // Something else is wrong. It may be one of many other states, but all we need to know is we can neither read nor write
			 mExternalStorageAvailable = mExternalStorageWriteable = false;
		 }
		 return mExternalStorageWriteable & mExternalStorageAvailable;
	 }
	
}
