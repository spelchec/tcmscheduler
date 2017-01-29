package olson.tcm;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.policy.resources.S3BucketResource;
import com.amazonaws.services.codepipeline.model.AWSSessionCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class WebPageParser {
	
	Logger log = Logger.getGlobal();
	
	private String s3bucketName = "tcmschedule";
	
	private final String TCM_URL = "http://www.tcm.com/schedule/index.html?tz=est&sdate=";
	private String accessKey = "";
	private String secretKey = "";

	private BasicAWSCredentials basicCredentials;
	private AmazonS3 amazonS3;
	private ObjectListing bucketList;
	
	public WebPageParser() {
		basicCredentials = new BasicAWSCredentials(accessKey, secretKey);
		amazonS3 = new AmazonS3Client(basicCredentials);
		bucketList = amazonS3.listObjects(s3bucketName);		
	}
	
	public int getFilesInBucket() {
		return bucketList.getObjectSummaries().size();
	}
	
	public void cleanOldS3Files() {
		Calendar staleCal = Calendar.getInstance();
		staleCal.add(Calendar.DAY_OF_WEEK, -1);
		for (S3ObjectSummary summary : bucketList.getObjectSummaries()) {
			if (summary.getLastModified().before(staleCal.getTime()) && (summary.getSize() < 1000000L)) {
				DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(s3bucketName, summary.getKey());
				amazonS3.deleteObject(deleteObjectRequest);
			}
		}
	}

	public String getS3File(Calendar day) throws IOException {
		StringBuilder sb = new StringBuilder();
		Calendar cal = Calendar.getInstance();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		S3Object obj;
		
		obj = amazonS3.getObject(s3bucketName, df.format(cal.getTime()));
		S3ObjectInputStream is;		
		InputStreamReader isr;
		BufferedReader br;
		
		is = obj.getObjectContent();
		isr = new InputStreamReader(is);
		br = new BufferedReader(isr);
		String input = null;
		while (null != (input = br.readLine())) {
			sb.append(input);
			sb.append("\n");
		}
		is.close();
		
		cal.add(Calendar.DAY_OF_WEEK, 1);
		obj = amazonS3.getObject(s3bucketName, df.format(cal.getTime()));
		is = obj.getObjectContent();

		isr = new InputStreamReader(is);
		br = new BufferedReader(isr);
		input = null;
		while (null != (input = br.readLine())) {
			sb.append(input);
			sb.append("\n");
		}
		is.close();
		return sb.toString();
}

	public void pullTcmPageToS3(Calendar calendarDate) throws ClientProtocolException, IOException {

		String s3assetName = StringUtils.getCalendarFormat(calendarDate);
		System.out.println("Getting bucket from " + amazonS3.getBucketLocation(s3bucketName));
		
		String url = TCM_URL + StringUtils.getCalendarFormat(calendarDate);
		WebPageParser parser = new WebPageParser();
		String content = parser.getWebPage(url);
		
		ByteArrayInputStream bais = new ByteArrayInputStream(content.getBytes());
		InputStream is = bais;
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(content.length());
		PutObjectRequest putObjectRequest = new PutObjectRequest(s3bucketName, s3assetName, is, metadata);
		
		amazonS3.putObject(putObjectRequest);
	}
	
	public String getWebPage(String url) throws ClientProtocolException, IOException {
		Document doc = Jsoup.connect(url).get();
		StringBuilder sb = new StringBuilder();
		Elements elements = doc.select("#mainSchedule .mini"); // scheduleRow
		log.info("element count: " + elements.size());
		Iterator<Element> elementIter = elements.iterator();
		while (elementIter.hasNext()) {
			Element el = elementIter.next();
			log.info("> " + el.childNodeSize());
			sb.append(el.select(".timeColumn .silentDate").text()); // timeData
			sb.append("000 ");
			log.info("text - "+ el.select(".titleData").text());
			sb.append(el.select(".titleData").text());
			sb.append("\n");
		}
		return sb.toString();
	}
}
