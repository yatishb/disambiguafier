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
		String testFile = args[2], statsFile = args[3], answerFile = args[4];
		
		List<String> prevWords = new ArrayList<String>();
		List<String> nextWords = new ArrayList<String>();
		List<String> correctWord = new ArrayList<String>();
		
		List<String> bigramPrecedingWord = new ArrayList<String>();
		List<Integer> bigramWord1 = new ArrayList<Integer>();
		List<Integer> bigramWord2 = new ArrayList<Integer>();
		
		File fileValidation = new File(statsFile); 
		if(!fileValidation.exists()) {
			System.out.print("No file found");
			return;
		}
		BufferedReader fin = new BufferedReader(new FileReader(statsFile));
		String input = fin.readLine();
		while(input != null) {
			String[] stat = input.split(" ");
			stat[0] = stat[0].toLowerCase();
			stat[1] = stat[1].toLowerCase();
			prevWords.add(stat[0]);
			nextWords.add(stat[1]);
			if(Integer.valueOf(stat[3]) > Integer.valueOf(stat[5])){
				correctWord.add(stat[2]);
			} else {
				correctWord.add(stat[4]);
			}
			
			if(bigramPrecedingWord.contains(stat[0]) == false) {
				bigramPrecedingWord.add(stat[0]);
				bigramWord1.add(Integer.valueOf(stat[3]));
				bigramWord2.add(Integer.valueOf(stat[5]));
			} else {
				int index = bigramPrecedingWord.indexOf(stat[0]);
				bigramWord1.set(index, bigramWord1.get(index)+Integer.valueOf(stat[3]));
				bigramWord2.set(index, bigramWord2.get(index)+Integer.valueOf(stat[5]));
			}
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
			String wordBefore = null, wordAfter = null;
			removePunctuation(wordsInArray);
			
			int index = searchWord(wordsInArray, ">>");
			if(index-2 >= 0) {
				wordBefore= wordsInArray[index-2].toLowerCase();
			}
			if(index+2 < wordsInArray.length) {
				wordAfter= wordsInArray[index+1].toLowerCase();
			}
			
			int indexOfPreceding = searchWord(prevWords, nextWords, 
					wordBefore, wordAfter);
			if(indexOfPreceding == -1) {
				/*if(bigramPrecedingWord.contains(wordBefore) == true){
					indexOfPreceding = bigramPrecedingWord.indexOf(wordBefore);
					if(bigramWord1.get(indexOfPreceding) > 
					bigramWord2.get(indexOfPreceding)){
						fout.write(id[0] + " " + word1 + "\n");
					} else {
						fout.write(id[0] + " " + word2 + "\n");
					}
				} else {
					fout.write(id[0] + "\n");
				}*/
			} else {
				fout.write(id[0] + " " + correctWord.get(indexOfPreceding) + "\n");
			}
			
			input = fin.readLine();
		}
		
		fin.close();
		fout.close();
	  }
}
