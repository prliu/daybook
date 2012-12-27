package tw.com.sunnybay.daybook;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {

	public static boolean isDate(String input) {

		boolean result = false;

		String expression = "(19|20)\\d\\d([-/])(0[1-9]|1[012])\\2(0[1-9]|[12][0-9]|3[01])";
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(input);
		if (matcher.matches())
			result = true;

		return result;
	}
	
	public static boolean isNumber(String input) {
		
		boolean result = false;
		
		String expression = "[0-9]+";
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(input);
		if(matcher.matches())
			result = true;
		
		return result;
	}

}
