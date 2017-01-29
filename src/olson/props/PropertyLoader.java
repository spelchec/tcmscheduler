package olson.props;

import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.ResourceBundle;

public class PropertyLoader {
	
	PropertyLoader loader = null;
	private static HashMap<String, String[]> properties = new HashMap<String, String[]>();
	
	private PropertyLoader() {
		Enumeration<URL> resources = this.getClass().getClassLoader().getResources("*.properties");
		while (resources.hasMoreElements()) {
			URL location = resources.nextElement();
			ResourceBundle bundle = ResourceBundle.getBundle(location);
		}
		for () {
			
		}
	}
	
	PropertyLoader build() {
		if (loader == null) {
			loader = new PropertyLoader();
		}
		return loader;
	}
	
	public String[] get(String term) {
		return properties.get(term);
	}
}
