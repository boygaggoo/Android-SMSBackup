package eu.ciambella.smsbackup.format;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.ContentValues;
import android.util.Log;
import eu.ciambella.smsbackup.SMSBackupApplication;

public class XMLReader extends FormatReader {
	
	static final boolean DEBUG_MODE = SMSBackupApplication.DEBUG_MODE;
	static final String TAG = XMLReader.class.getSimpleName();
	
	static final String XML_TAG_ITEMS = "items";
	
	@Override
	protected void process(InputStream is) {
		try {
			// Initiate DOM parser
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(is);

			// Parse items message
			NodeList items = dom.getElementsByTagName(XML_TAG_ITEMS);

			// Parse with DOM parser
			final int lenght = items.getLength();
			for (int i = 0; i < lenght; i++) {
				parseItem((Element) items.item(i));
			}

		} catch (Exception e) {
			if (DEBUG_MODE) {
				Log.e(TAG, "Error during parsing XML process", e);
			}
		}
	}
	
	private void parseItem(Element item) {
		ContentValues cv = new ContentValues();
		NodeList nodes = item.getChildNodes();
		final int childsCount = nodes.getLength();
		for (int i = 0; i < childsCount; i++) {
			Element elem = (Element) nodes.item(i);
			cv.put(elem.getTagName(), elem.getTextContent());
		}
		saveToProvider(cv);
	}

}
