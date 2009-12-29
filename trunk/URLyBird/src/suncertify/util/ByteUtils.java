package suncertify.util;

public class ByteUtils {
	public static int byteArrayToInt(byte[] byteArray) {
		int highBit = byteArray[0] & 0xff;
		int lowBit = byteArray[1] & 0xff;
		return (highBit << 8 | lowBit);
	}

}
