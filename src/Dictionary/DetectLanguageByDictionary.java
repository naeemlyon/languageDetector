package Dictionary;

/*
 * ==========================================================================
 * Driver class for building dictionary based map                            | 
 * call the dictionary functionality                                         |
 * Step 1. create the dictionary and then serialize the dictionary object.   |
 *         Once this step is carried out, no need to develop the map on      |
 *         every successive invocation of the method.                        |
 * Step 2. Load the dictionary map (if already created in step 1)            |
 *         once the dictionary is loaded, the program is ready to            |
 *         detect the language written in 'text file' or 'text at console'   |
 * Step 3. provide the text file. The text will be parsed and manipulated    |
 *         according to the dictionary object.                               |
 * Step 4. The step is same (step 3) except that the text is provided at     | 
 *         console.                                                          |
 * The final result is an array of probability showing how much it is        | 
 * probable to belong to any particular language.                            |
 * ==========================================================================                           
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class DetectLanguageByDictionary {

	// map object to load all of the dictionary words from various languages
	private static HashMap<String, String> map = new HashMap<String,String>();
	
	// final results are stored and processed. 
	private static HashMap<String, Double> lang = new HashMap<String,Double>();
	
	///////////////////////////////////////////////	
	public void Step_1() {
		BuildDictionaryMap BM = new BuildDictionaryMap();
		map = BM.DevelopModel();
		System.out.println("Serializing the Map for future use");
		BM.Serialize();		
	}
	
	///////////////////////////////////////////////
	public int Step_2() {
		int ret = 0; // error reprot
		BuildDictionaryMap BM = new BuildDictionaryMap();		
		map = BM.Deserialize();
		if (map.size() > 1) {
			ret = 1; // success
		}
		return ret;		
	}
	///////////////////////////////////////////////
	public void Step_3(String path) {
		StringBuilder out = new StringBuilder();
		
		// read the text file and store it into a 
		// single string variable.
		InputStream in;
		try {
			in = new FileInputStream(new File(path));
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	        String line;
	        while ((line = reader.readLine()) != null) {
	            out.append(line);
	        }	 
	        reader.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		// we have the text extracted from the input file 
		String Input = out.toString();		
		int countTokens = Detect(Input);
		Normalize(countTokens);
		Sorting(); // descending order
	}

    
	///////////////////////////////////////////////
	public void Step_4(String Input) {	
		int countTokens = Detect(Input);
		Normalize(countTokens);		
		Sorting(); // descending order
	}	
	
	///////////////////////////////////////////////
	private int Detect (String Input) {
		
		int wordFound = 0;
		Input = Input.toLowerCase().trim();
		
		// to avoid mix up on use on second and subsequent round up usage
		lang.clear();  
		
		String delimiters = "\\s+|,\\s*|\\.\\s*";
	    // analyzing the string 
		 String[] tokensVal = Input.split(delimiters);
		    
		   for(String token : tokensVal) {
		    if (map.containsKey(token) == true ) {
		    	String val = map.get(token);
		        // System.out.println("\n" + token + ":\t" + val + "\t");
		    	
		    	// parse and check how much probable each word belongs to a any language
		    	ComputeLanguagePercent(val);
		    	
		    	// for normalizaton, every score of language will be converted 
		    	// into probability by dividing the frequency by wordFound
		    	wordFound++;
		    }
		   }	   
		   
		// feed back whether everything goes correctly.
		// if this value is 0 then further steps will not be invoked in the caller class   
		return ((wordFound > 0) ? wordFound : 1);
	}
		
	///////////////////////////////////////////////
	private void ComputeLanguagePercent(String Input) {
		String[] Arr = Input.split(",");
		int i =0;
		double val = 0.0;
		int len = Arr.length;
		for (i=0; i<Arr.length; i++) {
			val = 1.0 / len;
			// if we got the previous record then update 
			if (lang.containsKey(Arr[i])==true) {
				val += (Double)lang.get(Arr[i]); 
			}
			lang.put(Arr[i], val);
            // System.out.println( Arr[i] +" = "+ lang.get(Arr[i]) + "\t"    );		
		}		
	}
	
	
	///////////////////////////////////////////////
	// convert frequency into probability 	
	private static void Normalize(int Count) {
		// temporar variable to store the normalized data
		HashMap<String, Double> tmp = new HashMap<String, Double>();
		
		// parse each and every token and compute its language probability
		@SuppressWarnings("rawtypes")
		Iterator it = lang.entrySet().iterator();
	    while (it.hasNext()) {
	        @SuppressWarnings("rawtypes")
			Map.Entry pair = (Map.Entry)it.next();
	     // System.out.println(pair.getKey() + " = " + pair.getValue());
	        it.remove(); // avoids a Concurrent Modification Exception
	        double val = (Double)pair.getValue();
	        val /= Count;
	        tmp.put(pair.getKey().toString(), val);
	    }
	    // result storing variable 'lang' has been processed in above loop.
	    // we need to wash out the unprocessed result. 	    
	    lang.clear();
	    
	    // populate the fresh (unsorted) result into 'lang'
	    lang.putAll(tmp);	    
	}
	
	
		
	////////////////////////////////////
	// we have the results but they are all in unsorted. 
	// for large number of languages, this function becomes imperative
	public static void Sorting(){
		//System.out.println(lang);
		TreeMap<String, Double> sortedMap = SortByValue(lang);  
		
		// display the final result
		System.out.println("\n" + sortedMap);
		
		// in case the calling step of this function is prematurely called on 
		// then proper feed back is required
		String Result = "Sorry! Either you have not loaded the DictionaryMap or\n";
		Result += "The dictionary size is too short to identify this text.";
		
		// the winner (highest score/probaility)
		if (sortedMap.size() > 0) {
			Result = "+++++++++++++++++++++++++++++++++\n";
			Result += "****\t" +  sortedMap.firstKey() + "\t****\n";  
			Result += "++++++++++++++++++++++++++++++++\n";
		}		
		
		System.out.println(Result);
	}
	
	// part of sorting function	
	public static TreeMap<String, Double> SortByValue 
		(HashMap<String, Double> map) {
		ValueComparator vc =  new ValueComparator(map);
		TreeMap<String,Double> sortedMap = new TreeMap<String,Double>(vc);
		sortedMap.putAll(map);
		return sortedMap;
	}
} // end of the DetectLanguage...class

		
	///////////////////////////////////////////////
    // comparison between every two members of the map
    // this function idea taken from stackoverflow.com
	class ValueComparator implements Comparator<String> {
		    Map<String, Double> tmpMap;
		    
		    public ValueComparator(Map<String, Double> base) {
		        this.tmpMap = base;
		    }
		 
		    public int compare(String first, String second) {
		        if (tmpMap.get(first) >= tmpMap.get(second)) {
		            return -1;
		        } else {
		            return 1;
		        } // returning 0 would merge keys 
		    }
		}
		
		
