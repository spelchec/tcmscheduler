package olson.bitly;

import org.junit.Test;

public class BitlyConterterTest {
	@Test
	public void test () {
		BitlyConverter conv = BitlyConverter.getSingleton();
		String shortUrl = conv.getLink("http://google.com");
		System.out.println("short url = " + shortUrl);
	}
}
