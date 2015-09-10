package sentiment;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


/**
 * Servlet implementation class SentimentServlet
 */
@WebServlet("/SentimentServlet")
public class SentimentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SentimentServlet() {
        super();
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
		JSONObject jsonobj = new JSONObject();
		if(multiplereview == null || multiplereview.equals("")){
			jsonobj.put("statusCode", new Integer(1));
		}
		else{
			jsonobj.put("statusCode", new Integer(0));
			
		}
//		System.out.println(multiplereview);
		String[] test = multiplereview.split("\\n");
		System.out.println(test[0]);
		System.out.println("Request URL " + request.getRequestURI());
		PrintWriter out = response.getWriter();
		out.println(jsonobj.toJSONString());
		out.flush();
		
	}

}
