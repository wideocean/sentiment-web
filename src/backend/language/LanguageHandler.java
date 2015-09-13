package backend.language;
import java.util.ArrayList;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;


public class LanguageHandler {
	
	private Detector detector;
	private ArrayList<String> undetected_reviews;
	private static boolean profilesLoaded = false;
	
	/**
	 * Constructor; responsible for loading the language profiles
	 */
	public LanguageHandler(String filepath){
		try {
			// specify here the language profiles directory
			if(!profilesLoaded){
				DetectorFactory.loadProfile(filepath+"/WEB-INF/language-detection/profiles.sm");
				profilesLoaded = true;
			}
			
			detector = DetectorFactory.create();
			undetected_reviews = new ArrayList<String>();
			
		} catch (LangDetectException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Detects the language from a given string
	 * @param string
	 * @return non-empty string, e.g. "en" for English or "de" for German OR 
	 * 			empty string if language could not be detected
	 */
	public String detectLanguageFromString(String string){
		String lang = "";
		try {
			detector = DetectorFactory.create();
			detector.append(string);
			lang = detector.detect();
			
		} catch (LangDetectException e) {
			e.printStackTrace();
			System.out.println(" "+string);
			undetected_reviews.add(string);
		}
		return lang;
	}
	
	/**
	 * Returns an ArrayList containing Strings which caused a LangDetectException
	 * @return
	 */
	public ArrayList<String> getUndetected_reviews() {
		return undetected_reviews;
	}

}
