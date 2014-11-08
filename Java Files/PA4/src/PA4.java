// PA3 Assignment
// Author: Alex Eibner and Jacob Rede
// Date:   9/17/2014
// Class:  CS200
// Email:  alexeibner@gmail.com	and jare1987@rams.colostate.edu

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class PA4 {

	public static void main(String[] args) {
		
		int numStopWords = 0;
		int tableSize = 0;
		ArrayList<String> fileNameList = new ArrayList();
		ArrayList<String> whichPagesWordList = new ArrayList();
		ArrayList<String> pruneStopWords = new ArrayList();
		try {
			
			Scanner fileScanner = new Scanner(new File(args[0]));
			
			//loop reads first int
				while (fileScanner.hasNextInt()) {
					tableSize = fileScanner.nextInt();
					break;	
				} 
			
			// *******************************************************
			// Loop reads only file names.
			while (fileScanner.hasNext()) {
				
				String readValue = fileScanner.next();
				
				if (readValue.equals("*EOFs*")) {
					
					break;
					
				} else {
					
					fileNameList.add(readValue);
					
				}
				
			}
	//###########################START OLD PA3 STUFF###################3		
			// *******************************************************
//			// Reads only the integer that comes after *EOFs*. 
//			if (fileScanner.hasNextInt()) {
//				
//				numStopWords = fileScanner.nextInt();
//				
//			}
			
			// *******************************************************
			// Reads the rest of the words for the WhichPages method.
		//###########################END OLD PA3 STUFF###################3	
			//reads in everything after EOF
			boolean stopTrigger = false;
			while (fileScanner.hasNext()) {
				String nextString = fileScanner.next();
				if (!nextString.equals("*STOPs*") && !stopTrigger) {
					pruneStopWords.add(nextString);
					
				} else if (nextString.equals("*STOPs*")) {
					stopTrigger = true;
				} else if (stopTrigger) {
					whichPagesWordList.add(nextString);
				}
			}
			
			fileScanner.close();
			
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
			
		}
		
		
		
		// **********************************************************************************************************
		// **********************************************************************************************************
		// **********************************************************************************************************
		
		WebPages webPageObject = new WebPages(tableSize);
		for (int j = 0; j < fileNameList.size(); j++) {
			webPageObject.addPage(fileNameList.get(j));
		}
		for (int j = 0; j < pruneStopWords.size(); j++) {
			//System.out.println(pruneStopWords.get(j));
			webPageObject.pruneStopWords(pruneStopWords.get(j));
		}
		
		// *******************************************************
		webPageObject.printTerms();
		// *******************************************************
		
		// *******************************************************
		System.out.println();
		
		/*
		 * Loops through the whichPagesWordList (arrayList) are getting all the Strings it has and checking them.
		 * foreach loop prints out the information needed
		 */
		for(int k = 0; k < whichPagesWordList.size();k++) {
			int counter = 0;
			String Term1 = whichPagesWordList.get(k);
			for(String resultFileName : webPageObject.whichPages(whichPagesWordList.get(k))) {
				if (resultFileName == null) {
					System.out.print(Term1+" not found");
				} else if (counter == 0) {
					System.out.print(Term1+" in pages: "+resultFileName+": "+webPageObject.TFIDF(resultFileName, Term1));
				} else {
					System.out.print(", "+resultFileName+": "+webPageObject.TFIDF(resultFileName, Term1));
				}
				counter++;
			}
			System.out.println();
			
		}
	}
}
