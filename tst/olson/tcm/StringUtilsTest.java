package olson.tcm;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.logging.Logger;

import org.junit.Test;

public class StringUtilsTest {
	
	Logger log = Logger.getGlobal();
	
	@Test
	public void testGetCalendarFormat() {
		log.info(StringUtils.getCalendarFormat(Calendar.getInstance()));
	}
	
	@Test
	public void testAdjustDateFromMillis() {
		String timeStr = null;
		String dateStr = null;
		
		Calendar cal = Calendar.getInstance();
		System.out.println(cal.getTimeInMillis() + "+" +cal.getTime());
		cal.setTimeInMillis(1458382500 * 1000L);
//		cal.setTimeInMillis(1458494377434);
		
		System.out.println(cal.getTimeInMillis() +"+"+cal.getTime());

		System.out.println(">" + StringUtils.adjustDate(1458382500000L));

		assertEquals("6:15AM", StringUtils.adjustDate(1458382500000L));

	}

	@Test
	public void testAdjustDate() {
		String timeStr = null;
		String dateStr = null;
		
		Calendar cal = Calendar.getInstance();
		System.out.println(cal.getTimeInMillis() + "+" +cal.getTime());
		cal.setTimeInMillis(1458382500 * 1000L);
//		cal.setTimeInMillis(1458494377434);
		
		System.out.println(cal.getTimeInMillis() +"+"+cal.getTime());

		timeStr = "6:15 AM";
		dateStr = "2016-03-20";
		assertEquals("2016-03-20 6:15AM", StringUtils.adjustDate(timeStr, dateStr));

		timeStr = "3:15 AM";
		dateStr = "2016-03-20";
		assertEquals("2016-03-21 3:15AM", StringUtils.adjustDate(timeStr, dateStr));

	}

	@Test
	public void testAdjustTitle() {
		String title = null;
		
		title = "Beast From 20,000 Fathoms, The";
		assertEquals("The Beast From 20,000 Fathoms", StringUtils.adjustTitle(title));

		title = "Man Escaped, A";
		assertEquals("A Man Escaped", StringUtils.adjustTitle(title));
		
		title = "Bad Sleep Well, The (1960)";
		assertEquals("The Bad Sleep Well (1960)", StringUtils.adjustTitle(title));

		title = "Those Lips, Those Eyes";
		assertEquals("Those Lips, Those Eyes", StringUtils.adjustTitle(title));

		title = "Those Lips, Those Eyes (1980)";
		assertEquals("Those Lips, Those Eyes (1980)", StringUtils.adjustTitle(title));
	}
	
	@Test
	public void testTrimSpaces() {
		assertEquals("", StringUtils.trimSpaces(""));
		assertEquals("", StringUtils.trimSpaces(" "));
		assertEquals("", StringUtils.trimSpaces("                "));

		assertEquals("new", StringUtils.trimSpaces("new"));
		assertEquals("new", StringUtils.trimSpaces(" new "));
		assertEquals("new", StringUtils.trimSpaces("     new           "));

		assertEquals("new york", StringUtils.trimSpaces("new york"));
		assertEquals("new york", StringUtils.trimSpaces("     new      york     "));
		assertEquals("new york", StringUtils.trimSpaces("new           york"));

	}
}
