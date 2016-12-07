package nGram;

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
import java.util.Iterator;
import java.util.Map;





import Utilities.Configuration;

public class BuildNgramMap {

	// two temporary map, one for starting n gram and other for ending n gram 
	private static HashMap<String, Double> sMap = new HashMap<String, Double>();
	private static HashMap<String, Double> eMap = new HashMap<String, Double>();
	
	// value to facilitate the counting of the nGram in a 0-indexed-based array
	// for n gram with max 4 gram, the offset is 2
	// we are not handling 0 gram (non existent) and 1 gram, 
	// these two values realizes into offset
	// if handling 1 gram then this offset will be 1
    private static int offset = 2;
    
    // two final maps which are the final output of this class
    // one for starting n gram and other for ending n gram
	public static HashMap<String, String> startMap = new HashMap<String, String>();
	public static HashMap<String, String> endMap = new HashMap<String, String>();
	
	// object to read the configuration parameters of the program
	Configuration Conf = new Configuration();

	public void DevelopModel () {
		//BuildMap BM = new BuildMap();
		
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
		    	
		    	
		    	long[] Count = ReadAndBuild(path, marker);
		    	
		    	// taking every language score on same level of comparison		    	
		    	Normalize(Count, 0); // sMap Probability 
		    	Normalize(Count, 1); // eMap Probability
		    	
		    	UpdateStartMaps (marker);
		    	UpdateEndMaps (marker);
		    	
		    }
		  } else {
			 System.out.println("Files are null"); 
		    // something wrong 
		    // to avoid race conditions with another process that deletes directories.
		  }
	}
		
	///////////////////////////////////////////////////////////////////////
	// process on each raw dictionary file, 
	// build up the n gram for every word
	// to fund out the specific pattern hidden in n gram for every language
	// rich the source dictionary file, better will be the quality of the ngram functionality
	// the count[] variable is to normalize the value of the related n gram score
	private long[] ReadAndBuild(String path, String marker) {		
		String  thisWord = null;
		sMap.clear();
		eMap.clear();
		long[] count = {0,0,0};
		
		try {
		     // open input stream dictionary file for reading purpose.
		     BufferedReader br = new BufferedReader(new FileReader(path));
		     
		     while ((thisWord = br.readLine()) != null) {
		    	 thisWord = thisWord.trim().toLowerCase();
                int len = thisWord.length();
                if (len >=4) {
                	ExtractNgram(thisWord, 4, len);
                	ExtractNgram(thisWord, 3, len);
                	ExtractNgram(thisWord, 2, len);
                	count[2]++;
                	count[1]++;
                	count[0]++;                	
                }
                else if(len == 3) {
                	ExtractNgram(thisWord, 3, len);
                	ExtractNgram(thisWord, 2, len);
                	count[1]++;
                	count[0]++;                	
                } 
                else if(len == 2) {
                	ExtractNgram(thisWord, 2, len);
                	count[0]++;                	
                } 
		     	        
		     }
		     br.close();
		  }catch(Exception e){
		     e.printStackTrace();
		  }		
		return count;
	}
	

	//////////////////////////////////////////////////////////////////////
	private void ExtractNgram(String In, int sz, int len) {
		String S = In.substring(0, sz);
		double count = 1.0;
		if (sMap.containsKey(S) == true) {
			count = (Double) sMap.get(S);
			count += 1.0;
		}
		sMap.put(S, count);
		
		// get value for ending n.gram
		String E = In.substring((len-sz), len);
		count = 1.0;
		if (eMap.containsKey(E) == true) {
			count = (Double) eMap.get(E);
			count += 1.0;
		}
		eMap.put(E, count);
		
	//	System.out.println( In + " -> " + S + "\t" + E);		
	}

	//////////////////////////////////////////////////////////////////////
	// Calculate Probability 	
	@SuppressWarnings("rawtypes")
	private void Normalize(long[] Count, int Type) {
		String key = "";
		HashMap<String, Double> tmp = new HashMap<String, Double>();
		Iterator it;
		
		if (Type == 0 ) 
			it = sMap.entrySet().iterator();
		else
			it = eMap.entrySet().iterator();
		
		while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	     // System.out.println(pair.getKey() + " = " + pair.getValue());
	        it.remove(); // avoids a Concurrent Modification Exception
	        key = pair.getKey().toString();
	        double val = (Double)pair.getValue();
	        val /= Count[(key.length() - offset)];
	        tmp.put(key , val);
	    }
		
		if (Type == 0 ) {
			sMap.clear();
		    sMap.putAll(tmp);
		}else {
			eMap.clear();
		    eMap.putAll(tmp);
		}	    
	}

	//////////////////////////////////////////////////////////////////////
	// Update Final Maps 	
	@SuppressWarnings("rawtypes")
	private void UpdateStartMaps (String Marker) {
		String key = "";
		String val = "";
		Iterator it = sMap.entrySet().iterator();
		
		while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	     // System.out.println(pair.getKey() + " = " + pair.getValue());
	        key = pair.getKey().toString();
		    val = Marker + "," + pair.getValue().toString();
	        it.remove(); // avoids a Concurrent Modification Exception
		    
	      if (startMap.containsKey(key) == true) {
	    	  String v = startMap.get(key).toLowerCase();
	    	  val += "," + v;
	      }	      
	      startMap.put(key, val);
	    // System.out.println(key + "->" + val);		   
	    }	    
	}

	//////////////////////////////////////////////////////////////////////
	// Update Final Maps 	
	@SuppressWarnings("rawtypes")
	private void UpdateEndMaps (String Marker) {
		String key = "";
		String val = "";
		Iterator it = eMap.entrySet().iterator();
		
		while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	     // System.out.println(pair.getKey() + " = " + pair.getValue());
	        key = pair.getKey().toString();
		    val = Marker + "," + pair.getValue().toString();
	        it.remove(); // avoids a Concurrent Modification Exception
		    
	      if (endMap.containsKey(key) == true) {
	    	  String v = endMap.get(key).toLowerCase();
	    	  val += "," + v;
	      }	      
	      endMap.put(key, val);
	 //   System.out.println(key + "->" + val);
	    }	    
	}

	////////////////////////////////////////////////////////////////	
	
	//// Serialize
	public void Serialize() {
	//	System.out.println(startMap.size() + "..." + endMap.size());
		Serialize("startMapPath");
		Serialize("endMapPath");
	}
	
	////////////////////////////////////////////////////////////////	
	// Serialize the object in hard disk
	// to speed up the processing on next call of the program
	public void Serialize(String Type) {
		try
        {
           FileOutputStream fos =
              new FileOutputStream(Conf.prop.getProperty(Type));
           ObjectOutputStream oos = new ObjectOutputStream(fos);
           
           if (Type.equalsIgnoreCase("startMapPath") ) 
             oos.writeObject(startMap);
           else
          	 oos.writeObject(endMap);
           
           oos.close();
           fos.close();
       //  System.out.printf("Serialized HashMap is saved at " + prop.getProperty(Type));
        }catch(IOException ioe)
         {
               ioe.printStackTrace();
         }		
	}
	
	////////////////////////////////////////////////////////////////
	// pull out the serialized object 
	// optimized way of running program
	@SuppressWarnings("unchecked")
	public HashMap<String, String> Deserialize(String Type) {
		
		HashMap<String, String> tmpMap = new HashMap<String, String>();
		try
	      {
	         FileInputStream fis = new FileInputStream(Conf.prop.getProperty(Type));
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
	
} // end of the class
