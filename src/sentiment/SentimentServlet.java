package sentiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import backend.language.LanguageHandler;
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
	

    public void init(ServletConfig cfg) throws ServletException
    {
    	ServletContext context = cfg.getServletContext();
    	String absoluteDiskPath = context.getRealPath("");
    	langHandler = new LanguageHandler(absoluteDiskPath);
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
		response.setContentType("text/html");
		//response.setContentType("application/json");
		
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
		if(multiplereview == null || multiplereview.equals("")){
			json.put("statusCode", new Integer(1));
		}
		else{
			json.put("statusCode", new Integer(0));
			String[] reviews = multiplereview.split("\\n");
			
			if(withKeywords != null){
				int lineNumber = 0;
				if(withKeywords.equals("false")){
					
//					String absoluteDiskPath = getServletContext().getRealPath("");
//					
//					LanguageHandler langHandler = new LanguageHandler(absoluteDiskPath);
//					SentimentHandler sentiHandler = new SentimentHandlerImpl(absoluteDiskPath);
					
					
					JSONArray sentimentList = new JSONArray();
					for(String e: reviews){
						lineNumber++;
						String lang = langHandler.detectLanguageFromString(e);
						String sentiment = sentiHandler.getSentiment(e,lang);
						JSONObject jsonobj = new JSONObject();
						jsonobj.put(lineNumber, sentiment);
						sentimentList.add(jsonobj);
					}
					json.put("sentiment", sentimentList);
				}
				else if(withKeywords.equals("true")){
					
					
					
					
				}
			}
			
		}
		PrintWriter out = response.getWriter();
		out.println(json.toJSONString());
		out.flush();
		
	}

}
