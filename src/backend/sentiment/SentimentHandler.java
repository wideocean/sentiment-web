package backend.sentiment;

import backend.data.SentimentWordScorePair;

public interface SentimentHandler {
	
	/**
	 * Analyzes the given string and returns its sentiment "pos" / "neg" / "neu"
	 * including a list of all detected sentiment words with their scores
	 * @param string the review to be analyzed
	 * @param lang the two-letter language code
	 * @return a SentimentWordScorePair storing the overall sentiment 
	 * "pos" for positive, "neg" for negative, "neu" for neutral
	 * and all sentiment words with its scores
	 */
	public SentimentWordScorePair getSentiment(String string, String lang);
	
	
}
