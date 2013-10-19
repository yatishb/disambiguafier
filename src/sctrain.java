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
					words[i].contains(PUNCTUATION_STOP)) {
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
	
	public static void main(String[] args) throws IOException {
		String word1 = args[0], word2 = args[1];
		String trainingFile = args[2], statsOutputFile = args[3];
		
		//List<surroundingWords> featureList = new ArrayList<surroundingWords>();
		List<String> prevWords = new ArrayList<String>();
		List<String> nextWords = new ArrayList<String>();
		List<Integer> count1 = new ArrayList<Integer>();
		List<Integer> count2 = new ArrayList<Integer>();
		
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
				} else {
					count1.add(0);
					count2.add(1);
				}
			} else {
				if(wordsInArray[index].equalsIgnoreCase(word1)) {
					count1.set(indexIfExists, count1.get(indexIfExists)+1);
				} else {
					count2.set(indexIfExists, count2.get(indexIfExists)+1);
				}
			}
			
			input = fin.readLine();
		}
		
		fin.close();
		int i= INITIALIZEZERO;		
		BufferedWriter fout = new BufferedWriter(new FileWriter(statsOutputFile));
		for(; i<prevWords.size(); i++) {
			String toPrint = prevWords.get(i)+" "+nextWords.get(i);
			toPrint += " "+word1+" "+count1.get(i)+" "+word2+" "+count2.get(i)+"\n";
			
			fout.write(toPrint);
		}
		fout.close();
	  }
}
