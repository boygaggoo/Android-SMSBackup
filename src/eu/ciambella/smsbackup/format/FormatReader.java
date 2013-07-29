package eu.ciambella.smsbackup.format;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

public abstract class FormatReader {

	private Context mContext;
	private Uri mUri;
	
	protected abstract void process(InputStream is);
	
	public void ksdaf(Context context, Uri uri) {
		mContext = context;
		mUri = uri;
		
//		process(is);
	}
	
	protected void saveToProvider(ContentValues cv) {
		mContext.getContentResolver().insert(mUri, cv);
	}
	
}
