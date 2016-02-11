package backend.data;
import java.io.File;

/**
 * This class is responsible for the selection of the dictionaries
 * @author Pazifik
 *
 */
public class Dictionaries {

	public static String filepath;
	public static File dictfile;
	public static File negationfile;
	public static File boosterfile;
	public static File emoticonfile;
	
	public Dictionaries(String filepath){
		Dictionaries.filepath = filepath;
	}
	
	public File getDictfile() {
		return dictfile;
	}

	public File getNegationfile() {
		return negationfile;
	}

	public File getBoosterfile() {
		return boosterfile;
	}

	public File getEmoticonfile() {
		return emoticonfile;
	}
	
	/**
	 * Specifies the filepaths for the ENGLISH dictionaries
	 */
	public void loadEN(){
		
		// SentiStrength
//		dictfile = new File(filepath + "/WEB-INF/dictionaries/en/SentiStrength/EmotionLookupTable.txt");
//		negationfile = new File(filepath + "/WEB-INF/dictionaries/en/SentiStrength/NegatingWordList.txt");
//		boosterfile = new File(filepath + "/WEB-INF/dictionaries/en/SentiStrength/BoosterWordList.txt");
//		emoticonfile = new File(filepath + "/WEB-INF/dictionaries/en/SentiStrength/EmoticonLookupTable.txt");
		
		// Taboada
//		dictfile = new File(filepath + "/WEB-INF/dictionaries/en/Taboada/EmotionLookupTable_Tabo.txt");
//		boosterfile = new File(filepath + "/WEB-INF/dictionaries/en/Taboada/BoosterWordList_Tabo.txt");
//		negationfile = new File(filepath + "/WEB-INF/dictionaries/en/Taboada/NegatingWordList_SentiStrength.txt");
//		emoticonfile = new File(filepath + "/WEB-INF/dictionaries/en/Taboada/EmoticonLookupTable_SentiStrength.txt");
		
		// Custom
		dictfile = new File(filepath + "/WEB-INF/dictionaries/en/Custom/EmotionLookupTable.txt");
		negationfile = new File(filepath + "/WEB-INF/dictionaries/en/Custom/NegatingWordList.txt");
			// SentiStrength Booster (+1/-1)
		boosterfile = new File(filepath + "/WEB-INF/dictionaries/en/Custom/BoosterWordList.txt");
			// Taboada Booster (%)
//		boosterfile = new File(filepath + "/WEB-INF/dictionaries/en/Custom/BoosterWordList_Tabo.txt");
		emoticonfile = new File(filepath + "/WEB-INF/dictionaries/en/Custom/EmoticonLookupTable.txt");
		
	}
	
	/**
	 * Specifies the filepaths for the GERMAN dictionaries
	 */
	public void loadDE(){
		// SentiStrength
//		dictfile = new File(filepath + "/WEB-INF/dictionaries/de/SentiStrength/EmotionLookupTable.txt");
//		negationfile = new File(filepath + "/WEB-INF/dictionaries/de/SentiStrength/NegatingWordList.txt");
//		boosterfile = new File(filepath + "/WEB-INF/dictionaries/de/SentiStrength/BoosterWordList.txt");
//		emoticonfile = new File(filepath + "/WEB-INF/dictionaries/de/SentiStrength/EmoticonLookupTable.txt");
		
		// Taboada
//		boosterfile = new File(filepath + "/WEB-INF/dictionaries/de/Taboada/BoosterWordList_Tabo.txt");
		
		// Custom
		dictfile = new File(filepath + "/WEB-INF/dictionaries/de/Custom/EmotionLookupTable.txt");
		negationfile = new File(filepath + "/WEB-INF/dictionaries/de/Custom/NegatingWordList.txt");
			// SentiStrength Booster (+1/-1)
		boosterfile = new File(filepath + "/WEB-INF/dictionaries/de/Custom/BoosterWordList.txt");
			// Taboada Booster (%)
//		boosterfile = new File(filepath + "/WEB-INF/dictionaries/de/Custom/BoosterWordList_Tabo.txt");
		emoticonfile = new File(filepath + "/WEB-INF/dictionaries/de/Custom/EmoticonLookupTable.txt");
	}
	
}
