package model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Data Structure for storing Sentiment Scores. Stores a positive and a negative
 * value.
 * 
 * @author Pazifik
 *
 */
@JsonPropertyOrder({ "positive", "negative" })
public class Score {

	private float positive;
	private float negative;

	public Score() {
	}

	public Score(float pos, float neg) {
		this.positive = pos;
		this.negative = neg;
	}

	public float getPositive() {
		return positive;
	}

	public float getNegative() {
		return negative;
	}

	public void setPositive(float pos) {
		this.positive = pos;
	}

	public void setNegative(float neg) {
		this.negative = neg;
	}

	@Override
	public String toString() {
		return "Score [pos=" + positive + ", neg=" + negative + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(negative, positive);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Score other = (Score) obj;
		return Float.floatToIntBits(negative) == Float.floatToIntBits(other.negative)
				&& Float.floatToIntBits(positive) == Float.floatToIntBits(other.positive);
	}

}