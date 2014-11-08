

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
		int keyToAdd = Math.abs(hash(word));
		int tempKeyCopy = keyToAdd;
        int quadraticProb = 1;
        do {
            if (keys[keyToAdd] == null) {
                keys[keyToAdd] = word;
                values[keyToAdd] = value;
                currentSize++;
                return;
            }
            if (keys[keyToAdd].equals(word)) { 
            	//do nothing, already accounted for
            	//might need to incFrequency here
                return; 
            }            
            keyToAdd = (keyToAdd + quadraticProb * quadraticProb++) % maxSize;            
        } while (tempKeyCopy != keyToAdd); 
	}
	@Override
	public void add(String filename, String newWord) {
		newWord = newWord.toLowerCase();
		Term newTermObject = new Term(newWord);
		newTermObject.incFrequency(filename);
		
		if (contains(newWord)) {
			Term existingTermObject = get(newWord, false);
			existingTermObject.incFrequency(filename);
		} else {
			insert(newTermObject, newWord);
			resize(hash(newWord));
		}
		// TODO Auto-generated method stub
	}
	private int hash(String word) {
        return Math.abs(word.hashCode() % maxSize);
    }  
	@Override
	public int size() {
       return currentSize;
    }
	public boolean contains(String key) 
    {
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
        keys[key] = null;
        values[key] = null;
 
        // rehash all keys         
        rehash(key);
        currentSize--;  
		
	}
	private void resize(int Key) {
		if (((double) currentSize / (double) maxSize) > 0.80) {
			maxSize = (2 * currentSize) + 1;
			rehash(Key);
		}
	}
	private void rehash(int key) {
		int quadraticProb = 1;
		for (key = (key + quadraticProb * quadraticProb++) % maxSize; keys[key] != null; key = (key + quadraticProb * quadraticProb++) % maxSize) {
            String tmpWord = keys[key];
            Term tmpValue = values[key];
            keys[key] = null;
            values[key] = null;
            currentSize--;  
            insert(tmpValue, tmpWord);            
        }
	}
	@Override
	public Term get(String word, Boolean printP) {
		int hashedWord = hash(word);
		int quadraticProb = 0;
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
        	if (keys[i] != null) {
                System.out.println(values[i].getWord());
        	}
        }
        System.out.println();
    } 
}
