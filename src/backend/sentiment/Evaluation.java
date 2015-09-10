package backend.sentiment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import backend.data.CharsetReader;
import backend.sentiment.SentimentHandlerImpl;
//import uk.ac.wlv.sentistrength.SentiStrength;


public class Evaluation {
	
	public Evaluation(){
		
	}
	
	/**
	 * Checks if the given sentiment dictionary file has a correct tabulation format
	 * @param file
	 * @return false if not
	 */
	public boolean checkDictionaryFormat(File file){
		String filepath = file.getAbsolutePath();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			int lineNumber = 0;
			
			String line = null;
			while( (line = br.readLine()) != null){
				lineNumber++;
				// If it's a comment, skip this line.
				if (!line.trim().startsWith("#")) {
					// TAB Separation
					String[] data = line.split("\t");

					// Example line:
					// SentimentWord \t Score \t Comments..
					
					// Is it a valid line? Otherwise, through exception.
					if (data.length < 2 || data[1].equals("")){
						System.out.println("Incorrect tabulation format in "+ filepath.substring(0, filepath.length()-4) + ", line: "+ lineNumber);
//						throw new IllegalArgumentException(
//								"Incorrect tabulation format in "+ filepath.substring(0, filepath.length()-4) + ", line: "+ lineNumber);
						return false;
					}
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally{
			try {
				if(br != null){
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	

	public static void main(String[] args) {
//		File file = new File("dictionaries/de/Custom/EmotionLookupTable.txt");
//		Evaluation eval = new Evaluation();
//		boolean result = eval.checkDictionaryFormat(file);
//		
//		if(result){
//			System.out.println("TRUE");
//		}
//		else{
//			System.out.println("FALSE");
//		}
		
//		// Custom 
//		SentimentHandler sentiHandler = new SentimentHandler();
//		
//		// SentiStrength Original
//		SentiStrength eng_classifier = new SentiStrength();
//		SentiStrength ger_classifier = new SentiStrength();
//		String eng_init[] = {"sentidata", "sentistrength/en/", "trinary"};
//		String de_init[] = {"sentidata", "sentistrength/de/", "trinary"};
//		eng_classifier.initialise(eng_init);
//		ger_classifier.initialise(de_init);
//
//		// Test File Location
////		File file = new File("C:/Users/Pazifik/Desktop/Test_DE.txt");
////		File file = new File("C:/Users/Pazifik/Desktop/Test_EN.txt");
//		File file = new File("C:/Users/Pazifik/Desktop/Test_EN_zweite10k.txt");
////		File file = new File("C:/Users/Pazifik/Desktop/Test_unsicher.txt");
//		String filepath = file.getAbsolutePath();
//		
//		
//		BufferedReader br = null;
//		BufferedWriter writer = null;
//		BufferedWriter writer2 = null;
//		try {
//			br = new CharsetReader().getBufferedReader(file);
//			writer = new BufferedWriter(new FileWriter(new File(filepath.substring(0, filepath.length()-4)+"_DIFFERENCES.txt")));
//			writer2 = new BufferedWriter(new FileWriter(new File(filepath.substring(0, filepath.length()-4)+"_GLEICH.txt")));
//			writer.write("SentiStrength\tMaximization\tAggregation"+ System.lineSeparator());
//			writer2.write("SentiStrength\tMaximization\tAggregation"+ System.lineSeparator());
//			long startTime = System.nanoTime();
//			
//			String line = null;
//			int lineNumber = 0;
//			while( (line = br.readLine()) != null){
//				lineNumber++;
//			    	
//		    	// SentiStrength German
////		    	String sentiment_SentiStrength = ger_classifier.computeSentimentScores(line);
//		    	
//		    	// SentiStrength English
//		    	String sentiment_SentiStrength = eng_classifier.computeSentimentScores(line);
//		    	
//		    	// Custom German
////		    	String sentiment_Maximization = sentiHandler.getSentimentMaximization(line, "de");
////		    	String sentiment_Aggregation = sentiHandler.getSentimentAggregation(line, "de");
//		    	
//		    	// Custom English
//			    String sentiment_Maximization = sentiHandler.getSentimentMaximization(line, "en");
//			    String sentiment_Aggregation = sentiHandler.getSentimentAggregation(line, "en");
//		    	
//	    		String[] trinary_score = sentiment_SentiStrength.split("\\s+");
//	    		String trinary = "";
//	    		if(trinary_score[2].equals("-1")){
//	    			trinary = "neg";
//	    		}
//	    		else if(trinary_score[2].equals("0")){
//	    			trinary = "neu";
//	    		}
//	    		else if(trinary_score[2].equals("1")){
//	    			trinary = "pos";
//	    		}
//	    		
//	    		//TODO NACHHER ENTFERNEN
//	    		if(!trinary.equals(sentiment_Maximization) || !trinary.equals(sentiment_Aggregation)){
//	    			writer.write(lineNumber + "." + trinary + "\t" + sentiment_Maximization + "\t" + sentiment_Aggregation + System.lineSeparator());
//				    writer.flush();
//	    		}
//	    		else{
//	    			writer2.write(lineNumber + "." + trinary + "\t" + sentiment_Maximization + "\t" + sentiment_Aggregation + System.lineSeparator());
//				    writer2.flush();
//	    		}
//		    		
//			}
//			long estimatedTime = System.nanoTime() - startTime;
//			System.out.println("End Sentiment Detection Single Language File: "+ estimatedTime +" ns");
//				
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally{
//			try {
//				if(br != null){
//					br.close();
//				}
//				if(writer != null){
//					writer.close();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
	}

}
