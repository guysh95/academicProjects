/**
 * student name: guy shnaider
 * username : guyshnaider , id : 313119679
 * student name: ido gazit
 * username : idogazit , id : 313197980 
 */
/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over integers.
 */
public class FibonacciHeap
{
	
	public HeapNode first;
	public HeapNode min;
	public int size;
	public int treeCount;
	public int markedCount;
	public static int totalLinks = 0;
	public static int totalCuts = 0;
	
	public FibonacciHeap() {
		this.first = null;
		this.min = null;
		this.size = 0;
		this.treeCount = 0;
		this.markedCount = 0;
	}
	

   /**
    * public boolean isEmpty()
    *
    * precondition: none
    * 
    * The method returns true if and only if the heap
    * is empty.
    *   O(1)
    */
    public boolean isEmpty()
    {
    	if(this.first == null)
    		return true;
    	return false;
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
    * O(1) 
    */
    public HeapNode insert(int key)
    {  
    	HeapNode toIns = new HeapNode(key);
    	return insertNode(toIns);
    }

   /**
    * public void deleteMin()
    *
    * Delete the node containing the minimum key.
    *
    */
    public void deleteMin()
    {
    	if(isEmpty()) // heap is empty
    		return;
    	this.size--;
    	this.treeCount--;
    	//Deleting...
     	if(this.min.getChild() == null) { //min with no children
     		if(this.size == 0) {		//only min in heap
     			this.first = null;
     			this.min = null;
     			return; //in this case finish here
     		}
     		else {						//min has a brother
     			connectNodes(this.min.getPrev(), this.min.getNext());
     			if(this.min.getKey() == this.first.getKey())
     				this.first = this.first.getNext();
     		}
     	}
     	else {							//min with children
     		if(this.min.getNext().getKey() == this.min.getKey())	//min doesn't have brother
     		{
     			makeChildrenTrees(this.min);
     		}
     		else {										//min has brother 
     			makeChildrenTrees(this.min);
     			connectNodes(this.first.getPrev(), this.min.next);
     			connectNodes(this.min.prev, this.first);
     		}
     	}
    	//Consolidating...
     	consolidate();
    }
    /**
     * Adds x children to heap
     * dealing with connecting new trees to x next and prev must be done after executing this func
     * (subfunc of consolidate)
     * O(log n)
     */
    public void makeChildrenTrees(HeapNode x) {		
    	this.treeCount += x.getRank();
    	this.first = x.getChild();
    	HeapNode childToTree = this.first;
    	do {
        	childToTree.setParent(null);
        	if(childToTree.isMarked())
        		this.markedCount--;
        	childToTree.unMark();
        	childToTree = childToTree.getNext();
    	}while(childToTree.getKey() != this.first.getKey());
    }
    /**
     * 
     * @param x != null
     * @param y != null
     * @prev x.rank == y.rank
     * x and y are trees (parent is null)
     * sub func of condolidate
     * O(1)
     */
    public HeapNode link(HeapNode x, HeapNode y) {
    	if(x.key > y.key) {
    		linkChildToFather(x, y);
    		x = y;
    	}
    	else 
    		linkChildToFather(y, x);
    	this.treeCount--;
    	return x;
    }
    /*
     * 	@prev child.key() < fathe.key()
     * 	linking two nodes
     * 	sub func of link
     *	O(1) 
     */
    public void linkChildToFather(HeapNode child, HeapNode father) {
		if(child.getKey() == this.first.getKey())
			this.first = father;
		child.setParent(father);
		father.rank++;
		if(father.getChild() == null) {
			father.setChild(child);
			connectNodes(child, child);
		}
		else {
			HeapNode firstChild = father.getChild();
			HeapNode lastChild = father.getChild().getPrev();
			connectNodes(child, firstChild);
			connectNodes(lastChild, child);
			father.setChild(child);
		}
    }
    /*
     * rearrange the heap after deletemin as similar to binomial heap
     */
    public void consolidate() {
    	this.first = fromBuckets(toBuckets(this.first));
    }
    /*
     * creates an array of HeapNode pointers, and linking trees with same rank
     * when finish buckets <= 1 tree of every rank  
     */
    public HeapNode[] toBuckets(HeapNode x) {
    	double goldRatio = (1.0 + Math.sqrt(5))/(2.0);
    	double maxRank = Math.log(this.size)/Math.log(goldRatio);
    	int length = (int)Math.floor(maxRank + 1.0);
    	HeapNode[] Buckets = new HeapNode[length];
    	HeapNode y;
    	x.getPrev().setNext(null);
    	while(x != null) {
    		y = x;
    		x = x.getNext();
    		while(Buckets[y.getRank()] != null) {
    			y = link(y, Buckets[y.getRank()]);
    			totalLinks++;
    			Buckets[y.getRank() - 1] = null;
    		}
    		Buckets[y.getRank()] = y;
    		connectNodes(y, y);
    	}
    	return Buckets;
    }
    /*
     * rearrange heap, relinking roots of trees, from smallest tree to largest
     */
    public HeapNode fromBuckets(HeapNode[] Buckets) {
    	HeapNode newFirst = null;
    	for(int i = 0; i < Buckets.length; i++) {
    		if(Buckets[i] != null) {
    			if (newFirst == null) {
    				newFirst = Buckets[i];
    				newFirst.setPrev(newFirst);
    				newFirst.setNext(newFirst);
    				this.min = newFirst;
    			}
    			else {
    				connectNodes(newFirst.getPrev(), Buckets[i]);
    				connectNodes(Buckets[i], newFirst);
    				if(Buckets[i].getKey() < this.min.getKey())
    					this.min = Buckets[i];
    			}
    		}
    	}
    	return newFirst;
    }

   /**
    * public HeapNode findMin()
    *
    * Return the node of the heap whose key is minimal. 
    * O(1)
    */
    public HeapNode findMin()
    {
    	return this.min;
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Meld the heap with heap2
    * O(1)
    */
    public void meld (FibonacciHeap heap2)
    {
    	 if(!heap2.isEmpty()) {
    		if(this.first == null) {
    			this.first = heap2.first;
    			this.min = heap2.min;
    		}
    		else {
                HeapNode heap2Last = heap2.first.getPrev();
                HeapNode thisLast = this.first.getPrev();
                connectNodes(heap2Last, this.first);
                connectNodes(thisLast, heap2.first);
   			 	if(heap2.findMin().getKey() < this.min.getKey())
   			 		this.min = heap2.findMin();
    		}
        		 this.size += heap2.size();
        		 this.treeCount += heap2.treeCount;
        		 this.markedCount += heap2.markedCount;
    	 }
    }
    /**
     * connect one to two 
     * @param one != null
     * @param two != null
     * @return one is before two in heap
     * O(1)
     */
    public void connectNodes(HeapNode one, HeapNode two) {
    	one.setNext(two);
    	two.setPrev(one);
    }

   /**
    * public int size()
    *
    * Return the number of elements in the heap
    *   O(1)
    */
    public int size()
    {
    	return this.size;
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap. 
    * O(n)
    */
    public int[] countersRep()
    {
    if(isEmpty())
    	return new int[0];
    double goldRatio = (1.0 + Math.sqrt(5))/(2.0);
    double temp = Math.log(this.size)/Math.log(goldRatio);
    int length = (int)Math.floor(temp + 1.0);
	int[] arr = new int[length];
	int maxRank = 0;
	HeapNode x = this.first;
	// go over roots of heap, inc the suitable array cell according to root rank
	do {				
		if(maxRank < x.getRank())
			maxRank = x.getRank();
		arr[x.getRank()]++;
		x = x.getNext();
	}while(x.getKey() != this.first.getKey());
	// rearrange to a new array with no extra cells
	if((maxRank + 1) < arr.length) {
		int[] arr2 = new int[maxRank+1];
		for(int i = 0; i < maxRank + 1; i++)
			arr2[i] = arr[i];
		arr = arr2;
	}
    return arr;
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap. 
    * O(n), ammortized O(logn)
    */
    public void delete(HeapNode x) 
    {    
    	if(x.getKey() == this.min.getKey())
    		deleteMin();
    	else
    	{
	    	int reqDelta = 2*(x.getKey() - this.min.getKey()); // was -
	    	decreaseKey(x, reqDelta);
	    	deleteMin();
    	}
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * The function decreases the key of the node x by delta. The structure of the heap should be updated
    * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
    * O(logn), ammortized: O(1)
    */
    public void decreaseKey(HeapNode x, int delta)
    {
    	x.setKey(x.getKey() - delta); // was +
		if(x.getKey() < this.min.getKey())
			this.min = x;
    	if(x.getParent()!= null)
    	{
	    	if(x.getKey() < x.getParent().getKey()) //case heap order needs to be fixed
	    	{
	    		cascadingCuts(x,x.getParent());
	    	}
    	}
    }
    /**
     * 
     * @param x != null
     * @param y != null
     * cut x from it parent y
     * O(1)
     */
    public void cut(HeapNode x, HeapNode y) 
        {
            x.setParent(null);
            if(x.isMarked())
            	this.markedCount--;
        	x.unMark();
        	y.setRank(y.getRank() - 1);
        	if (x.getNext().getKey() == x.getKey())
        		y.setChild(null);
        	else
        	{
        		if(y.getChild().getKey() == x.getKey())
        			y.setChild(x.getNext());
        		x.getPrev().setNext(x.getNext());
        		x.getNext().setPrev(x.getPrev());
        	}
        	//connect x to first position:
        	HeapNode last = this.first.getPrev();
        	connectNodes(x,this.first);
        	connectNodes(last,x);
        	this.first = x;
        	this.treeCount++;
        	totalCuts++;
        }
    /**
     * 
     * @param x != null
     * @param y != null
     * cut x from it parent y, and cutting ancestors of y if marked
     * O(logn), ammortized O(1)
     */
    public void cascadingCuts(HeapNode x, HeapNode y)
    {
    	cut(x,y);
    	if(y.getParent() != null)
    	{
    		if(y.isMarked())
    			cascadingCuts(y,y.getParent());
    		else {
    			y.mark();
    			this.markedCount++;
    		}
    	}
    }
    
   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap. 
    */
    public int potential() 
    {    
    	return this.treeCount + 2*(this.markedCount);
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the run-time of the program.
    * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of 
    * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value 
    * in its root.
    */
    public static int totalLinks()
    {    
    	return totalLinks;
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the run-time of the program.
    * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts()
    {    
    	return totalCuts;
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the k minimal elements in a binomial tree H.
    * The function should run in O(k(logk + deg(H)). 
    */
    public static int[] kMin(FibonacciHeap H, int k)
    {    
        int[] arr = new int[k];
        if(H.isEmpty())
        	return arr;
        
        FibonacciHeap tempHeap = new FibonacciHeap();
        
        int key = H.getFirst().getKey();
        HeapNode tempNode = H.new HeapNode(key);
        tempNode.setPointer(H.getFirst());
        tempHeap.insertNode(tempNode);
        HeapNode child;
        HeapNode min;
        
        for(int i = 0; i<k; i++) { 
        	min = tempHeap.findMin();
        	arr[i] = min.getKey(); //insert minimum value of tempHeap to arr
        	if(min.getPointer().getChild() != null) //insert minimum item children to tempHeap
        	{
        		child = min.getPointer().getChild();
        		for(int j=0; j<min.getPointer().getRank(); j++)
        		{
        			key = child.getKey();
        			HeapNode tempChild = H.new HeapNode(key);
        			tempChild.setPointer(child);
        			tempHeap.insertNode(tempChild);
        			child = child.getNext();
        		}
        		
        	}
        	tempHeap.deleteMin(); //delete minimum item of tempHeap
        }
        return arr;
    }
    /**
     * 
     * @param node != null
     * @return node
     * inserting existing node to heap
     * O(1)
     */
    public HeapNode insertNode(HeapNode node)
    {  
    	if(this.first == null) {
    		this.first = node;
    		this.min = node;
    	}
    	else {
    		HeapNode temp = this.first.getPrev();
        	node.setNext(this.first);
        	node.setPrev(temp);
        	this.first.setPrev(node);
        	temp.setNext(node);
    		if(node.getKey() < this.min.getKey())
    			this.min = node;
    		this.first = node;
    	}
    	this.size++;
    	this.treeCount++;
    	return node;
    }
    
    public HeapNode getFirst()
    {
    	return this.first;
    }
	
	
   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in 
    * another file 
    *  
    */
    public class HeapNode{

	public int key;
	public boolean mark;
	public HeapNode child;
	public HeapNode next;
	public HeapNode prev;
	public HeapNode parent;
	public int rank;
	public HeapNode pointer;


  	public HeapNode(int key) {
	    this.key = key;
	    this.child = null;
	    this.next = this;
	    this.prev = this;
	    this.parent = null;
	    this.rank = 0;
	    this.mark = false;
	    this.pointer = null;
      }

  	public int getKey() {
	    return this.key;
      }
  	public HeapNode getChild() {
	    return this.child;
      }
  	public HeapNode getNext() {
	    return this.next;
      }
  	public HeapNode getPrev() {
	    return this.prev;
      }
  	public HeapNode getParent() {
	    return this.parent;
      }
  	public int getRank() {
	    return this.rank;
      }
  	public boolean isMarked() {
	    return this.mark;
      }
  	
  	public void setKey(int key) {
	    this.key = key;
      }
  	
  	public void setRank(int rank) {
	    this.rank = rank;
      }
  	
  	public void setChild(HeapNode child) {
	    this.child = child;
      }
  	public void setParent(HeapNode parent) {
	    this.parent = parent;
      }
  	public void setNext(HeapNode next) {
	    this.next = next;
      }
  	public void setPrev(HeapNode prev) {
	    this.prev = prev;
      }
  	public void mark() {
	    this.mark = true;
      }
  	public void unMark() {
	    this.mark = false;
      }
  	public void setPointer(HeapNode pointer) {
  		this.pointer = pointer; 
  	}
  	public HeapNode getPointer() {
  		return this.pointer;
  	}
    }
    
}
