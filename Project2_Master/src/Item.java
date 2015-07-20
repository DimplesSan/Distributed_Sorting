import java.io.Serializable;


public class Item implements Comparable<Item>,Serializable{
	
	public static final long serialVersionUID = 340460459746761958L;
	
	public int key, value;
	
	Item(int key, int value){
		this.key = key;
		this.value = value;
	}

	@Override
	public int compareTo(Item objItem) {
		
		if(this.key == objItem.key)
			return 0;
		else if(this.key > objItem.key)
			return 1;
		else
			return -1;
	}
}
