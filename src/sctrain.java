import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class sctrain {
	public static final int INITIALIZEZERO = 0;
	public static final int INVALIDNEGATIVE = -1;
	public static final CharSequence PUNCTUATION_STOP = ".";
	public static final CharSequence PUNCTUATION_COMMA = ",";
	public static final CharSequence PUNCTUATION_APOS = "\'";
	public static final CharSequence PUNCTUATION_DQUOTES = "\"";
	public static final CharSequence PUNCTUATION_COLON = ":";
	public static final CharSequence PUNCTUATION_SEMICOLON = ";";
	
	
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
	
	public static int searchWord(List<String> prevWords, List<String> nextWords,
			String previous, String next) {
		int i = INITIALIZEZERO;
		int limit = prevWords.size();
		while(i<limit){
			String precedingWord = prevWords.get(i);
			String succedingWord = nextWords.get(i);
			if(precedingWord.equalsIgnoreCase(previous) && 
					succedingWord.equalsIgnoreCase(next))
				return i;
			i++;
		}
		return INVALIDNEGATIVE;
	}
	
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
	
	public static void main(String[] args) throws IOException {
		String word1 = args[0], word2 = args[1];
		String trainingFile = args[2], statsOutputFile = args[3];
		int countWord1 = 0, countWord2 = 0;
		
		List<String> prevWords = new ArrayList<String>();
		List<String> nextWords = new ArrayList<String>();
		List<Integer> count1 = new ArrayList<Integer>();
		List<Integer> count2 = new ArrayList<Integer>();
		
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
			String wordBefore = "", wordAfter = "";
			
			removePunctuation(wordsInArray);
			
			int index = searchWord(wordsInArray, ">>");
			if(index-2 >= 0) {
				wordBefore= wordsInArray[index-2].toLowerCase();
			}
			if(index+2 < wordsInArray.length) {
				wordAfter= wordsInArray[index+2].toLowerCase();
			}
			int indexIfExists = searchWord(prevWords, nextWords, wordBefore, wordAfter);
			if(indexIfExists == -1) {
				nextWords.add(wordAfter);
				prevWords.add(wordBefore);
				if(wordsInArray[index].equalsIgnoreCase(word1)) {
					count1.add(1);
					count2.add(0);
					countWord1++;
					removeStopWords(stopWords, wordsInArray);
					extractSurroundingWords(surroundingWord, surroundingCount1,
							surroundingCount2, wordsInArray,1);
				} else {
					count1.add(0);
					count2.add(1);
					countWord2++;
					removeStopWords(stopWords, wordsInArray);
					extractSurroundingWords(surroundingWord, surroundingCount1,
							surroundingCount2, wordsInArray,2);
				}
			} else {
				if(wordsInArray[index].equalsIgnoreCase(word1)) {
					count1.set(indexIfExists, count1.get(indexIfExists)+1);
					countWord1++;
					removeStopWords(stopWords, wordsInArray);
					extractSurroundingWords(surroundingWord, surroundingCount1,
							surroundingCount2, wordsInArray,1);
				} else {
					count2.set(indexIfExists, count2.get(indexIfExists)+1);
					countWord2++;
					removeStopWords(stopWords, wordsInArray);
					extractSurroundingWords(surroundingWord, surroundingCount1,
							surroundingCount2, wordsInArray,2);
				}
			}
			
			
			input = fin.readLine();
		}
		
		fin.close();
		int i= INITIALIZEZERO;		
		BufferedWriter fout = new BufferedWriter(new FileWriter(statsOutputFile));
		String toPrint = word1+" "+countWord1+"\n";
		fout.write(toPrint);
		toPrint = word2+" "+countWord2+"\n";
		fout.write(toPrint);
		fout.write(prevWords.size()+"\n");
		for(; i<prevWords.size(); i++) {
			toPrint = prevWords.get(i)+" "+nextWords.get(i);
			toPrint += " "+word1+" "+count1.get(i)+" "+word2+" "+count2.get(i)+"\n";
			
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

	private static void extractSurroundingWords(List<String> surroundingWord,
			List<Integer> surroundingCount1, List<Integer> surroundingCount2,
			String[] wordsInArray, int num) {
		int newIndex = searchWord(wordsInArray, ">>")-2;//should give me the word before w
		int i=0;
		while(newIndex-i>=0) {
			int exists = surroundingWord.indexOf(wordsInArray[newIndex-i].toLowerCase());
			if(exists == -1) {
				surroundingWord.add(wordsInArray[newIndex-i].toLowerCase());
				if(num == 1){
					surroundingCount1.add(2);
					surroundingCount2.add(1);
				} else {
					surroundingCount1.add(1);
					surroundingCount2.add(2);
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
		newIndex = searchWord(wordsInArray, ">>")+2;//should give me the word before w
		while(newIndex<wordsInArray.length) {
			int exists = surroundingWord.indexOf(wordsInArray[newIndex].toLowerCase());
			if(exists == -1) {
				surroundingWord.add(wordsInArray[newIndex].toLowerCase());
				if(num == 1){
					surroundingCount1.add(2);
					surroundingCount2.add(1);
				} else {
					surroundingCount1.add(1);
					surroundingCount2.add(2);
				}
			} else {
				if(num == 1) {
					surroundingCount1.set(exists, surroundingCount1.get(exists)+1);
				} else {
					surroundingCount2.set(exists, surroundingCount2.get(exists)+1);
				}
			}
			newIndex++;
		}
	}
}
