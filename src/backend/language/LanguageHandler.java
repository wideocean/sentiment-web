package backend.language;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;


public class LanguageHandler {
	
	private Detector detector;
	private ArrayList<String> undetected_reviews;
	
	/**
	 * Constructor; responsible for loading the language profiles
	 */
	public LanguageHandler(){
		try {
			// specify here the language profiles directory
			DetectorFactory.loadProfile("language-detection/profiles.sm");
			
			detector = DetectorFactory.create();
			undetected_reviews = new ArrayList<String>();
			
		} catch (LangDetectException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Detects the language from a given txt file
	 * by considerating only the first 5 words max of the first line of the txt file
	 * ONLY USEFUL IF txt file consists of texts of one same language
	 * @param reviewsfile
	 * @return the detected language, e.g. "en" for English or "de" for German
	 */
	public String detectLanguageFromFile(File reviewsfile){
		String lang = "";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(reviewsfile));
			String line = null;
			if((line = br.readLine()) != null){
				String [] tokens = line.split("\\s+");
				String firstwords = "";
				int count = 0;
				for(String s: tokens){
					System.out.println(s);
					firstwords = firstwords + " " + s;
					count++;
					if(count==10)
						break;
				}
				lang = detectLanguageFromString(firstwords);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				if(br != null){
					br.close();
				}	
			} catch (IOException e){
					e.printStackTrace();
			}
		}
		return lang;
	}
	
	/**
	 * Detects the language from a given string
	 * @param string
	 * @return the detected language, e.g. "en" for English or "de" for German
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
	
	public static void main(String[] args) {
		LanguageHandler languageHandler = new LanguageHandler();
		
		// detectLanguageFromFile
//		File reviews_eng = new File("C:/Users/Pazifik/Desktop/Reviews_English.txt");
//		File reviews_ger = new File("C:/Users/Pazifik/Desktop/Reviews_German.txt");
//		String lang1 = languageHandler.detectLanguageFromFile(reviews_eng);
//		String lang2 = languageHandler.detectLanguageFromFile(reviews_ger);
//		System.out.println("lang1 is "+lang1+", should be english");
//		System.out.println("lang2 is "+lang2+", should be german");
//		
		// detectLanguageFromString
		String ger = "Heute was es ein wunderschöner Tag. Hallo wie geht es dir so?";
		String eng = "well it was great.";
		String test = "Alles super.";
		System.out.println("ger "+languageHandler.detectLanguageFromString(ger));
		System.out.println("eng "+languageHandler.detectLanguageFromString(eng));
		System.out.println("test "+languageHandler.detectLanguageFromString(test));
	}

}
