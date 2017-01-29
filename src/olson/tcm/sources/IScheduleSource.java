package olson.tcm.sources;

import java.io.InputStream;

public interface IScheduleSource {
	public InputStream getFromSource(String name);
}
