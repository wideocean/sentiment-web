package backend.language;

import java.util.HashMap;
import java.util.Map;

/**
 * Representaion of all languages which are supported by the language-detection library
 * and where a corresponding language profile exists in the "language-detection/profiles.sm" directory
 * @author Pazifik
 *
 */
public class Language {
	public static final String ar = "ARABIC";
	public static final String bg = "BULGARIAN";
	public static final String bn = "BENGALI";
	public static final String cs = "CZECH";
	public static final String da = "DANISH";
	public static final String de = "GERMAN";
	public static final String el = "GREEK";
	public static final String en = "ENGLISH";
	public static final String es = "Spanish";
	public static final String et = "Estonian";
	public static final String fa = "PERSIAN";
	public static final String fi = "FINNISH";
	public static final String fr = "FRENCH";
	public static final String gu = "GUJARATI";
	public static final String he = "HEBREW";
	public static final String hi = "HINDI";
	public static final String hr = "CROATIAN";
	public static final String hu = "HUNGARIAN";
	public static final String id = "INDONESIAN";
	public static final String it = "ITALIAN";
	public static final String ja = "JAPANESE";
	public static final String ko = "KOREAN";
	public static final String lt = "LITHUANIAN";
	public static final String lv = "LATVIAN";
	public static final String mk = "MACEDONIAN";
	public static final String ml = "MALAYALAM";
	public static final String nl = "DUTCH";
	public static final String no = "NORWEGIAN";
	public static final String pa = "PUNJABI";
	public static final String pl = "POLISH";
	public static final String pt = "PORTUGUESE";
	public static final String ro = "ROMANIAN";
	public static final String ru = "RUSSIAN";
	public static final String sk = "SLOVAK";
	public static final String sl = "SLOVENE";
	public static final String so = "SOMALI";
	public static final String sq = "ALBANIAN";
	public static final String sv = "SWEDISH";
	public static final String ta = "TAMIL";
	public static final String te = "TELUGU";
	public static final String th = "THAI";
	public static final String tl = "TAGALOG";
	public static final String tr = "TURKISH";
	public static final String uk = "UKRAINIAN";
	public static final String ur = "URDU";
	public static final String vi = "VIETNAMESE";
	public static final String zh_cn = "SIMPLIFIED_CHINESE";
	public static final String zh_tw = "TRADITIONAL_CHINESE";
	
	private static Map<String,String> languages;
	static
    {
		languages = new HashMap<String,String>();
		languages.put("ar","ARABIC");
		languages.put("bg","BULGARIAN");
		languages.put("bn","BENGALI");
		languages.put("cs","CZECH");
		languages.put("da","DANISH");
		languages.put("de","GERMAN");
		languages.put("el","GREEK");
		languages.put("en","ENGLISH");
		languages.put("es","Spanish");
		languages.put("et","Estonian");
		languages.put("fa","PERSIAN");
		languages.put("fi","FINNISH");
		languages.put("fr","FRENCH");
		languages.put("gu","GUJARATI");
		languages.put("he","HEBREW");
		languages.put("hi","HINDI");
		languages.put("hr","CROATIAN");
		languages.put("hu","HUNGARIAN");
		languages.put("id","INDONESIAN");
		languages.put("it","ITALIAN");
		languages.put("ja","JAPANESE");
		languages.put("ko","KOREAN");
		languages.put("lt","LITHUANIAN");
		languages.put("lv","LATVIAN");
		languages.put("mk","MACEDONIAN");
		languages.put("ml","MALAYALAM");
		languages.put("nl","DUTCH");
		languages.put("no","NORWEGIAN");
		languages.put("pa","PUNJABI");
		languages.put("pl","POLISH");
		languages.put("pt","PORTUGUESE");
		languages.put("ro","ROMANIAN");
		languages.put("ru","RUSSIAN");
		languages.put("sk","SLOVAK");
		languages.put("sl","SLOVENE");
		languages.put("so","SOMALI");
		languages.put("sq","ALBANIAN");
		languages.put("sv","SWEDISH");
		languages.put("ta","TAMIL");
		languages.put("te","TELUGU");
		languages.put("th","THAI");
		languages.put("tl","TAGALOG");
		languages.put("tr","TURKISH");
		languages.put("uk","UKRAINIAN");
		languages.put("ur","URDU");
		languages.put("vi","VIETNAMESE");
		languages.put("zh_cn","SIMPLIFIED_CHINESE");
		languages.put("zh_tw","TRADITIONAL_CHINESE");
    }
	
	/**
	 * Returns the language from a language code
	 * @param lang
	 * @return
	 */
	public static String get(String lang){
		String language = "";
		for(Map.Entry<String, String> entry : languages.entrySet()){
			if(lang.equals(entry.getKey())){
				language = entry.getValue();
				break;
			}
		}
		return language;
	}
}
