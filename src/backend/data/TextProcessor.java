package backend.data;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import backend.language.LanguageHandler;

/**
 * Helping class for Data preparation or Data Processing
 * @author Pazifik
 *
 */
public class TextProcessor {

	private LanguageHandler languageHandler;
	
	
	public TextProcessor(){
		
	}
	
	/**
	 * Helper method to order the reviews to get 1 review per line
	 * @param file
	 */
	public void sortReviews(File file){
		
		long startTime = System.nanoTime();
		try {
			String filepath = file.getAbsolutePath();
			
			BufferedReader br = new BufferedReader(new FileReader(file));
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filepath.substring(0, filepath.length()-4)+"+Results.txt")));
			
			String line = null;
			while( (line = br.readLine()) != null){
			    if(line.startsWith("|")){
			    	writer.write(System.lineSeparator() + line); 
			    	writer.flush();
			    }
			    else{
			    	writer.write(line); 
			    	writer.flush();
			    }
				
			}
			br.close();
			writer.close();
			
			long estimatedTime = System.nanoTime() - startTime;
			System.out.println("End Reviews Ordering: "+ estimatedTime +" ns");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Extracts only German & English reviews of a txt file and writes them into 2 separate txt files
	 * @param file
	 */
	public void separateLanguage(File file){
		
		languageHandler = new LanguageHandler();
		ArrayList<String> en_reviews = new ArrayList<String>();
		ArrayList<String> de_reviews = new ArrayList<String>();
		
		long startTime = System.nanoTime();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));

			String line = null;
			while( (line = br.readLine()) != null){
		    	String lang = languageHandler.detectLanguageFromString(line);
		    	// We only want EN and DE
		    	if(lang.equals("en")){
//			    	System.out.println(lang);
		    		en_reviews.add(line);
		    	}
		    	else if(lang.equals("de")){
//			    	System.out.println(lang);
		    		de_reviews.add(line);
		    	}
			}
			long estimatedTime = System.nanoTime() - startTime;
			System.out.println("End Language Detection: "+ estimatedTime +" ns");
			
		    BufferedWriter writer;
		    
		    String filepath = file.getAbsolutePath();
		    
		    // en
		    writer = new BufferedWriter(new FileWriter(new File(filepath.substring(0, filepath.length()-4)+"+onlyEN.txt")));
		    for(String s : en_reviews){
		        writer.write(s + System.lineSeparator()); 	
			}
		    writer.flush();
		    writer.close();
		    // de
		    writer = new BufferedWriter(new FileWriter(new File(filepath.substring(0, filepath.length()-4)+"+onlyDE.txt")));
		    for(String s : de_reviews){
		        writer.write(s + System.lineSeparator()); 	
			}
		    writer.flush();
		    writer.close();
		    
		    try {
				writer = new BufferedWriter(new FileWriter(new File(filepath.substring(0, filepath.length()-4)+"+LangNotDetected.txt")));
				
				for(String s: languageHandler.getUndetected_reviews()){
					writer.write((s + System.lineSeparator()));
				}
				writer.flush();
				writer.close();
		    } catch (IOException e1) {
				e1.printStackTrace();
			}
		    
		    long estimatedTime2 = System.nanoTime() - startTime;
			System.out.println("End Language Detection + Separating EN/DE reviews into txt files: "+ estimatedTime2 +" ns");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Counts all occurrences of all words from a txt file and writes them in a output txt file.
	 * For GERMAN, one has to use a BreakIterator with Locale setting to correctly handle german specific things 
	 * e.g. "Umlaute" etc.
	 * @param file
	 */
	public void countWords(File file){
		
		HashMap<String,Integer> occurences = new HashMap<String,Integer>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));

			String line = null;
			while( (line = br.readLine()) != null){
					////////////////
					// FOR ENGLISH
					////////////////
			    String [] tokens = line.split("\\s+");
			    for(String word: tokens) {
			    	word = word.replaceAll("[^a-zA-Z]", "");
			    	word = word.toLowerCase();
			    	if(occurences.containsKey(word))
			    		occurences.put(word, occurences.get(word)+1);
			    	else
			    		occurences.put(word, 1);
		    	}
				
			    	////////////////
			    	// FOR GERMAN
			    	////////////////
//			    BreakIterator bisentence = BreakIterator.getSentenceInstance(Locale.GERMAN);
//				BreakIterator biword = BreakIterator.getWordInstance(Locale.GERMAN);
//			    
//		        // Set the text string to be scanned.
//				bisentence.setText(line);
//				
//				String s;
//				ArrayList<String> sentences = new ArrayList<String>();
//		        // Extract all Sentences
//				int index = 0;
//				while (bisentence.next() != BreakIterator.DONE) {
//					s = line.substring(index, bisentence.current());
//					System.out.println("\nSentence: " + s);
//					sentences.add(s);
//					index = bisentence.current();
//				}
//				
//				for(String sentence: sentences){
//					// Set the text string to be scanned.
//					biword.setText(sentence);
//
//			        int lastIndex = biword.first();
//			        // Iterate through words
//			        while (lastIndex != BreakIterator.DONE){
//			        	int firstIndex = lastIndex;
//			            lastIndex = biword.next();
//			            if (lastIndex != BreakIterator.DONE && Character.isLetterOrDigit(sentence.charAt(firstIndex))){
//			            	String word = sentence.substring(firstIndex, lastIndex);
//			            	word = word.toLowerCase();
//			            	if(occurences.containsKey(word))
//					            occurences.put(word, occurences.get(word)+1);
//					        else
//					            occurences.put(word, 1);
//			            }
//			        }
//				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally{
			if(br != null){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		Object[] a = occurences.entrySet().toArray();
	    Arrays.sort(a, new Comparator() {
	        public int compare(Object o1, Object o2) {
	            return ((Map.Entry<String, Integer>) o2).getValue().compareTo(
	                    ((Map.Entry<String, Integer>) o1).getValue());
	        }
	    });
	    for (Object e : a) {
	        System.out.println(((Map.Entry<String, Integer>) e).getKey() + " : "
	                + ((Map.Entry<String, Integer>) e).getValue());
	    }
		
	    BufferedWriter writer;
	    try
	    {
	    	String filepath = file.getAbsolutePath();
	        writer = new BufferedWriter(new FileWriter(new File(filepath.substring(0, filepath.length()-4)+"+RESULTS.txt")));
	        for(Object e : a){
	        	writer.write(((Map.Entry<String, Integer>) e).getKey() + "\t"
	        			+ ((Map.Entry<String, Integer>) e).getValue() + System.lineSeparator());
	        	
		    }
	        writer.flush();
	    }
	    catch ( IOException e){
	    	e.printStackTrace();
	    }
	
	}
	
	
	/**
	 * Compares two sentiment dictionaries with format word \t score.
	 * Loops through file1 and checks for each word if it is present in file2.
	 * Missing words will be written in a output file.
	 * @param file1
	 * @param file2
	 */
	public void compareDictionaries(File file1, File file2){
		BufferedReader br = null;
		BufferedReader br2 = null;
		BufferedWriter writer = null;
		HashMap<String,Integer> occurences = new HashMap<String,Integer>();
		try {
			br = new BufferedReader(new FileReader(file1));
			br2 = new BufferedReader(new FileReader(file2));
			writer = new BufferedWriter(new FileWriter(new File("C:/Users/Pazifik/Desktop/results.txt")));

			long startTime = System.nanoTime();
			
			String line = null;
			boolean matched = false;
			String dictentryFile2 = "";
			String[] dictentry2 = null;
			while( (line = br.readLine()) != null){
				String[] dictentry = line.split("\\t");
//				String[] dictentry = line.split("\\s+");
				matched = false;
				
			    String line2 = null;
			    br2 = new BufferedReader(new FileReader(file2));
			    
				while( (line2 = br2.readLine()) != null){
					dictentry2 = line2.split("\\t");
				    System.out.println(dictentry[0] + " + " + dictentry2[0]);
					if(match(dictentry[0], dictentry2[0])){
//				    if(dictentry[0].toLowerCase().equals(dictentry2[0])){
//				    if(dictentry[0].contains(dictentry2[0])){
						matched = true;
						break;
//						if(matched){
//							writer.write(dictentry2[0] + "\t" + dictentry2[1] + System.lineSeparator());
////							writer.write(dictentry[0] + "\t" + dictentry[1] + "\t" + dictentry2[0] + "\t" + dictentry2[1] + System.lineSeparator());
////							writer.write(dictentry[0] + "\t" + dictentry[1] + "\t" + dictentryFile2 + System.lineSeparator());
//						    writer.flush();
//						    matched = false;
//						}
					}
				}
				 
//				if(matched){
////					if(occurences.containsKey(dictentry2[0]))
////			            occurences.put(dictentry2[0], occurences.get(dictentry2[0])+ Integer.parseInt(dictentry[1]));
////			        else
////			            occurences.put(dictentry2[0], Integer.parseInt(dictentry[1]));
//					writer.write(dictentry2[0] + "\t" + dictentry2[1] + System.lineSeparator());
////					writer.write(dictentry[0] + "\t" + dictentry[1] + "\t" + dictentry2[0] + System.lineSeparator());
////					writer.write(dictentry[0] + "\t" + dictentry[1] + "\t" + dictentry2[0] + "\t" + dictentry2[1] + System.lineSeparator());
//				    writer.flush();
//				}
				
				if(!matched){
//					for(String s: dictentry){
//						writer.write(s+" ");
//					}
//					writer.write(System.lineSeparator());
					
					writer.write(dictentry[0] + "\t" + dictentry[1] + System.lineSeparator());
				    writer.flush();
				}
				
			}
			
//			Object[] a = occurences.entrySet().toArray();
//		    Arrays.sort(a, new Comparator() {
//		        public int compare(Object o1, Object o2) {
//		            return ((Map.Entry<String, Integer>) o2).getValue().compareTo(
//		                    ((Map.Entry<String, Integer>) o1).getValue());
//		        }
//		    });
//		    for(Object e : a){
//	        	writer.write(((Map.Entry<String, Integer>) e).getKey() + "\t"
//	        			+ ((Map.Entry<String, Integer>) e).getValue() + System.lineSeparator());
//	        	
//		    }
//	        writer.flush();
			
			
			long estimatedTime = System.nanoTime() - startTime;
			System.out.println("End Sentiment Detection Single Language File: "+ estimatedTime +" ns");
				
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				if(br != null){
					br.close();
				}
				if(br2 != null){
					br2.close();
				}
				if(writer != null){
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Filtering all lowercase words from a word list.
	 * (Used for Clematide & Klenner Dictionary)
	 * @param file
	 */
	public void filterSmall(File file){
		
		BufferedReader br = null;
		BufferedWriter writer = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String filepath = file.getAbsolutePath();
			writer = new BufferedWriter(new FileWriter(new File(filepath.substring(0, filepath.length()-4)+"+RESULTS.txt")));
			String line = null;
			while( (line = br.readLine()) != null){
			    String [] tokens = line.split("\\t");
			    
			    if(Character.isLowerCase(tokens[0].charAt(0))){
//			        writer.write(tokens[0] + "\t" + tokens[1] + System.lineSeparator());
			        writer.write(tokens[0] + System.lineSeparator());
			        writer.flush();
			    }
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally{
			try {
				if(br != null){
					br.close();
				}
				if(writer != null){
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * This method tests if a string matches a wildcard expression
	 * (supporting ? for exactly one character or * for an arbitrary number of characters)
	 * @param text
	 * @param pattern
	 * @return
	 */
	public static boolean match(String text, String pattern)
	{
		String text2 = text.toLowerCase();
		String pattern2 = pattern.toLowerCase();
		return text2.matches(pattern2.replace("?", ".?").replace("*", ".*?"));
	}

	
	public static void main(String[] args){
		TextProcessor textprocessor = new TextProcessor();
		
//		File file = new File("C:/Users/Pazifik/Desktop/reviews_set1-5.txt");
//		File file2 = new File("C:/Users/Pazifik/Desktop/reviews_set1-5+onlyEN.txt");
//		File file3 = new File("C:/Users/Pazifik/Desktop/reviews_set1-5+onlyDE.txt");
		
//		File file4 = new File("C:/Users/Pazifik/Desktop/GermanPolarityClues-Negative_onlyUppercase_NOT_in_SentiStrength.txt");
//		File file5 = new File("C:/Users/Pazifik/Desktop/reviews_set1-5+onlyDE+RESULTS.txt");
//		File file4 = new File("C:/Users/Pazifik/Desktop/test1.txt");
//		File file5 = new File("C:/Users/Pazifik/Desktop/test2.txt");
		
//		File file6 = new File("C:/Users/Pazifik/Desktop/GermanPolarityClues-Negative.txt");
//		File file7 = new File("C:/Users/Pazifik/Desktop/test.txt");
		
//		textprocessor.sortReviews(file);
//		textprocessor.filterSmall(file6);
//		textprocessor.countWords(file2);
//		textprocessor.countWords(file3);
//		textprocessor.separateLanguage(file);
//		textprocessor.compareDictionaries(file4, file5);
		
		
		
	}
	

}


