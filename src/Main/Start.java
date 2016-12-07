package Main;

import Dictionary.DetectLanguageByDictionary;
import nGram.DetectLanguageByNgramMap;

public class Start {
  public static void main(String[] args) {
    // to be taken frol the user of the program
    int option;

    // Display menu graphics
    System.out.println("==============================================================");
    System.out.println("|               Language Detection Program                   |");
    System.out.println("|   Muhammad Naeem  (muhammad.naeem@univ.lyon2.fr            |");
    System.out.println("================     Dictionary Based    ====================|");
    System.out.println("| Options:                                                   |");
    System.out.println("|        Press 1 for Building Map from dictionary files.     |");
    System.out.println("|                It will serialize the map first time        |");
    System.out.println("|        Press 2 for running program from serialized map     |");
    System.out.println("|                (optional, if 1 is already pressed)         |");
    System.out.println("|        Press 3 Provide the text file                       |");
    System.out.println("|        Press 4 Provide the text at console                 |");
    System.out.println("================         n-gram Based      ==================|");
    System.out.println("|        Press 5 for Building Map from dictionary files.     |");
    System.out.println("|                It will serialize the map first time        |");
    System.out.println("|        Press 6 Load the serialized map                     |");
    System.out.println("|                (compulsory, even if 5 is already pressed)  |");
    System.out.println("|        Press 7 Provide the text file                       |");
    System.out.println("|        Press 8 Provide the text at console                 |");
    System.out.println("|        Press 0 for exit                                    |");
    System.out.println("==============================================================");
    
    DetectLanguageByDictionary DLD = new DetectLanguageByDictionary(); 
    DetectLanguageByNgramMap DLN = new DetectLanguageByNgramMap();  
	
    while (true) {
    	option = Keyin.inInt(" Select option: ");
	    // Switch construct
	    switch (option) {
	    case 1:
	    {
	    	System.out.println("Building Map from dictionaries");
	    	DLD.Step_1();		      
	      break;
	    }
	    case 2:
	    {
	    	System.out.println("Loading the serialized Map");
	    	int ret = DLD.Step_2();
	    	if (ret == 1)
	    		System.out.println("Program is ready to use for option 3 or 4 ");		      
	      break;
	    }
	    case 3:
	    {
	    	System.out.println("write down the input document file (file.txt)");
	    	System.out.println("file be placed root of this program");
	    	String fn = Keyin.inString();
	    	DLD.Step_3(fn);    	
	    			      
	      break;
	    }

	    case 4:
	    {
	    	System.out.println("write down the text...");
	    	String txt = Keyin.inString();
	    	DLD.Step_4(txt);    	
	    			      
	      break;
	    }

	    ///////////////////////////////////////////////////////////////////////////////
	    
	    case 5:
	    {
	    	System.out.println("Building Map from dictionaries...now load the serialized object");
	    	DLN.Step_1();
	    	System.out.println("Kindly load the serialized object before using 7 or 8 option ");
	    	break;
	    }
	    case 6:
	    {
	    	System.out.println("Loading the serialized Map");
	    	int ret = DLN.Step_2();
	    	if (ret == 1)
	    		System.out.println("Program is ready to use for option 7 or 8 ");		      
	    	break;
	    }
	    case 7:
	    {
	    	System.out.println("write down the input document file (file.txt)");
	    	System.out.println("file be placed root of this program");
	    	String fn = Keyin.inString();
	    	DLN.Step_3(fn);    	
	    	break;
	    }

	    case 8:
	    {
	    	System.out.println("write down the text...");
	    	String txt = Keyin.inString();
	    	DLN.Step_4(txt);    	
	    	break;
	    }


	    case 0:
	    {
	      System.out.println("Exit program");
	      return;
	    }
	    default:
	    {
	      System.out.println("Invalid selection");
	      break;
	    }
	    
	    
	    } // switch
    } // while
    
  } // end of main
} // end of Start class


///////////////////////////////////////////////////////////////////////////
/*
 * Menu Class is taken from this source
 * http://www.java2s.com/Code/Java/Development-Class/Javaprogramtodemonstratemenuselection.htm
 * The menu class is customized according to the requirement
 * Java Programming for Engineers
 * Julio Sanchez
 * Maria P. Canton
 * ISBN: 0849308100
 * Publisher: CRC Press
*/

class Keyin {

  //*******************************
  //   support methods
  //*******************************
  //Method to display the user's prompt string
  public static void printPrompt(String prompt) {
    System.out.print(prompt + " ");
    System.out.flush();
  }

  //Method to make sure no data is available in the
  //input stream
  @SuppressWarnings("unused")
public static void inputFlush() {
    int dummy;
    int bAvail;

    try {
      while ((System.in.available()) != 0)
        dummy = System.in.read();
    } catch (java.io.IOException e) {
      System.out.println("Input error");
    }
  }

  //********************************
  //  data input methods for
  //string, int, char, and double
  //********************************
  public static String inString(String prompt) {
    inputFlush();
    printPrompt(prompt);
    return inString();
  }

  public static String inString() {
    int aChar;
    String s = "";
    boolean finished = false;

    while (!finished) {
      try {
        aChar = System.in.read();
        if (aChar < 0 || (char) aChar == '\n')
          finished = true;
        else if ((char) aChar != '\r')
          s = s + (char) aChar; // Enter into string
      }

      catch (java.io.IOException e) {
        System.out.println("Input error");
        finished = true;
      }
    }
    return s;
  }

  public static int inInt(String prompt) {
    while (true) {
      inputFlush();
      printPrompt(prompt);
      try {
        return Integer.valueOf(inString().trim()).intValue();
      }

      catch (NumberFormatException e) {
        System.out.println("Invalid input. Not an integer");
      }
    }
  }

  public static char inChar(String prompt) {
    int aChar = 0;

    inputFlush();
    printPrompt(prompt);

    try {
      aChar = System.in.read();
    }

    catch (java.io.IOException e) {
      System.out.println("Input error");
    }
    inputFlush();
    return (char) aChar;
  }

  public static double inDouble(String prompt) {
    while (true) {
      inputFlush();
      printPrompt(prompt);
      try {
        return Double.valueOf(inString().trim()).doubleValue();
      }

      catch (NumberFormatException e) {
        System.out
            .println("Invalid input. Not a floating point number");
      }
    }
  }
}
           
         