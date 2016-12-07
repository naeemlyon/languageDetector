package nGram;

/*
 * ==========================================================================
 * Driver class for building nGram based map                                 | 
 * call the nGram functionality                                              |
 * Step 1. create the nGram Map and then serialize the object.               |
 *         Once this step is carried out, no need to develop the map on      |
 *         every successive invocation of the method.                        |
 * Step 2. Load the nGram map (if already created in step 1)                 |
 *         once the nGram is loaded, the program is ready to                 |
 *         detect the language written in 'text file' or 'text at console'   |
 * Step 3. provide the text file. The text will be parsed and manipulated    |
 *         according to the dictionary object.                               |
 * Step 4. The step is same (step 3) except that the text is provided at     | 
 *         console.                                                          |
 * The final result is an array of score (not probability) showing how much  | 
 * it is likely to belong to any particular language.                        |
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


public class DetectLanguageByNgramMap {

	// map object to load all of the nGram words from various languages
	// we need two maps, one for nGram in the begining of the word
	public static HashMap<String, String> startMap = new HashMap<String,String>();
	// and this one is for nGram in the end of the word
	public static HashMap<String, String> endMap = new HashMap<String,String>();
	
	// final results are stored and processed.lang for intermediate results 
	public static HashMap<String, String> lang = new HashMap<String,String>();
	public static HashMap<String, Double> finalLang = new HashMap<String, Double>();
	
	
	///////////////////////////////////////////////
	public void Step_1() {
		BuildNgramMap BN = new BuildNgramMap();
		BN.DevelopModel();
		System.out.println("Serializing the nGram Map for future use");
		BN.Serialize();		
	}
	////////////////////////////////////////////////////////////////////////////
	public int Step_2() {
		int ret = 0; // error reprot
		BuildNgramMap BN = new BuildNgramMap();		
		startMap = BN.Deserialize("startMapPath");
		endMap = BN.Deserialize("endMapPath");
		if (startMap.size() > 1) {
			ret = 1; // success
		}
		return ret;		
	}
   //////////////////////////////////////////////////////////////////////////////
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
		Detect(Input);
		ComputeProbability();
		Sorting(); // descending order
	}
	
	
	///////////////////////////////////////////////
	public void Step_4(String Input) {				
		Detect(Input);
		ComputeProbability();
		Sorting(); // descending order
	}
	
	///////////////////////////////////////////////
	private void Detect (String Input) {
		Input = Input.toLowerCase().trim();
		
		// to avoid mix up on use on second and subsequent round up usage
		lang.clear();  
		finalLang.clear();
		
		String delimiters = "\\s+|,\\s*|\\.\\s*";
		// analyzing the string 
		String[] tokensVal = Input.split(delimiters);
		
		
		// find 2,3,4 nGram and store them in intermediate Map		
		for(String token : tokensVal) {
            int len = token.length();
            if (len >=4) {
            	ProcessNgram(token, 4, len);
            	ProcessNgram(token, 3, len);
            	ProcessNgram(token, 2, len);
            }
            else if(len == 3) {
            	ProcessNgram(token, 3, len);
            	ProcessNgram(token, 2, len);
            } 
            else if(len == 2) {
            	ProcessNgram(token, 2, len);
            }			
			
		}
		
	}	
	
	///////////////////////////////////////////////////////////////////////////////////
	// 'n' is the value nGram (e.g., 4 gram)
	// 'In' is the input string 
	// 'len' is the length of the input string 'In'
	private void ProcessNgram(String In, int sz, int len) {
		 
		// starting nGram 
		String S = In.substring(0, sz);
		String val = "";
		
		//	System.out.println( "\n" + In);
		if (lang.containsKey(S) == true) {
			val = lang.get(S);			
		}
		
		// if found the previous entry, update the value		
		if (startMap.containsKey(S) == true) {
			if (val.length() > 0) {
				val += "," + startMap.get(S).toString();
			} else {
				val = startMap.get(S).toString();
			}			
		}
		if (val.length() > 0)
			lang.put(S, val);
		//	System.out.println( S + " -> " + val);		
		
		// get value for ending n.gram
		String E = In.substring((len-sz), len);
		val = "";
		
		if (lang.containsKey(E) == true) {
			val = lang.get(E);			
		}
		
		// if found the previous entry, update the value		
		if (endMap.containsKey(E) == true) {
			if (val.length() > 0) {
				val += "," + endMap.get(E).toString();
			} else {
				val = endMap.get(E).toString();
			}			
		}
		if (val.length() > 0)
		    lang.put(E, val);
	//	System.out.println( E + " -> " + val);					
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////
	// input for this function is as below
    // all -> Nl,0.0024261138067931186,fr,0.0021708455443395203,en,0.002403846153846154,de,0.002599090318388564,Nl,5.513895015438906E-4,fr,5.427113860848801e-4,en,0.0020604395604395605,de,0.00205761316872428
	// output for this function is 
	// Map[fr] = 0.0021708455443395203 
	// but if there is any previous value for this key then sum up the value and not override
	@SuppressWarnings("rawtypes")
	private void ComputeProbability() {
		String val = "";
		Iterator it = lang.entrySet().iterator();
		
		while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	     // System.out.println(pair.getKey() + " = " + pair.getValue());
	        it.remove(); // avoids a Concurrent Modification Exception
	        val = pair.getValue().toString();
	        ComputeProbability(val);
	    }
	}

	///////////////////////////////////////////////////////////////////////////////////
	// function works in connetion with ComputeProbability()
	// updates class level map variable
	private void ComputeProbability(String input) {
		int i=0;
		String key = "";
		Double val = 0.0;
		String[] Arr = input.split(",");
		for (i=0; i < Arr.length; i+=2) {
			key = Arr[i].toString();
			val = Double.parseDouble(Arr[i+1]);
			// System.out.println(input + " -> " + key + "\t" + val);
			
			if (finalLang.containsKey(key)==true) {
				val += (Double)finalLang.get(key);
			}
			finalLang.put(key, val);			
		}		
	}
	
		
	////////////////////////////////////////////////////////////////////
	// we have the results but they are all in unsorted. 
	// for large number of languages, this function becomes imperative
	public static void Sorting(){
		TreeMap<String, Double> sortedMap = SortByValue(finalLang); 
		
		// display the final result
		System.out.println("\n" + sortedMap);

		// in case the calling step of this function is prematurely called on 
		// then proper feed back is required
		
		String Result = "Sorry! Either you have not loaded the nGramMap or\n";
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
	public static TreeMap<String, Double> SortByValue (HashMap<String, Double> map) {
		ValueComparator vc =  new ValueComparator(map);
		TreeMap<String,Double> sortedMap = new TreeMap<String,Double>(vc);
		sortedMap.putAll(map);
		return sortedMap;
	}
} // end of the class


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

	

