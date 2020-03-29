package service;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.tika.language.detect.LanguageDetector;

public class LanguageServiceImpl implements LanguageService {

	private LanguageDetector detector;

	public LanguageServiceImpl() {
		detector = LanguageDetector.getDefaultLanguageDetector();
		Set<String> languages = new HashSet<String>();
		Collections.addAll(languages, "de", "en");
		try {
			detector = detector.loadModels(languages);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getLanguage(String text) {
		if (text.length() < 100)
			detector.setShortText(true);
		String langCode = detector.detect(text).getLanguage();
		return langCode;
	}

}
