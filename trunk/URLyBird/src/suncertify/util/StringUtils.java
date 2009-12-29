package suncertify.util;

public class StringUtils {
	public static void padWithSpaces(StringBuilder value, int length) {
		int initialLength = value.length();
		value.setLength(length);
		if (initialLength < length) {
			for (int i = initialLength; i < length; i++) {
				value.setCharAt(i, ' '); // Space
			}
		}
	}
}
