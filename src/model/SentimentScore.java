package model;

import java.util.ArrayList;

/**
 * Data Structure for storing a sentiment and all sentiment word score pairs
 * 
 * @author Pazifik
 *
 */
public class SentimentScore {

	private Sentiment sentiment;
	private Pair finalScore;
	private ArrayList<WordScorePair> wordScorePairs;

	public SentimentScore(Sentiment sentiment, Pair finalScore, ArrayList<WordScorePair> wordScorePairs) {
		this.sentiment = sentiment;
		this.finalScore = finalScore;
		this.wordScorePairs = wordScorePairs;
	}

	public Sentiment getSentiment() {
		return sentiment;
	}

	public void setSentiment(Sentiment sentiment) {
		this.sentiment = sentiment;
	}

	public Pair getFinalScore() {
		return finalScore;
	}

	public void setFinalScore(Pair finalScore) {
		this.finalScore = finalScore;
	}

	public ArrayList<WordScorePair> getWordScorePairs() {
		return wordScorePairs;
	}

	public void setWordScorePairs(ArrayList<WordScorePair> wordScorePairs) {
		this.wordScorePairs = wordScorePairs;
	}

	@Override
	public String toString() {
		return "SentimentWordScorePair [sentiment=" + sentiment + ", wordScorePairs=" + wordScorePairs + "]";
	}

}
