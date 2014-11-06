// PA3 Assignment
// Author: Alex Eibner and Jacob Rede
// Date:   9/17/2014
// Class:  CS200
// Email:  alexeibner@gmail.com	and jare1987@rams.colostate.edu

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class PA3 {

	public static void main(String[] args) {
		
		int numStopWords = 0;
		ArrayList<String> fileNameList = new ArrayList();
		ArrayList<String> whichPagesWordList = new ArrayList();
		
		try {
			
			Scanner fileScanner = new Scanner(new File(args[0]));
			
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
			
			// *******************************************************
			// Reads only the integer that comes after *EOFs*. 
			if (fileScanner.hasNextInt()) {
				
				numStopWords = fileScanner.nextInt();
				
			}
			
			// *******************************************************
			// Reads the rest of the words for the WhichPages method.
			while (fileScanner.hasNext()) {
				
				whichPagesWordList.add(fileScanner.next());
				
			}
			
			fileScanner.close();
			
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
			
		}
		
		
		
		// **********************************************************************************************************
		// **********************************************************************************************************
		// **********************************************************************************************************
		
		WebPages webPageObject = new WebPages();
		//testing
		//	webPageObject.FinalTree.	
		//end testing
		for (int j = 0; j < fileNameList.size(); j++) {
			webPageObject.addPage(fileNameList.get(j));
		}
		
		// *******************************************************
		webPageObject.printTerms();
//		System.out.println("WORDS");
//		for (int j = 0; j < webPageObject.termIndex.size(); j++) {
//			
//			System.out.println(webPageObject.termIndex.get(j).getWord());
//			
//		}
		
		// *******************************************************
		//webPageObject.pruneStopWords(numStopWords);
		// *******************************************************
		//System.out.println("WORDS");
		
//		for (int j = 0; j < webPageObject.termIndex.size(); j++) {
//			
//			System.out.println(webPageObject.termIndex.get(j).getWord());
//			
//		}
		
		System.out.println();
		
		/*
		 * Loops through the whichPagesWordList (arrayList) are getting all the Strings it has and checking them.
		 * foreach loop prints out the information needed
		 */
		for(int k = 0; k < whichPagesWordList.size();k++) {
			int counter = 0;
			String word = whichPagesWordList.get(k);
			for(String resultFileName : webPageObject.whichPages(whichPagesWordList.get(k))) {
				if (resultFileName == null) {
					System.out.print(word+" not found");
				} else if (counter == 0) {
					System.out.print(word+" in pages: "+resultFileName+": "+webPageObject.TFIDF(resultFileName, word));
				} else {
					System.out.print(", "+resultFileName+": "+webPageObject.TFIDF(resultFileName, word));
				}
				counter++;
			}
			System.out.println();
			
		}
	}
}
