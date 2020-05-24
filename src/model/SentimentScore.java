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
	private Score finalScore;
	private ArrayList<WordScore> wordScorePairs;

	public SentimentScore(Sentiment sentiment, Score finalScore, ArrayList<WordScore> wordScorePairs) {
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

	public Score getFinalScore() {
		return finalScore;
	}

	public void setFinalScore(Score finalScore) {
		this.finalScore = finalScore;
	}

	public ArrayList<WordScore> getWordScorePairs() {
		return wordScorePairs;
	}

	public void setWordScorePairs(ArrayList<WordScore> wordScorePairs) {
		this.wordScorePairs = wordScorePairs;
	}

	@Override
	public String toString() {
		return "SentimentWordScorePair [sentiment=" + sentiment + ", wordScorePairs=" + wordScorePairs + "]";
	}

}
