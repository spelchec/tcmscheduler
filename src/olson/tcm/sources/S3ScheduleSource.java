package olson.tcm.sources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Logger;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;

import olson.tcm.WebPageParser;

public class S3ScheduleSource implements IScheduleSource {

	Logger log = Logger.getGlobal();
	
	private static final String s3bucketName = "";
	private String accessKey = "";
	private String secretKey = "";

	private BasicAWSCredentials basicCredentials;
	private AmazonS3 amazonS3;
	private ObjectListing bucketList;
	
	public S3ScheduleSource() {
		basicCredentials = new BasicAWSCredentials(accessKey, secretKey);
		amazonS3 = new AmazonS3Client(basicCredentials);
		bucketList = amazonS3.listObjects(s3bucketName);		
	}
	
	@Override
	public InputStream getFromSource(String name) {
		S3Object s3object = null;
		try {
			try {
				s3object = amazonS3.getObject(s3bucketName, name);
			} catch (AmazonS3Exception re) {
				s3object = loadIntoAws(name);
			}

			if (s3object == null) {
				s3object = loadIntoAws(name);
			}
			InputStream is = s3object.getObjectContent();
			return is;
		} catch (Exception re) {
			throw new RuntimeException(re);
		}
	}
	
	public S3Object loadIntoAws(String name) throws ParseException, IOException {
		log.info("load into aws");
		WebPageParser parser = new WebPageParser();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar day = Calendar.getInstance();
		day.setTime(df.parse(name));
		parser.pullTcmPageToS3(day);
		S3Object s3object = amazonS3.getObject(s3bucketName, name);
		return s3object;

	}

}
