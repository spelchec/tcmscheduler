package olson.tcm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Logger;

import olson.bitly.BitlyConverter;
import olson.tcm.sources.IScheduleSource;
import olson.tcm.sources.S3ScheduleSource;
import olson.tcm.sources.TestSource;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class TweetLoader {
	
	private static final String CONSUMER_KEY = "";
	private static final String CONSUMER_SECRET = "";
	private static final String TOKEN = "";
	private static final String TOKEN_SECRET = "";
	
	StringBuilder responseString;
	Logger log = Logger.getGlobal();
	BitlyConverter conv = BitlyConverter.getSingleton();
	
	public String getFromTime(Calendar currentTime, int responseCharacterLength) throws NumberFormatException, IOException, ParseException {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar fileTime = (Calendar) currentTime.clone();
		fileTime.add(Calendar.HOUR, -6);
		IScheduleSource source = new S3ScheduleSource();
		String fileName;
		String responseString = "";
		while (responseString.length() < responseCharacterLength) {
			fileName = df.format(fileTime.getTime());
			log.info("get from location " + fileName);
			InputStream is = source.getFromSource(fileName);
			String returnString = getFromStream(is, currentTime);
			is.close();
			responseString = responseString.concat(returnString);
			if (responseString.length() < responseCharacterLength) {
				fileTime.add(Calendar.DAY_OF_WEEK, 1);
				log.info("increment for more data: ("+responseString.length() +" < "+ responseCharacterLength+")");
			}
			log.info("responseString=" + responseString);
		}
		responseString = StringUtils.trimSpaces(responseString);
		responseString = responseString.substring(0, responseCharacterLength);
		responseString = responseString.substring(0, responseString.lastIndexOf(";"));

		return responseString;
	}
	
	public String getFromStream(InputStream is, Calendar currentTime) throws NumberFormatException, IOException {
		Calendar cal = Calendar.getInstance();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		responseString = new StringBuilder();
		
		String input = null;
		while (null != (input = br.readLine())) {
			Long millis = Long.parseLong(input.substring(0, input.indexOf(" ")));
			Calendar time = Calendar.getInstance();
			time.setTimeInMillis(millis);
			if (time.after(cal)) {
				responseString.append(StringUtils.adjustDate(millis));
				responseString.append(" ");
				String title = StringUtils.adjustTitle(input.substring(input.indexOf(" ")));
				responseString.append(title);
				responseString.append(" ");
				String shortUrl = conv.getLink("http://www.imdb.com/find?s=title&q="+URLEncoder.encode(title, "UTF-8"));
				responseString.append(shortUrl);
				responseString.append("; ");
			}
		}
		
		br.close();
		isr.close();
		return responseString.toString();
	}

	public String getFromFile(File file, Calendar currentTime) throws NumberFormatException, IOException {
		Calendar cal = Calendar.getInstance();
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		responseString = new StringBuilder();
		
		String input = null;
		while (null != (input = br.readLine())) {
			Long millis = Long.parseLong(input.substring(0, input.indexOf(" ")));
			Calendar time = Calendar.getInstance();
			time.setTimeInMillis(millis);
			if (time.after(cal)) {
				responseString.append(StringUtils.adjustDate(millis));
				responseString.append(" ");
				responseString.append(StringUtils.adjustTitle(input.substring(input.indexOf(" "))));
				responseString.append("; ");
			}
		}
		
		br.close();
		fr.close();
		return responseString.toString();
	}

	public boolean postTweet() throws NumberFormatException, IOException, ParseException, TwitterException {
		log.info("postTweet");
		Calendar currentTime = Calendar.getInstance();
		String post = getFromTime(currentTime, 135);
		log.info("have post: "+post);
		Twitter twitter = TwitterFactory.getSingleton();
	    twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
		AccessToken accessToken = new AccessToken(TOKEN, TOKEN_SECRET);
		twitter.setOAuthAccessToken(accessToken);
		twitter.verifyCredentials();
		log.info("established Twitter connection");
		
		String mostRecentPost = null;
		
		boolean routeToPublic = true;
		if (routeToPublic) {
			mostRecentPost = twitter.getUserTimeline().get(0).getText();
		} else {
			mostRecentPost = twitter.getDirectMessages().get(0).getText();
		}
		log.info("most recent post:  \""+mostRecentPost+"\"");
		log.info("post to update to: \""+post+"\"");
		if (!mostRecentPost.equalsIgnoreCase(post)) {
			log.info("send post = " + post);
			if (routeToPublic) {
				twitter.updateStatus(post);			
			} else {
				twitter.sendDirectMessage("tcmschedule", post);
			}
		} else {
			log.info("don't send post: is the same");
		}
		return true;
	}
}
