package Test;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


import Dictionary.BuildDictionaryMap;
import Dictionary.DetectLanguageByDictionary;



public class TestDictionary {
	
	static BuildDictionaryMap BD = new BuildDictionaryMap();
	static DetectLanguageByDictionary DD = new DetectLanguageByDictionary();	
	
    @BeforeClass
    public static void TestBeforeClass() {        
    	System.out.println("Size of Map: " + BuildDictionaryMap.map.size() + "\n");
    }

    @AfterClass
    public static void TestLeakage() {
    	// leadkage test
    	System.out.println("Size of Map: " + BuildDictionaryMap.map.size() + "\n");
    }

  //"@Before method will execute before every JUnit4 test"
    @Before
    public void Execution() {
        System.out.println("Size of Map: " + BuildDictionaryMap.map.size() + "\n");        
    }

    //"@After method will execute after every JUnit4 test"
    @After
    public void tearDown() {        
        System.out.println("Size of Map: " + BuildDictionaryMap.map.size() + "\n");        
    }
    

    @Test
    public void TestRoutineFunctions() {
        assertTrue((BuildDictionaryMap.map.size() > 0));       
    }

    
    @Test
    public void TestSerilize() {        
        assertEquals("Serialize must return 1", 1, BD.Serialize());
    }
    
    @Test(timeout = 100)  
	public void infinity() {  
    	BD.DevelopModel();  
	}  
	
    @Test
    public void TestStep_1_2() {
    	DD.Step_1();
    	int ret = DD.Step_2();    	
        assertEquals("Serialize must return 1", 1 , ret);
    }
    
    @Test(timeout = 400)  
    public void TestSteps_time() {
    	System.out.println("Time of execution test for all of the step in caller of dictionary class");
    	DD.Step_1();
    	DD.Step_2();
    	//DD.Step_3("1.txt");
    	DD.Step_4("bonjour tous a tout a monde");
    }
    
    
    
}
