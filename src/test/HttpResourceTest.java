package test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import model.Score;
import model.Sentiment;
import model.SentimentResult;
import model.WordScore;

/**
 * Tests regarding http resources.
 * 
 * NOTE: Requires the web service to be already running.
 * 
 * @author Pazifik
 *
 */
class HttpResourceTest {
	private final String URL = "http://localhost:8080/sentiment-web/api";
	private final String EN = "en";
	private ObjectMapper mapper = new ObjectMapper();

	@Test
	void testSimple() throws Exception {
		WebTarget webTarget = ClientBuilder.newClient().target(URL).path("sentiment");
		Response response = webTarget//
				.queryParam("text", "Today is a good day.")//
				.request(MediaType.APPLICATION_JSON)//
				.get();

		assertThat(response.getStatus()).isEqualTo(200);

		String result = response.readEntity(String.class);
		System.out.println("Result: " + result);

		List<Object> sentimentResults = Arrays.asList(mapper.readValue(result, SentimentResult[].class));
		for (Object obj : sentimentResults) {
			SentimentResult sentimentResult = (SentimentResult) obj;
			assertThat(sentimentResult.getLineNumber()).isEqualTo(1);
			assertThat(sentimentResult.getLang()).isEqualTo(EN);
			assertThat(sentimentResult.getSentiment()).isEqualTo(Sentiment.POS);
		}
	}

	@Test
	void testWithKeywords() throws Exception {
		WebTarget webTarget = ClientBuilder.newClient().target(URL).path("sentiment");
		Response response = webTarget//
				.path("keywords")//
				.queryParam("text", "Today is not very good day.")//
				.request(MediaType.APPLICATION_JSON)//
				.get();

		assertThat(response.getStatus()).isEqualTo(200);

		String result = response.readEntity(String.class);
		System.out.println("Result: " + result);

		List<Object> sentimentResults = Arrays.asList(mapper.readValue(result, SentimentResult[].class));
		for (Object obj : sentimentResults) {
			SentimentResult sentimentResult = (SentimentResult) obj;
			assertThat(sentimentResult.getLineNumber()).isEqualTo(1);
			assertThat(sentimentResult.getLang()).isEqualTo(EN);
			assertThat(sentimentResult.getSentiment()).isEqualTo(Sentiment.NEG);
			List<WordScore> keywords = sentimentResult.getKeywords();
			assertThat(keywords).anySatisfy(wordScore -> {
				assertThat(wordScore.getWord()).isEqualTo("not");
				assertThat(wordScore.getNegation()).isTrue();
			});
			assertThat(keywords).anySatisfy(wordScore -> {
				assertThat(wordScore.getWord()).isEqualTo("very");
				assertThat(wordScore.getBoosterScore()).isEqualTo(1);
			});
			assertThat(keywords).anySatisfy(wordScore -> {
				assertThat(wordScore.getWord()).isEqualTo("good");
				assertThat(wordScore.getScore()).isEqualTo(new Score(2, -1));
			});
		}

	}

	@Test
	void testNoTextProvided() throws Exception {
		WebTarget webTarget = ClientBuilder.newClient().target(URL).path("sentiment");
		Response response = webTarget//
				.request(MediaType.APPLICATION_JSON)//
				.get();
		assertThat(response.getStatus()).isEqualTo(400);

		response = webTarget//
				.queryParam("text", "")//
				.request(MediaType.APPLICATION_JSON)//
				.get();
		assertThat(response.getStatus()).isEqualTo(400);

		response = webTarget//
				.path("keywords")//
				.request(MediaType.APPLICATION_JSON)//
				.get();
		assertThat(response.getStatus()).isEqualTo(400);

		response = webTarget//
				.path("keywords")//
				.queryParam("text", "")//
				.request(MediaType.APPLICATION_JSON)//
				.get();
		assertThat(response.getStatus()).isEqualTo(400);
	}

}
