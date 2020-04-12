package model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * POJO for JSON representation
 * 
 * @author Pazifik
 *
 */
public class SentimentJsonResult {

	private int lineNumber;
	private Sentiment sentiment;
	private String lang;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<WordScorePair> keywords;

	public SentimentJsonResult(int lineNumber, Sentiment sentiment, String lang, List<WordScorePair> keywords) {
		this.lineNumber = lineNumber;
		this.sentiment = sentiment;
		this.lang = lang;
		this.keywords = keywords;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public Sentiment getSentiment() {
		return sentiment;
	}

	public void setSentiment(Sentiment sentiment) {
		this.sentiment = sentiment;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public List<WordScorePair> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<WordScorePair> keywords) {
		this.keywords = keywords;
	}

}
