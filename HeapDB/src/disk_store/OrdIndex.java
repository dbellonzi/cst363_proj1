package disk_store;

import java.util.ArrayList;
import java.util.List;

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
	}
	
	private class BlockCount {
		int blockNo;
		int count;
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
		if(size == 0 || key < entries.get(0).key){return null;}
		if(key > entries.get(size-1).key){return null;}
		int lo = 0;
		int hi = size-1;
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
		if(size==0){
			Entry temp = new Entry();
			BlockCount b = new BlockCount();
			b.blockNo = blockNum;
			b.count = 1;
			temp.key = key;
			temp.blocks.add(b);
			entries.add(temp);
		}
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(int key, int blockNum) {
		// lookup key 
		//  if key not found, should not occur.  Ignore it.
		//  decrement count for blockNum.
		//  if count is now 0, remove the blockNum.
		//  if there are no block number for this key, remove the key entry.
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
	
	@Override
	public String toString() {
		throw new UnsupportedOperationException();
	}
}