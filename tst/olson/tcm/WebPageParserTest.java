package olson.tcm;

import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Logger;

import org.apache.http.client.ClientProtocolException;
import org.junit.Test;

public class WebPageParserTest {
	
	Logger log = Logger.getGlobal();
	
	@Test
	public void test() throws ClientProtocolException, IOException {
		WebPageParser parser = new WebPageParser();

		parser.pullTcmPageToS3(Calendar.getInstance());
	}

	@Test
	public void getTest() throws ClientProtocolException, IOException {
		WebPageParser parser = new WebPageParser();

		Calendar day = Calendar.getInstance();
		day.set(Calendar.DATE, 24);
		log.info("LOG>out\n"+parser.getS3File(day));
	}

	@Test
	public void testClean() throws ClientProtocolException, IOException {
		WebPageParser parser = new WebPageParser();
		parser.cleanOldS3Files();
	}
	
	@Test
	public void getWebPageTest() throws ClientProtocolException, IOException {
		WebPageParser parser = new WebPageParser();
		StringBuilder sb = new StringBuilder();
		sb.append(parser.getWebPage("http://www.tcm.com/schedule/index.html?tz=est&sdate=2016-03-23"));
		log.info(sb.toString());
	}
}
