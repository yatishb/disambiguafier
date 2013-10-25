import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class sctrain {
	public static final int INITIALIZEZERO = 0;
	public static final int INVALIDNEGATIVE = -1;
	public static final CharSequence PUNCTUATION_STOP = ".";
	public static final CharSequence PUNCTUATION_COMMA = ",";
	public static final CharSequence PUNCTUATION_APOS = "\'";
	public static final CharSequence PUNCTUATION_DPOS = "\"";
	public static final CharSequence PUNCTUATION_SLASH = "\\";
	public static final CharSequence PUNCTUATION_COLON = ":";
	public static final CharSequence PUNCTUATION_SEMICOLON = ";";
	public static final CharSequence PUNCTUATION_UNK = "`";
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
	public static void removePunctuation(Vector<String> words) {
		int i = INITIALIZEZERO;
		int limit = words.size();
		while (i<limit) {
			if(words.get(i).equals(PUNCTUATION_APOS) || 
					words.get(i).equals(PUNCTUATION_COMMA) ||
					words.get(i).equals(PUNCTUATION_STOP) ||
					words.get(i).equals(PUNCTUATION_SLASH) ||
					words.get(i).equals(PUNCTUATION_COLON) ||
					words.get(i).equals(PUNCTUATION_SEMICOLON) ||
					words.get(i).equals(PUNCTUATION_DPOS) ||
					words.get(i).equals(PUNCTUATION_UNK)) {
				words.remove(i);
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
			Vector<String> words) {
		int i = INITIALIZEZERO, j = INITIALIZEZERO;
		for(; i<stopWords.size(); i++) {
			j = INITIALIZEZERO;
			int limit = words.size();
			while (j<limit) {
				if(words.get(j).equalsIgnoreCase(stopWords.get(i))) {
					words.remove(j);
					j--;
					limit--;
				}
				j++;
			}
		}
	}

	/*
	 * Function that generates the required data for collocation
	 * C(-1, 1)
	 */
	private static void collocMinus1To1(String word1, List<String> cminus1to1,
			List<Integer> count1, List<Integer> count2, Vector<String> words, int index) {

		String wordBefore = "", wordAfter = ""; 
		if(index-2 >= 0) {
			wordBefore= words.get(index-2).toLowerCase();
		}
		if(index+2 < words.size()) {
			wordAfter= words.get(index+2).toLowerCase();
		}
		String checkString = wordBefore+" "+wordAfter;
		int indexIfExists = cminus1to1.indexOf(checkString);
		if(indexIfExists == -1) {
			cminus1to1.add(checkString);
			if(words.get(index).equalsIgnoreCase(word1)) {
				count1.add(1);
				count2.add(0);
			} else {
				count1.add(0);
				count2.add(1);
			}
		} else {
			if(words.get(index).equalsIgnoreCase(word1)) {
				count1.set(indexIfExists, count1.get(indexIfExists)+1);
			} else {
				count2.set(indexIfExists, count2.get(indexIfExists)+1);
			}
		}
	}
	
	/*
	 * Function that generates the required data for collocation
	 * C(-2, 2)
	 */
	private static void collocMinus2To2(String word1, List<String> cminus2to2,
			List<Integer> count1, List<Integer> count2, Vector<String> words, 
			int index) {
		String checkString = ""; 
		if(index-3 >= 0) {
			checkString= words.get(index-3).toLowerCase();
		} else {
			checkString = ":";
		}
		if(index-2 >= 0) {
			checkString += " "+words.get(index-2).toLowerCase();
		} else {
			checkString +=" :";
		}
		if(index+2 < words.size()) {
			checkString += " "+words.get(index+2).toLowerCase();
		} else {
			checkString += " :";
		}
		if(index+3 < words.size()) {
			checkString += " "+words.get(index+3).toLowerCase();
		} else {
			checkString += " :";
		}
		int indexIfExists = cminus2to2.indexOf(checkString);
		if(indexIfExists == -1) {
			cminus2to2.add(checkString);
			if(words.get(index).equalsIgnoreCase(word1)) {
				count1.add(1);
				count2.add(0);
			} else {
				count1.add(0);
				count2.add(1);
			}
		} else {
			if(words.get(index).equalsIgnoreCase(word1)) {
				count1.set(indexIfExists, count1.get(indexIfExists)+1);
			} else {
				count2.set(indexIfExists, count2.get(indexIfExists)+1);
			}
		}
	}

	/*
	 * Function that extracts the surrounding words from the training set
	 */
	private static void extractSurroundingWords(List<String> surroundingWord,
			List<Integer> surroundingCount1, List<Integer> surroundingCount2,
			Vector<String> words, int num) {
		int newIndex = words.indexOf(">>")-1;//should give me the word before w
		int i=0;
		while(newIndex-i>=0 && newIndex-i>=newIndex-DIST_SURROUNDING) {
			int exists = surroundingWord.indexOf(words.get(newIndex-i).toLowerCase());
			if(exists == -1) {
				surroundingWord.add(words.get(newIndex-i).toLowerCase());
				if(num == 1){
					surroundingCount1.add(1);
					surroundingCount2.add(0);
				} else {
					surroundingCount1.add(0);
					surroundingCount2.add(1);
				}
			} else {
				if(num == 1) {
					surroundingCount1.set(exists, surroundingCount1.get(exists)+1);
				} else {
					surroundingCount2.set(exists, surroundingCount2.get(exists)+1);
				}
			}
			i++;
		}
		newIndex = words.indexOf(">>")+1;//should give me the word before w
		i=0;
		while(newIndex+i<words.size() && newIndex+i<newIndex+DIST_SURROUNDING) {
			int exists = surroundingWord.indexOf(words.get(newIndex+i).toLowerCase());
			if(exists == -1) {
				surroundingWord.add(words.get(newIndex+i).toLowerCase());
				if(num == 1){
					surroundingCount1.add(1);
					surroundingCount2.add(0);
				} else {
					surroundingCount1.add(0);
					surroundingCount2.add(1);
				}
			} else {
				if(num == 1) {
					surroundingCount1.set(exists, surroundingCount1.get(exists)+1);
				} else {
					surroundingCount2.set(exists, surroundingCount2.get(exists)+1);
				}
			}
			i++;
		}
	}

	
	public static void main(String[] args) throws IOException {
		String word1 = args[0], word2 = args[1];
		String trainingFile = args[2], statsOutputFile = args[3];
		int countWord1 = 0, countWord2 = 0;
		
		List<String> cminus1to1 = new ArrayList<String>();
		List<String> cminus2to2 = new ArrayList<String>();
		List<Integer> count1Colloc1 = new ArrayList<Integer>();
		List<Integer> count2Colloc1 = new ArrayList<Integer>();
		List<Integer> count1Colloc2 = new ArrayList<Integer>();
		List<Integer> count2Colloc2 = new ArrayList<Integer>();
		
		
		//read Stop word file and update stop word list
		List<String> stopWords = new ArrayList<String>();
		readStopWrds(stopWords);
		
		List<String> surroundingWord = new ArrayList<String>();
		List<Integer> surroundingCount1 = new ArrayList<Integer>();
		List<Integer> surroundingCount2 = new ArrayList<Integer>();
		
		File fileValidation = new File(trainingFile);
		if(!fileValidation.exists()) {
			System.out.print("No file found");
			return;
		}
		BufferedReader fin = new BufferedReader(new FileReader(trainingFile));
		String input = fin.readLine();
		while(input != null) {
			String[] id = input.split("\t");
			String[] wordsInArray = id[1].split(" ");
			Vector<String> words = new Vector<String>();
			
			int i=0;
			for(; i<wordsInArray.length;i++){
				words.add(wordsInArray[i]);
			}
			
			/*
			 * Remove punctuations and then determine the values for
			 * the various collocations C(-1,1) and C(-2,2)
			 */
			removePunctuation(words);
			
			int index = words.indexOf(">>")+1;
			collocMinus1To1(word1, cminus1to1, count1Colloc1, count2Colloc1, words, index);
			collocMinus2To2(word1, cminus2to2, count1Colloc2, count2Colloc2, words, index);
			
			if(words.get(index).equalsIgnoreCase(word1)) {
				countWord1++;
				removeStopWords(stopWords, words);
				extractSurroundingWords(surroundingWord, surroundingCount1,
					surroundingCount2, words,1);
			} else {
				countWord2++;
				removeStopWords(stopWords, words);
				extractSurroundingWords(surroundingWord, surroundingCount1,
					surroundingCount2, words,2);
			}
			
			input = fin.readLine();
		}
		fin.close();
		
		//Start writing onto model file
		int i= INITIALIZEZERO;		
		BufferedWriter fout = new BufferedWriter(new FileWriter(statsOutputFile));
		String toPrint = word1+" "+countWord1+"\n";
		fout.write(toPrint);
		toPrint = word2+" "+countWord2+"\n";
		fout.write(toPrint);
		fout.write(cminus2to2.size()+"\n");
		for(; i<cminus2to2.size(); i++) {
			toPrint = cminus2to2.get(i);
			toPrint += " "+word1+" "+count1Colloc2.get(i)+" "+word2+" "+count2Colloc2.get(i)+"\n";
			
			fout.write(toPrint);
		}
		fout.write(cminus1to1.size()+"\n");
		for(i= INITIALIZEZERO; i<cminus1to1.size(); i++) {
			toPrint = cminus1to1.get(i);
			toPrint += " "+word1+" "+count1Colloc1.get(i)+" "+word2+" "+count2Colloc1.get(i)+"\n";
			
			fout.write(toPrint);
		}
		fout.write(surroundingWord.size()+"\n");
		for(i= INITIALIZEZERO; i<surroundingWord.size(); i++) {
			toPrint = surroundingWord.get(i)+" "+word1+" "+surroundingCount1.get(i);
			toPrint += " "+word2+" "+surroundingCount2.get(i)+"\n";
			
			fout.write(toPrint);
		}
		fout.close();
	  }
}
