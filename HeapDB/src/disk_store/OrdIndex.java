package disk_store;

import java.util.*;

/**
 * An ordered index.  Duplicate search key values are allowed,
 * but not duplicate index table entries.  In DB terminology, a
 * search key is not a superkey.
 * 
 * A limitation of this class is that only single integer search
 * keys are supported.
 *
 */


public class OrdIndex implements DBIndex {
	
	private class Entry {
		int key;
		ArrayList<BlockCount> blocks;

		@Override
		public String toString(){
			String blockString = "";
			for(BlockCount b: blocks){
				blockString += b + " , ";
			}
			return String.format("Key: %d, %s", key, blockString);
		}
	}
	
	private class BlockCount {
		int blockNo;
		int count;

		@Override
		public String toString(){
			return String.format("block num: %d, count: %d", blockNo, count);
		}
	}
	
	ArrayList<Entry> entries;
	int size = 0;
	
	/**
	 * Create an new ordered index.
	 */
	public OrdIndex() {
		entries = new ArrayList<>();
	}
	
	@Override
	public List<Integer> lookup(int key) {
		// binary search of entries arraylist
		if(size == 0 || key < entries.get(0).key){return new ArrayList<>();}
		if(key > entries.get(entries.size()-1).key){return new ArrayList<>();}
		int lo = 0;
		int hi = entries.size()-1;
		if(key==entries.get(lo).key){hi = lo;}
		if(key==entries.get(hi).key){lo = hi;}
		while(hi-lo > 1){
			int mid = (lo+hi)/2;
			if(key==entries.get(mid).key){
				lo = hi = mid;
			} else if(key<entries.get(mid).key){
				hi = mid;
			} else {
				lo = mid;
			}
		}
		// if key not found, return empty list
		if(key!=entries.get(lo).key){return new ArrayList<>();}
		// return list of block numbers (no duplicates).
		ArrayList<Integer> temp = new ArrayList<Integer>();
		for(BlockCount block: entries.get(lo).blocks){
			temp.add(block.blockNo);
		}
		return temp;
		//throw new UnsupportedOperationException();
	}
	
	@Override
	public void insert(int key, int blockNum) {
		// Case 1 insert into empty list
		if(size==0){
			Entry temp = new Entry();
			BlockCount b = new BlockCount();
			b.blockNo = blockNum;
			b.count = 1;
			temp.key = key;
			temp.blocks = new ArrayList<>();
			temp.blocks.add(b);
			entries.add(temp);
			size++;
			return;
		}
		int idx = -1;
		boolean exists = false;
//		System.out.printf("Inserting key: %d, index 0: %d, index end: %d%n",key, entries.get(0).key, entries.get(size-1).key);
		if(key >= entries.get(0).key && key <= entries.get(entries.size()-1).key){
//			System.out.println("In loop");
			int lo = 0;
			int hi = entries.size()-1;
			if(key==entries.get(lo).key){
				hi = lo;
				exists = true;
			}
			if(key==entries.get(hi).key){
				lo = hi;
				exists = true;
			}
			while(hi-lo > 1){
				int mid = (lo+hi)/2;
				if(key==entries.get(mid).key){
					lo = hi = mid;
					exists = true;
				} else if(key<entries.get(mid).key){
					hi = mid;
				} else {
					lo = mid;
				}
			}
			idx = lo;
		}
//		System.out.println(key + " "+idx + " " + exists);

		if(exists){
			Entry temp = entries.remove(idx);
			boolean blockUsed = false;
			for(BlockCount b: temp.blocks){
				if(b.blockNo == blockNum){
					blockUsed = true;
					b.count++;
				}
			}
			if(!blockUsed){
				BlockCount b = new BlockCount();
				b.count = 1;
				b.blockNo = blockNum;
				temp.blocks.add(b);
			}
			entries.add(idx, temp);
			size++;
		}else{
			Entry temp = new Entry();
			BlockCount b = new BlockCount();
			b.blockNo = blockNum;
			b.count = 1;
			temp.key = key;
			temp.blocks = new ArrayList<>();
			temp.blocks.add(b);
//			System.out.println("adding temp: " + temp.toString() + "idx: " + idx);
			if(idx == -1 && key > entries.get(entries.size()-1).key){entries.add(entries.size(), temp);}
			else if(idx == -1){entries.add(0, temp);}
			else{entries.add(idx,temp);}
			size++;
		}

//		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(int key, int blockNum) {
		// lookup key
		List<Integer> item = lookup(key);
		// if key not found or blockNum doesn't exist, should not occure. Ignore it.
		if (item.contains(blockNum)) {
			size--;
			Iterator<Entry> e = entries.iterator();
			while (e.hasNext()) {
				Entry entry = e.next();
				if (entry.key == key) {
					//  decrement count for blockNum.
					List<BlockCount> blocks = entry.blocks;
					Iterator<BlockCount> b = blocks.iterator();
					while(b.hasNext()) {
						BlockCount block = b.next();
						if (block.blockNo == blockNum) {
							block.count -= 1;
						}
						//  if count is now 0, remove the blockNum.
						if (block.count == 0) {
							b.remove();
						}
					}
					//  if there are no block number for this key, remove the key entry.
					if(blocks.size() == 0) {
						e.remove();
					}
					return;
				}
			}
		}
		
//		throw new UnsupportedOperationException();
	}
	
	/**
	 * Return the number of entries in the index
	 * @return
	 */
	public int size() {
		return size;
		// you may find it useful to implement this
		
	}
	
	@Override
	public String toString() {
		String temp = "Final Index: \n";
		for(int i = 0; i < entries.size(); i++){
			temp += String.format("index: %d - key: %d; size: %d%n", i, entries.get(i).key, entries.get(i).blocks.size());
		}
		return temp;
//		throw new UnsupportedOperationException();
	}
}