package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import backend.data.SentimentWordScorePair;
import backend.data.WordScorePair;
import backend.language.LanguageHandler;
import backend.language.LanguageHandlerImpl;
import backend.sentiment.SentimentHandler;
import backend.sentiment.SentimentHandlerImpl;


/**
 * Servlet implementation class SentimentServlet
 */
@WebServlet("/SentimentServlet")
public class SentimentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private SentimentHandler sentiHandler;
	private LanguageHandler langHandler;
	

    public void init(ServletConfig config) throws ServletException
    {
    	ServletContext context = config.getServletContext();
    	String absoluteDiskPath = context.getRealPath("");
    	langHandler = new LanguageHandlerImpl(absoluteDiskPath);
		sentiHandler = new SentimentHandlerImpl(absoluteDiskPath);
		System.out.println("-----------------------------");
		System.out.println("    SentimentServlet started   ");
		System.out.println("-----------------------------");
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");
		
		StringBuffer requestURL = request.getRequestURL();
		
		if(requestURL.indexOf("detectSentiment") != -1){
			detectSentiment(request,response);
		}
		else{
			PrintWriter out = response.getWriter();
			out.println("Request URL " + request.getRequestURI() + " is unknown");
			out.flush();
		}
	}
	
	private void detectSentiment(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		
		String multiplereview = request.getParameter("reviewtext");
		String withKeywords = request.getParameter("keywords");
		
		JSONObject json = new JSONObject();
		if(multiplereview == null || multiplereview.equals("") || 
			withKeywords == null || withKeywords.equals("") || (!withKeywords.equals("true") && !withKeywords.equals("false"))){
			json.put("statusCode", new Integer(1));
		}
		else{
			json.put("statusCode", new Integer(0));
			String[] reviews = multiplereview.split("\\n");
			int lineNumber = 0;
			if(withKeywords.equals("false")){
				
				JSONArray resultsList = new JSONArray();
				for(String e: reviews){
					lineNumber++;
					String lang = langHandler.detectLanguageFromString(e);
					String sentiment = sentiHandler.getSentiment(e,lang).getSentiment();
					
					if(sentiment.equals("")){
						sentiment = "unknown";
					}
					if(lang.equals("")){
						lang = "unknown";
					}
					JSONObject jsonobj = new JSONObject();
					jsonobj.put("sentiment", sentiment);
					jsonobj.put("lang", lang);
					
					JSONObject line = new JSONObject();
					line.put(lineNumber, jsonobj);
					resultsList.add(line);
				}
				json.put("results", resultsList);
			}
			else if(withKeywords.equals("true")){
				
				JSONArray resultsList = new JSONArray();
				for(String e: reviews){
					lineNumber++;
					String lang = langHandler.detectLanguageFromString(e);
					SentimentWordScorePair swsp = sentiHandler.getSentiment(e,lang);
					
					String sentiment = swsp.getSentiment();
					ArrayList<WordScorePair> keywords = swsp.getWordScorePairs();
					
					if(sentiment.equals("")){
						sentiment = "unknown";
					}
					if(lang.equals("")){
						lang = "unknown";
					}
					JSONObject jsonobj = new JSONObject();
					JSONArray wordList = new JSONArray();
					jsonobj.put("sentiment", sentiment);
					jsonobj.put("lang", lang);
					
					
					for(WordScorePair wsp: keywords){
						JSONObject jsonobj2 = new JSONObject();
						jsonobj2.put("word", wsp.getWord());
						if(wsp.getScorePair() != null){
							jsonobj2.put("score", wsp.getScorePair().toString());
						}
						else if(wsp.getScore() != 0){
							jsonobj2.put("score", wsp.getScore());
						}
						else if(wsp.getNegation() != null){
							jsonobj2.put("score", wsp.getNegation());
						}
						wordList.add(jsonobj2);
					}
					
					jsonobj.put("keywords", wordList);
					
					JSONObject line = new JSONObject();
					line.put(lineNumber, jsonobj);
					resultsList.add(line);
				}
				json.put("results", resultsList);
			}
			
		}
		
		PrintWriter out = response.getWriter();
		out.println(json);
		out.flush();
		
	}

}
