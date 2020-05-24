package facade;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
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
	public Response getSentiment(@QueryParam("text") String text) {
		if (text == null || text.equals(""))
			return Response.status(400).entity("Invalid Request: no text provided").build();

		String absoluteDiskPath = servletContext.getRealPath("");
		String result = "";
		try {
			languageService = new LanguageServiceImpl();
			sentimentService = new SentimentServiceImpl(absoluteDiskPath);

			String[] reviews = text.split("\\n");
			int lineNumber = 0;

			for (String e : reviews) {
				lineNumber++;
				String lang = languageService.getLanguage(e);
				Sentiment sentiment;

				sentiment = sentimentService.getSentimentScore(e, lang).getSentiment();

				SentimentResult sentimentResult = new SentimentResult(lineNumber, sentiment, lang, null);

				result = mapper.writeValueAsString(sentimentResult);
			}

		} catch (IOException e1) {
			e1.printStackTrace();
			Response response = Response.status(500).entity(e1).build();
			throw new WebApplicationException(response);
		}

		return Response.status(200).entity(result).build();
	}

	@GET
	@Path("/keywords")
	public Response getSentimentKeywords(@QueryParam("text") String text) {
		if (text == null || text.equals(""))
			return Response.status(400).entity("Invalid Request: no text provided").build();

		String absoluteDiskPath = servletContext.getRealPath("");
		String result = "";
		try {
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

				SentimentResult sentimentResult = new SentimentResult(lineNumber, sentiment, lang, keywords);

				result = mapper.writeValueAsString(sentimentResult);
			}

		} catch (IOException e1) {
			e1.printStackTrace();
			Response response = Response.status(500).entity(e1).build();
			throw new WebApplicationException(response);
		}

		return Response.status(200).entity(result).build();
	}

}