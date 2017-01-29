package olson.tcm;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import org.junit.Test;

import twitter4j.TwitterException;

public class TweetLoaderTest {
	Logger log = Logger.getGlobal(); 

	@Test
	public void testGetFromTime () throws NumberFormatException, IOException, ParseException {
		TweetLoader loader = new TweetLoader();
		Calendar currentTime = Calendar.getInstance();
		log.info("res = \"" + loader.getFromTime(currentTime, 135));
	}

	@Test
	public void testGetFromFile () throws NumberFormatException, IOException {
		File file = new File("./tst/olson/tcm/2016-03-24");
		log.info("file path  ="+file.getAbsolutePath());
		log.info("file found = "+file.exists());
		TweetLoader loader = new TweetLoader();
		log.info("res = \"" + loader.getFromFile(file, Calendar.getInstance()) +"\"");
	}
	@Test
	public void test () throws NumberFormatException, IOException, ParseException, TwitterException {
		TweetLoader loader = new TweetLoader();
		loader.postTweet();
	}
}
