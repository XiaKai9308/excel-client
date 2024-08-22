package cn.tamilin.api.client;

import static java.lang.Character.isWhitespace;
import static java.lang.System.currentTimeMillis;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Utils {

	public static final NumberFormat NUMBER_FORMAT = new DecimalFormat("#.######");

	public static int unixTime() {
		return (int) (currentTimeMillis() / 1000);
	}

	public static boolean isBlank(final CharSequence chars) {
		int strLen;
		if (chars == null || (strLen = chars.length()) == 0)
			return true;
		for (int i = 0; i < strLen; ++i)
			if (!isWhitespace(chars.charAt(i)))
				return false;
		return true;
	}
}
