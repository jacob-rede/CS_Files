//PA3 Assignment
//Author: Alex Eibner and Jacob Rede
//Date:   9/17/2014
//Class:  CS200
//Email:  alexeibner@gmail.com	and jare1987@rams.colostate.edu

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class WebPages {
	
	//public ArrayList<Term> termIndex;
	private BST FinalTree;
	private int totalDocuments;
	
	public WebPages() {
		//termIndex = new ArrayList<Term>();
		FinalTree = new BST();
		totalDocuments = 0;
	}
	public void addPage(String filename) {
		try {
			Scanner fileScanner = new Scanner(new File(filename));
			while (fileScanner.hasNextLine()) {
				
				 /*
				 * replaceAll removes all punctuation except '<' and  '>'. This will make it easier to clean the brackets.
				 */
				 String tempLine = fileScanner.nextLine().toLowerCase().replaceAll("[\\t\\p{Punct}&&[^>]&&[^<]&&[^']]", " ");
				
				 ArrayList<String> bracketStack = new ArrayList<String>();
				 String cleanedLine = "";
			 	/*
				 * This loop implements a stack implementation for brackets.
				 * If there is a '<' bracket, then we add it onto the stack.
				 * If there is a '>' bracket, then we remove the last item on the stack.
				 * Finally we check and make sure the stack is empty.
				 * If it's empty, we begin adding characters into cleanedWord.
				 * If not, we move onto the next character in tempword.
				 */
				 for(int j = 0; j < tempLine.length(); j++) {
						//Then adds to the stack.
						if(tempLine.charAt(j) == '<') {
							bracketStack.add(tempLine);
						//Then removes from the stack.
						} else if (tempLine.charAt(j) == '>') {
							bracketStack.remove(bracketStack.size()-1);
						//Concats chars onto the string.	
						} else if (bracketStack.size() == 0) {
							cleanedLine = cleanedLine + tempLine.charAt(j);
						} 
					}
				 String[] cleanedWords = cleanedLine.split("\\s+");//splits on space characters and tabs
				 /*
				  * This is a foreach loop.
				  * For each cleanedword, put it into word.
				  */
				 for(String word : cleanedWords) {
					// Add the Term to our arraylist, doesn't yet matter if duplicates.
					if (!word.isEmpty()) {
//###################old binary search
//						int[] location = binarySearch(word);
//						// Didn't find the word in the list, so add it.
//						if (location[1] == 0) {
//							// Adds the word.
//							Term newTermObject = new Term(word);
//							// Increments the counts and creates occurrence objects.
//							newTermObject.incFrequency(filename);
//							termIndex.add(location[0], newTermObject);
//						} else {
//							//Increase the Frequency since the word already exists.
//							termIndex.get(location[0]).incFrequency(filename);
//						}
//###################END old binary search
						//##############################Start Binary Search Tree
						
						this.FinalTree.add(filename, word);
						
						//###############################END Binary Search Tree
						
					}//end if
				 }//end for loop
			 }//end while loop
			totalDocuments++;
			fileScanner.close();
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("Error: While reading file line by line: "+ e.getMessage());
		}
		
	}
	public void printTerms() {
		System.out.println("WORDS");
		while (FinalTree.BSTIterator.hasNext()) {
			Term tempTerm = new Term("temp");
			tempTerm = FinalTree.BSTIterator.next();
			System.out.println(tempTerm.getWord());
		}
	}
	//###########################################OLD PA2 Code pruneStopWords
		/*
		 * First sorts the termIndex by totalFrequency in the term objects.
		 * Then removes n words from the end of the sort.
		 * Then resorts based on the word in Term objects(alphabetize).
		 */
		//	public void pruneStopWords(int n) {
		//		Mergesort msort = new Mergesort();
		//		//One for keyword, anything else for frequency.
		//		this.termIndex = msort.Mergesort(this.termIndex, 0);
		//		System.out.println("Copies: "+msort.getCounter());
		//		//Removes the n items at the end of the arraylist.
		//		while(n > 0) {
		//			this.termIndex.remove(termIndex.size()-1);
		//			n--;
		//		}
		//		//One for keyword, anything else for frequency.
		//		this.termIndex = msort.Mergesort(this.termIndex, 1);
		//		System.out.println("Copies: "+msort.getCounter());
		//	}
	//###########################################END OLD PA2 Code pruneStopWords
	
	public String[] whichPages(String word) {
		
		//#################OLD PA2 CODE
			/*
			 * loops through termIndex and grabbing each Term object then checking if the 
			 * word in the Term object equals the word that were looking for, 
			 * if it does we access all the Occurrence objects in the correct Term object and access
			 * the fileNames that the Occurrence objects have and put them into an array of Strings
			 */
			//		for(int j = 0; j < this.termIndex.size();j++) {
			//			if (termIndex.get(j).getWord().equals(word.toLowerCase())) {
			//				int numDocuments = termIndex.get(j).getDocFrequency();
			//				String[] fileNames = new String[numDocuments];
			//				for(int k = 0; k < numDocuments;k++) {
			//					fileNames[k] = termIndex.get(j).docList.get(k).getDocName();
			//				}
			//				return fileNames;
			//			}
			//		}
		//#################END OLD PA2 CODE
		/*
		 * below calls the get method on our BST (FinalTree) which will return the term object where
		 * the word is located, if the result is null then we return a String Array w/ null in it 
		 */
		Term tempTerm = this.FinalTree.get(word, false);
		if (tempTerm != null) {
			int numDocuments = tempTerm.getDocFrequency();
			String[] fileNames = new String[numDocuments];
			int printCounter = 0;
			for(int k = 0; k < numDocuments;k++) {
				if (printCounter == 0) {
					//prints out the depth
					fileNames[k] = this.FinalTree.get(word, true).docList.get(k).getDocName();
					printCounter++;
				} else {
					//DOES NOT print out the depth
					fileNames[k] = this.FinalTree.get(word, false).docList.get(k).getDocName();
				}
				
			}
			return fileNames;
		}
		String[] notFoundStringArray = new String[1];
		notFoundStringArray[0] = null;
		return notFoundStringArray;
	}
	/*
	 * calculates the TFIDF based on the formula provided
	 */
	public String TFIDF(String document, String word) {
		//TFIDF(document,word) =  the number of occurrences of term t in document d ("term frequency")
		//* log (total number of available documents / number of documents containing term t)
		Term tempTerm = this.FinalTree.get(word, false);
		int docListSize = tempTerm.docList.size();
		int occurrenceTermFrequency = 0;
		for(int j = 0; j < docListSize; j++) {
			if (tempTerm.docList.get(j).getDocName().equals(document)) {
				//if document matches in this part of the TermsDocument Linked List,
				//then we grab the term frequency in the Occurrence object
				occurrenceTermFrequency = tempTerm.docList.get(j).getTermFrequency();
			}
		}
		double result = occurrenceTermFrequency * (Math.log((double)totalDocuments / (double)docListSize));
		DecimalFormat formatter = new DecimalFormat("0.00");
		return formatter.format(result);
	}
	//#######################################################OLD PA2 Code
		//	public int[] binarySearch(String word) {
		//		
		//		int first = 0;
		//		int length = termIndex.size();
		//		int mid = 0;
		//		int foundMatch = 0;
		//		int[] statusToReturn = {mid, foundMatch};
		//
		//		if (length == 0) {//first time searching
		//			return statusToReturn;
		//		}
		//
		//		while (first < length) {
		//			
		//			mid = (first + length) / 2;
		//			
		//			if (word.compareTo(termIndex.get(mid).getWord()) < 0) {
		//				// Repeat search in bottom half.
		//				length = mid;
		//	        } else if (word.compareTo(termIndex.get(mid).getWord()) > 0) {
		//	        	// Repeat search in top half.
		//	            first = mid + 1;  
		//	        } else if (word.compareTo(termIndex.get(mid).getWord()) == 0){
		//	        	// Found it.
		//	        	foundMatch = 1;
		//	        	statusToReturn[0] = mid;
		//	        	statusToReturn[1] = foundMatch;
		//
		//	        	return statusToReturn;
		//	        }
		//		}
		//		mid = (first + length) / 2;
		//		statusToReturn[0] = mid;
		//    	statusToReturn[1] = foundMatch;
		//		return statusToReturn;
		//	}
	//#######################################################END OLD PA2 Code
	/*
	 * #####################################################################################################
	 * #####################################PRIVATE BST CLASS###############################################
	 * #####################################################################################################
	 * 
	 * New private sub-class called BST, which makes it only available to the WebPages object
	 * 
	 * @param <int> count = counts the words, only incremented in add and insertWord
	 * @param <TreeNode> root = the very top of our BST, never changes
	 * @param <InorderIterator> BSTIterator = Queue of Terms in the BST, 
	 * is only fully created once the last add method has ran
	 * @param <int> searchDepth = stores the depth when search is called, resets to 1 every time get is called
	 */
	private class BST {
		private int count;
		private TreeNode root;
		public InorderIterator BSTIterator;
		private int searchDepth;
		
		public BST() {
			this.count = 0;
			this.searchDepth = 0;
			this.root = null;
			BSTIterator = null;
			
		}
		public int size() {
			return this.count;
		}
		/*
		 * adds words into the BST, if the root is null then we have an empty BST, so we create our first TreeNode
		 * if the root node isn't null we call the insetWord method (which is recursive) 
		 */
		public void add(String documentName, String word) {
			if (this.root == null) {
				Term newTermObject = new Term(word);
				newTermObject.incFrequency(documentName);
				this.root = new TreeNode(newTermObject);
				BSTIterator = new InorderIterator(this.root);
				this.count++;//increment the word count since were creating a new Term object
			} else {
				insertWord(word, this.root, documentName);
				BSTIterator = new InorderIterator(this.root);
			}
			//adds a new term or increments frequencies if the term already exists in the BST
		}
		/*
		 * returns the Term Object of word by searching the BST
		 * calls search (which is recursive)
		 * prints the serachDepth if printDepth is true or 
		 * if its false but the search returned null (meaning it did not find the word in our BST, so it doesn't exist)
		 */
		public Term get(String word, Boolean printDepth) {
			this.searchDepth = 1;
			Term tempTerm = search(word.toLowerCase(), this.root);
			if (printDepth) {
				System.out.println("  At depth "+this.searchDepth);
				printDepth = false;
			} else if (printDepth == false && tempTerm == null) {
				System.out.println("  At depth "+this.searchDepth);
			}
			return tempTerm;
		}
		/*
		 * recursively search the BST, same format as insertWord below, 
		 * without the documentName since we dont need it
		 */
		public Term search(String word, TreeNode treeNode) {
			if (treeNode == null) {
				return null;
			} else if (word.compareTo(treeNode.getItem().getWord()) == 0) {
				return treeNode.getItem();
			} else if (word.compareTo(treeNode.getItem().getWord()) < 0) {
				this.searchDepth++;
				return search(word, treeNode.getLeft());
			} else {
				this.searchDepth++;
				return search(word, treeNode.getRight());
			}
			
		}
		/*
		 * recursive method that will traverse the binary tree and insert nodes where they belong
		 * if TreeNode is null, then we hit our base case so we create a new TreeNode w/ inputWord 
		 * inputWord < TreeNode's word then recurse with the leftSubTree
		 * inputWord == TreeNode's word then increment the frequency of the word that already exists
		 * inputWord > TreeNode's word then recurse onto the rightSubTree
		 */
		public TreeNode insertWord(String inputWord, TreeNode treeNode, String documentName) {
			//System.out.print("asdasd");
			if (treeNode == null) {
				Term newTermObject = new Term(inputWord);
				newTermObject.incFrequency(documentName);
				treeNode = new TreeNode(newTermObject);
				this.count++;//increment the word count since were creating a new Term object
			} else if (inputWord.compareTo(treeNode.getItem().getWord()) < 0) {
				treeNode.setLeft(insertWord(inputWord, treeNode.getLeft(), documentName));
			} else if (inputWord.compareTo(treeNode.getItem().getWord()) == 0) {
				treeNode.getItem().incFrequency(documentName);
			} else {
				treeNode.setRight(insertWord(inputWord, treeNode.getRight(), documentName));
			}
			return treeNode;
		}
	}//end private BST
	/*
	 * #####################################################################################################
	 * #####################################END PRIVATE BST CLASS###########################################
	 * #####################################################################################################
	 */
}