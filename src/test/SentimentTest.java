package test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.Pair;
import model.Sentiment;
import model.SentimentScore;
import model.WordScorePair;
import service.SentimentServiceImpl;

class SentimentTest {
	private SentimentServiceImpl sentiHandler;
	private final String EN = "en";

	@BeforeEach
	void setUp() throws Exception {
		sentiHandler = new SentimentServiceImpl("WebContent");
	}

	@Test
	void testNoSentiment() throws IOException {
		String sentence = "I go to school today.";
		Sentiment sentiment = sentiHandler.getSentimentAggregation(sentence, EN);
		assertThat(sentiment).isEqualTo(Sentiment.NEU);
	}

	@Test
	void testPositive() throws IOException {
		String sentence = "good day";
		Sentiment sentiment = sentiHandler.getSentimentAggregation(sentence, EN);
		assertThat(sentiment).isEqualTo(Sentiment.POS);
	}

	@Test
	void testNeutral() throws IOException {
		List<String> testSentences = new ArrayList<String>();
		testSentences.add("Good day, bad weather");
		testSentences.add("Its bad weather. Good day");
		testSentences.add("I love and hate dogs.");
		testSentences.add("Amazing and good day. Its devastating weather");
		testSentences.add("Excellent day, horrible and bad weather");
		testSentences.add("Excellent apt, bad host and bad choice");
		for (String sentence : testSentences) {
			Sentiment sentiment = sentiHandler.getSentimentAggregation(sentence, EN);
			assertThat(sentiment).isEqualTo(Sentiment.NEU);
		}
	}

	@Test
	void testNegative() throws IOException {
		List<String> testSentences = new ArrayList<String>();
		testSentences.add("bad day");
		testSentences.add("good day, horrible weather");
		testSentences.add("She is nice but also horrible.");
		for (String sentence : testSentences) {
			Sentiment sentiment = sentiHandler.getSentimentAggregation(sentence, EN);
			assertThat(sentiment).isEqualTo(Sentiment.NEG);
		}
	}

	@Test
	void testPositiveBooster() throws IOException {
		String s = "very good day";
		SentimentScore sentimentScore = sentiHandler.getSentimentScore(s, EN);
		assertThat(sentimentScore.getSentiment()).isEqualTo(Sentiment.POS);
		ArrayList<WordScorePair> wordScorePairs = sentimentScore.getWordScorePairs();

		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			assertThat(wordScorePair.getWord()).isEqualTo("very");
			assertThat(wordScorePair.getScore()).isEqualTo(1);
			assertThat(wordScorePair.getScorePair()).isNull();
		});
		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			assertThat(wordScorePair.getWord()).isEqualTo("good");
			assertThat(wordScorePair.getScore()).isEqualTo(0);
			assertThat(wordScorePair.getScorePair().getPositive()).isEqualTo(2);
			assertThat(wordScorePair.getScorePair().getNegative()).isEqualTo(-1);
		});
		assertThat(sentimentScore.getFinalScore()).isEqualTo(new Pair(3, -1));
	}

	@Test
	void testNegativeBooster() throws IOException {
		String s = "sometimes good food";
		SentimentScore sentimentScore = sentiHandler.getSentimentScore(s, EN);
		assertThat(sentimentScore.getSentiment()).isEqualTo(Sentiment.POS);
		ArrayList<WordScorePair> wordScorePairs = sentimentScore.getWordScorePairs();

		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			assertThat(wordScorePair.getWord()).isEqualTo("sometimes");
			assertThat(wordScorePair.getScore()).isEqualTo(-1);
			assertThat(wordScorePair.getScorePair()).isNull();
		});
		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			assertThat(wordScorePair.getWord()).isEqualTo("good");
			assertThat(wordScorePair.getScore()).isEqualTo(0);
			assertThat(wordScorePair.getScorePair().getPositive()).isEqualTo(2);
			assertThat(wordScorePair.getScorePair().getNegative()).isEqualTo(-1);
		});
		assertThat(sentimentScore.getFinalScore()).isEqualTo(new Pair(1, -1));
	}

	@Test
	void testMultipleBooster() throws IOException {
		String s = "very slightly bad day";
		SentimentScore sentimentScore = sentiHandler.getSentimentScore(s, EN);
		assertThat(sentimentScore.getSentiment()).isEqualTo(Sentiment.NEG);
		ArrayList<WordScorePair> wordScorePairs = sentimentScore.getWordScorePairs();

		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			assertThat(wordScorePair.getWord()).isEqualTo("slightly");
			assertThat(wordScorePair.getScore()).isEqualTo(-1);
			assertThat(wordScorePair.getScorePair()).isNull();
		});
		assertThat(wordScorePairs).allSatisfy(wordScorePair -> {
			assertThat(wordScorePair.getWord()).isNotEqualTo("very");
		});
		assertThat(sentimentScore.getFinalScore()).isEqualTo(new Pair(1, -3));
	}

}
