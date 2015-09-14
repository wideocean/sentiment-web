package backend.language;
import java.util.ArrayList;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;


public class LanguageHandlerImpl implements LanguageHandler{
	
	private Detector detector;
	private ArrayList<String> undetected_reviews;
	private static boolean profilesLoaded = false;
	
	/**
	 * Constructor; responsible for loading the language profiles
	 */
	public LanguageHandlerImpl(String filepath){
		try {
			// specify here the language profiles directory
			if(!profilesLoaded){
				DetectorFactory.loadProfile(filepath+"/WEB-INF/language-detection/profiles.sm");
				// containing all languages
				DetectorFactory.loadProfile(filepath+"/WEB-INF/language-detection/profiles.sm_allLanguages");
				profilesLoaded = true;
			}
			detector = DetectorFactory.create();
			undetected_reviews = new ArrayList<String>();
		} catch (LangDetectException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
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
