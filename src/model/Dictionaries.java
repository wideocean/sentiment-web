package model;

import java.io.File;
import java.text.MessageFormat;

/**
 * This class is responsible for the selection of the dictionaries
 * 
 * @author Pazifik
 *
 */
public class Dictionaries {

	private String filepath;
	private File dictfile;
	private File negationfile;
	private File boosterfile;
	private File emoticonfile;

	public Dictionaries(String filepath) {
		this.filepath = filepath;
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

	private enum LangCode {
		DE, EN
	}

	private enum Booster {
		SENTISTRENGTH, TABOADA
	}

	/**
	 * Specifies the file paths for the ENGLISH dictionaries
	 */
	public void loadEN() {

		// SentiStrength
//		loadSentiStrength(LangCode.EN);

		// Taboada
//		loadTaboada(LangCode.EN);

		// Custom
		loadCustom(LangCode.EN, Booster.SENTISTRENGTH);
	}

	/**
	 * Specifies the file paths for the GERMAN dictionaries
	 */
	public void loadDE() {
		// SentiStrength
//		loadSentiStrength(LangCode.DE);

		// Taboada
//		loadTaboada(LangCode.DE);

		// Custom
		loadCustom(LangCode.DE, Booster.SENTISTRENGTH);
	}

	private void loadSentiStrength(LangCode code) {
		dictfile = new File(filepath //
				+ MessageFormat.format("/WEB-INF/dictionaries/{0}/SentiStrength/EmotionLookupTable.txt", code));
		negationfile = new File(filepath //
				+ MessageFormat.format("/WEB-INF/dictionaries/{0}/SentiStrength/NegatingWordList.txt", code));
		boosterfile = new File(filepath //
				+ MessageFormat.format("/WEB-INF/dictionaries/{0}/SentiStrength/BoosterWordList.txt", code));
		emoticonfile = new File(filepath //
				+ MessageFormat.format("/WEB-INF/dictionaries/{0}/SentiStrength/EmoticonLookupTable.txt", code));
	}

	private void loadTaboada(LangCode code) {
		if (code == LangCode.EN) {
			dictfile = new File(filepath //
					+ MessageFormat.format("/WEB-INF/dictionaries/{0}/Taboada/EmotionLookupTable.txt", code));
			negationfile = new File(filepath //
					+ MessageFormat.format("/WEB-INF/dictionaries/{0}/SentiStrength/NegatingWordList.txt", code));
			boosterfile = new File(filepath //
					+ MessageFormat.format("/WEB-INF/dictionaries/{0}/Taboada/BoosterWordList.txt", code));
			emoticonfile = new File(filepath //
					+ MessageFormat.format("/WEB-INF/dictionaries/{0}/SentiStrength/EmoticonLookupTable.txt", code));
		}
	}

	/**
	 * 
	 * @param code
	 * @param booster SentiStrength Booster (+1/-1) OR Taboada Booster (%)
	 */
	private void loadCustom(LangCode code, Booster booster) {
		dictfile = new File(filepath //
				+ MessageFormat.format("/WEB-INF/dictionaries/{0}/Custom/EmotionLookupTable.txt", code));
		negationfile = new File(filepath //
				+ MessageFormat.format("/WEB-INF/dictionaries/{0}/Custom/NegatingWordList.txt", code));
		boosterfile = new File(filepath //
				+ MessageFormat.format("/WEB-INF/dictionaries/{0}/{1}/BoosterWordList.txt", code, booster));
		emoticonfile = new File(filepath //
				+ MessageFormat.format("/WEB-INF/dictionaries/{0}/Custom/EmoticonLookupTable.txt", code));
	}

}
