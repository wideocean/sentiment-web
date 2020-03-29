package facade;
//package servlet;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.NameValuePair;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.util.EntityUtils;
//
///**
// * Demo class for sending a request for sentiment analysis
// * @author Pazifik
// *
// */
//public class PostSimulator {
//
//	// this is your target url
//    private static String url = "http://localhost:8080/sentiment-web/detectSentiment";
//     
//    public static void main(String[] args) throws Exception  {
//    	HttpClient httpclient = HttpClients.createDefault();
//    	HttpPost httppost = new HttpPost(url);
//
//    	// Request parameters and other properties.
//    	List<NameValuePair> params = new ArrayList<NameValuePair>(2);
//
//    	params.add(new BasicNameValuePair("reviewtext", "schöner tag heute."));
//    	params.add(new BasicNameValuePair("keywords", "true"));
//    	
//    	// Set encoding to UTF-8 IMPORTANT!!!
//    	httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
//
//    	//Execute and get the response.
//    	HttpResponse response = httpclient.execute(httppost);
//    	HttpEntity entity = response.getEntity();
//
//    	if (entity != null) {
//    		// read the server's response contents
//            System.out.println(EntityUtils.toString(entity));
//    	}
//    }
//
//}
