package test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.Score;
import model.Sentiment;
import model.SentimentScore;
import model.WordScore;
import service.SentimentServiceImpl;

class SentimentTest {
	private SentimentServiceImpl sentiHandler;
	private final String EN = "en";

	@BeforeEach
	void setUp() throws Exception {
		sentiHandler = new SentimentServiceImpl("WebContent");
	}

	private Consumer<WordScore> isSentimentWord(String word, Score score) {
		return n -> {
			assertThat(n.getWord()).isEqualTo(word);
			assertThat(n.getBoosterScore()).isEqualTo(0);
			assertThat(n.getScore()).isEqualTo(score);
		};
	}

	private Consumer<WordScore> isBooster(String word, float score) {
		return n -> {
			assertThat(n.getWord()).isEqualTo(word);
			assertThat(n.getBoosterScore()).isEqualTo(score);
			assertThat(n.getScore()).isEqualTo(null);
		};
	}

	private Consumer<WordScore> isNegation(String word) {
		return n -> {
			assertThat(n.getWord()).isEqualTo(word);
			assertThat(n.getBoosterScore()).isEqualTo(0);
			assertThat(n.getScore()).isEqualTo(null);
			assertThat(n.getNegation()).isTrue();
		};
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
		ArrayList<WordScore> wordScorePairs = sentimentScore.getWordScorePairs();

		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			isBooster("very", 1);
		});
		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			isSentimentWord("good", new Score(2, -1));
		});
		assertThat(sentimentScore.getFinalScore()).isEqualTo(new Score(3, -1));
	}

	@Test
	void testNegativeBooster() throws IOException {
		String s = "sometimes good food";
		SentimentScore sentimentScore = sentiHandler.getSentimentScore(s, EN);
		assertThat(sentimentScore.getSentiment()).isEqualTo(Sentiment.POS);
		ArrayList<WordScore> wordScorePairs = sentimentScore.getWordScorePairs();

		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			isBooster("sometimes", -1);
		});
		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			isSentimentWord("good", new Score(2, -1));
		});
		assertThat(sentimentScore.getFinalScore()).isEqualTo(new Score(1, -1));
	}

	@Test
	void testMultipleBooster() throws IOException {
		String s = "very slightly bad day";
		SentimentScore sentimentScore = sentiHandler.getSentimentScore(s, EN);
		assertThat(sentimentScore.getSentiment()).isEqualTo(Sentiment.NEG);
		ArrayList<WordScore> wordScorePairs = sentimentScore.getWordScorePairs();

		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			isBooster("slightly", -1);
		});
		assertThat(wordScorePairs).allSatisfy(wordScorePair -> {
			assertThat(wordScorePair.getWord()).isNotEqualTo("very");
		});
		assertThat(sentimentScore.getFinalScore()).isEqualTo(new Score(1, -3));
	}

	@Test
	void testBoosterMaximumValue() throws IOException {
		String s = "very phenomenal day";
		SentimentScore sentimentScore = sentiHandler.getSentimentScore(s, EN);
		assertThat(sentimentScore.getSentiment()).isEqualTo(Sentiment.POS);
		ArrayList<WordScore> wordScorePairs = sentimentScore.getWordScorePairs();

		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			isBooster("very", 1);
		});
		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			isSentimentWord("phenomenal", new Score(5, -1));
		});
		assertThat(sentimentScore.getFinalScore()).isEqualTo(new Score(5, -1));
	}

	@Test
	void testBoosterInfluenceOnlyNextWord() throws IOException {
		String s = "very bla good day";
		SentimentScore sentimentScore = sentiHandler.getSentimentScore(s, EN);
		assertThat(sentimentScore.getSentiment()).isEqualTo(Sentiment.POS);
		ArrayList<WordScore> wordScorePairs = sentimentScore.getWordScorePairs();

		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			isBooster("very", 1);
		});
		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			isSentimentWord("good", new Score(2, -1));
		});
		assertThat(sentimentScore.getFinalScore()).isEqualTo(new Score(2, -1));
	}

	@Test
	void testNegationPostiveSentiment() throws IOException {
		String s = "not good day";
		SentimentScore sentimentScore = sentiHandler.getSentimentScore(s, EN);
		assertThat(sentimentScore.getSentiment()).isEqualTo(Sentiment.NEG);
		ArrayList<WordScore> wordScorePairs = sentimentScore.getWordScorePairs();

		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			isNegation("not");
		});
		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			isSentimentWord("good", new Score(2, -1));
		});
		assertThat(sentimentScore.getFinalScore()).isEqualTo(new Score(1, -2));
	}

	@Test
	void testNegationNegativeSentiment() throws IOException {
		String s = "not bad day";
		SentimentScore sentimentScore = sentiHandler.getSentimentScore(s, EN);
		assertThat(sentimentScore.getSentiment()).isEqualTo(Sentiment.POS);
		ArrayList<WordScore> wordScorePairs = sentimentScore.getWordScorePairs();

		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			isNegation("not");
		});
		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			isSentimentWord("bad", new Score(1, -2));
		});
		assertThat(sentimentScore.getFinalScore()).isEqualTo(new Score(1, -1));
	}

	@Test
	void testNegationNegativeSentimentExtreme() throws IOException {
		String s = "not devastating day";
		SentimentScore sentimentScore = sentiHandler.getSentimentScore(s, EN);
		assertThat(sentimentScore.getSentiment()).isEqualTo(Sentiment.POS);
		ArrayList<WordScore> wordScorePairs = sentimentScore.getWordScorePairs();

		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			isNegation("not");
		});
		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			isSentimentWord("devastating", new Score(1, -5));
		});
		assertThat(sentimentScore.getFinalScore()).isEqualTo(new Score(1, -1));
	}

	@Test
	void testNegationInfluenceOnlyNext3Words() throws IOException {
		String s = "Unfortunately I was not able to meet the wonderful and lovely host.";
		SentimentScore sentimentScore = sentiHandler.getSentimentScore(s, EN);
		assertThat(sentimentScore.getSentiment()).isEqualTo(Sentiment.POS);
		ArrayList<WordScore> wordScorePairs = sentimentScore.getWordScorePairs();

		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			isSentimentWord("Unfortunately", new Score(1, -2));
		});
		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			isNegation("not");
		});
		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			isSentimentWord("wonderful", new Score(3, -1));
		});
		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			isSentimentWord("lovely", new Score(2, -1));
		});
		assertThat(sentimentScore.getFinalScore()).isEqualTo(new Score(6, -4));

		s = "not a really good day";
		sentimentScore = sentiHandler.getSentimentScore(s, EN);
		assertThat(sentimentScore.getSentiment()).isEqualTo(Sentiment.NEG);
		wordScorePairs = sentimentScore.getWordScorePairs();

		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			isNegation("not");
		});
		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			isBooster("really", 1);
		});
		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			isSentimentWord("wonderful", new Score(3, -1));
		});
		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			isSentimentWord("lovely", new Score(2, -1));
		});
		assertThat(sentimentScore.getFinalScore()).isEqualTo(new Score(1, -1));
	}

	@Test
	void testNegationBoosterPositive() throws IOException {
		String s = "It is not very good.";
		SentimentScore sentimentScore = sentiHandler.getSentimentScore(s, EN);
		assertThat(sentimentScore.getSentiment()).isEqualTo(Sentiment.NEG);
		ArrayList<WordScore> wordScorePairs = sentimentScore.getWordScorePairs();

		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			isNegation("not");
		});
		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			isBooster("very", 1);
		});
		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			isSentimentWord("good", new Score(2, -1));
		});
		assertThat(sentimentScore.getFinalScore()).isEqualTo(new Score(1, -1));
	}

	@Test
	void testNegationBoosterPositiveExtreme() throws IOException {
		String s = "It is not very phenomenal.";
		SentimentScore sentimentScore = sentiHandler.getSentimentScore(s, EN);
		assertThat(sentimentScore.getSentiment()).isEqualTo(Sentiment.NEG);
		ArrayList<WordScore> wordScorePairs = sentimentScore.getWordScorePairs();

		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			isNegation("not");
		});
		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			isBooster("very", 1);
		});
		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			isSentimentWord("phenomenal", new Score(5, -1));
		});
		assertThat(sentimentScore.getFinalScore()).isEqualTo(new Score(1, -1));
	}

	@Test
	void testExclamationPositive() throws IOException {
		String s = "It's a good day!";
		SentimentScore sentimentScore = sentiHandler.getSentimentScore(s, EN);
		assertThat(sentimentScore.getSentiment()).isEqualTo(Sentiment.POS);
		ArrayList<WordScore> wordScorePairs = sentimentScore.getWordScorePairs();

		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			isSentimentWord("good", new Score(2, -1));
		});
		assertThat(sentimentScore.getFinalScore()).isEqualTo(new Score(3, -1));
	}

	@Test
	void testExclamationNegative() throws IOException {
		String s = "Host was unfriendly!";
		SentimentScore sentimentScore = sentiHandler.getSentimentScore(s, EN);
		assertThat(sentimentScore.getSentiment()).isEqualTo(Sentiment.NEG);
		ArrayList<WordScore> wordScorePairs = sentimentScore.getWordScorePairs();

		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			isSentimentWord("unfriendly", new Score(1, -3));
		});
		assertThat(sentimentScore.getFinalScore()).isEqualTo(new Score(1, -4));
	}

	@Test
	void testExclamationInfluenceOnlyLastSentiment() throws IOException {
		String s = "Good location but unfriendly host!";
		SentimentScore sentimentScore = sentiHandler.getSentimentScore(s, EN);
		assertThat(sentimentScore.getSentiment()).isEqualTo(Sentiment.NEG);
		ArrayList<WordScore> wordScorePairs = sentimentScore.getWordScorePairs();

		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			isSentimentWord("good", new Score(2, -1));
		});

		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			isSentimentWord("unfriendly", new Score(1, -3));
		});
		assertThat(sentimentScore.getFinalScore()).isEqualTo(new Score(3, -5));
	}

	@Test
	void testEmoticon() throws IOException {
		String s = "hello :D";
		SentimentScore sentimentScore = sentiHandler.getSentimentScore(s, EN);
		assertThat(sentimentScore.getSentiment()).isEqualTo(Sentiment.POS);
		ArrayList<WordScore> wordScorePairs = sentimentScore.getWordScorePairs();

		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			isBooster(":D", 1);
		});
		assertThat(sentimentScore.getFinalScore()).isEqualTo(new Score(1, 0));
	}

	@Test
	void testEmoticonMultiple() throws IOException {
		String s = "hello :D :) (^.^)";
		SentimentScore sentimentScore = sentiHandler.getSentimentScore(s, EN);
		assertThat(sentimentScore.getSentiment()).isEqualTo(Sentiment.POS);
		ArrayList<WordScore> wordScorePairs = sentimentScore.getWordScorePairs();

		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			isBooster(":D", 1);
		});
		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			isBooster(":)", 1);
		});
		assertThat(wordScorePairs).anySatisfy(wordScorePair -> {
			isBooster("(^.^)", 1);
		});
		assertThat(sentimentScore.getFinalScore()).isEqualTo(new Score(1, 0));
	}

}
