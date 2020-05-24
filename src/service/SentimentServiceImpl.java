package service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Locale;

import model.CharsetDetector;
import model.Dictionaries;
import model.Pair;
import model.Sentiment;
import model.SentimentScore;
import model.WordScorePair;

public class SentimentServiceImpl implements SentimentService {

	private Dictionaries dictionaries;
	private ArrayList<WordScorePair> wordscorepairs;
	private Pair finalScore;

	public SentimentServiceImpl(String filepath) {
		dictionaries = new Dictionaries(filepath);
		wordscorepairs = new ArrayList<WordScorePair>();
	}

	@Override
	public SentimentScore getSentimentScore(String string, String lang) throws IOException {

		this.wordscorepairs = new ArrayList<WordScorePair>();

		// Aggregation is chosen based on better results in evaluation
		Sentiment sentiment = getSentimentAggregation(string, lang);

		SentimentScore swsp = new SentimentScore(sentiment, this.finalScore, this.wordscorepairs);
		return swsp;
	}

	/**
	 * Extracts the sentiment by line aggregation from a given string and returns
	 * the sentiment POS, NEG, NEU
	 * 
	 * @param string
	 * @param lang
	 * @return null if language is not German or English. Otherwise, returns the
	 *         sentiment POS, NEG, NEU
	 * @throws IOException
	 */
	public Sentiment getSentimentAggregation(String string, String lang) throws IOException {

		// Gets an instance of BreakIterator for sentence/word break for the
		// given locale. We can instantiate a BreakIterator without
		// specifying the locale. The locale is important when we
		// are working with languages like Japanese or Chinese where
		// the breaks standard may be different compared to English.
		BreakIterator bisentence;
		BreakIterator biword;
		if (lang.equals("en")) {
			bisentence = BreakIterator.getSentenceInstance(Locale.ENGLISH);
			biword = BreakIterator.getWordInstance(Locale.ENGLISH);
			dictionaries.loadEN();

		} else if (lang.equals("de")) {
			bisentence = BreakIterator.getSentenceInstance(Locale.GERMAN);
			biword = BreakIterator.getWordInstance(Locale.GERMAN);
			dictionaries.loadDE();

		} else {
			lang = lang.replace("-", "_");
			if (lang.equals(""))
				System.out.println("Language could not be detected.");
			else
				System.out.println("Language '" + lang + "' is not supported.");
			return null;
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

		String emoticonFileEncoding = CharsetDetector.getEncoding(dictionaries.getEmoticonfile());
		String dictFileEncoding = CharsetDetector.getEncoding(dictionaries.getDictfile());
		String boosterFileEncoding = CharsetDetector.getEncoding(dictionaries.getBoosterfile());
		String negationFileEncoding = CharsetDetector.getEncoding(dictionaries.getNegationfile());

		// Iterate through Sentences
		for (String sentence : sentences) {
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
			if (sentence.contains("!")) {
				exclamationDetected = true;
			}

			// Emoticon (Smileys) Detection
			String[] words = sentence.split("\\s+");
			for (String e : words) {
				// Iterate through Emoticon-File
				lineNumber = 0;
				try (BufferedReader emoticonbr = new BufferedReader(new InputStreamReader(
						new FileInputStream(dictionaries.getEmoticonfile()), emoticonFileEncoding))) {
					String emoticonline = null;
					while ((emoticonline = emoticonbr.readLine()) != null) {
						lineNumber++;
						String[] emoticonentries = emoticonline.split("\\t");

						if (e.equals(emoticonentries[0])) {
							if (!isFloat(emoticonentries[1])) {
								throw new IOException("Sentiment Score for \"" + e
										+ "\" has to be Integer/Float, line: " + lineNumber);
							}
							emoticonScore = emoticonScore + Float.parseFloat(emoticonentries[1]);
							wordscorepairs.add(new WordScorePair(emoticonentries[0], emoticonentries[1]));
						}
					}
				}

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

					// Iterate through Negation-File
					try (BufferedReader negationbr = new BufferedReader(new InputStreamReader(
							new FileInputStream(dictionaries.getNegationfile()), negationFileEncoding))) {
						String negationline = null;
						while ((negationline = negationbr.readLine()) != null) {
							String[] negationentries = negationline.split("\\t");
							if (match(word, negationentries[0])) {
								sysprints.add(word + "\n");
								wordscorepairs.add(new WordScorePair(word, "negation"));
								negateSentiment = true;
								negationDetected = true;
								negationCounter = 0;
							}
						}
					}
					if (negationDetected) {
						negationDetected = false;
						continue;
					}

					// Negation only for the first 3 words
					negationCounter++;
					if (negationCounter > 3) {
						negateSentiment = false;
					}

					// Iterate through Booster-File
					lineNumber = 0;
					try (BufferedReader boosterbr = new BufferedReader(new InputStreamReader(
							new FileInputStream(dictionaries.getBoosterfile()), boosterFileEncoding))) {
						String boosterline = null;
						while ((boosterline = boosterbr.readLine()) != null) {
							lineNumber++;
							String[] boosterentries = boosterline.split("\\t");
							if (match(word, boosterentries[0])) {
								sysprints.add(word);
								if (!isFloat(boosterentries[1])) {
									throw new NumberFormatException("Sentiment Score for \"" + word
											+ "\" has to be Integer/Float, line: " + lineNumber);
								}
								float currentBoosterScore = Float.parseFloat(boosterentries[1]);
								sysprints.add("\t[" + currentBoosterScore + "]\n");
								boosterScore = currentBoosterScore;
								boosterDetected = true;
							}
						}
					}

					if (boosterDetected) {
						boosterDetected = false;
						boosterWord = word;
						continue;
					}
					if (!boosterWord.equals("")) {
						wordscorepairs.add(new WordScorePair(boosterWord, boosterScore));
						boosterWord = "";
					}

					int dictwordlength = 0;
					float tempPosScore = 0;
					float tempNegScore = 0;

					ArrayList<String> scoreprints = new ArrayList<String>();
					ArrayList<WordScorePair> tempwordscorepairs = new ArrayList<WordScorePair>();

					// Iterate through Dictionary-File
					lineNumber = 0;
					try (BufferedReader dictbr = new BufferedReader(
							new InputStreamReader(new FileInputStream(dictionaries.getDictfile()), dictFileEncoding))) {
						String dictline = null;
						while ((dictline = dictbr.readLine()) != null) {
							lineNumber++;
							// If it's a comment, skip this line
							if (!dictline.trim().startsWith("#")) {
								String[] dictentries = dictline.split("\\t");

								if (match(word, dictentries[0])) {

									// always consider the longest match
									if (dictentries[0].length() > dictwordlength) {
										dictwordlength = dictentries[0].length();
										scoreprints = new ArrayList<String>();
										tempwordscorepairs = new ArrayList<WordScorePair>();
										tempPosScore = 0;
										tempNegScore = 0;

										scoreprints.add(word);
										printed = true;
										if (!isFloat(dictentries[1])) {
											throw new NumberFormatException("Sentiment Score for \"" + word
													+ "\" has to be Integer/Float, line: " + lineNumber);
										}
										float currentScore = Integer.parseInt(dictentries[1]);
										// if positive
										if (currentScore > 0) {
											scoreprints.add("\t[" + currentScore + ",-1]\n");
											tempwordscorepairs.add(new WordScorePair(word, new Pair(currentScore, -1)));

											// SentiStrength boost version
											currentScore = currentScore + boosterScore;

											// Taboada boost version
//										    	currentScore = currentScore * (1 + boosterScore);

											// for single word max. score [5;-5], even with booster
											if (currentScore > 5) {
												currentScore = 5;
											}

											// Taboada shift version
											if (negateSentiment) {
												if ((currentScore - 4) < 0) {
//									    			scoreprints.add("\t["+currentScore+",-1] -> "+"[1,"+(currentScore-4)+"]\n");
													tempNegScore = tempNegScore + (currentScore - 4);
													tempPosScore++;
													lastSentimentPositive = false;
													negDetected = true;
												} else if ((currentScore - 4) == 0) {
//									    			scoreprints.add("\t["+currentScore+",-1] -> "+"["+(currentScore-4)+",-1]\n");
													tempPosScore = currentScore - 4;
													tempNegScore--;
													lastSentimentPositive = false;
													negDetected = true;
												} else {
//													scoreprints.add("\t[" + currentScore + ",-1] -> " + "["+ (currentScore - 4) + ",-1]\n");
													tempPosScore = tempPosScore + (currentScore - 4);
													tempNegScore--;
													lastSentimentPositive = false;
													negDetected = true;
												}
												currentScore = 0;
											} else {
												tempPosScore = tempPosScore + currentScore;
												tempNegScore--;
												lastSentimentPositive = true;
												posDetected = true;
											}

										}
										// if negative
										else if (currentScore < 0) {
											scoreprints.add("\t[1," + currentScore + "]\n");
											tempwordscorepairs.add(new WordScorePair(word, new Pair(1, currentScore)));

											// SentiStrength boost version
											currentScore = currentScore + boosterScore;

											// Taboada boost version
//											    currentScore = currentScore * (1 + boosterScore);

											// for single word max. score [5;-5], even with booster
											if (currentScore < -5) {
												currentScore = -5;
											}

											// SentiStrength version (neutralize)
											if (negateSentiment) {
												scoreprints.add(" -> [1,-1]\n");
												posScore++;
												negScore--;
												currentScore = 0;
												lastSentimentPositive = true;
												posDetected = true;
											}
											// Taboada version (shift)
//									    		if(negateSentiment){
//									    			if((currentScore+4) < 0){
//										    			scoreprints.add("\t[1,"+currentScore+"] -> [1,"+(currentScore+4)+"]\n");
//										    			tempNegScore = tempNegScore + (currentScore + 4);
//										    			tempPosScore++;
//									    				lastSentimentPositive = false;
//									    				negDetected = true;
//									    			}
//									    			else if((currentScore+4) == 0){
//									    				scoreprints.add("\t[1,"+currentScore+"] -> [1,"+(currentScore+4)+"]\n");
//								    					tempNegScore = currentScore+4;
//								    					tempPosScore++;
//									    				lastSentimentPositive = true;
//									    				posDetected = true;
//								    				}
//									    			else{
//									    				scoreprints.add("\t[1,"+currentScore+"] -> ["+(currentScore+4)+",-1]\n");
//									    				tempPosScore = tempPosScore + (currentScore + 4);
//									    				tempNegScore--;
//									    				lastSentimentPositive = true;
//									    				posDetected = true;
//									    			}
//									    		}
											else {
												tempNegScore = tempNegScore + currentScore;
												tempPosScore++;
												lastSentimentPositive = false;
												negDetected = true;
											}

										}
									}
								}
							}
						} // END Iterate through Dictionary-File
					}

					boosterScore = 0;
					sysprints.addAll(scoreprints);
					wordscorepairs.addAll(tempwordscorepairs);
					if (!printed) {
						sysprints.add(word + "\n");
					}

					posScore = posScore + tempPosScore;
					negScore = negScore + tempNegScore;
				}

			} // END Iterate through words

			// Exclamation (!) handling
			if (exclamationDetected) {
				if (lastSentimentPositive && posScore > 1) {
					posScore++;
				} else if (!lastSentimentPositive && negScore < -1) {
					negScore--;
				}
			}

			// Emoticon (Smileys) handling
			if (emoticonScore > 0) {
				sysprints.add("EmoticonScore: " + emoticonScore + "\n");
				posScore++;
				posDetected = true;
			} else if (emoticonScore < 0) {
				sysprints.add("EmoticonScore: " + emoticonScore + "\n");
				negScore--;
				negDetected = true;
			}

			Pair sentenceScore = new Pair(posScore, negScore);
			sentenceScores.add(sentenceScore);
			sysprints.add("Sentence: " + sentenceScore.toString() + "\n");

			// print the words and scores
			for (String out : sysprints) {
				System.out.print(out);
			}

		} // END Iterate through Sentences

		float pos = 0;
		float neg = 0;
		for (Pair i : sentenceScores) {
			pos = pos + i.getPositive();
			neg = neg + i.getNegative();
		}
		// Consider only the absolute value
		Pair textScore = new Pair(0, 0);
		textScore.setPositive(pos / (float) sentenceScores.size());
		textScore.setNegative(neg / (float) sentenceScores.size());
		finalScore = textScore;
		float absolutetextPos = Math.abs(textScore.getPositive());
		float absolutetextNeg = Math.abs(textScore.getNegative());

		Sentiment result = null;
		// if POS and NEG is detected together and its difference is <= 0.5, then
		// consider it as neutral (NEU)
		if (posDetected && negDetected) {
			float difference = Math.abs(absolutetextPos - absolutetextNeg);
			if (difference <= 0.5) {
				System.out.println("NEUTRAL" + "\t" + textScore.toString());
				result = Sentiment.NEU;
			} else if (absolutetextPos > absolutetextNeg) {
				System.out.println("POSITIVE" + "\t" + textScore.toString());
				result = Sentiment.POS;
			} else {
				System.out.println("NEGATIVE" + "\t" + textScore.toString());
				result = Sentiment.NEG;
			}
		}
		if (!posDetected && !negDetected) {
			System.out.println("NEUTRAL" + "\t" + textScore.toString());
			result = Sentiment.NEU;
		}
		if ((posDetected & !negDetected) || (!posDetected & negDetected)) {
			if (absolutetextPos > absolutetextNeg) {
				System.out.println("POSITIVE" + "\t" + textScore.toString());
				result = Sentiment.POS;
			} else if (absolutetextPos == absolutetextNeg) {
				if (posDetected && !negDetected) {
					System.out.println("POSITIVE" + "\t" + textScore.toString());
					result = Sentiment.POS;
				} else if (!posDetected && negDetected) {
					System.out.println("NEGATIVE" + "\t" + textScore.toString());
					result = Sentiment.NEG;
				} else {
					System.out.println("NEUTRAL" + "\t" + textScore.toString());
					result = Sentiment.NEU;
				}
			} else {
				System.out.println("NEGATIVE" + "\t" + textScore.toString());
				result = Sentiment.NEG;
			}
		}
		return result;
	}

	/**
	 * This method tests if a string matches a wild card expression (supporting ?
	 * for exactly one character or * for an arbitrary number of characters)
	 * 
	 * @param text
	 * @param pattern
	 * @return
	 */
	public static boolean match(String text, String pattern) {
		String text2 = text.toLowerCase();
		String pattern2 = pattern.toLowerCase();
		return text2.matches(pattern2.replace("?", ".?").replace("*", ".*?"));
	}

	/**
	 * This method tests if a string is an float (or integer)
	 * 
	 * @param input
	 * @return
	 */
	public boolean isFloat(String input) {
		try {
			Float.parseFloat(input);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

}
