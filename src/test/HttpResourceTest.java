package test;

import static org.assertj.core.api.Assertions.assertThat;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import model.Sentiment;
import model.SentimentResult;

class HttpResourceTest {
	private final static String URL = "http://localhost:8080/sentiment-web";
	private ObjectMapper mapper = new ObjectMapper();

	@Test
	void test() throws Exception {
		WebTarget webTarget = ClientBuilder.newClient().target(URL).path("sentiment");
		Response response = webTarget//
				.queryParam("text", "Heute ist ein schöner Tag")//
				.request(MediaType.APPLICATION_JSON)//
				.get();

		assertThat(response.getStatus()).isEqualTo(200);

		String result = response.readEntity(String.class);
		SentimentResult sentimentResult = mapper.readValue(result, SentimentResult.class);

		assertThat(sentimentResult.getLineNumber()).isEqualTo(1);
		assertThat(sentimentResult.getLang()).isEqualTo("de");
		assertThat(sentimentResult.getSentiment()).isEqualTo(Sentiment.POS);
	}

}
