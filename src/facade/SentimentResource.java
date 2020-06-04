package facade;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;

import model.Sentiment;
import model.SentimentResult;
import model.SentimentScore;
import model.WordScore;
import service.LanguageService;
import service.LanguageServiceImpl;
import service.SentimentService;
import service.SentimentServiceImpl;

@Path("/sentiment")
public class SentimentResource {

	@Context
	private ServletContext servletContext;

	private LanguageService languageService;
	private SentimentService sentimentService;

	private final ObjectMapper mapper = new ObjectMapper();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSentiment(@QueryParam("text") String text) {
		String result = "";
		try {
			List<SentimentResult> results = getSentiment(text, false);

			if (results == null)
				return Response.status(400).entity("Invalid Request: no text provided").build();

			result = mapper.writeValueAsString(results);

		} catch (IOException e1) {
			e1.printStackTrace();
			Response response = Response.status(500).entity(e1).build();
			throw new WebApplicationException(response);
		}

		return Response.status(200).entity(result).build();
	}

	@GET
	@Path("/keywords")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSentimentKeywords(@QueryParam("text") String text) {
		String result = "";
		try {
			List<SentimentResult> results = getSentiment(text, true);

			if (results == null)
				return Response.status(400).entity("Invalid Request: no text provided").build();

			result = mapper.writeValueAsString(results);

		} catch (IOException e1) {
			e1.printStackTrace();
			Response response = Response.status(500).entity(e1).build();
			throw new WebApplicationException(response);
		}

		return Response.status(200).entity(result).build();
	}

	private List<SentimentResult> getSentiment(String text, boolean withKeywords) throws IOException {
		if (text == null || text.equals(""))
			return null;
		String absoluteDiskPath = servletContext.getRealPath("");
		List<SentimentResult> results = new ArrayList<SentimentResult>();

		languageService = new LanguageServiceImpl();
		sentimentService = new SentimentServiceImpl(absoluteDiskPath);

		String[] reviews = text.split("\\n");
		int lineNumber = 0;

		for (String e : reviews) {
			lineNumber++;
			String lang = languageService.getLanguage(e);
			SentimentScore swsp;

			swsp = sentimentService.getSentimentScore(e, lang);

			Sentiment sentiment = swsp.getSentiment();
			List<WordScore> keywords = swsp.getWordScorePairs();

			SentimentResult sentimentResult = new SentimentResult(lineNumber, sentiment, lang, null);
			if (withKeywords)
				sentimentResult.setKeywords(keywords);

			results.add(sentimentResult);
		}
		return results;
	}

}