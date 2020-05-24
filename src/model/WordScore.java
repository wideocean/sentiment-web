package model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Data Structure for storing a sentiment word and its score. <br>
 * Stores a string (sentiment word) and a {@link Score} (sentiment score) OR
 * <br>
 * Stores a string (sentiment word) and a float (booster score) (e.g. Booster-,
 * Negating- or Emoticon word ). <br>
 * Examples: <br>
 * * for the word "good": <br>
 * word = good ; score = 0.0 ; scorePair = [2.0,-1.0] <br>
 * * for the booster "very": <br>
 * word = very ; score = 1.0 ; scorePair = null <br>
 * * for the negation "not": <br>
 * word = not ; score = 0.0 ; scorePair = null ; negation = true
 * 
 * @author Pazifik
 *
 */
public class WordScore {

	private String word;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Score score;

	@JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = CustomJsonFilter.class)
	private float boosterScore;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Boolean negation;

	public WordScore() {
	}

	public WordScore(String word, Score scorePair) {
		this.word = word;
		this.score = scorePair;
	}

	public WordScore(String word, float boosterScore) {
		this.word = word;
		this.boosterScore = boosterScore;
	}

	public WordScore(String word, Boolean negation) {
		this.word = word;
		this.negation = negation;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public Score getScore() {
		return score;
	}

	public void setScore(Score score) {
		this.score = score;
	}

	public float getBoosterScore() {
		return boosterScore;
	}

	public void setBoosterScore(float boosterScore) {
		this.boosterScore = boosterScore;
	}

	public Boolean getNegation() {
		return negation;
	}

	public void setNegation(Boolean negation) {
		this.negation = negation;
	}

	@Override
	public String toString() {
		return "WordScore [word=" + word + ", score=" + score + ", boosterScore=" + boosterScore + ", negation="
				+ negation + "]";
	}

}
