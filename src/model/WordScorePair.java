package model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Data Structure for storing a sentiment word and its score. <br>
 * Stores a string (sentiment word) and a Pair (sentiment score) OR <br>
 * Stores a string (sentiment word) and a float (score) (e.g. Booster-,
 * Negating- or Emoticon word ).
 * 
 * Examples: <br>
 * for the word "good": <br>
 * word = good ; score = 0.0 ; scorePair = [2.0,-1.0]
 * 
 * for the booster "very": <br>
 * word = very ; score = 1.0 ; scorePair = null
 * 
 * for the negation "not": <br>
 * word = not ; score = 0.0 ; scorePair = null ; negation = true
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
	private boolean negation;

	public WordScorePair(String word, Pair scorePair, float score, boolean negation) {
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

	public WordScorePair(String word, boolean negation) {
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

	public boolean getNegation() {
		return negation;
	}

	public void setNegation(boolean negation) {
		this.negation = negation;
	}

	@Override
	public String toString() {
		return "WordScorePair [word=" + word + ", scorePair=" + scorePair + ", score=" + score + ", negation="
				+ negation + "]";
	}

}
