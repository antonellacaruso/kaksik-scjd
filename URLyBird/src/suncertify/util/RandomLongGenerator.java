package suncertify.util;

public class RandomLongGenerator {
	public static long generate() {
		long randomNumber = (long) (Math.random() * Long.MAX_VALUE);
		return randomNumber;
	}
}
