package backend.data;
import java.io.File;


public class Dictionaries {

	public static File dictfile;
	public static File negationfile;
	public static File boosterfile;
	public static File emoticonfile;
	
	public Dictionaries(){
		
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
	
	public void loadEN(){
		
		// SentiStrength
//		dictfile = new File("dictionaries/en/SentiStrength/EmotionLookupTable.txt");
//		negationfile = new File("dictionaries/en/SentiStrength/NegatingWordList.txt");
//		boosterfile = new File("dictionaries/en/SentiStrength/BoosterWordList.txt");
//		emoticonfile = new File("dictionaries/en/SentiStrength/EmoticonLookupTable.txt");
		
		// Taboada
//		dictfile = new File("dictionaries/en/Taboada/EmotionLookupTable_Tabo.txt");
//		boosterfile = new File("dictionaries/en/Taboada/BoosterWordList_Tabo.txt");
//		negationfile = new File("dictionaries/en/Taboada/NegatingWordList_SentiStrength.txt");
//		emoticonfile = new File("dictionaries/en/Taboada/EmoticonLookupTable_SentiStrength.txt");
		
		// Custom
		dictfile = new File("dictionaries/en/Custom/EmotionLookupTable.txt");
		negationfile = new File("dictionaries/en/Custom/NegatingWordList.txt");
			// SentiStrength Booster (+1/-1)
		boosterfile = new File("dictionaries/en/Custom/BoosterWordList.txt");
			// Taboada Booster (%)
//		boosterfile = new File("dictionaries/en/Custom/BoosterWordList_Tabo.txt");
		emoticonfile = new File("dictionaries/en/Custom/EmoticonLookupTable.txt");
		
	}
	
	public void loadDE(){
		// SentiStrength
//		dictfile = new File("dictionaries/de/SentiStrength/EmotionLookupTable.txt");
//		negationfile = new File("dictionaries/de/SentiStrength/NegatingWordList.txt");
//		boosterfile = new File("dictionaries/de/SentiStrength/BoosterWordList.txt");
//		emoticonfile = new File("dictionaries/de/SentiStrength/EmoticonLookupTable.txt");
		
		// Taboada
//		boosterfile = new File("dictionaries/de/Taboada/BoosterWordList_Tabo.txt");
		
		// Custom
		dictfile = new File("dictionaries/de/Custom/EmotionLookupTable.txt");
		negationfile = new File("dictionaries/de/Custom/NegatingWordList.txt");
			// SentiStrength Booster (+1/-1)
		boosterfile = new File("dictionaries/de/Custom/BoosterWordList.txt");
			// Taboada Booster (%)
//		boosterfile = new File("dictionaries/de/Custom/BoosterWordList_Tabo.txt");
		emoticonfile = new File("dictionaries/de/Custom/EmoticonLookupTable.txt");
	}
	
}
