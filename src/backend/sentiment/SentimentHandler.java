package backend.sentiment;

import backend.data.SentimentWordScorePair;

public interface SentimentHandler {
	
	/**
	 * Analyzes the given string using dictionaries for given language and
	 * returns its sentiment "pos" / "neg" / "neu"
	 * @param string
	 * @param lang the language which the given string is in
	 * @return "pos" for positive, "neg" for negative, "neu" for neutral
	 */
	public String getSentiment(String string, String lang);
	
	/**
	 * Analyzes the given string and returns its sentiment "pos" / "neg" / "neu"
	 * including a list of all detected sentiment words with their scores
	 * @param string
	 * @return a SentimentWordScorePair storing the overall sentiment and all sentiment words with its scores
	 */
	public SentimentWordScorePair getSentimentWithKeywords(String string, String lang);
	
	
}
