package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.mozilla.universalchardet.UniversalDetector;

/**
 * This class is used to detect the charset encoding of files.
 * 
 * @author Pazifik
 *
 */
public class CharsetDetector {

	public static String getEncoding(File file) throws IOException {
		byte[] buf = new byte[4096];

		try (FileInputStream fis = new FileInputStream(file)) {

			// (1)
			UniversalDetector detector = new UniversalDetector(null);

			// (2)
			int nread;
			while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
				detector.handleData(buf, 0, nread);
			}
			// (3)
			detector.dataEnd();

			// (4)
			String encoding = detector.getDetectedCharset();

			// (5)
			detector.reset();

			if (encoding != null)
				return encoding;
			else
				return "UTF-8";
		}
	}

}