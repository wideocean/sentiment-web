package facade;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import model.Sentiment;
import service.LanguageService;
import service.LanguageServiceImpl;

@Path("/hello")
public class HelloWorldResource {

	LanguageService languageService = new LanguageServiceImpl();

	@GET
	@Path("/{param}")
	public Response getMsg(@PathParam("param") String msg) {

		String output = "Jersey say : " + msg;

		return Response.status(200).entity(output).build();

	}

	@GET
	@Path("/test")
	public Response getLanguage(@QueryParam("text") String msg) {

		String lang = languageService.getLanguage(msg);
		String output = "Jersey say : " + lang;
		System.out.println("ASDSAD: " + Sentiment.POS);

		return Response.status(200).entity(output).build();

	}

}