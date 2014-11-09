//PA3 Assignment
//Author: Alex Eibner and Jacob Rede
//Date:   9/17/2014
//Class:  CS200
//Email:  alexeibner@gmail.com	and jare1987@rams.colostate.edu

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class WebPages {
	
	//public ArrayList<Term> termIndex;
	private TermIndexTable FinalTree;
	private int totalDocuments;
	private ArrayList<String> fileNameList = new ArrayList();
	public WebPages(int startSize) {
		//termIndex = new ArrayList<Term>();
		FinalTree = new TermIndexTable(startSize);
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
						//##############################Start Binary Search Tree
						
						this.FinalTree.add(filename, word);
						
						//###############################END Binary Search Tree
						
					}//end if
				 }//end for loop
			 }//end while loop
			totalDocuments++;
			fileNameList.add(filename);
			Collections.sort(fileNameList);
			fileScanner.close();
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("Error: While reading file line by line: "+ e.getMessage());
		}
		
	}
	public void printTerms() {
		System.out.println("WORDS");
		Iterator hashTableIterator = new Iterator(FinalTree);
		while (hashTableIterator.hasNext()) {
			System.out.println(hashTableIterator.next().getWord());
		}
		//FinalTree.printHashTableWords();
	}
	//###########################################OLD PA2 Code pruneStopWords
	/*
	 * First sorts the termIndex by totalFrequency in the term objects.
	 * Then removes n words from the end of the sort.
	 * Then resorts based on the word in Term objects(alphabetize).
	 */
	public void pruneStopWords(String removeTerm) {
		if (FinalTree.contains(removeTerm)) {
			FinalTree.delete(removeTerm);
		}
	 }
	//###########################################END OLD PA2 Code pruneStopWords
	
	public String[] whichPages(String word) {
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
	public Double TFIDF(String document, String word) {
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
		//DecimalFormat formatter = new DecimalFormat("0.00");
		return result;
	}
	public void bestPages(ArrayList<String> Query) {
		DecimalFormat formatter = new DecimalFormat("0.000000000");
		double common[][] = new double[Query.size()][this.totalDocuments];
		double docSpecific[] = new double[this.totalDocuments];
		double totalSim[][] = new double[Query.size()][this.totalDocuments];
		String docs[] = new String[this.totalDocuments];
		int docsCounter = 0;
		for(String documentName : this.fileNameList) {
			docs[docsCounter++] = documentName;
		}
		String[][] cleanedWords = new String[Query.size()][];
		
		double queryWeights[] = new double[Query.size()];
		int queryCounter = 0;
		
		for(int j = 0; j < docs.length; j++) {//for every document, iterat through all terms and do 
			//b.     For each document d that contains term i:
			Iterator TermIteratorBeforeScan = new Iterator(FinalTree);
			while (TermIteratorBeforeScan.hasNext()) {
				Term tempTerm = TermIteratorBeforeScan.next();
				for(Occurrence occurrenceObject: tempTerm.docList) {//loop through all occurrence objects in the term
					if(occurrenceObject.getDocName().equals(docs[j])) {//  i.     Compute the tfidf value (wi,d), square it and add it to the value in docSpecific in the position for doc d 
						Double TFIDF = TFIDF(occurrenceObject.getDocName(), tempTerm.getWord());
						
						docSpecific[j] += (TFIDF*TFIDF);
					}
				}
			}
		}
		
		for(int m = 0; m < Query.size();m++) {// arraylist contains raw line by line Strings
			String temp = Query.get(m);
			cleanedWords[m] = temp.split("\\s+");
			for(int j = 0; j < cleanedWords[m].length; j++) {
				cleanedWords[m][j] = cleanedWords[m][j].toLowerCase();// make them lowercase
			}
			Arrays.sort(cleanedWords[m]); //sort
			for(int j = 0; j < cleanedWords.length; j++) {// for every item that exists in query, we compare it with all the terms 
				String singleQuery = cleanedWords[m][j].toLowerCase();
				//System.out.print(singleQuery+" ");
				Iterator TermIterator = new Iterator(FinalTree);
				while (TermIterator.hasNext()) {
					Term tempTerm = TermIterator.next();
					if (singleQuery.equals(tempTerm.getWord())) {
						//compute wi,q add it to queryWeights
						int dfi = tempTerm.getDocFrequency();
						int documentsReadIn = this.totalDocuments;
						//WIQ = .5X(1+ln(n/dfi))
						double WIQ = 0.5 * (1 + Math.log((double)documentsReadIn/dfi));
						
						//double formatedWIQWIQ = Double.parseDouble(formatter.format((WIQ*WIQ)));
						queryWeights[queryCounter] += (WIQ*WIQ);
						for(Occurrence occurrenceObject: tempTerm.docList) {
							for(int k = 0; k < this.totalDocuments; k++) {
								if(docs[k].equals(occurrenceObject.getDocName())) {
									Double TFIDF = TFIDF(occurrenceObject.getDocName(), tempTerm.getWord());
//									if(occurrenceObject.getDocName().equals("simple4b.txt")) {
//										System.out.println("simple4b.txt - TFIDF*WIQ: "+TFIDF*WIQ);
//									}
									//k is the position
									//docSpecific[k] += (TFIDF*TFIDF); // add it to the value in docSpecific in the position for doc d 
									 // ii.     If the term is in both query and document d, mutiply wi,d with wi,q  and add it to the value in common in the position for document d
									//double formatedTFWIQ = Double.parseDouble(formatter.format((TFIDF*WIQ)));
									common[m][k] += (TFIDF*WIQ);
								}
							}
						}//end for loop through occurrence object
					}
				}//end while iterator has next loop
			}//end cleanedWords loop
			queryCounter++;
		}//end final for loop
		for(int k = 0; k < Query.size(); k++) {
			for(int j = 0; j < docs.length; j++) {
				//double formatedTFWIQ = Double.parseDouble(formatter.format((TFIDF*WIQ)));
				totalSim[k][j] = common[k][j] / (Double.parseDouble(formatter.format((Math.sqrt(docSpecific[j]))))*Double.parseDouble(formatter.format((Math.sqrt(queryWeights[k])))));
				//totalSim[k][j] = (Math.sqrt(queryWeights[k]) / (Math.sqrt(docSpecific[j]))+(Math.sqrt(queryWeights[k]));
//				System.out.println("common[k][j]: "+common[k][j]);
//				System.out.println("/");
//				System.out.println("(Math.sqrt(docSpecific[j])): "+Double.parseDouble(formatter.format((Math.sqrt(docSpecific[j])))));
//				System.out.println("*");
//				System.out.println("Math.sqrt(queryWeights[k]): "+Double.parseDouble(formatter.format((Math.sqrt(queryWeights[k])))));
//				System.out.println("=");
//				System.out.println("totalSim[k][j]: "+ Double.parseDouble(formatter.format(totalSim[k][j])));
//				System.out.println();
			}
		}
		
		double maxValue[] = new double[Query.size()];
		for(int k = 0; k < Query.size(); k++) {
			for(int j = 0; j < this.totalDocuments; j++) {
				if(totalSim[k][j] > maxValue[k]) {
					maxValue[k] = totalSim[k][j];
				}
			}
		}
		double finalSim[] = new double[Query.size()];
		String finalDocumentName[] = new String[Query.size()];
		for(int k = 0; k < Query.size(); k++) {
			for(int h = 0; h < this.totalDocuments; h++) {
				if (totalSim[k][h] == maxValue[k]) {
					finalSim[k] = totalSim[k][h];
					finalDocumentName[k] = docs[h];
				}
			}
		}
		DecimalFormat finalFormatter = new DecimalFormat("0.00");
		for(int j = 0; j < finalSim.length; j++) {
			System.out.print("[");
			for(String word : cleanedWords[j]) {
				System.out.print(word+" ");
			}
			System.out.print("]");
			System.out.print(" in "+finalDocumentName[j]+": "+Double.parseDouble(finalFormatter.format(finalSim[j])));
			System.out.println();
		}
		
	}
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
//	private class BST {
//		private int count;
//		private TreeNode root;
//		public InorderIterator BSTIterator;
//		private int searchDepth;
//		
//		public BST() {
//			this.count = 0;
//			this.searchDepth = 0;
//			this.root = null;
//			BSTIterator = null;
//			
//		}
//		public int size() {
//			return this.count;
//		}
//		/*
//		 * adds words into the BST, if the root is null then we have an empty BST, so we create our first TreeNode
//		 * if the root node isn't null we call the insetWord method (which is recursive) 
//		 */
//		public void add(String documentName, String word) {
//			if (this.root == null) {
//				Term newTermObject = new Term(word);
//				newTermObject.incFrequency(documentName);
//				this.root = new TreeNode(newTermObject);
//				BSTIterator = new InorderIterator(this.root);
//				this.count++;//increment the word count since were creating a new Term object
//			} else {
//				insertWord(word, this.root, documentName);
//				BSTIterator = new InorderIterator(this.root);
//			}
//			//adds a new term or increments frequencies if the term already exists in the BST
//		}
//		/*
//		 * returns the Term Object of word by searching the BST
//		 * calls search (which is recursive)
//		 * prints the serachDepth if printDepth is true or 
//		 * if its false but the search returned null (meaning it did not find the word in our BST, so it doesn't exist)
//		 */
//		public Term get(String word, Boolean printDepth) {
//			this.searchDepth = 1;
//			Term tempTerm = search(word.toLowerCase(), this.root);
//			if (printDepth) {
//				System.out.println("  At depth "+this.searchDepth);
//				printDepth = false;
//			} else if (printDepth == false && tempTerm == null) {
//				System.out.println("  At depth "+this.searchDepth);
//			}
//			return tempTerm;
//		}
//		/*
//		 * recursively search the BST, same format as insertWord below, 
//		 * without the documentName since we dont need it
//		 */
//		public Term search(String word, TreeNode treeNode) {
//			if (treeNode == null) {
//				return null;
//			} else if (word.compareTo(treeNode.getItem().getWord()) == 0) {
//				return treeNode.getItem();
//			} else if (word.compareTo(treeNode.getItem().getWord()) < 0) {
//				this.searchDepth++;
//				return search(word, treeNode.getLeft());
//			} else {
//				this.searchDepth++;
//				return search(word, treeNode.getRight());
//			}
//			
//		}
//		/*
//		 * recursive method that will traverse the binary tree and insert nodes where they belong
//		 * if TreeNode is null, then we hit our base case so we create a new TreeNode w/ inputWord 
//		 * inputWord < TreeNode's word then recurse with the leftSubTree
//		 * inputWord == TreeNode's word then increment the frequency of the word that already exists
//		 * inputWord > TreeNode's word then recurse onto the rightSubTree
//		 */
//		public TreeNode insertWord(String inputWord, TreeNode treeNode, String documentName) {
//			//System.out.print("asdasd");
//			if (treeNode == null) {
//				Term newTermObject = new Term(inputWord);
//				newTermObject.incFrequency(documentName);
//				treeNode = new TreeNode(newTermObject);
//				this.count++;//increment the word count since were creating a new Term object
//			} else if (inputWord.compareTo(treeNode.getItem().getWord()) < 0) {
//				treeNode.setLeft(insertWord(inputWord, treeNode.getLeft(), documentName));
//			} else if (inputWord.compareTo(treeNode.getItem().getWord()) == 0) {
//				treeNode.getItem().incFrequency(documentName);
//			} else {
//				treeNode.setRight(insertWord(inputWord, treeNode.getRight(), documentName));
//			}
//			return treeNode;
//		}
//	}//end private BST
	/*
	 * #####################################################################################################
	 * #####################################END PRIVATE BST CLASS###########################################
	 * #####################################################################################################
	 */
}
