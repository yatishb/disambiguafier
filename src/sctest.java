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
		int countWord1 = 0, countWord2 = 0, numCollocation = 0, numSurrounding = 0;
		String testFile = args[2], statsFile = args[3], answerFile = args[4];
		
		List<String> prevWords = new ArrayList<String>();
		List<String> nextWords = new ArrayList<String>();
		List<String> correctWord = new ArrayList<String>();
		
		List<String> bigramPrecedingWord = new ArrayList<String>();
		List<Integer> bigramWord1 = new ArrayList<Integer>();
		List<Integer> bigramWord2 = new ArrayList<Integer>();
		
		List<String> surroundingWord = new ArrayList<String>();
		List<Integer> surroundingCount1 = new ArrayList<Integer>();
		List<Integer> surroundingCount2 = new ArrayList<Integer>();
		int numSurrounding1 = 0, numSurrounding2 = 0;
		
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
				System.out.print(numCollocation);
			} else if(counter <= 2+numCollocation) {
				stat[0] = stat[0].toLowerCase();
				stat[1] = stat[1].toLowerCase();
				prevWords.add(stat[0]);
				nextWords.add(stat[1]);
				if(Integer.valueOf(stat[3]) > Integer.valueOf(stat[5])){
					correctWord.add(stat[2]);
				} else if(Integer.valueOf(stat[3]) < Integer.valueOf(stat[5])) {
					correctWord.add(stat[4]);
				} else if(countWord1 > countWord2) {
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
			} else if(counter == 3+numCollocation){
				numSurrounding = Integer.parseInt(stat[0]);
			} else if(counter <= 3+numSurrounding+numCollocation){
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
			
			String word = getCorrectWord(word1, word2, prevWords, 
					nextWords, correctWord, wordBefore, wordAfter);
			removeStopWords(stopWords, wordsInArray);
			
			if(word.equals("")){
				int newIndex = searchWord(wordsInArray, ">>")-2;//should give me the word before w
				int i=0;
				float prob1 = 0, prob2 = 0;
				while(newIndex-i>=0) {
					int exists = surroundingWord.indexOf(wordsInArray[newIndex-i].toLowerCase());
					if(exists != -1) {
						if(prob1 == 0){
							//System.out.println(surroundingCount1.get(exists)/numSurrounding1);
							prob1 = surroundingCount1.get(exists);///numSurrounding1;
							prob2 = surroundingCount2.get(exists);///numSurrounding2;
						} else {
							prob1 += surroundingCount1.get(exists)/numSurrounding1;
							prob2 += surroundingCount2.get(exists)/numSurrounding2;
							/*prob1 *= surroundingCount1.get(exists)/numSurrounding1;
							prob2 *= surroundingCount2.get(exists)/numSurrounding2;*/
						}
					}
					i++;
				}
				newIndex = searchWord(wordsInArray, ">>")+1;//should give me the word before w
				while(newIndex<wordsInArray.length) {
					int exists = surroundingWord.indexOf(wordsInArray[newIndex].toLowerCase());
					if(exists != -1) {
						if(prob1 == 0){
							//System.out.println(surroundingCount1.get(exists)/numSurrounding1);
							prob1 = surroundingCount1.get(exists);///numSurrounding1;
							prob2 = surroundingCount2.get(exists);///numSurrounding2;
						} else {
							prob1 += surroundingCount1.get(exists)/numSurrounding1;
							prob2 += surroundingCount2.get(exists)/numSurrounding2;
							/*prob1 *= surroundingCount1.get(exists)/numSurrounding1;
							prob2 *= surroundingCount2.get(exists)/numSurrounding2;*/
						}
					}
					newIndex++;
				}
				
				if(prob1>prob2){
					word = word1;
				} else if(prob2>prob1) {
					word = word2;
				} else {
					word = extractUsingBigram(word1, word2,
							bigramPrecedingWord, bigramWord1, bigramWord2,
							wordBefore);
				}
			}
			fout.write(id[0] + " " + word + "\n");
			
			input = fin.readLine();
		}
		
		fin.close();
		fout.close();
		System.out.print(numSurrounding1);
	  }

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
			return "";
		}
	}

	private static String getCorrectWord(String word1, String word2,
			List<String> prevWords, List<String> nextWords,
			List<String> correctWord, String wordBefore, String wordAfter) 
					throws IOException {
		
		int indexOfPreceding = searchWord(prevWords, nextWords, 
				wordBefore, wordAfter);
		if(indexOfPreceding == -1) {
			return "";			
		} else {
			return(correctWord.get(indexOfPreceding));
		}
	}
}
