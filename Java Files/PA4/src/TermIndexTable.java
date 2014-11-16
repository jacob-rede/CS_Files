// PA4 Assignment
// Author: Jacob Rede and Anna Beckman
// Date:   11/14/2014
// Class:  CS200
// Email:  anna_beckman@yahoo.com and jare1987@rams.colostate.edu

public class TermIndexTable implements TermIndex {
	private Term values[];
	private String keys[];
	private int currentSize, maxSize;
	
	
	public TermIndexTable(int startSize) {
		values = new Term[startSize];	//sets the size
		keys = new String[startSize];
		currentSize = 0;
		maxSize = startSize;
	}
	public void insert(Term value, String word) {
		//resize();
		int key = Math.abs(hash(word));
		int tempKeyCopy = key;
        int quadraticProb = 1;
        do {
            if (keys[key] == null) {
            	keys[key] = word;
                values[key] = value;
                currentSize++;
                return;
            } else if(keys[key].equals("RESERVED")) {
            	 keys[key] = word;
                 values[key] = value;
                 return;
            }
            if (keys[key].equals(word)) { 
            	//values[key] = value;
                return; 
            }            
            
            key = (key + quadraticProb * quadraticProb) % maxSize;   
            quadraticProb++;
        } while (tempKeyCopy != key); 
	}
	@Override
	public void add(String filename, String newWord) {
		resize();
		newWord = newWord.toLowerCase();
		Term newTermObject = new Term(newWord);
		newTermObject.incFrequency(filename);
		
		if (contains(newWord)) {
			Term existingTermObject = get(newWord, false);
			existingTermObject.incFrequency(filename);
		} else {
			insert(newTermObject, newWord);
			//resize();
		}
		// TODO Auto-generated method stub
	}
	public int getHashIndex(String word) {
        
		int hashIndex = hash(word);
        
        if (hashIndex < 0) {
            hashIndex = hashIndex + maxSize;
        } // end if
        return hashIndex;
    } // end getHashIndex
	
	private int hash(String word) {
        return Math.abs(word.hashCode() % maxSize);
    }  
	@Override
	public int size() {
       return currentSize;
    }
	public boolean contains(String key) {
        return get(key, false) !=  null;
    }
	@Override
	public void delete(String word) {
		//System.out.println("word to delete: "+word);
		if (!contains(word)) 
            return;
 
        int key = hash(word);
        int quadraticProb = 1;
        
        while (!word.equals(keys[key])) 
        	key = (key + quadraticProb * quadraticProb++) % maxSize;
        // Clear the keys and values
        keys[key] = "RESERVED";
        values[key] = null;
        //currentSize--;  
        // rehash all keys         
        //rehash();
        
		
	}
	private void resize() {
		//System.out.println(((double) currentSize / (double) maxSize));
		if (((double) currentSize / (double) maxSize) >= 0.8) {
			rehash();
		}
	}
	
	/**
     * Expand the hash table.
     */
	 private void rehash() {
		 Term[] oldValues = values;
		 String[] oldKeys = keys;
		 int oldSize = oldValues.length;
		 int newSize = (2 * this.currentSize) + 1;
		 this.values = new Term[newSize];
		 this.keys = new String[newSize];
		 this.currentSize = 0;
		 this.maxSize = newSize;
		 
		 for (int index = 0; index < oldSize; index++) {
			 if (oldKeys[index] != null && !oldKeys[index].equals("RESERVED")) {
				 insert(oldValues[index], oldKeys[index]);
			 } 
		 }
	    } // end rehash
	
	@Override
	public Term get(String word, Boolean printP) {
		int hashedWord = hash(word);
		int quadraticProb = 1;
        while (keys[hashedWord] != null) {
            if (keys[hashedWord].equals(word)) {
            	if(printP) {
            		values[hashedWord].printTest();//????maybe this im not sure
            	}
                return values[hashedWord];
            }
            hashedWord = (hashedWord + quadraticProb * quadraticProb++) % maxSize;// Increments quadraticProb after the math
        }            
        return null;
	}
    public void printHashTableWords() {
        for (int i = 0; i < maxSize; i++) {
        	if (keys[i] != null && !keys[i].equalsIgnoreCase("RESERVED")) {
                System.out.println(keys[i]);
        	}
        }
        System.out.println();
    } 
    public Term[] getValues() {
    	return values;
    }
}
