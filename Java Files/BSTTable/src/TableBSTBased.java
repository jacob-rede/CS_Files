
public class TableBSTBased {
	protected BST bst;
	protected int size;
	/**
	 * @param args
	 */
	public TableBSTBased() {
		bst = new BST();
		size = 0;
	}
	public boolean tableIsEmpty() {
		return size == 0;
	}
	public int tableLength() {
		return size;
	}
	public void tableInsert(String newString) {
		if(bst.get(newString, false) == null) {
			bst.add("asdas", newString);
		} 
	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

}
