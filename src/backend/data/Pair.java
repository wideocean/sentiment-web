package backend.data;

/**
 * Data Structure for storing Sentiment Scores.
 * Stores a positive and a negative value.
 * @author Pazifik
 *
 */
public class Pair {
	
	private float pos;
	private float neg;
	
	public Pair(float pos, float neg){
		this.pos=pos;
		this.neg=neg;
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
	
	public String toString(){
		return "["+pos+","+neg+"]";
	}
	
}