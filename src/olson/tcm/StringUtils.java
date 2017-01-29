package olson.tcm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
	
	private static Logger log = Logger.getGlobal();
	
	public static String getCalendarFormat(Calendar cal) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(cal.getTime());
	}

	public static String adjustDate(Object timeMillis) {
		Long millis = Long.parseLong(timeMillis.toString());
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		cal.setTimeInMillis(millis);
		DateFormat df = new SimpleDateFormat("h:mma");
		df.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		return df.format(cal.getTime());
	}

	public static String adjustDate(String timeString, String dateString) {
		Pattern pattern = Pattern.compile("h");
		return null;
	}

	public static String trimDate(String string) {
		Pattern pattern = Pattern.compile("h");
		return null;
	}

	public static String trimSpaces(String string) {
		StringBuilder newString = new StringBuilder();
		Scanner scanner = new Scanner(string);
		while (scanner.hasNext()) {
			newString.append(scanner.next());
			newString.append(" ");
		}
		if (newString.length() > 0) {
			newString.deleteCharAt(newString.length()-1);
		}
		return newString.toString();
	}
	
	public static String adjustTitle(String title) {
		Pattern pattern = Pattern.compile("(.*?)(,\\s+([A-Za-z]{1,3}))?(\\s+(\\(\\d+\\)))?$");
		Matcher matcher = pattern.matcher(title);
		if (matcher.matches()) {
			log.finest("groupCount=" + matcher.groupCount());
			for (int gc = 0; gc <= matcher.groupCount(); gc++) {
				log.finest("group > " + matcher.group(gc));
			}
			String result = ((matcher.group(3) == null) ? "" : matcher.group(3)) + " " + matcher.group(1) + " " + ((matcher.group(5) == null)?"":matcher.group(5));
			return result.trim();
		}
		return title;
	}
}
