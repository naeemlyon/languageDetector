package Dictionary;
/*
 * ==========================================================================
 * Core class for building dictionary based map                              | 
 * read every word from dictionary file for each of the language             |
 * Build the Map and then serialize it for future use                        |
 * ==========================================================================                           
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import Utilities.Configuration;

public class BuildDictionaryMap {

	public static HashMap<String, String> map = new HashMap<String,String>();
	
	// object to read the configuration parameters of the program
	Configuration Conf = new Configuration();	
	   
	
	public HashMap<String, String> DevelopModel () {		
		
		// loop over the folder with all of the raw data in dictionary. 
		// add the file with extension dic in the data folder
		// the format of the raw dic files needs to be consistent
		// every line should contain a single word only
		File dir = new File(Conf.prop.getProperty("dictPath"));
		
		File[] files = dir.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.toLowerCase().endsWith(Conf.prop.getProperty("dictExt"));
		    }
		});
		
		  
		  if (files != null) {
		    for (File f : files) {		    	
		    	String path = f.getAbsolutePath().toString();
		    	
		    	// marker means the abbreviaton of any language
		    	// French -> Fr 
		    	String marker = f.getName().toString();
		    	marker = marker.replace(Conf.prop.getProperty("dictExt"),"");
		    	
		    	// process each dict file one by one
		    	ReadAndBuild(path, marker);
		    }
		  } else {
			 System.out.println("Files are null"); 
		    // something wrong 
		    // to avoid race conditions with another process that deletes directories.
		  }
		
		return map;
	}
		
	// @input: path of the dictionary file
	//         marker is abbreviation of the language such as En, Fr, De, Nl
	private static void ReadAndBuild(String path, String marker) {		
		String val = "";
		String  thisWord = null;
		try {
		     // open input stream dictionary file for reading purpose.
		     BufferedReader br = new BufferedReader(new FileReader(path));
		     
		     while ((thisWord = br.readLine()) != null) {
		    	thisWord = thisWord.trim().toLowerCase();
		    	
		        // multiple marker for a single word
		    	// a word belongs to more than two languages
		        if (map.containsKey(thisWord)==true) {
		        	val = map.get(thisWord);
		            val += "," + marker; 
		            map.put(thisWord, val);
		        }
		        else {
		        	map.put(thisWord, marker);	
		        }		        
		     }
		     br.close();
		  }
		catch(Exception e){
		     e.printStackTrace();
		  }		
		
	}
	
	
	// Serialize the object in hard disk
	// to speed up the processing on next call of the program
	public int Serialize() {
		int ret = 1;
		try
        {
           FileOutputStream fos =
              new FileOutputStream(Conf.prop.getProperty("mapPath"));
           ObjectOutputStream oos = new ObjectOutputStream(fos);
           oos.writeObject(map);
           oos.close();
           fos.close();
         //  System.out.printf("Serialized HashMap is saved at " + prop.getProperty("mapPath"));
        }catch(IOException ioe)
        {
           ioe.printStackTrace();
           ret = -1;
        }		
		return ret;
	}
	
	// pull out the serialized object 
	// optimized way of running program
	@SuppressWarnings("unchecked")
	public HashMap<String, String> Deserialize() {
		
		HashMap<String, String> tmpMap = new HashMap<String, String>();
		try
	      {
	         FileInputStream fis = new FileInputStream(Conf.prop.getProperty("mapPath"));
	         ObjectInputStream ois = new ObjectInputStream(fis);
	         tmpMap = (HashMap<String, String>) ois.readObject();
	         ois.close();
	         fis.close();
	      }catch(IOException ioe)
	      {
	         ioe.printStackTrace();
	         System.out.println("Kindly develop the map (one time only), then you can use this option on every start up of this program");
	         return null;
	      }catch(ClassNotFoundException c)
	      {	         
	         c.printStackTrace();
	         return null;
	      }
	      //System.out.println("Deserialized HashMap..");
		return tmpMap;	
	}
	
	
	// Constructor to load the properties file and essential path	
	public BuildDictionaryMap() {
     	//   nothing 
	}
		
		
	
} // end of class 

