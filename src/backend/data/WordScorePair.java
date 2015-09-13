package backend.data;

/**
 * Data Structure for storing a sentiment word and its score.
 * Stores a string (sentiment word) and a Pair (sentiment score) OR
 * Stores a string (sentiment word) and a float (score) (e.g. Booster-, Negating- or Emoticon word )
 * @author Pazifik
 *
 */
public class WordScorePair {
		
	private String word;
	private Pair scorePair;
	private float score;
	
	public WordScorePair(String word, Pair scorePair){
		this.word=word;
		this.scorePair=scorePair;
	}
	
	public WordScorePair(String word, float score){
		this.word=word;
		this.score=score;
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
	
}
