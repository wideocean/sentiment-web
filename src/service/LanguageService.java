package service;

import java.io.IOException;

public interface LanguageService {

	/**
	 * Detects the language from a given string
	 * 
	 * @param string
	 * @return the language code (e.g. EN for English)
	 * @throws IOException
	 */

	public String getLanguage(String string);

}
