package model;

import java.util.ArrayList;

/**
 * Data Structure for storing a sentiment and all sentiment word score pairs
 * 
 * @author Pazifik
 *
 */
public class SentimentWordScorePair {

	private Sentiment sentiment;
	private ArrayList<WordScorePair> wordScorePairs;

	public SentimentWordScorePair(Sentiment sentiment, ArrayList<WordScorePair> wordScorePairs) {
		this.sentiment = sentiment;
		this.wordScorePairs = wordScorePairs;
	}

	public Sentiment getSentiment() {
		return sentiment;
	}

	public void setSentiment(Sentiment sentiment) {
		this.sentiment = sentiment;
	}

	public ArrayList<WordScorePair> getWordScorePairs() {
		return wordScorePairs;
	}

	public void setWordScorePairs(ArrayList<WordScorePair> wordScorePairs) {
		this.wordScorePairs = wordScorePairs;
	}

}
