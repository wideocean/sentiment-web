package backend.data;

import java.util.ArrayList;

public class SentimentWordScorePair {
	
	private String sentiment;
	private ArrayList<WordScorePair> wordScorePairs;
	
	public SentimentWordScorePair(String sentiment, ArrayList<WordScorePair> wordScorePairs){
		this.sentiment = sentiment;
		this.wordScorePairs = wordScorePairs;
	}

	public String getSentiment() {
		return sentiment;
	}

	public void setSentiment(String sentiment) {
		this.sentiment = sentiment;
	}

	public ArrayList<WordScorePair> getWordScorePairs() {
		return wordScorePairs;
	}

	public void setWordScorePairs(ArrayList<WordScorePair> wordScorePairs) {
		this.wordScorePairs = wordScorePairs;
	}

}
