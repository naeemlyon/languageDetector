package Utilities;

/*
 * ==========================================================================
 * Class to load the configuration parameters                                | 
 * Important, if the program is called from Linux then change the slash sign |
 * according to the requirement of the operating system                      |
 *                                                                           | 
 *  for windows based operating system                                       |
 *  following is required to be updated at config.properties file            | 
 *                  mapPath=data\\dictMap.ser                                |
 *                                                                           | 
 *  for linux based operating system                                         |
 *  following is required to be updated at config.properties file            | 
 *                  mapPath=data/dictMap.ser                                 |
 *                                                                           |
 * ==========================================================================                           
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {

	public Properties prop = new Properties();
	
	// constructor will empower the public variable with 
	// all of the parameters to be used by the other classes
	public Configuration() {
		InputStream input = null;
		try {

			input = new FileInputStream("config.properties");

			// load a properties file
			prop.load(input);			

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}	
	}

}
