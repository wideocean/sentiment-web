package model;

/**
 * Representation of all languages which are supported by the language-detection
 * library and where a corresponding language profile exists in the
 * "language-detection/profiles.sm" directory
 * 
 * @author Pazifik
 *
 */
public enum Language {
	AR("ARABIC"),
	BG("BULGARIAN"),
	BN("BENGALI"),
	CS("CZECH"),
	DA("DANISH"),
	DE("GERMAN"),
	EL("GREEK"),
	EN("ENGLISH"),
	ES("SPANISH"),
	ET("ESTONIAN"),
	FA("PERSIAN"),
	FI("FINNISH"),
	FR("FRENCH"),
	GU("GUJARATI"),
	HE("HEBREW"),
	HI("HINDI"),
	HR("CROATIAN"),
	HU("HUNGARIAN"),
	ID("INDONESIAN"),
	IT("ITALIAN"),
	JA("JAPANESE"),
	KO("KOREAN"),
	LT("LITHUANIAN"),
	LV("LATVIAN"),
	MK("MACEDONIAN"),
	ML("MALAYALAM"),
	NL("DUTCH"),
	NO("NORWEGIAN"),
	PA("PUNJABI"),
	PL("POLISH"),
	PT("PORTUGUESE"),
	RO("ROMANIAN"),
	RU("RUSSIAN"),
	SK("SLOVAK"),
	SL("SLOVENE"),
	SO("SOMALI"),
	SQ("ALBANIAN"),
	SV("SWEDISH"),
	TA("TAMIL"),
	TE("TELUGU"),
	TH("THAI"),
	TL("TAGALOG"),
	TR("TURKISH"),
	UK("UKRAINIAN"),
	UR("URDU"),
	VI("VIETNAMESE"),
	ZH_CN("SIMPLIFIED_CHINESE"),
	ZH_TW("TRADITIONAL_CHINESE");

	private final String language;

	private Language(String language) {
		this.language = language;
	}

	public String getLanguage() {
		return this.language;
	}

}
