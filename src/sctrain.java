import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class surroundingWords {
	private static String precedingWord;
	private static String succedingWord;
	private static int count1;
	private static int count2;
	
	public surroundingWords() {
		count1 = 0;
		count2 = 0;
	}
	
	public String getPrecedingWord() {
		return precedingWord;
	}
	public String getSuccedingWord() {
		return succedingWord;
	}
	public int getCount1() {
		return count1;
	}
	public void incrementCount1() {
		count1++;
	}
	public int getCount2() {
		return count2;
	}
	public void incrementCount2() {
		count2++;
	}
	public void setWords(String previous, String next) {
		precedingWord = previous;
		succedingWord = next;
	}
}


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
	
	public static int searchWord(List<surroundingWords> featureWords, 
			String previous, String next) {
		int i = INITIALIZEZERO;
		int limit = featureWords.size();
		while(i<limit){
			String precedingWord = featureWords.get(i).getPrecedingWord();
			String succedingWord = featureWords.get(i).getSuccedingWord();
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
		
		List<surroundingWords> featureList = new ArrayList<surroundingWords>();
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
			String wordBefore = "", wordAfter = "";
			
			int index = searchWord(wordsInArray, ">>");
			if(index-2 >= 0) {
				wordBefore= wordsInArray[index-2];
			}
			if(index+2 < wordsInArray.length) {
				wordAfter= wordsInArray[index+2];
			}
			int indexIfExists = searchWord(featureList, wordBefore, wordAfter);
			if(indexIfExists == -1) {
				featureList.add(new surroundingWords());
				surroundingWords newFeature = featureList.get(noOfDiffPrecedingWords);
				newFeature.setWords(wordBefore, wordAfter);
				if(wordsInArray[index].equalsIgnoreCase(word1)) {
					newFeature.incrementCount1();
				} else {
					newFeature.incrementCount2();
				}
				featureList.set(noOfDiffPrecedingWords, newFeature);
				noOfDiffPrecedingWords ++;
				System.out.print(newFeature.getPrecedingWord()+" "+newFeature.getSuccedingWord()+"\n");
			} else {
				System.out.print("Coming here");
				surroundingWords existingFeature = featureList.get(indexIfExists);
				if(wordsInArray[index].equalsIgnoreCase(word1)) {
					existingFeature.incrementCount1();
				} else {
					existingFeature.incrementCount2();
				}
				featureList.set(indexIfExists, existingFeature);
			}
			
			input = fin.readLine();
		}
		
		fin.close();
		int i= INITIALIZEZERO;		
		BufferedWriter fout = new BufferedWriter(new FileWriter(statsOutputFile));
		for(; i<noOfDiffPrecedingWords; i++) {
			//System.out.print(i);
			surroundingWords feature = featureList.get(i);
			String toPrint = feature.getPrecedingWord()+"\t"+feature.getSuccedingWord()+"\t"+feature.getCount1()+"\t"+feature.getCount2()+"\n";
			
			/*if(feature.getCount1() > feature.getCount2()) {
				toPrint += "\t"+word1+"\n";
			} else {
				toPrint += "\t"+word2+"\n";
			}*/
			fout.write(toPrint);
			//System.out.print(toPrint);
		}
		fout.close();
	  }
}
