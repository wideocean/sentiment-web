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
		testSentences.add("The weather is bad. Good day");
		testSentences.add("Amazing and good day. The weather was devastating");
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
		testSentences.add("Excellent day, horrible and bad weather");
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
			assertThat(wordScorePair.getScorePair()).isEqualTo(new Pair(2, -1));
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
			assertThat(wordScorePair.getScorePair()).isEqualTo(new Pair(2, -1));
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

	@Test
	void testBoosterMaximumValue() throws IOException {
		String s = "very phenomenal day";
		SentimentScore sentimentScore = sentiHandler.getSentimentScore(s, EN);
		assertThat(sentimentScore.getSentiment()).isEqualTo(Sentiment.POS);
		ArrayList<WordScorePair> wordScorePairs = sentimentScore.getWordScorePairs();

		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			assertThat(wordScorePair.getWord()).isEqualTo("very");
			assertThat(wordScorePair.getScore()).isEqualTo(1);
			assertThat(wordScorePair.getScorePair()).isNull();
		});
		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			assertThat(wordScorePair.getWord()).isEqualTo("phenomenal");
			assertThat(wordScorePair.getScore()).isEqualTo(0);
			assertThat(wordScorePair.getScorePair()).isEqualTo(new Pair(5, -1));
		});
		assertThat(sentimentScore.getFinalScore()).isEqualTo(new Pair(5, -1));
	}

	@Test
	void testBoosterInfluenceOnlyNextWord() throws IOException {
		String s = "very bla good day";
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
			assertThat(wordScorePair.getScorePair()).isEqualTo(new Pair(2, -1));
		});
		assertThat(sentimentScore.getFinalScore()).isEqualTo(new Pair(2, -1));
	}

	@Test
	void testNegationPostiveSentiment() throws IOException {
		String s = "not good day";
		SentimentScore sentimentScore = sentiHandler.getSentimentScore(s, EN);
		assertThat(sentimentScore.getSentiment()).isEqualTo(Sentiment.NEG);
		ArrayList<WordScorePair> wordScorePairs = sentimentScore.getWordScorePairs();

		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			assertThat(wordScorePair.getWord()).isEqualTo("not");
			assertThat(wordScorePair.getScore()).isEqualTo(0);
			assertThat(wordScorePair.getScorePair()).isNull();
			assertThat(wordScorePair.getNegation()).isEqualTo("negation");
		});
		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			assertThat(wordScorePair.getWord()).isEqualTo("good");
			assertThat(wordScorePair.getScore()).isEqualTo(0);
			assertThat(wordScorePair.getScorePair()).isEqualTo(new Pair(2, -1));
		});
		assertThat(sentimentScore.getFinalScore()).isEqualTo(new Pair(1, -2));
	}

	@Test
	void testNegationNegativeSentiment() throws IOException {
		String s = "not bad day";
		SentimentScore sentimentScore = sentiHandler.getSentimentScore(s, EN);
		assertThat(sentimentScore.getSentiment()).isEqualTo(Sentiment.POS);
		ArrayList<WordScorePair> wordScorePairs = sentimentScore.getWordScorePairs();

		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			assertThat(wordScorePair.getWord()).isEqualTo("not");
			assertThat(wordScorePair.getScore()).isEqualTo(0);
			assertThat(wordScorePair.getScorePair()).isNull();
			assertThat(wordScorePair.getNegation()).isEqualTo("negation");
		});
		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			assertThat(wordScorePair.getWord()).isEqualTo("bad");
			assertThat(wordScorePair.getScore()).isEqualTo(0);
			assertThat(wordScorePair.getScorePair()).isEqualTo(new Pair(1, -2));
		});
		assertThat(sentimentScore.getFinalScore()).isEqualTo(new Pair(1, -1));
	}

	@Test
	void testNegationNegativeSentimentExtreme() throws IOException {
		String s = "not devastating day";
		SentimentScore sentimentScore = sentiHandler.getSentimentScore(s, EN);
		assertThat(sentimentScore.getSentiment()).isEqualTo(Sentiment.POS);
		ArrayList<WordScorePair> wordScorePairs = sentimentScore.getWordScorePairs();

		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			assertThat(wordScorePair.getWord()).isEqualTo("not");
			assertThat(wordScorePair.getScore()).isEqualTo(0);
			assertThat(wordScorePair.getScorePair()).isNull();
			assertThat(wordScorePair.getNegation()).isEqualTo("negation");
		});
		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			assertThat(wordScorePair.getWord()).isEqualTo("devastating");
			assertThat(wordScorePair.getScore()).isEqualTo(0);
			assertThat(wordScorePair.getScorePair()).isEqualTo(new Pair(1, -5));
		});
		assertThat(sentimentScore.getFinalScore()).isEqualTo(new Pair(1, -1));
	}

	@Test
	void testNegationInfluenceOnlyNext3Words() throws IOException {
		String s = "Unfortunately I was not able to meet the wonderful and lovely host.";
		SentimentScore sentimentScore = sentiHandler.getSentimentScore(s, EN);
		assertThat(sentimentScore.getSentiment()).isEqualTo(Sentiment.POS);
		ArrayList<WordScorePair> wordScorePairs = sentimentScore.getWordScorePairs();

		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			assertThat(wordScorePair.getWord()).isEqualTo("Unfortunately");
			assertThat(wordScorePair.getScore()).isEqualTo(0);
			assertThat(wordScorePair.getScorePair()).isEqualTo(new Pair(1, -2));
		});
		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			assertThat(wordScorePair.getWord()).isEqualTo("not");
			assertThat(wordScorePair.getScore()).isEqualTo(0);
			assertThat(wordScorePair.getScorePair()).isNull();
			assertThat(wordScorePair.getNegation()).isEqualTo("negation");
		});
		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			assertThat(wordScorePair.getWord()).isEqualTo("wonderful");
			assertThat(wordScorePair.getScore()).isEqualTo(0);
			assertThat(wordScorePair.getScorePair()).isEqualTo(new Pair(3, -1));
		});
		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			assertThat(wordScorePair.getWord()).isEqualTo("lovely");
			assertThat(wordScorePair.getScore()).isEqualTo(0);
			assertThat(wordScorePair.getScorePair()).isEqualTo(new Pair(2, -1));
		});
		assertThat(sentimentScore.getFinalScore()).isEqualTo(new Pair(6, -4));

		s = "not a really good day";
		sentimentScore = sentiHandler.getSentimentScore(s, EN);
		assertThat(sentimentScore.getSentiment()).isEqualTo(Sentiment.POS);
		wordScorePairs = sentimentScore.getWordScorePairs();

		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			assertThat(wordScorePair.getWord()).isEqualTo("not");
			assertThat(wordScorePair.getScore()).isEqualTo(0);
			assertThat(wordScorePair.getScorePair()).isNull();
			assertThat(wordScorePair.getNegation()).isEqualTo("negation");
		});
		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			assertThat(wordScorePair.getWord()).isEqualTo("really");
			assertThat(wordScorePair.getScore()).isEqualTo(1);
			assertThat(wordScorePair.getScorePair()).isEqualTo(null);
		});
		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			assertThat(wordScorePair.getWord()).isEqualTo("wonderful");
			assertThat(wordScorePair.getScore()).isEqualTo(0);
			assertThat(wordScorePair.getScorePair()).isEqualTo(new Pair(3, -1));
		});
		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			assertThat(wordScorePair.getWord()).isEqualTo("lovely");
			assertThat(wordScorePair.getScore()).isEqualTo(0);
			assertThat(wordScorePair.getScorePair()).isEqualTo(new Pair(2, -1));
		});
		assertThat(sentimentScore.getFinalScore()).isEqualTo(new Pair(6, -4));
	}

	@Test
	void testNegationBoosterPositive() throws IOException {
		String s = "It is not very good.";
		SentimentScore sentimentScore = sentiHandler.getSentimentScore(s, EN);
		assertThat(sentimentScore.getSentiment()).isEqualTo(Sentiment.NEG);
		ArrayList<WordScorePair> wordScorePairs = sentimentScore.getWordScorePairs();

		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			assertThat(wordScorePair.getWord()).isEqualTo("not");
			assertThat(wordScorePair.getScore()).isEqualTo(0);
			assertThat(wordScorePair.getScorePair()).isNull();
			assertThat(wordScorePair.getNegation()).isEqualTo("negation");
		});
		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			assertThat(wordScorePair.getWord()).isEqualTo("very");
			assertThat(wordScorePair.getScore()).isEqualTo(1);
			assertThat(wordScorePair.getScorePair()).isEqualTo(null);
		});
		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			assertThat(wordScorePair.getWord()).isEqualTo("good");
			assertThat(wordScorePair.getScore()).isEqualTo(0);
			assertThat(wordScorePair.getScorePair()).isEqualTo(new Pair(2, -1));
		});
		assertThat(sentimentScore.getFinalScore()).isEqualTo(new Pair(1, -1));
	}

	@Test
	void testNegationBoosterPositiveExtreme() throws IOException {
		String s = "It is not very phenomenal.";
		SentimentScore sentimentScore = sentiHandler.getSentimentScore(s, EN);
		assertThat(sentimentScore.getSentiment()).isEqualTo(Sentiment.NEG);
		ArrayList<WordScorePair> wordScorePairs = sentimentScore.getWordScorePairs();

		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			assertThat(wordScorePair.getWord()).isEqualTo("not");
			assertThat(wordScorePair.getScore()).isEqualTo(0);
			assertThat(wordScorePair.getScorePair()).isNull();
			assertThat(wordScorePair.getNegation()).isEqualTo("negation");
		});
		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			assertThat(wordScorePair.getWord()).isEqualTo("very");
			assertThat(wordScorePair.getScore()).isEqualTo(1);
			assertThat(wordScorePair.getScorePair()).isEqualTo(null);
		});
		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			assertThat(wordScorePair.getWord()).isEqualTo("phenomenal");
			assertThat(wordScorePair.getScore()).isEqualTo(0);
			assertThat(wordScorePair.getScorePair()).isEqualTo(new Pair(5, -1));
		});
		assertThat(sentimentScore.getFinalScore()).isEqualTo(new Pair(1, -1));
	}

	@Test
	void testExclamationPositive() throws IOException {
		String s = "It's a good day!";
		SentimentScore sentimentScore = sentiHandler.getSentimentScore(s, EN);
		assertThat(sentimentScore.getSentiment()).isEqualTo(Sentiment.POS);
		ArrayList<WordScorePair> wordScorePairs = sentimentScore.getWordScorePairs();

		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			assertThat(wordScorePair.getWord()).isEqualTo("good");
			assertThat(wordScorePair.getScore()).isEqualTo(0);
			assertThat(wordScorePair.getScorePair()).isEqualTo(new Pair(2, -1));
		});
		assertThat(sentimentScore.getFinalScore()).isEqualTo(new Pair(3, -1));
	}

	@Test
	void testExclamationNegative() throws IOException {
		String s = "Host was unfriendly!";
		SentimentScore sentimentScore = sentiHandler.getSentimentScore(s, EN);
		assertThat(sentimentScore.getSentiment()).isEqualTo(Sentiment.NEG);
		ArrayList<WordScorePair> wordScorePairs = sentimentScore.getWordScorePairs();

		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			assertThat(wordScorePair.getWord()).isEqualTo("unfriendly");
			assertThat(wordScorePair.getScore()).isEqualTo(0);
			assertThat(wordScorePair.getScorePair()).isEqualTo(new Pair(1, -3));
		});
		assertThat(sentimentScore.getFinalScore()).isEqualTo(new Pair(1, -4));
	}

	@Test
	void testExclamationInfluenceOnlyLastSentiment() throws IOException {
		String s = "Good location but unfriendly host!";
		SentimentScore sentimentScore = sentiHandler.getSentimentScore(s, EN);
		assertThat(sentimentScore.getSentiment()).isEqualTo(Sentiment.NEG);
		ArrayList<WordScorePair> wordScorePairs = sentimentScore.getWordScorePairs();

		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			assertThat(wordScorePair.getWord()).isEqualTo("Good");
			assertThat(wordScorePair.getScore()).isEqualTo(0);
			assertThat(wordScorePair.getScorePair()).isEqualTo(new Pair(2, -1));
		});

		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			assertThat(wordScorePair.getWord()).isEqualTo("unfriendly");
			assertThat(wordScorePair.getScore()).isEqualTo(0);
			assertThat(wordScorePair.getScorePair()).isEqualTo(new Pair(1, -3));
		});
		assertThat(sentimentScore.getFinalScore()).isEqualTo(new Pair(3, -5));
	}

	@Test
	void testEmoticon() throws IOException {
		String s = "hello :D";
		SentimentScore sentimentScore = sentiHandler.getSentimentScore(s, EN);
		assertThat(sentimentScore.getSentiment()).isEqualTo(Sentiment.POS);
		ArrayList<WordScorePair> wordScorePairs = sentimentScore.getWordScorePairs();

		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			assertThat(wordScorePair.getWord()).isEqualTo(":D");
			assertThat(wordScorePair.getScore()).isEqualTo(0);
			assertThat(wordScorePair.getScorePair()).isEqualTo(null);
		});
		assertThat(sentimentScore.getFinalScore()).isEqualTo(new Pair(1, 0));
	}

	@Test
	void testEmoticonMultiple() throws IOException {
		String s = "hello :D :) (^.^)";
		SentimentScore sentimentScore = sentiHandler.getSentimentScore(s, EN);
		assertThat(sentimentScore.getSentiment()).isEqualTo(Sentiment.POS);
		ArrayList<WordScorePair> wordScorePairs = sentimentScore.getWordScorePairs();

		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			assertThat(wordScorePair.getWord()).isEqualTo(":D");
			assertThat(wordScorePair.getScore()).isEqualTo(0);
			assertThat(wordScorePair.getScorePair()).isEqualTo(null);
		});
		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			assertThat(wordScorePair.getWord()).isEqualTo(":)");
			assertThat(wordScorePair.getScore()).isEqualTo(0);
			assertThat(wordScorePair.getScorePair()).isEqualTo(null);
		});
		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			assertThat(wordScorePair.getWord()).isEqualTo("(^.^)");
			assertThat(wordScorePair.getScore()).isEqualTo(0);
			assertThat(wordScorePair.getScorePair()).isEqualTo(null);
		});
		assertThat(sentimentScore.getFinalScore()).isEqualTo(new Pair(1, 0));
	}

}
