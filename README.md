# languageDetector
Report (Natural Language Detection)
Muhammad Naeem (Muhammad.Naeem@univ-lyon2.fr)

Program Name:  languageDetector
Programming Environment: Java (J2SE), Maven, junit: 4.11  
Natural Languages Support:  German, French, English, Dutch
This feature is absolutely flexible independent of change in code. One needs to add up a dictionary file in the data folder and the system will incorporate it. The format of the dictionary file is discussed later in this report. This program uses no external jar. The program is developed solely by means of java native libraries. 

Summary
This program is used to detect the language of the provided text file or 'text at console'. The program has implemented two popular classes of concepts. These include dictionary based detection and pattern mining technique using n-gram. 

Detection by Dictionary 
The program first develops a model from the dictionary files. Each dictionary file is supposed to contain one word per line. If the dictionary contains duplicate values then preprocess module of this program can be used to eliminate them all. Currently, I got four dictionary files in German, French, English and Dutch language only. The size and data quality of all of these dictionary files is same. This justifies that there is no hidden bias in functionality of the program. Each file is composed of top ten thousand words. These files are available in the package folder data. http://www.streetsmartlanguagelearning.com/2008/12/top-10000-words-in-dutch-english-french.html. The data contains duplicate in terms of Al and al. I have converted every one into lower case.  The keyset of the map is comprised of single words of the dictionary file. The value of the corresponding key is an abbreviated two letters character (De, Fr, En, Nl). This value is useful to identify the type of the key. If the word is present in more than one language then value shows all of the language separated by comma. Whenever an input text is fed to the system, the system tokenizes the sentence into an array of words. Each word is assessed for its language identification individually. Each word is given a probability score according to the number of languages it is found in. The probability for each word is summed up and then normalized on same scale. The probability for all of the languages is sorted out in descending order. The highest value identifies the language of the sentence.

Example
Sentence:   in natural language processing, that's all
Each word belongs to the following languages
in:	De,En,Fr,Nl	
De = 0.25	
En = 0.25	
Fr = 0.25	
Nl = 0.25	

natural:	En	
En = 1.25	

language:	En	
En = 2.25	

processing:	En	
En = 3.25	

that's:	En	
En = 4.25	

all:	De,En,Nl	
De = 0.5833333333333333	
En = 4.583333333333333	
Nl = 0.5833333333333333	

The values at final words for every language were
{ De=0.5833333333333333, Nl=0.5833333333333333, En=4.583333333333333, Fr=0.25}

but after dividing each value by 6 (total words in given sentence), it is normalized. Moreover after nomalization, it is sorted in descending order as given below.

{En=0.7638888888888888, Nl=0.09722222222222221, De=0.09722222222222221, Fr=0.041666666666666664}
+++++++++++++++++++++++++++++++++
****	En	****
++++++++++++++++++++++++++++++++




Detection by n-gram 
We know that every language has some unique patterns. These unique patterns are characterized by the combination of the starting letters or ending letters. These letters may be 2-gram, 3-gram or 4-gram. We have exploited this class of patterns in this technique. The idea in this implementation is persuasive and straightforward. We already have raw dictionaries where each dictionary posses around ten thousand most frequent words. We found out the frequency of each of the bi-gram , tri-gram and quad-gram both in ending as well as starting pattern. It goes without say that the same n-gram may be observant in other language as well but the difference is marked by the its corresponding frequency. The Map contains the detail of all of these  n-grams with its specific probability or herein likelihood scoring value. Here it is very important to normalize every frequency of the n-gram with following rules.
Quad-grams are normalized by the count of the words with the count of the letters greater than three.
Tri-grams are normalized by the count of the words with the count of the letters greater than two.
Bi-grams are normalized by the count of the words with the count of the letters greater than one. 
When a word is provide, it is tokenized into words. The program extracts all of the n-gram and then compares these n-grams to the map. Each n-gram of the word is searched into the map and its corresponding frequency value is also calculated. The preliminary results of the experiment have shown that this technique is more useful when the underlying dictionary is rich and the input text is also large.  


Run the Program


Run Main.Start in eclipse or run the jar file provided in the package folder run-by-jar
Java –jar langDetector.jar


==============================================================
|               Language Detection Program                   |
|   Muhammad Naeem  (muhammad.naeem@univ.lyon2.fr)           |
================     Dictionary Based    ====================|
| Options:                                                   |
|        Press 1 for Building Map from dictionary files.     |
|                It will serialize the map first time        |
|        Press 2 for running program from serialized map     |
|                (optional, if 1 is already pressed)         |
|        Press 3 Provide the text file                       |
|        Press 4 Provide the text at console                 |
================         nGramy Based      ==================|
|        Press 5 for Building Map from dictionary files.     |
|                It will serialize the map first time        |
|        Press 6 Load the serialized map                     |
|                (compulsory, even if 5 is already pressed)  |
|        Press 7 Provide the text file                       |
|        Press 8 Provide the text at console                 |
|        Press 0 for exit                                    |
==============================================================
 Select option:  1
Building Map from dictionaries
Serializing the Map for future use
 Select option:  2
Loading the serialized Map
Program is ready to use for option 3 or 4 
 Select option:  3
write down the input document file (file.txt)
file be placed root of this program
1.txt


{En=0.7447916666666667, Nl=0.08854166666666666, De=0.08854166666666666, Fr=0.078125}
+++++++++++++++++++++++++++++++++
****	En	****
++++++++++++++++++++++++++++++++


 Select option:  4
write down the text...
hello world how are you


{En=0.5625, Nl=0.3125, Fr=0.0625, De=0.0625}
+++++++++++++++++++++++++++++++++
****	En	****
++++++++++++++++++++++++++++++++


 Select option:  5
Building Map from dictionaries...now load the serialized object
Serializing the nGram Map for future use
Kindly load the serialized object before using 7 or 8 option 
 Select option:  6
Loading the serialized Map
Program is ready to use for option 7 or 8 
 Select option:  7
write down the input document file (file.txt)
file be placed root of this program
2.txt


{fr=2.4395344451750685, en=1.5544222370693488, de=1.320602042334755, Nl=1.2333629655600278, Fr=0.1257941913858479, En=0.003369679774073005}
+++++++++++++++++++++++++++++++++
****	fr	****
++++++++++++++++++++++++++++++++


 Select option:  8
write down the text...
bonjour tout a tous a mond


{fr=0.08068253177758063, Nl=0.0771150572527724, en=0.06109461454099102, de=0.04210584886447267, Fr=0.0020837707609010787}
+++++++++++++++++++++++++++++++++
****	fr	****
++++++++++++++++++++++++++++++++


 Select option:  0
Exit program


Note: System is tested at ubuntu 12.04 and Windows 7
============    END  =============
