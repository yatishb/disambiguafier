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
	
	public static int searchWord(List<String> precedingWords, String check) {
		int i = INITIALIZEZERO;
		int limit = precedingWords.size();
		while(i<limit){
			if(precedingWords.get(i).equalsIgnoreCase(check))
				return i;
			i++;
		}
		return INVALIDNEGATIVE;
	}
	
	public static void main(String[] args) throws IOException {
		String word1 = args[0], word2 = args[1];
		String testFile = args[2], statsFile = args[3], answerFile = args[4];
		
		List<String> precedingWords = new ArrayList<String>();
		List<Integer> correctWord = new ArrayList<Integer>();
		int noOfDiffPrecedingWords = INITIALIZEZERO ;
		
		File fileValidation = new File(statsFile); 
		if(!fileValidation.exists()) {
			System.out.print("No file found");
			return;
		}
		BufferedReader fin = new BufferedReader(new FileReader(statsFile));
		String input = fin.readLine();
		while(input != null) {
			String[] stat = input.split(" ");
			precedingWords.add(stat[0]);
			if(stat[1].equalsIgnoreCase(word1)) {
				correctWord.add(1);
			} else {
				correctWord.add(2);
			}
			noOfDiffPrecedingWords++;
			input = fin.readLine();
		}
		fin.close();
		
		fileValidation = new File(testFile);
		if(!fileValidation.exists()) {
			System.out.print("No file found");
			return;
		}
		fin = new BufferedReader(new FileReader(testFile));
		BufferedWriter fout = new BufferedWriter(new FileWriter(answerFile));
		input = fin.readLine();
		while(input != null) {
			String[] id = input.split("\t");
			String[] wordsInArray = id[1].split(" ");
			
			int index = searchWord(wordsInArray, ">>");
			
			String wordBefore = wordsInArray[index-2];
			int indexOfPreceding = searchWord(precedingWords, wordBefore);
			if(indexOfPreceding == -1) {
				fout.write(id[0] + "\n");
			} else {
				int wordToBeUsed = correctWord.get(indexOfPreceding);
				if (wordToBeUsed == 1) {
					fout.write(id[0] + " " + word1 + "\n");
				} else {
					fout.write(id[0] + " " + word2 + "\n");
				}
			}
			
			input = fin.readLine();
		}
		
		fin.close();
		fout.close();
	  }
}
