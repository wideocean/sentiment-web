package model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Data Structure for storing a sentiment word and its score. Stores a string
 * (sentiment word) and a Pair (sentiment score) OR Stores a string (sentiment
 * word) and a float (score) (e.g. Booster-, Negating- or Emoticon word )
 * 
 * @author Pazifik
 *
 */
public class WordScorePair {

	private String word;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Pair scorePair;

	@JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = CustomJsonFilter.class)
	private float score;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String negation;

	public WordScorePair(String word, Pair scorePair, float score, String negation) {
		this.word = word;
		this.scorePair = scorePair;
		this.score = score;
		this.negation = negation;
	}

	public WordScorePair(String word, Pair scorePair) {
		this.word = word;
		this.scorePair = scorePair;
	}

	public WordScorePair(String word, float score) {
		this.word = word;
		this.score = score;
	}

	public WordScorePair(String word, String negation) {
		this.word = word;
		this.negation = negation;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public Pair getScorePair() {
		return scorePair;
	}

	public void setScorePair(Pair scorePair) {
		this.scorePair = scorePair;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public String getNegation() {
		return negation;
	}

	public void setNegation(String negation) {
		this.negation = negation;
	}

}
