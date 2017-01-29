package olson.tcm.sources;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Logger;

public class TestSource implements IScheduleSource {
	
	Logger log = Logger.getGlobal();
	String LOCATION = "./tst/olson/tcm/";
	
	@Override
	public InputStream getFromSource(String name) {
		FileInputStream fis = null;
		try {
			File file = new File(LOCATION + name);
			fis = new FileInputStream(file);
		} catch (Exception e) {
			log.info("File Input Couldn't be loaded! -> " + LOCATION + name);
		}
		return fis;
	}

}
