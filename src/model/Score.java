package model;

import java.util.Objects;

/**
 * Data Structure for storing Sentiment Scores. Stores a positive and a negative
 * value.
 * 
 * @author Pazifik
 *
 */
public class Score {

	private float pos;
	private float neg;

	public Score() {
	}

	public Score(float pos, float neg) {
		this.pos = pos;
		this.neg = neg;
	}

	public float getPositive() {
		return pos;
	}

	public float getNegative() {
		return neg;
	}

	public void setPositive(float pos) {
		this.pos = pos;
	}

	public void setNegative(float neg) {
		this.neg = neg;
	}

	@Override
	public String toString() {
		return "Score [pos=" + pos + ", neg=" + neg + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(neg, pos);
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
		return Float.floatToIntBits(neg) == Float.floatToIntBits(other.neg)
				&& Float.floatToIntBits(pos) == Float.floatToIntBits(other.pos);
	}

}