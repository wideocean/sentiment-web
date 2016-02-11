package backend.sentiment;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Locale;

import backend.language.Language;
import backend.data.CharsetReader;
import backend.data.Dictionaries;
import backend.data.Pair;
import backend.data.SentimentWordScorePair;
import backend.data.WordScorePair;


public class SentimentHandlerImpl implements SentimentHandler{
	
	private Dictionaries dictionaries;
	private File dictfile;
	private File negationfile;
	private File boosterfile;
	private File emoticonfile;
	private ArrayList<WordScorePair> wordscorepairs;

	public SentimentHandlerImpl(String filepath){
		dictionaries = new Dictionaries(filepath);
		wordscorepairs = new ArrayList<WordScorePair>();
	}
	

	@Override
	public SentimentWordScorePair getSentiment(String string, String lang) {
		
		this.wordscorepairs = new ArrayList<WordScorePair>();
		
		// Aggregation is chosen based on better results in evaluation
//		String sentiment = getSentimentMaximization(string, lang);
		String sentiment = getSentimentAggregation(string, lang);
		
		SentimentWordScorePair swsp = new SentimentWordScorePair(sentiment,this.wordscorepairs);
		return swsp;
	}
	
	
	/**
	 * Extracts the sentiment by line maximization from a given string and returns the sentiment
	 * "pos" , "neg" , "neu"
	 * @param string
	 * @param lang
	 * @return empty string if language is not German or English. Otherwise, returns the sentiment "pos" , "neg" , "neu"
	 */
	public String getSentimentMaximization(String string, String lang){
			
        // Gets an instance of BreakIterator for sentence/word break for the
        // given locale. We can instantiate a BreakIterator without
        // specifying the locale. The locale is important when we
        // are working with languages like Japanese or Chinese where
        // the breaks standard may be different compared to English.
		BreakIterator bisentence;
		BreakIterator biword;
		if(lang.equals("en")){
			bisentence = BreakIterator.getSentenceInstance(Locale.ENGLISH);
			biword = BreakIterator.getWordInstance(Locale.ENGLISH);
			
			dictionaries.loadEN();
			dictfile = Dictionaries.dictfile;
			negationfile = Dictionaries.negationfile;
			boosterfile = Dictionaries.boosterfile;
			emoticonfile = Dictionaries.emoticonfile;
		}
		else if(lang.equals("de")){
			bisentence = BreakIterator.getSentenceInstance(Locale.GERMAN);
			biword = BreakIterator.getWordInstance(Locale.GERMAN);
			
			dictionaries.loadDE();
			dictfile = Dictionaries.dictfile;
			negationfile = Dictionaries.negationfile;
			boosterfile = Dictionaries.boosterfile;
			emoticonfile = Dictionaries.emoticonfile;
		}
		else{
			if(lang.contains("-"))
				lang.replace("-", "_");
			if(lang.equals(""))
				System.out.println("Language could not be detected.");
			else
				System.out.println("Language "+ Language.get(lang) +" is not supported.");
			return "";
		}
		
        // Set the text string to be scanned.
		bisentence.setText(string);
		
		ArrayList<String> sentences = new ArrayList<String>();
		String s;
		
        // Extract all Sentences
		int index = 0;
		while (bisentence.next() != BreakIterator.DONE) {
			s = string.substring(index, bisentence.current());
			System.out.println("\nSentence: " + s);
			sentences.add(s);
			index = bisentence.current();
		}
		
		ArrayList<Pair> sentenceScores = new ArrayList<Pair>();
		boolean printed = false;
		int lineNumber = 0;
		
		boolean posDetected = false;
		boolean negDetected = false;
		// Iterate through Sentences
		for(String sentence: sentences){
			float posScore = 1;
			float negScore = -1;
			float tempPosScore = 0;
			float tempNegScore = 0;
			
			float boosterScore = 0;
			boolean boosterDetected = false;
			String boosterWord = "";
			
			boolean negateSentiment = false;
			boolean negationDetected = false;
			int negationCounter = 0;
			
			boolean exclamationDetected = false;
			boolean lastSentimentPositive = false;
			
			float emoticonScore = 0;
			
			// Set the text string to be scanned.
			biword.setText(sentence);
			
			// Exclamation (!) Detection
			if(sentence.contains("!")){
				exclamationDetected = true;
			}
			
			// Emoticon (Smileys) Detection
			BufferedReader emoticonbr = null;
            try {
				String[] words = sentence.split("\\s+");
				for(String e: words){
					emoticonbr = new CharsetReader().getBufferedReader(emoticonfile);
					// Iterate through Emoticon-File
					lineNumber = 0;
					String emoticonline = null;
	                while( (emoticonline = emoticonbr.readLine()) != null){
	                	lineNumber++;
	                	String[] emoticonentries = emoticonline.split("\\t");
	                	
                		if(e.equals(emoticonentries[0])){
                			if(!isFloat(emoticonentries[1])){
					    		throw new NumberFormatException(
					    				"Sentiment Score for \"" + e + "\" has to be Integer/Float, line: "+lineNumber);
					    	}
	                		emoticonScore = emoticonScore + Float.parseFloat(emoticonentries[1]);
	                		wordscorepairs.add(new WordScorePair(emoticonentries[0],emoticonentries[1]));
                		}
	                } // END Iterate through Emoticon-File
				}
				
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
            
            ArrayList<String> sysprints = new ArrayList<String>();
            
	        System.out.println("\nIterate each word: ");
	        int lastIndex = biword.first();
	        // Iterate through words
	        while (lastIndex != BreakIterator.DONE) {
	            int firstIndex = lastIndex;
	            lastIndex = biword.next();
	            if (lastIndex != BreakIterator.DONE && Character.isLetterOrDigit(sentence.charAt(firstIndex))) {
	                String word = sentence.substring(firstIndex, lastIndex);
	                
	                printed = false;
	                
	                BufferedReader dictbr = null;
	                BufferedReader boosterbr = null;
	                BufferedReader negationbr = null;
	                
	                try {
	                	dictbr = new CharsetReader().getBufferedReader(dictfile);
		                boosterbr = new CharsetReader().getBufferedReader(boosterfile);
		                negationbr = new CharsetReader().getBufferedReader(negationfile);
		                
		                String dictline = null;
		                String boosterline = null;
		                String negationline = null;
		                
		                // Iterate through Negation-File
		                while( (negationline = negationbr.readLine()) != null){
		                	String[] negationentries = negationline.split("\\t");
		                	if(match(word,negationentries[0])){
		                		sysprints.add(word+"\n");
		                		wordscorepairs.add(new WordScorePair(word,"negation"));
		                		negateSentiment = true;
		                		negationDetected = true;
		                		negationCounter = 0;
		                	}
		                }// END Iterate through Negation-File
		                if(negationDetected){
		                	negationDetected = false;
		                	continue;
		                }
		                
		                // Negation only for the first 3 words
			            negationCounter++;
			            if(negationCounter > 3){
			            	negateSentiment = false;
			            }
			            
		                // Iterate through Booster-File
			            lineNumber = 0;
		                while( (boosterline = boosterbr.readLine()) != null){
		                	lineNumber++;
		                	String[] boosterentries = boosterline.split("\\t");
		                	if(match(word,boosterentries[0])){
		                		sysprints.add(word);
		                		if(!isFloat(boosterentries[1])){
						    		throw new NumberFormatException(
						    				"Sentiment Score for \"" + word + "\" has to be Integer/Float, line: "+lineNumber);
						    	}
		                		float currentBoosterScore = Float.parseFloat(boosterentries[1]);
		                		sysprints.add("\t["+currentBoosterScore+"]\n");
		                		
		                		boosterScore = currentBoosterScore;
		                		boosterDetected = true;
		                	}
		                } // END Iterate through Booster-File
		                
		                if(boosterDetected){
		                	boosterDetected = false;
		                	boosterWord = word;
		                	continue;
		                }
		                if(!boosterWord.equals("")){
		                	wordscorepairs.add(new WordScorePair(boosterWord,boosterScore));
		                	boosterWord = "";
		                }
		                
		                
		                int dictwordlength = 0;
		                ArrayList<String> scoreprints = new ArrayList<String>();
		                ArrayList<WordScorePair> tempwordscorepairs = new ArrayList<WordScorePair>();
		                // Iterate through Dictionary-File
		                lineNumber = 0;
						while( (dictline = dictbr.readLine()) != null){
							lineNumber++;
							// If it's a comment, skip this line
							if(!dictline.trim().startsWith("#")){
								String[] dictentries = dictline.split("\\t");
							    if(match(word,dictentries[0])){
							    	// always consider the longest match
							    	if(dictentries[0].length() > dictwordlength){
							    		dictwordlength = dictentries[0].length();
							    		scoreprints = new ArrayList<String>();
							    		tempwordscorepairs = new ArrayList<WordScorePair>();
							    		scoreprints.add(word);
								    	printed = true;
								    	if(!isFloat(dictentries[1])){
								    		throw new NumberFormatException(
								    				"Sentiment Score for \"" + word + "\" has to be Integer/Float, line: "+lineNumber);
								    	}
								    	float currentScore = Integer.parseInt(dictentries[1]);
								    	// if positiv
								    	if(currentScore > 0){
								    		tempPosScore = currentScore;
								    		scoreprints.add("\t["+tempPosScore+",-1]\n");
								    		tempwordscorepairs.add(new WordScorePair(word, new Pair(tempPosScore,-1)));
								    		
								    		
								    		// SentiStrength boost version
								    		tempPosScore = tempPosScore + boosterScore;
								    		
								    		// Taboada boost version
//									    	tempPosScore = tempPosScore * (1 + boosterScore);
								    		
								    		
								    		// für a single word is max. score [5;-5], even with booster
								    		if(tempPosScore > 5){
								    			tempPosScore = 5;
								    		}
								    		
								    		// Taboada shift version
								    		if(negateSentiment){
								    			if((tempPosScore-4) < 0){
									    			scoreprints.add("\t["+tempPosScore+",-1] -> "+"[1,"+(tempPosScore-4)+"]\n");
									    			tempNegScore = tempPosScore - 4;
									    			tempPosScore = 1;
									    			lastSentimentPositive = false;
									    			negDetected = true;
								    			}
								    			else if((tempPosScore-4) == 0){
								    				scoreprints.add("\t["+tempPosScore+",-1] -> "+"["+(tempPosScore-4)+",-1]\n");
								    				tempPosScore = 1;
									    			lastSentimentPositive = false;
									    			negDetected = true;
								    			}
								    			else{
									    			scoreprints.add("\t["+tempPosScore+",-1] -> "+"["+(tempPosScore-4)+",-1]\n");
									    			tempPosScore = tempPosScore - 4;
									    			lastSentimentPositive = false;
									    			negDetected = true;
								    			}
								    		}
								    	}
								    	// if negativ
								    	else if(currentScore < 0){
								    		tempNegScore = currentScore;
							    			scoreprints.add("\t[1,"+tempNegScore+"]\n");
							    			tempwordscorepairs.add(new WordScorePair(word, new Pair(1,tempNegScore)));
							    			
							    			
							    			// SentiStrength boost version
							    			tempNegScore = tempNegScore - boosterScore;
							    			
									    	// Taboada boost version
//										    tempNegScore = tempNegScore * (1 + boosterScore);
							    			
							    			
							    			// für a single word is max. score [5;-5], even with booster
								    		if(tempNegScore < -5){
								    			tempNegScore = -5;
								    		}
								    		
								    		// SentiStrength version (neutralize)
								    		if(negateSentiment){
								    			scoreprints.add(" -> [1,-1]\n");
								    			tempNegScore = -1;
								    			lastSentimentPositive = true;
								    			posDetected = true;
								    		}
								    		// Taboada version (shift)
//								    		if(negateSentiment){
//								    			if((tempNegScore+4) < 0){
//									    			scoreprints.add("\t[1,"+tempNegScore+"] -> [1,"+(tempNegScore+4)+"]\n");
//									    			tempNegScore = tempNegScore + 4;
//								    				lastSentimentPositive = false;
//								    				negDetected = true;
//								    			}
//										    	else if((tempNegScore+4) == 0){
//										    		scoreprints.add("\t[1,"+tempNegScore+"] -> [1,"+(tempNegScore+4)+"]\n");
//										    		tempNegScore = -1;
//										    		lastSentimentPositive = true;
//								    				posDetected = true;
//									    		}
//								    			else{
//									    			scoreprints.add("\t[1,"+tempNegScore+"] -> ["+(tempNegScore+4)+",-1]\n");
//									    			tempPosScore = tempNegScore + 4;
//									    			tempNegScore = -1;
//								    				lastSentimentPositive = true;
//								    				posDetected = true;
//								    			}
//								    		}
								    	}
								    	if(tempPosScore > posScore){
							    			posScore = tempPosScore;
							    			lastSentimentPositive = true;
							    			posDetected = true;
							    		}
								    	if(tempNegScore < negScore){
							    			negScore = tempNegScore;
							    			lastSentimentPositive = false;
							    			negDetected = true;
							    		}
							    	}
							    	
							    } // END match(word,dictentry)
							}
						    
						} // END Iterate through Dictionary-File
						boosterScore = 0;
						sysprints.addAll(scoreprints);
						wordscorepairs.addAll(tempwordscorepairs);
						if(!printed){
							sysprints.add(word+"\n");
						}
					
	                } catch (IOException e){
						e.printStackTrace();
					}
	            }
	            
	        } // END Iterate through words
	        
	        // Exclamation (!) handling
	        if(exclamationDetected){
	        	if(lastSentimentPositive && posScore > 1){
	        		posScore++;
	        	}
	        	else if(!lastSentimentPositive && negScore < -1){
	        		negScore--;
	        	}
	        }
	        
	        // Emoticon (Smileys) handling
	        if(emoticonScore > 0){
	        	sysprints.add("EmoticonScore: "+emoticonScore+"\n");
	        	posScore++;
	        	posDetected = true;
	        }
	        else if(emoticonScore < 0){
	        	sysprints.add("EmoticonScore: "+emoticonScore+"\n");
	        	negScore--;
	        	negDetected = true;
	        }
	        
	        Pair sentenceScore = new Pair(posScore,negScore);
	        sentenceScores.add(sentenceScore);
	        sysprints.add("Sentence: "+sentenceScore.toString()+"\n");
	        
	        // sysoprint the words and scores
	        for(String out: sysprints){
				System.out.print(out);
			}
	        
		} // END Iterate through Sentences
		
		float pos = 0;
		float neg = 0;
		for(Pair i: sentenceScores){
			pos = pos + i.getPositive();
			neg = neg + i.getNegative();
		}
		// Consider only the absolute value (Betragwert)
		Pair textScore = new Pair(0,0);
		textScore.setPositive(pos/(float)sentenceScores.size());
		textScore.setNegative(neg/(float)sentenceScores.size());
		float absolutetextPos = Math.abs(textScore.getPositive());
		float absolutetextNeg = Math.abs(textScore.getNegative());
		
		
//		if(posDetected)
//			System.out.println("posDetected: TRUE");
//		else
//			System.out.println("posDetected: FALSE");
//		
//		if(negDetected)
//			System.out.println("negDetected: TRUE");
//		else
//			System.out.println("negDetected: FALSE");
//		System.out.println(absolutetextPos);
//		System.out.println(absolutetextNeg);
		
		
		String result = "MISTAKE";
		// wenn positiv und negativ gleichzeitig vorhanden
		// wenn Unterschied pos und neg <= 0.5 beträgt, soll immer noch neutral sein
		if(posDetected && negDetected){
			float difference = Math.abs(absolutetextPos - absolutetextNeg);
			if(difference <= 0.5){
				System.out.println("NEUTRAL"+"\t"+textScore.toString());
				result = "neu";
			}
			else if(absolutetextPos > absolutetextNeg){
		        System.out.println("POSITIV"+"\t"+textScore.toString());
		        result = "pos";
		    }
	        else{
	        	System.out.println("NEGATIV"+"\t"+textScore.toString());
	        	result = "neg";
	        }
		}
		if(!posDetected && !negDetected){
			System.out.println("NEUTRAL"+"\t"+textScore.toString());
			result = "neu";
		}
		if( (posDetected & !negDetected)  ||  (!posDetected & negDetected) ){
			if(absolutetextPos > absolutetextNeg){
		        System.out.println("POSITIV"+"\t"+textScore.toString());
		        result = "pos";
		    }
	        else if(absolutetextPos == absolutetextNeg){
	        	if(posDetected && !negDetected){
	        		System.out.println("POSITIV"+"\t"+textScore.toString());
	        		result = "pos";
	        	}
	        	else if(!posDetected && negDetected){
	        		System.out.println("NEGATIV"+"\t"+textScore.toString());
	        		result = "neg";
	        	}
	        	else{
	        		System.out.println("NEUTRAL"+"\t"+textScore.toString());
		        	result = "neu";
	        	}
	        }
	        else{
	        	System.out.println("NEGATIV"+"\t"+textScore.toString());
	        	result = "neg";
	        }
		}
		return result;
	}
	
	
	/**
	 * Extracts the sentiment by line aggregation from a given string and returns the sentiment
	 * "pos" , "neg" , "neu"
	 * @param string
	 * @param lang
	 * @return empty string if language is not German or English. Otherwise, returns the sentiment "pos" , "neg" , "neu"
	 */
	public String getSentimentAggregation(String string, String lang){
		
        // Gets an instance of BreakIterator for sentence/word break for the
        // given locale. We can instantiate a BreakIterator without
        // specifying the locale. The locale is important when we
        // are working with languages like Japanese or Chinese where
        // the breaks standard may be different compared to English.
		BreakIterator bisentence;
		BreakIterator biword;
		if(lang.equals("en")){
			bisentence = BreakIterator.getSentenceInstance(Locale.ENGLISH);
			biword = BreakIterator.getWordInstance(Locale.ENGLISH);
			
			dictionaries.loadEN();
			dictfile = Dictionaries.dictfile;
			negationfile = Dictionaries.negationfile;
			boosterfile = Dictionaries.boosterfile;
			emoticonfile = Dictionaries.emoticonfile;
		}
		else if(lang.equals("de")){
			bisentence = BreakIterator.getSentenceInstance(Locale.GERMAN);
			biword = BreakIterator.getWordInstance(Locale.GERMAN);
			
			dictionaries.loadDE();
			dictfile = Dictionaries.dictfile;
			negationfile = Dictionaries.negationfile;
			boosterfile = Dictionaries.boosterfile;
			emoticonfile = Dictionaries.emoticonfile;
		}
		else{
			if(lang.contains("-"))
				lang.replace("-", "_");
			if(lang.equals(""))
				System.out.println("Language could not be detected.");
			else
				System.out.println("Language "+ Language.get(lang) +" is not supported.");
			return "";
		}
		
        // Set the text string to be scanned.
		bisentence.setText(string);
		
		ArrayList<String> sentences = new ArrayList<String>();
		String s;
		int lineNumber = 0;
		
        // Extract all Sentences
		int index = 0;
		while (bisentence.next() != BreakIterator.DONE) {
		s = string.substring(index, bisentence.current());
		System.out.println("\nSentence: " + s);
		sentences.add(s);
		index = bisentence.current();
		}
		
		ArrayList<Pair> sentenceScores = new ArrayList<Pair>();
		boolean printed = false;
		
		boolean posDetected = false;
		boolean negDetected = false;
		// Iterate through Sentences
		for(String sentence: sentences){
			float posScore = 0;
			float negScore = 0;
			
			float boosterScore = 0;
			boolean boosterDetected = false;
			String boosterWord = "";
			
			boolean negateSentiment = false;
			boolean negationDetected = false;
			int negationCounter = 0;
			
			boolean exclamationDetected = false;
			boolean lastSentimentPositive = false;
			
			float emoticonScore = 0;
			
			// Set the text string to be scanned.
			biword.setText(sentence);
			
			// Exclamation (!) Detection
			if(sentence.contains("!")){
				exclamationDetected = true;
			}
			
			// Emoticon (Smileys) Detection
			BufferedReader emoticonbr = null;
            try {
				String[] words = sentence.split("\\s+");
				for(String e: words){
					emoticonbr = new CharsetReader().getBufferedReader(emoticonfile);
					// Iterate through Emoticon-File
					lineNumber = 0;
					String emoticonline = null;
	                while( (emoticonline = emoticonbr.readLine()) != null){
	                	lineNumber++;
	                	String[] emoticonentries = emoticonline.split("\\t");
	                	
                		if(e.equals(emoticonentries[0])){
                			if(!isFloat(emoticonentries[1])){
					    		throw new NumberFormatException(
					    				"Sentiment Score for \"" + e + "\" has to be Integer/Float, line: "+lineNumber);
					    	}
	                		emoticonScore = emoticonScore + Float.parseFloat(emoticonentries[1]);
	                		wordscorepairs.add(new WordScorePair(emoticonentries[0],emoticonentries[1]));
                		}
	                } // END Iterate through Emoticon-File
				}
				
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
	        ArrayList<String> sysprints = new ArrayList<String>();
	        
	        System.out.println("\nIterate each word: ");
	        int lastIndex = biword.first();
	        // Iterate through words
	        while (lastIndex != BreakIterator.DONE) {
	            int firstIndex = lastIndex;
	            lastIndex = biword.next();
	            if (lastIndex != BreakIterator.DONE && Character.isLetterOrDigit(sentence.charAt(firstIndex))) {
	                String word = sentence.substring(firstIndex, lastIndex);
	                
	                printed = false;
	                
	                
	                BufferedReader dictbr = null;
	                BufferedReader boosterbr = null;
	                BufferedReader negationbr = null;
	                
	                try {
	                	dictbr = new CharsetReader().getBufferedReader(dictfile);
		                boosterbr = new CharsetReader().getBufferedReader(boosterfile);
		                negationbr = new CharsetReader().getBufferedReader(negationfile);
		                
		                String dictline = null;
		                String boosterline = null;
		                String negationline = null;
		                
		                // Iterate through Negation-File
		                while( (negationline = negationbr.readLine()) != null){
		                	String[] negationentries = negationline.split("\\t");
		                	if(match(word,negationentries[0])){
		                		sysprints.add(word+"\n");
		                		wordscorepairs.add(new WordScorePair(word,"negation"));
		                		negateSentiment = true;
		                		negationDetected = true;
		                		negationCounter = 0;
		                	}
		                }// END Iterate through Negation-File
		                if(negationDetected){
		                	negationDetected = false;
		                	continue;
		                }
		                
		                // Negation only for the first 3 words
			            negationCounter++;
			            if(negationCounter > 3){
			            	negateSentiment = false;
			            }
			            
		                // Iterate through Booster-File
			            lineNumber = 0;
		                while( (boosterline = boosterbr.readLine()) != null){
		                	lineNumber++;
		                	String[] boosterentries = boosterline.split("\\t");
		                	if(match(word,boosterentries[0])){
		                		sysprints.add(word);
		                		if(!isFloat(boosterentries[1])){
						    		throw new NumberFormatException(
						    				"Sentiment Score for \"" + word + "\" has to be Integer/Float, line: "+lineNumber);
						    	}
		                		float currentBoosterScore = Float.parseFloat(boosterentries[1]);
		                		sysprints.add("\t["+currentBoosterScore+"]\n");
		                		boosterScore = currentBoosterScore;
		                		boosterDetected = true;
		                	}
		                } // END Iterate through Booster-File
		                if(boosterDetected){
		                	boosterDetected = false;
		                	boosterWord = word;
		                	continue;
		                }
		                if(!boosterWord.equals("")){
		                	wordscorepairs.add(new WordScorePair(boosterWord,boosterScore));
		                	boosterWord = "";
		                }
		                
		                int dictwordlength = 0;
		                float tempPosScore = 0;
		                float tempNegScore = 0;
		                
		                ArrayList<String> scoreprints = new ArrayList<String>();
		                ArrayList<WordScorePair> tempwordscorepairs = new ArrayList<WordScorePair>();
		                
		                // Iterate through Dictionary-File
		                lineNumber = 0;
						while( (dictline = dictbr.readLine()) != null){
							lineNumber++;
							// If it's a comment, skip this line
							if(!dictline.trim().startsWith("#")){
								String[] dictentries = dictline.split("\\t");
							    
							    if(match(word,dictentries[0])){

							    	// always consider the longest match
							    	if(dictentries[0].length() > dictwordlength){
								    	dictwordlength = dictentries[0].length();
								    	scoreprints = new ArrayList<String>();
								    	tempwordscorepairs = new ArrayList<WordScorePair>();
								    	tempPosScore = 0;
								    	tempNegScore = 0;
								    	
										scoreprints.add(word);
								    	printed = true;
								    	if(!isFloat(dictentries[1])){
								    		throw new NumberFormatException(
								    				"Sentiment Score for \"" + word + "\" has to be Integer/Float, line: "+lineNumber);
								    	}
								    	float currentScore = Integer.parseInt(dictentries[1]);
								    	// if positiv
								    	if(currentScore > 0){
								    		scoreprints.add("\t["+currentScore+",-1]\n");
								    		tempwordscorepairs.add(new WordScorePair(word, new Pair(currentScore,-1)));
								    		
								    		
								    		// SentiStrength boost version
								    		currentScore = currentScore + boosterScore;
								    		
								    		// Taboada boost version
//									    	currentScore = currentScore * (1 + boosterScore);
								    		
								    		
								    		// für a single word is max. score [5;-5], even with booster
								    		if(currentScore > 5){
								    			currentScore = 5;
								    		}
								    		
								    		// Taboada shift version
								    		if(negateSentiment){
								    			if((currentScore-4) < 0){
//								    				scoreprints.add("\t["+currentScore+",-1] -> "+"[1,"+(currentScore-4)+"]\n");
								    				tempNegScore = tempNegScore + (currentScore - 4);
									    			tempPosScore++;
									    			lastSentimentPositive = false;
									    			negDetected = true;
								    			}
								    			else if((currentScore-4) == 0){
//								    				scoreprints.add("\t["+currentScore+",-1] -> "+"["+(currentScore-4)+",-1]\n");
								    				tempPosScore = currentScore-4;
								    				tempNegScore--;
									    			lastSentimentPositive = false;
									    			negDetected = true;
								    			}
								    			else{
//								    				scoreprints.add("\t["+currentScore+",-1] -> "+"["+(currentScore-4)+",-1]\n");
								    				tempPosScore = tempPosScore + (currentScore - 4);
								    				tempNegScore--;
								    				lastSentimentPositive = false;
									    			negDetected = true;
								    			}
								    			currentScore = 0;
								    		}
								    		else{
								    			tempPosScore = tempPosScore + currentScore;
								    			tempNegScore--;
								    			lastSentimentPositive = true;
								    			posDetected = true;
								    		}
							    			
								    	}
								    	// if negativ
								    	else if(currentScore < 0){
							    			scoreprints.add("\t[1,"+currentScore+"]\n");
							    			tempwordscorepairs.add(new WordScorePair(word, new Pair(1,currentScore)));
							    			
							    			
							    			// SentiStrength boost version
									    	currentScore = currentScore - boosterScore;
									    	
									    	// Taboada boost version
//										    currentScore = currentScore * (1 + boosterScore);
								    		
									    	
									    	// für a single word is max. score [5;-5], even with booster
								    		if(currentScore < -5){
								    			currentScore = -5;
								    		}
									    	
								    		// SentiStrength version (neutralize)
								    		if(negateSentiment){
								    			scoreprints.add(" -> [1,-1]\n");
								    			posScore++;
								    			negScore--;
								    			currentScore = 0;
									    		lastSentimentPositive = true;
									    		posDetected = true;
								    		}
								    		// Taboada version (shift)
//								    		if(negateSentiment){
//								    			if((currentScore+4) < 0){
//									    			scoreprints.add("\t[1,"+currentScore+"] -> [1,"+(currentScore+4)+"]\n");
//									    			tempNegScore = tempNegScore + (currentScore + 4);
//									    			tempPosScore++;
//								    				lastSentimentPositive = false;
//								    				negDetected = true;
//								    			}
//								    			else if((currentScore+4) == 0){
//								    				scoreprints.add("\t[1,"+currentScore+"] -> [1,"+(currentScore+4)+"]\n");
//							    					tempNegScore = currentScore+4;
//							    					tempPosScore++;
//								    				lastSentimentPositive = true;
//								    				posDetected = true;
//							    				}
//								    			else{
//								    				scoreprints.add("\t[1,"+currentScore+"] -> ["+(currentScore+4)+",-1]\n");
//								    				tempPosScore = tempPosScore + (currentScore + 4);
//								    				tempNegScore--;
//								    				lastSentimentPositive = true;
//								    				posDetected = true;
//								    			}
//								    		}
								    		else{
								    			tempNegScore = tempNegScore + currentScore;
								    			tempPosScore++;
									    		lastSentimentPositive = false;
									    		negDetected = true;
								    		}
								    		
								    	}
								    }
							    } // END match(word,dictentry)
							    
							}
							
						} // END Iterate through Dictionary-File
						boosterScore = 0;
						sysprints.addAll(scoreprints);
						wordscorepairs.addAll(tempwordscorepairs);
						if(!printed){
							sysprints.add(word+"\n");
						}
						
						posScore = posScore + tempPosScore;
						negScore = negScore + tempNegScore;
					
	                } catch (IOException e){
						e.printStackTrace();
					}
	            }
	            
	        } // END Iterate through words
	        
	        
	        // Exclamation (!) handling
	        if(exclamationDetected){
	        	if(lastSentimentPositive && posScore > 1){
	        		posScore++;
	        	}
	        	else if(!lastSentimentPositive && negScore < -1){
	        		negScore--;
	        	}
	        }
	        
	        // Emoticon (Smileys) handling
	        if(emoticonScore > 0){
	        	sysprints.add("EmoticonScore: "+emoticonScore+"\n");
	        	posScore++;
	        	posDetected = true;
	        }
	        else if(emoticonScore < 0){
	        	sysprints.add("EmoticonScore: "+emoticonScore+"\n");
	        	negScore--;
	        	negDetected = true;
	        }
	        
	        Pair sentenceScore = new Pair(posScore,negScore);
	        sentenceScores.add(sentenceScore);
	        sysprints.add("Sentence: "+sentenceScore.toString()+"\n");
	        
	        // sysoprint the words and scores
	        for(String out: sysprints){
				System.out.print(out);
			}
	        
		} // END Iterate through Sentences
		
		float pos = 0;
		float neg = 0;
		for(Pair i: sentenceScores){
			pos = pos + i.getPositive();
			neg = neg + i.getNegative();
		}
		// Consider only the absolute value (Betragwert)
		Pair textScore = new Pair(0,0);
		textScore.setPositive(pos/(float)sentenceScores.size());
		textScore.setNegative(neg/(float)sentenceScores.size());
		float absolutetextPos = Math.abs(textScore.getPositive());
		float absolutetextNeg = Math.abs(textScore.getNegative());
		
		
//		if(posDetected)
//			System.out.println("posDetected: TRUE");
//		else
//			System.out.println("posDetected: FALSE");
//		
//		if(negDetected)
//			System.out.println("negDetected: TRUE");
//		else
//			System.out.println("negDetected: FALSE");
//		System.out.println(absolutetextPos);
//		System.out.println(absolutetextNeg);
		
		
		String result = "MISTAKE";
		// wenn positiv und negativ gleichzeitig vorhanden
		// wenn Unterschied pos und neg <= 0.5 beträgt, soll immer noch neutral sein
		if(posDetected && negDetected){
			float difference = Math.abs(absolutetextPos - absolutetextNeg);
			if(difference <= 0.5){
				System.out.println("NEUTRAL"+"\t"+textScore.toString());
				result = "neu";
			}
			else if(absolutetextPos > absolutetextNeg){
		        System.out.println("POSITIV"+"\t"+textScore.toString());
		        result = "pos";
		    }
	        else{
	        	System.out.println("NEGATIV"+"\t"+textScore.toString());
	        	result = "neg";
	        }
		}
		if(!posDetected && !negDetected){
			System.out.println("NEUTRAL"+"\t"+textScore.toString());
			result = "neu";
		}
		if( (posDetected & !negDetected)  ||  (!posDetected & negDetected) ){
			if(absolutetextPos > absolutetextNeg){
		        System.out.println("POSITIV"+"\t"+textScore.toString());
		        result = "pos";
		    }
	        else if(absolutetextPos == absolutetextNeg){
	        	if(posDetected && !negDetected){
	        		System.out.println("POSITIV"+"\t"+textScore.toString());
	        		result = "pos";
	        	}
	        	else if(!posDetected && negDetected){
	        		System.out.println("NEGATIV"+"\t"+textScore.toString());
	        		result = "neg";
	        	}
	        	else{
	        		System.out.println("NEUTRAL"+"\t"+textScore.toString());
		        	result = "neu";
	        	}
	        }
	        else{
	        	System.out.println("NEGATIV"+"\t"+textScore.toString());
	        	result = "neg";
	        }
		}
		return result;
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
	
	
	/**
	 * This method tests if a string is an float (or integer)
	 * @param input
	 * @return
	 */
	public boolean isFloat( String input ) {
	    try {
	        Float.parseFloat( input );
	        return true;
	    }
	    catch(NumberFormatException e ) {
	        return false;
	    }
	}
	
	
	public static void main(String[] args) throws IOException {
		SentimentHandlerImpl sentiHandler = new SentimentHandlerImpl("WebContent");
//		
		ArrayList<String> testSentences = new ArrayList<String>();
//		// no sentiment
//		testSentences.add("I go to school today.");
//		// positive sentiment
//		testSentences.add("good day");
//		// positive & negative sentiment
//		testSentences.add("Good day, bad weather");
//		testSentences.add("Its bad weather. Good day");
//		testSentences.add("good day, horrible weather");
//		testSentences.add("I love and hate dogs.");
//		testSentences.add("She is nice but also horrible.");
//		testSentences.add("Amazing and good day. Its devastating weather");
//		testSentences.add("Excellent day, horrible and bad weather");
//		testSentences.add("Excellent apt, bad host and bad choice");
//		// Booster
//		testSentences.add("very good day");
//		testSentences.add("very bad day");
//		testSentences.add("very slightly bad day");
//		testSentences.add("sometimes good day");
//		testSentences.add("sometimes bad day");
//		testSentences.add("fucking very absolutely incredibly good day");
//		// Negation
//		testSentences.add("not good day");
//		testSentences.add("not bliss day");
//		testSentences.add("not devastating day");
//		testSentences.add("not bad day");
//		testSentences.add("Today is not a good day");
//		testSentences.add("Unfortunately I was not able to meet the wonderful and lovely host.");
//		// Negation + Booster
//		testSentences.add("Everything is not very good.");
//		testSentences.add("Everything is not very amazing.");
//		testSentences.add("not a really good day");
//		testSentences.add("He was not a really helpful person and also not friendly.");
//		// Ausrufezeichen
//		testSentences.add("Just a normal day!");
//		testSentences.add("Its a good day!");
//		testSentences.add("Host was unfriendly!");
//		testSentences.add("Good location but unfriendly host!");
//		testSentences.add("Good location but very unfriendly host!");
//		// Smileys
//		testSentences.add("hello :D");
//		testSentences.add("It was a nice experience (^.^)");
//		testSentences.add("Its a good day :(");
//		testSentences.add("Host was unfriendly :(");
//		// Miscellaneous
//		testSentences.add("nicht sehr spektakulär");
//		testSentences.add("nicht sehr top");
//		testSentences.add("leider war die nicht sehr sauber");
//		testSentences.add("gut und schlecht");

		for(String sentence: testSentences){
			System.out.println(sentiHandler.getSentimentMaximization(sentence, "en"));
			System.out.println(sentiHandler.getSentimentAggregation(sentence, "en"));
//			
//			System.out.println(sentiHandler.getSentimentMaximization(sentence, "de"));
//			System.out.println(sentiHandler.getSentimentAggregation(sentence, "de"));
		}

		
//		boolean x = match("enttäuscht","enttäusch*");
//		if(x){
//			System.out.println("TRUE");
//		}
//		else{System.out.println("FALSE");}
		
	}

}

