package preprocessing;

/*
 * the data (10000 word list)
 * http://www.streetsmartlanguagelearning.com/2008/12/top-10000-words-in-dutch-english-french.html
 * the data contains duplicate in terms of Al and al... i converted every one into lower case
 * 
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;



import Utilities.Configuration;

public class PrepareDictionaries {
	
	//static Properties prop = new Properties();
	public static HashMap<String, String> map = new HashMap<String,String>();

	// object to read the configuration parameters of the program
	static Configuration Conf = new Configuration();	
	
	public static void main(String [] args) {		
		ReadAndPopulate();
		WriteFile() ;		
		System.out.println(Conf.prop.getProperty("rawDictPath") + "\t-->\t" + 
		                   Conf.prop.getProperty("tmpFile") + "\t done.\n");
		System.out.println("Delete " + Conf.prop.getProperty("rawDictPath") + " and rename " + 
                           Conf.prop.getProperty("tmpFile") + " to " + 
				           Conf.prop.getProperty("rawDictPath"));

	}
		
	/////////////////////////////////////////////////////////////////////////////
	// the objective of this function is to remove the duplicate word files 
	// read every word in a map
	// HashMap automatically over rides if found any duplicate
	private static void ReadAndPopulate() {
		//	long i = 0;
		String  thisLine = null;
		Reader filePath;
		try {
			filePath = new FileReader(Conf.prop.getProperty("rawDictPath"));
		      try {
		         // open input stream test.txt for reading purpose.
		         BufferedReader br = new BufferedReader(filePath);
		         
		         while ((thisLine = br.readLine()) != null) {
		        	thisLine = thisLine.trim().toLowerCase();		            
		            map.put(thisLine,"");	
		            //System.out.println(i + ": " + thisLine + "\t" + map.get(thisLine) );		          
		            
		         }
		         br.close();
		      }catch(Exception e){
		         e.printStackTrace();
		      }		
		} 
		catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
	}
	
	
	/////////////////////////////////////////////////////////////////////////////
	// write down every key and value of the map into the text file
	// the out of the function is a text file ensured to be free from duplicate entries
	// if output file is already present then it will be over written
	// the out file is out.dir, we need to change its name to the original input file (with duplicate entries)
	@SuppressWarnings("rawtypes")
	private static void WriteFile() {		
		File fout = new File ( Conf.prop.getProperty("tmpFile"));
		FileOutputStream fos;
				
		try {
			fos = new FileOutputStream(fout);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			Iterator it = map.entrySet().iterator();
		
			while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry)it.next();	       
		      //  System.out.println(pair.getKey());
		        bw.write(pair.getKey().toString());
				bw.newLine();
		        it.remove(); // avoids a ConcurrentModificationException
		    }			
			bw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	    
	}
	
	
	
	
	
	
	
}
