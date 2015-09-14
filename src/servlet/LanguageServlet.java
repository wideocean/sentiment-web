package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import backend.language.LanguageHandler;
import backend.language.LanguageHandlerImpl;

/**
 * Servlet implementation class LanguageServlet
 */
@WebServlet("/LanguageServlet")
public class LanguageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private LanguageHandler langHandler;
	
	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		ServletContext context = config.getServletContext();
    	String absoluteDiskPath = context.getRealPath("");
    	langHandler = new LanguageHandlerImpl(absoluteDiskPath);
		System.out.println("-----------------------------");
		System.out.println("    LanguageServlet started   ");
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
		
		if(requestURL.indexOf("detectLanguage") != -1){
			detectLanguage(request,response);
		}
		else{
			PrintWriter out = response.getWriter();
			out.println("Request URL " + request.getRequestURI() + " is unknown");
			out.flush();
		}
	}
	
	
	private void detectLanguage(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		
		String text = request.getParameter("text");
		
		JSONObject json = new JSONObject();
		if(text == null || text.equals("")){
			json.put("statusCode", new Integer(1));
		}
		else{
			json.put("statusCode", new Integer(0));
			String lang = langHandler.detectLanguageFromString(text);
			if(lang.equals("")){
				lang = "unknown";
			}
			json.put("lang", lang);
		}
		
		PrintWriter out = response.getWriter();
		out.println(json);
		out.flush();
		
	}

}
