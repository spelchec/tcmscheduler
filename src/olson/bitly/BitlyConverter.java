package olson.bitly;

import net.swisstech.bitly.BitlyClient;
import net.swisstech.bitly.model.Response;
import net.swisstech.bitly.model.v3.ShortenResponse;

import java.util.logging.Logger;

public class BitlyConverter {
	
	private Logger log = Logger.getGlobal();
	
	String CLIENT_ID = "";
	String CLIENT_SECRET = "";
	String GENERIC_ACCESS_TOKEN = "";
	
	private static BitlyConverter bitlyConverter = null;
	private static BitlyClient client = null;
	
	private BitlyConverter() {
		log.info("setting provider...");
		client = new BitlyClient(GENERIC_ACCESS_TOKEN);
		log.info("provider is " + client);
	}
	
	public static BitlyConverter getSingleton() {
		if (bitlyConverter == null) {
			
			bitlyConverter = new BitlyConverter();
		}
		return bitlyConverter;
	}
	
	public String getLink(String longUrl) {
		Response<ShortenResponse> shortUrl = client.shorten().setLongUrl(longUrl).call();
		return shortUrl.data.url;
	}
	
}
