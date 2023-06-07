import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTest {

	public static void main(String[] args) {
		for (int c=0; c<99;c++) {
			try {
				String str=""+c;
				str = "0"+c;
				System.out.println(str +"\t"+getRegexBefore(str));
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

		private static String getRegexBefore(String num) throws Exception {
			//Take <num> and return an expression that matches in the range 0-<num> (num can only be up to 99)

			if (num.length()>2)
				throw (new Exception("Number out of range (allowable range is 0-99)"));
			String expr = "\\b(";
			int digit1 = num.charAt(0)-48;//less 48 to convert ASCII to dec
			int digit2 = num.charAt(1)-48;
			expr = expr +"[";
			for (int c=0; c < digit1; c++)
				expr = expr + c;
			expr = expr +"][0-9]|";

			expr = expr + digit2;

			expr = expr +"[";
			for (int c=0; c < digit2; c++)
				expr = expr + c;
			expr = expr +"]";
			expr = expr + ")\\b";
			return expr;
		}
		
//
//		Pattern pattern = Pattern.compile("\\b([01][0-9]|3[0123])\\b", Pattern.CASE_INSENSITIVE);
		
//		Pattern pattern = Pattern.compile("\\b is\\b", Pattern.CASE_INSENSITIVE);
//		Matcher matcher = pattern.matcher("ABCDEFGHIJKLM");
//		boolean matchFound = matcher.find();
//		if(matchFound) {
//			System.out.println(" Match found");
//		} else {
//			System.out.println(" Match not found");
//		}
	




}
