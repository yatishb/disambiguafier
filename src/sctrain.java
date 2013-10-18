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
	
	public static void removePunctuation(String[] words) {
		int i = INITIALIZEZERO;
		int limit = words.length;
		while (i<limit) {
			if(words[i].contains(PUNCTUATION_APOS) || words[i].contains(PUNCTUATION_COMMA) || words[i].contains(PUNCTUATION_STOP)) {
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
		
		List<String> precedingWords = new ArrayList<String>();
		List<Integer> bigramCountWord1 = new ArrayList<Integer>();
		List<Integer> bigramCountWord2 = new ArrayList<Integer>();
		List<Integer> countPrecedingWords = new ArrayList<Integer>();
		int noOfDiffPrecedingWords = INITIALIZEZERO ;
		
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
			
			int index = searchWord(wordsInArray, ">>");
			
			String wordBefore = wordsInArray[index-2];
			int indexIfExists = searchWord(precedingWords, wordBefore);
			if(indexIfExists == -1) {
				noOfDiffPrecedingWords ++;
				precedingWords.add(noOfDiffPrecedingWords-1, wordBefore);
				if(wordsInArray[index].equalsIgnoreCase(word1)) {
					bigramCountWord1.add(noOfDiffPrecedingWords - 1, 1);
					bigramCountWord2.add(noOfDiffPrecedingWords - 1, 0);
				} else {
					bigramCountWord1.add(noOfDiffPrecedingWords - 1, 0);
					bigramCountWord2.add(noOfDiffPrecedingWords - 1, 1);
				}
			} else {
				if(wordsInArray[index].equalsIgnoreCase(word1)) {
					int previousCount = bigramCountWord1.get(indexIfExists);
					bigramCountWord1.set(indexIfExists, previousCount+1);
				} else {
					int previousCount = bigramCountWord2.get(indexIfExists);
					bigramCountWord2.set(indexIfExists, previousCount+1);
				}
			}
			
			input = fin.readLine();
		}
		
		fin.close();
		int i= INITIALIZEZERO;		
		BufferedWriter fout = new BufferedWriter(new FileWriter(statsOutputFile));
		for(; i<noOfDiffPrecedingWords; i++) {
			fout.write(precedingWords.get(i));
			if(bigramCountWord1.get(i) > bigramCountWord2.get(i)) {
				fout.write(" "+word1+"\n");
			} else {
				fout.write(" "+word2+"\n");
			}
			System.out.print(precedingWords.get(i)+" "+bigramCountWord1.get(i)+" "+bigramCountWord2.get(i)+"\n");
		}
		fout.close();
	  }
}
