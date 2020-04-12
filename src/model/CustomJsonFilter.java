package model;

/**
 * Ignore JSON fields for type float if value == 0
 * 
 * @author Pazifik
 *
 */
public class CustomJsonFilter {

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return true;
		}
		float value = (float) other;
		return value == 0;
	}

}
