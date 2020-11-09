package disk_store;


import java.util.*;

/**
 * A hash index.  
 * 
 */

public class HashIndex implements DBIndex {

	private class BlockCount {
		int blockNo;
		int count;

		@Override
		public String toString(){
			return String.format("block num: %d, count: %d", blockNo, count);
		}
	}

	HashMap<Integer, List<BlockCount>> entries;
	int size = 0;

	/**
	 * Create a new index.
	 */
	public HashIndex() {
		entries = new HashMap<>();
	}
	
	@Override
	public List<Integer> lookup(int key) {
		if(entries.containsKey(key)) {
			List<Integer> bl = new ArrayList<Integer>();
			for (BlockCount b : entries.get(key)){
				bl.add(b.blockNo);
			}
			return bl;
		} else {
			return new ArrayList<>();
		}
	}
	
	@Override
	public void insert(int key, int blockNum) {
		if(entries.containsKey(key)){
			List<BlockCount> temp = entries.get(key);
			boolean found = false;
			for(BlockCount b: temp){
				if(b.blockNo==blockNum){
					b.count++;
					found = true;
				}
			}
			if(!found) {
				BlockCount b = new BlockCount();
				b.blockNo = blockNum;
				b.count = 1;
				temp.add(b);
			}
		} else {
			BlockCount b = new BlockCount();
			ArrayList<BlockCount> bl = new ArrayList<BlockCount>();
			b.blockNo = blockNum;
			b.count = 1;
			bl.add(b);
			entries.put(key, bl);
		}
		size++;
	}

	@Override
	public void delete(int key, int blockNum) {
		if(entries.containsKey(key)){
			List<BlockCount> bl = entries.get(key);
			Iterator<BlockCount> b = bl.iterator();
			while(b.hasNext()) {
				BlockCount block = b.next();
				if(block.blockNo==blockNum){
					block.count--;
					size--;
				}
				if(block.count==0){
					b.remove();
				}
			}
			if(bl.size()==0) entries.remove(key);
		}
	}
	
	@Override
	public String toString() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Return the number of entries in the index
	 * @return
	 */
	public int size() {
		return size;
		// you may find it useful to implement this

	}

}
