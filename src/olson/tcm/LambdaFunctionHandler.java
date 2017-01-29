package olson.tcm;

import java.util.Calendar;
import java.util.logging.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class LambdaFunctionHandler implements RequestHandler<Object, Object> {

	Logger log = Logger.getGlobal();
	
    @Override
    public Object handleRequest(Object input, Context context) {

    	WebPageParser parser = new WebPageParser();
    	context.getLogger().log("Input: " + input);
    	
    	TweetLoader loader = new TweetLoader();
    	try {
    		loader.postTweet();

//	        int filesInBucket = parser.getFilesInBucket();
//	        log.info("filesInBucket="+filesInBucket);
//	        Calendar calendarDate = Calendar.getInstance();
//	        if (filesInBucket < 7) {
//	        	parser.cleanOldS3Files();
//	        }
//	        for (int i = 0; i < 2; i++) {
//	        	parser.pullTcmPageToS3(calendarDate);
//	        	calendarDate.add(Calendar.DAY_OF_WEEK, 1);
//	        }	    
        } catch (Exception e) {
        	log.info("Error! "+e.getMessage());
        }

        // TODO: implement your handler
        return null;
    }

}
