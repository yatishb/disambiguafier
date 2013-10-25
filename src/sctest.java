import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class sctest {
	public static final int INITIALIZEZERO = 0;
	public static final int INVALIDNEGATIVE = -1;
	public static final CharSequence PUNCTUATION_STOP = ".";
	public static final CharSequence PUNCTUATION_COMMA = ",";
	public static final CharSequence PUNCTUATION_APOS = "\'";
	public static final CharSequence PUNCTUATION_DQUOTES = "\"";
	public static final CharSequence PUNCTUATION_COLON = ":";
	public static final CharSequence PUNCTUATION_SEMICOLON = ";";
	public static final int DIST_SURROUNDING = 5;
	
	
	/*
	 * Function to search for a particular word in an array of Strings
	 * and return its index+1
	 */
	public static int searchWord(String[] words, String check) {
		int i = INITIALIZEZERO;
		int limit = words.length;
		while(i<limit){
			if(words[i].equalsIgnoreCase(check))
				return i+1;
			i++;
		}
		return INVALIDNEGATIVE;
	}
	
	/*
	 * Function to remove the punctuation symbols from the array of
	 * words that form part of the sentence
	 */
	public static void removePunctuation(String[] words) {
		int i = INITIALIZEZERO;
		int limit = words.length;
		while (i<limit) {
			if(words[i].contains(PUNCTUATION_APOS) || 
					words[i].contains(PUNCTUATION_COMMA) ||
					words[i].contains(PUNCTUATION_STOP) ||
					words[i].contains(PUNCTUATION_DQUOTES) ||
					words[i].contains(PUNCTUATION_COLON) ||
					words[i].contains(PUNCTUATION_SEMICOLON)) {
				int j=i;
				for(j=i; j<limit-1; j++) {
					words[j] = words[j+1]; 
				}
				i--;
				limit--;
			}
			i++;
		}
	}
	
	/*
	 * Function that reads the stop words from a file
	 * and stores them into a list containing all the stop words
	 */
	public static void readStopWrds(List<String> stopWords) 
			throws IOException {
		BufferedReader fin = new BufferedReader(new FileReader("stopwd.txt"));
		String input = fin.readLine();
		while(input!=null){
			stopWords.add(input);
			input = fin.readLine();
		}
		fin.close();
	}
	
	/*
	 * Function that removes the stop words from the array of words
	 * that are part of the sentence
	 */
	public static void removeStopWords(List<String> stopWords, 
			String[] words) {
		int i = INITIALIZEZERO, j = INITIALIZEZERO;
		for(; i<stopWords.size(); i++) {
			j = INITIALIZEZERO;
			int limit = words.length;
			while (j<limit) {
				if(words[j].equalsIgnoreCase(stopWords.get(i))) {
					int k=j;
					for(k=j; k<limit-1; k++) {
						words[k] = words[k+1]; 
					}
					j--;
					limit--;
				}
				j++;
			}
		}
	}
	
	/*
	 * Function that returns the ambiguous word that is to be used
	 * when only considering the preceding word to the ambiguous
	 * word
	 */
	private static String extractUsingBigram(String word1, String word2,
			List<String> bigramPrecedingWord, List<Integer> bigramWord1,
			List<Integer> bigramWord2, String wordBefore) {
		int indexOfPreceding;
		if(bigramPrecedingWord.contains(wordBefore) == true){
			indexOfPreceding = bigramPrecedingWord.indexOf(wordBefore);
			if(bigramWord1.get(indexOfPreceding) > 
			bigramWord2.get(indexOfPreceding)){
				return word1;
			} else {
				return word2;
			}
		} else {
			int num = (int) (Math.random()*2);
			if(num == 0)
				return word1;
			else
				return word2;
		}
	}

	/*
	 * Function that returns the ambiguous word that matches the 
	 * collocation C(-1,1) that has been stored in the training/model file
	 */
	private static String getCorrectWord(String word1, String word2,
			List<String> cminus1to1, List<String> correctWord, 
			String wordBefore, String wordAfter) throws IOException {
		
		int indexOfPreceding = cminus1to1.indexOf(wordBefore+" "+wordAfter);
		if(indexOfPreceding == -1) {
			return "";			
		} else {
			return(correctWord.get(indexOfPreceding));
		}
	}
	
	/*
	 * Function that returns the ambiguous word that matches the 
	 * collocation C(-2,2) that has been stored in the training/model file
	 */
	private static String getCorrectWord(String word1, String word2,
			List<String> cminus2to2, List<String> correctWord, 
			String wordBefore2, String wordBefore1, String wordAfter1, 
			String wordAfter2) throws IOException {
		
		int indexOfPreceding = cminus2to2.indexOf(wordBefore2+" "+wordBefore1+
				" "+wordAfter1+" "+wordAfter2);
		if(indexOfPreceding == -1) {
			return "";			
		} else {
			return(correctWord.get(indexOfPreceding));
		}
	}
	
	
	public static void main(String[] args) throws IOException {
		String word1 = args[0], word2 = args[1];
		double countWord1 = 0, countWord2 = 0, numCollocation = 0, numSurrounding = 0;
		String testFile = args[2], statsFile = args[3], answerFile = args[4];
		
		List<String> cminus1to1 = new ArrayList<String>();
		List<String> cminus2to2 = new ArrayList<String>();
		List<String> correctWordColloc1 = new ArrayList<String>();
		List<String> correctWordColloc2 = new ArrayList<String>();
		
		List<String> bigramPrecedingWord = new ArrayList<String>();
		List<Integer> bigramWord1 = new ArrayList<Integer>();
		List<Integer> bigramWord2 = new ArrayList<Integer>();
		
		List<String> surroundingWord = new ArrayList<String>();
		List<Integer> surroundingCount1 = new ArrayList<Integer>();
		List<Integer> surroundingCount2 = new ArrayList<Integer>();
		double numSurrounding1 = 0, numSurrounding2 = 0;
		
		/*
		 * Read from the model file and generate the required lists
		 */
		File fileValidation = new File(statsFile); 
		if(!fileValidation.exists()) {
			System.out.print("No file found");
			return;
		}
		BufferedReader fin = new BufferedReader(new FileReader(statsFile));
		String input = fin.readLine();
		int counter = 0;
		while(input != null) {
			String[] stat = input.split(" ");
			if(counter<2){
				if(word1.equalsIgnoreCase(stat[0])) {
					countWord1 = Integer.parseInt(stat[1]);
				} else {
					countWord2 = Integer.parseInt(stat[1]);
				}
			} else if(counter == 2) {
				numCollocation = Integer.parseInt(stat[0]);
			} else if(counter <= 2+numCollocation) {
				//acquiring collocation C(-2,2) data
				stat[0] = stat[0].toLowerCase();
				stat[1] = stat[1].toLowerCase();
				cminus2to2.add(stat[0]+" "+stat[1]+" "+stat[2]+" "+stat[3]);
				if(Integer.valueOf(stat[5]) > Integer.valueOf(stat[7])){
					correctWordColloc2.add(stat[4]);
				} else if(Integer.valueOf(stat[5]) < Integer.valueOf(stat[7])) {
					correctWordColloc2.add(stat[6]);
				} else if(countWord1 > countWord2) {
					correctWordColloc2.add(stat[4]);
				} else {
					correctWordColloc2.add(stat[6]);
				}
			} else if(counter == 3+cminus2to2.size()) {
				numCollocation = Integer.parseInt(stat[0]);				
			} else if(counter <= 3+cminus2to2.size()+numCollocation) {
				//acquiring collocation C(-1,1) data
				stat[0] = stat[0].toLowerCase();
				stat[1] = stat[1].toLowerCase();
				cminus1to1.add(stat[0]+" "+stat[1]);
				if(Integer.valueOf(stat[3]) > Integer.valueOf(stat[5])){
					correctWordColloc1.add(stat[2]);
				} else if(Integer.valueOf(stat[3]) < Integer.valueOf(stat[5])) {
					correctWordColloc1.add(stat[4]);
				} else if(countWord1 > countWord2) {
					correctWordColloc1.add(stat[2]);
				} else {
					correctWordColloc1.add(stat[4]);
				}
				
				//calculating preceding word data
				if(bigramPrecedingWord.contains(stat[0]) == false) {
					bigramPrecedingWord.add(stat[0]);
					bigramWord1.add(Integer.valueOf(stat[3]));
					bigramWord2.add(Integer.valueOf(stat[5]));
				} else {
					int index = bigramPrecedingWord.indexOf(stat[0]);
					bigramWord1.set(index, bigramWord1.get(index)+Integer.valueOf(stat[3]));
					bigramWord2.set(index, bigramWord2.get(index)+Integer.valueOf(stat[5]));
				}
			} else if(counter == 4+cminus1to1.size()+cminus2to2.size()){
				numSurrounding = Integer.parseInt(stat[0]);
			} else if(counter <= 4+cminus1to1.size()+cminus2to2.size()+numCollocation){
				//Surrounding Words upto distance 5 data
				stat[0] = stat[0].toLowerCase();
				surroundingWord.add(stat[0]);
				surroundingCount1.add(Integer.parseInt(stat[2]));
				surroundingCount2.add(Integer.parseInt(stat[4]));
				numSurrounding1 += Integer.parseInt(stat[2]);
				numSurrounding2 += Integer.parseInt(stat[4]);
			}
			counter++;
			input = fin.readLine();
		}
		fin.close();
		
		fileValidation = new File(testFile);
		if(!fileValidation.exists()) {
			System.out.print("No file found");
			return;
		}
		
		//read Stop word file and update stop word list
		List<String> stopWords = new ArrayList<String>();
		readStopWrds(stopWords);
				
		/*
		 * Start reading the test file and using the interpreted data
		 * from the model file use the following methods in the order
		 * of priority:
		 * 1. Collocation C(-2,2)
		 * 2. Collocation C(-1,1)
		 * 3. Surrounding words upto a distance of 5 words
		 * 4. Preceding words to the ambiguous word
		 */
		fin = new BufferedReader(new FileReader(testFile));
		BufferedWriter fout = new BufferedWriter(new FileWriter(answerFile));
		input = fin.readLine();
		while(input != null) {
			String[] id = input.split("\t");
			String[] wordsInArray = id[1].split(" ");
			String wordBefore1 = ":", wordAfter1 = ":", wordBefore2 = ":", wordAfter2 = ":";
			
			int index = searchWord(wordsInArray, ">>");
			if(index-2 >= 0) {
				wordBefore1= wordsInArray[index-2].toLowerCase();
			}
			if(index+1 < wordsInArray.length) {
				wordAfter1= wordsInArray[index+1].toLowerCase();
			}
			if(index-3 >= 0) {
				wordBefore2= wordsInArray[index-3].toLowerCase();
			}
			if(index+2 < wordsInArray.length) {
				wordAfter2= wordsInArray[index+2].toLowerCase();
			}
			
			String word = getCorrectWord(word1, word2, cminus2to2, 
					correctWordColloc2, wordBefore2, wordBefore1, 
					wordAfter1, wordAfter2);
			if(word.equals("")){
				word = getCorrectWord(word1, word2, cminus1to1, 
					correctWordColloc1, wordBefore1, wordAfter1);
			}
			//removeStopWords(stopWords, wordsInArray);
			
			if(word.equals("")){
				int newIndex = searchWord(wordsInArray, ">>")-2;//should give me the word before w
				int i=0;
				double prob1 = countWord1/(countWord1+countWord2);
				double prob2 = countWord2/(countWord1+countWord2);
				while(newIndex-i>=0 && newIndex-i>=newIndex-DIST_SURROUNDING) {
					int exists = surroundingWord.indexOf(wordsInArray[newIndex-i].toLowerCase());
					if(exists != -1 && surroundingCount1.get(exists)!=0 
							&& surroundingCount2.get(exists)!=0) {
						prob1 *= (surroundingCount1.get(exists)/countWord1);
						prob2 *= surroundingCount2.get(exists)/countWord2;
					}
					i++;
				}
				i=0;
				newIndex = searchWord(wordsInArray, ">>")+1;//should give me the word before w
				while(newIndex+i<wordsInArray.length && newIndex+i<newIndex+DIST_SURROUNDING) {
					int exists = surroundingWord.indexOf(wordsInArray[newIndex+i].toLowerCase());
					if(exists != -1 && surroundingCount1.get(exists)!=0 
							&& surroundingCount2.get(exists)!=0) {
						prob1 *= (surroundingCount1.get(exists)/countWord1);
						prob2 *= surroundingCount2.get(exists)/countWord2;
					}
					i++;
				}
				if(prob1>prob2){
					word = word1;
				} else if(prob2>prob1) {
					word = word2;
				} else {
					word = extractUsingBigram(word1, word2,
							bigramPrecedingWord, bigramWord1, bigramWord2,
							wordBefore1);
				}
			}
			fout.write(id[0] + " " + word + "\n");
			input = fin.readLine();
		}
		fin.close();
		fout.close();
	  }

}
