package backend.language;

public interface LanguageHandler {

	/**
	 * Detects the language from a given string
	 * @param string
	 * @return non-empty string, e.g. "en" for English or "de" for German OR 
	 * 			empty string if language could not be detected
	 */
	public String detectLanguageFromString(String string);
	
	
}
