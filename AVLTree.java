/**
 *  Name1: Ido Gazit username: idogazit id: 313197980
 *  Name2: Guy Shnaider username: guyshnaider id: 313119679 
 * AVLTree
 *
 * An implementation of a AVL Tree with
 * distinct integer keys and info
 *
 */
public class AVLTree {
	
	private final IAVLNode EXT = new AVLNode(-1, null, -1); // External leaf
	private IAVLNode root;
	public int sumOfjoin = 0;
	
	public AVLTree()
	{
		this.root = null;
	}
	
	
  /**
   * public boolean empty()
   *
   * returns true if and only if the tree is empty
   *
   */
  public boolean empty() {
	  if (this.root == null)
		  return true;
    return false;
  }
  
 /**
   * public String search(int k)
   *
   * returns the info of an item with key k if it exists in the tree
   * otherwise, returns null
   */
  public String search(int k)
  {
	IAVLNode x = this.root;
	while(x.getKey() != EXT.getKey())
	{
		if (x.getKey() == k)   // the function reached the wanted node
			return x.getValue();
		else
		{
			if(k < x.getKey())
				x = x.getLeft();
			else
				x = x.getRight();
		}
	}
	return null;
  }
  /**
   * searches node with key k and returns pointer to node
   * @return EXT if k is not a key in tree
   */
  public IAVLNode searchNode(int k) {
	  IAVLNode x = this.root;
	  while(x.getKey() != k) {
		  if(x.getKey() == EXT.getKey())
			  return EXT;
		  else {
			  if(k < x.getKey())
				  x = x.getLeft();
			  else
				  x = x.getRight();
		  }
	  }
	  return x;
  }
  /**
   * searches node with key k, or if node doesn't exists, returns pointer to 
   * Appropriate node to be parent for node with key k
   * @param x is not null
   * @return null if x is EXT
   */
  private IAVLNode treePosition(IAVLNode x, int k)
  {
	  IAVLNode y = null;
	  while (x.getKey() != EXT.getKey())
	  {
		  y = x;
		  if (k == x.getKey())
			  return x;
		  else {
			  if (k < x.getKey())
				  x = x.getLeft();
			  else
				  x = x.getRight();
		  }
	  }
	  return y;
  }
  /**
   * public int insert(int k, String i)
   *
   * inserts an item with key k and info i to the AVL tree.
   * the tree must remain valid (keep its invariants).
   * returns the number of rebalancing operations, or 0 if no rebalancing operations were necessary.
   * returns -1 if an item with key k already exists in the tree.
   */
   public int insert(int k, String i) {
	   if (this.root == null) // case of empty tree 
	   {
		   this.root = new AVLNode(k,i,0);
		   this.root.setLeft(EXT);
		   this.root.setRight(EXT);
		   this.root.setSize(1);
		   return 0;
	   }
	   IAVLNode postionNode = treePosition(this.root, k);
	   if (postionNode.getKey() == k) // case of item with key k already exists in the tree
		   return -1;
	   IAVLNode nodeToInsert = new AVLNode(k, i, 0);
	   nodeToInsert.setLeft(EXT);
	   nodeToInsert.setRight(EXT);
	   nodeToInsert.setSize(1);
	   nodeToInsert.setParent(postionNode);
	   if (postionNode.getKey() < k)
		   postionNode.setRight(nodeToInsert);
	   else
		   postionNode.setLeft(nodeToInsert);
	   insertFixSize(nodeToInsert);
	  return insertRebalance(nodeToInsert.getParent());	// rebalance 
   }
   /**
    * function update the node's ancestors size
    * @param x != null
    */
   public void insertFixSize(IAVLNode x)
   {
	   while (x.getParent() != null)
	   {
		   x = x.getParent();
		   x.setSize(x.getSize() + 1);
	   }
   }
   /**
    * rebalance the tree after insertion according to the algorithm we learned in class
    * @param parent != null
    * @return number of balance operation conducted
    */
   public int insertRebalance(IAVLNode parent)
   {
	   if(validCheck(parent))
		   return 0;
	   int parentDiRight = parent.getHeight() - parent.getRight().getHeight();
	   int parentDiLeft = parent.getHeight() - parent.getLeft().getHeight();
	   if ((parentDiLeft == 0 && parentDiRight == 1) || (parentDiLeft == 1 && parentDiRight == 0))  // first case: ranks of 1 and 0
	   {
		   promote(parent);
		   if(parent.getParent() == null)
			   return 1;
		   return 1 + insertRebalance(parent.getParent());
	   }
	   else
	   {
		   if(parentDiLeft == 0 && parentDiRight == 2) // cases 2,3 - left rank = 0, right rank = 2
		   {
			   int childDiLeft = parent.getLeft().getHeight() - parent.getLeft().getLeft().getHeight();
			   if (childDiLeft == 1) // case 2 - left child: left rank = 1, right rank = 2
			   {
				   demote(parent);
				   rightRotation(parent);
				   return 2;
			   }
			   else // case 3 - left child: left rank = 2, right rank = 1
			   {
				   demote(parent);
				   demote(parent.getLeft());
				   promote(parent.getLeft().getRight());
				   LRRotation(parent);
				   return 5;
			   }
		   }
		   else // cases 2,3 symmetric - left rank = 2, right rank = 0
		   {
			   if(parentDiLeft == 2 && parentDiRight == 0)  // debug problem
			   {
				   int childDiRight = parent.getRight().getHeight() - parent.getRight().getRight().getHeight();
				   if (childDiRight == 1) // case 2 - right child: left rank = 2, right rank = 1
				   {
					   demote(parent); //check this row
					   leftRotation(parent);
					   return 2;
				   }
				   else // case 3 - right child: left rank = 1, right rank = 2
				   {
					   demote(parent);
					   demote(parent.getRight());
					   promote(parent.getRight().getLeft());
					   RLRotation(parent);
					   return 5;
				   }
			   }
		   }
	   }
	   return 0;
   }
   /**
    * 
    * @param x != null
    * @return true if node is one of 1,1 or 2,1 or 1,2, else return false
    */
   public boolean validCheck(IAVLNode x) 
   {
	   int DiRight = x.getHeight() - x.getRight().getHeight();
	   int DiLeft = x.getHeight() - x.getLeft().getHeight();
	   if(DiRight == 1)
	   {
		   if(DiLeft == 1 || DiLeft == 2)
			   return true;
	   }
	   if(DiRight == 2 && DiLeft == 1)
		   return true;
	   return false;
   }
   /**
    * promote rank of x by 1
    * @param x != null, EXT
    */
   public void promote(IAVLNode x)
   {
	   x.setHeight(x.getHeight() + 1);
   }
   /**
    * demote rank of x by 1
    * @param x != null, EXT
    */
   public void demote(IAVLNode x)
   {
	   x.setHeight(x.getHeight() - 1);
   }
   /**
    * rotate the node y and y.left according to what we learned in class
    * @param y != null, EXT
    */
   public void rightRotation(IAVLNode y)
   { 
	   IAVLNode x = y.getLeft(); 
	   y.setSize(y.getSize() - x.getSize()); 
	   y.setLeft(x.getRight()); 
	   x.getRight().setParent(y); 
	   y.setSize(y.getSize() + y.getLeft().getSize());
	   x.setRight(y);  
	   x.setSize(x.getRight().getSize() + 1 + x.getLeft().getSize()); 
	   if(y.getParent() == null)
	   {
		   this.root = x; 
		   y.setParent(x); 
		   x.setParent(null);
	   }
	   else
	   {
		   if (y.getParent().getKey() < y.getKey()) // if y is right son
			   y.getParent().setRight(x);
		   else                                     // if y is left son
			   y.getParent().setLeft(x); 
		   x.setParent(y.getParent());  
		   y.setParent(x); 
	   }
   }
   /**
    * rotate the node x and x.right according to what we learned in class
    * @param y != null, EXT
    */
   public void leftRotation(IAVLNode x)
   {
	   IAVLNode y = x.getRight();
	   x.setSize(x.getSize() - y.getSize());
	   x.setRight(y.getLeft());
	   y.getLeft().setParent(x);
	   x.setSize(x.getSize() + x.getRight().getSize());
	   y.setLeft(x);
	   y.setSize(y.getLeft().getSize() + 1 + y.getRight().getSize());
	   if(x.getParent() == null)
	   {
		   this.root = y;
		   x.setParent(y);
		   y.setParent(null);
	   }
	   else
	   {
		   if(x.getParent().getKey() < x.getKey())
			   x.getParent().setRight(y);
		   else
			   x.getParent().setLeft(y);
		   y.setParent(x.getParent());
		   x.setParent(y);
	   }
   }
   /**
    * Left and right rotations according to what we learned in class
    * @param head != null, EXT
    */
   public void LRRotation(IAVLNode head)
   {
	   leftRotation(head.getLeft());
	   rightRotation(head);
   }
   /**
    * Right and left rotations according to what we learned in class
    * @param head != null, EXT
    */
   public void RLRotation(IAVLNode head)
   {
	   rightRotation(head.getRight());
	   leftRotation(head);
   }
   /**
    * finds in tree node with the smallest key larger than x.key
    * @param x != EXT, null
    * @return y.key > x.key
    */
   private IAVLNode successor(IAVLNode x)
   {
	   if (x.getRight().getKey() != EXT.getKey())
		   return minNode(x.getRight());
	   IAVLNode y = x.getParent();
	   while (y != null && x.getKey() == y.getRight().getKey()) {
		   x = y;
		   y = x.getParent();
	   }
	   return y;
   }
   
  /**
   * public int delete(int k)
   *
   * deletes an item with key k from the binary tree, if it is there;
   * the tree must remain valid (keep its invariants).
   * returns the number of rebalancing operations, or 0 if no rebalancing operations were needed.
   * returns -1 if an item with key k was not found in the tree.
   */
   public int delete(int k)
   {
	   if(this.root == null)
		   return -1;
	   IAVLNode toDelete = searchNode(k);
	   if(toDelete.getKey() != k || toDelete.getKey() == -1)
		   return -1;
	   int numOfBal = 0;
	   if(this.root.getKey() == toDelete.getKey())
	   {
		   if(isLeaf(this.root))
		   {
			   this.root = null;
			   return 0;
		   }
		   if(hasOnlyLeftChild(this.root)) {
			   this.root = this.root.getLeft();
			   this.root.setParent(null);
			   return 0;
		   }
		   if(hasOnlyRightChild(this.root)) {
			   this.root = this.root.getRight();
			   this.root.setParent(null);
			   return 0;
		   }
		   if(hasTwoChilds(toDelete)) {
				  IAVLNode succ = successor(toDelete);
				  numOfBal += delete(succ.getKey());
				  toDelete.setKey(succ.getKey());
				  toDelete.setValue(succ.getValue());
				  return numOfBal;
		   }
	   }
	   else {
		   IAVLNode TDparent = toDelete.getParent();
		   if(isLeaf(toDelete)) {
			   fixSizeDelete(toDelete);
			   if(isLeftChild(toDelete)) 
				   TDparent.setLeft(EXT);
			   else
				   TDparent.setRight(EXT);
			  numOfBal += deleteRebalance(TDparent);
		   }
		   else {
			   if(hasOnlyLeftChild(toDelete)) {
				   fixSizeDelete(toDelete);
				   if(toDelete.getKey() == TDparent.getLeft().getKey()) {
					   toDelete.getLeft().setParent(TDparent);
					   TDparent.setLeft(toDelete.getLeft());
				   }
				   else {
					   toDelete.getLeft().setParent(TDparent);
					   TDparent.setRight(toDelete.getLeft());
				   }
				   numOfBal += deleteRebalance(TDparent);
			   }
			   if(hasOnlyRightChild(toDelete)) {
				   fixSizeDelete(toDelete);
				   if(toDelete.getKey() == TDparent.getRight().getKey())
				   {
					   toDelete.getRight().setParent(TDparent);
					   TDparent.setRight(toDelete.getRight());
				   }
				   else {
					   toDelete.getRight().setParent(TDparent);
					   TDparent.setLeft(toDelete.getRight());
				   }
				   numOfBal += deleteRebalance(TDparent);
			   }
			   if(hasTwoChilds(toDelete)) {
					  IAVLNode succ = successor(toDelete);
					  numOfBal += delete(succ.getKey());
					  toDelete.setKey(succ.getKey());
					  toDelete.setValue(succ.getValue());  
			   }
		   }
	   }
	   return numOfBal;	
   }
   
   /**
    * Rebalance tree after delete according to algorithm learned in class
    * @param parent != null or EXT
    * @return == number of balance operations conducted
    */
   public int deleteRebalance(IAVLNode parent) {
	   int rankDiRight = parent.getHeight() - parent.getRight().getHeight();
	   int rankDiLeft = parent.getHeight() - parent.getLeft().getHeight();
	   int numOfB = 0;
	   if((rankDiRight == 2 && rankDiLeft == 1) || (rankDiRight == 1 && rankDiLeft == 2))
	   {
		   return 0;  // tree is balanced
	   }
	   if(rankDiRight == 2 && rankDiLeft == 2) {  // case 2,2
		   demote(parent);
		   numOfB++;
		   if(parent.getParent() != null) {
			  numOfB += deleteRebalance(parent.getParent());
		   }
		   return numOfB;
	   }
	   if(rankDiRight == 1 && rankDiLeft == 3) // case 3,1 
	   {
		   IAVLNode r = parent.getRight();
		   int childRDR = r.getHeight() - r.getRight().getHeight();
		   int childRDL = r.getHeight() - r.getLeft().getHeight();
		   if(childRDR == 1 && childRDL == 1) { // case 3,1 and right son 1,1
			   demote(parent);
			   promote(r);
			   this.leftRotation(parent);
			   numOfB += 3;
			   return numOfB;
		   }
		   if(childRDR == 2 && childRDL == 1) { // case 3,1 and right son 2,1
			   demote(parent);
			   demote(parent);
			   IAVLNode a = parent.getRight().getLeft();
			   IAVLNode y = parent.getRight();
			   promote(a);
			   demote(y);
			   if(parent.getParent() == null) {
				   this.RLRotation(parent);
				   numOfB += 6;
			   }
			   else {                        
				   IAVLNode parentP = parent.getParent();
				   this.RLRotation(parent);
				   numOfB += 6;
				   numOfB += deleteRebalance(parentP);
			   }
			   return numOfB;
		   }
		   if(childRDR == 1 && childRDL == 2) {        // case 3,1 and right son 2,1
			   demote(parent);
			   demote(parent);
			   IAVLNode parentP = parent.getParent();
			   if(parentP == null) {
				   this.leftRotation(parent);
				   numOfB += 3;
				   return numOfB;
			   }
			   this.leftRotation(parent);
			   numOfB += deleteRebalance(parentP);
			   return numOfB;
		   }
	   }
	   if(rankDiRight == 3 && rankDiLeft == 1)      // case 1,3 
	   {
		   IAVLNode l = parent.getLeft();
		   int childRDR = l.getHeight() - l.getRight().getHeight();
		   int childRDL = l.getHeight() - l.getLeft().getHeight();
		   if(childRDR == 1 && childRDL == 1) {    // case 1,3 and left son 1,1
			   demote(parent);
			   promote(l);
			   this.rightRotation(parent);
			   numOfB += 3;
			   return numOfB;
		   }
		   if(childRDR == 2 && childRDL == 1) {    // case 1,3 and left son 1,2
			   demote(parent);
			   demote(parent);
			   IAVLNode parentP = parent.getParent();
			   if(parentP == null) {
				   this.rightRotation(parent);
				   numOfB += 3;
				   return numOfB;
			   }
			   this.rightRotation(parent);
			   numOfB += 3;
			   numOfB += deleteRebalance(parentP);
			   return numOfB;
		   }
		   if(childRDR == 1 && childRDL == 2) {   // case 1,3 and left son 2,1
			   demote(parent); // 12--
			   demote(parent);
			   IAVLNode y = parent.getLeft();
			   IAVLNode a = y.getRight();
			   promote(a);
			   demote(y);
			   IAVLNode parentP = parent.getParent();
			   this.LRRotation(parent);
			   numOfB += 6;
			   if(parentP == null) {
				   return numOfB;
			   }
			   numOfB += deleteRebalance(parentP);
			   return numOfB;
		   }
	   }
	   return numOfB;
	   
   }
   /*
    * @param x != EXT, null
    * return true if x has two children
    */
   public boolean hasTwoChilds(IAVLNode x) {
	   if(x.getLeft().getKey() != -1 && x.getRight().getKey() != -1)
		   return true;
	   return false;
   }
   
   /*
    * fix size of deleted node ancestors
    * @param x != EXT, null
    */
   public void fixSizeDelete(IAVLNode x) {
	   while(x.getParent() != null) {
		   x = x.getParent();
		   x.setSize(x.getSize() - 1);
	   }
   }
   
   /*
    * @param x != EXT, null
    * return true if x is leaf
    */
   private static boolean isLeaf(IAVLNode x)
   {
	   if(x.getLeft().getKey() == -1 && x.getRight().getKey() == -1)
		   return true;
	   return false;
   }
   /*
    * @param x != EXT, null
    * return true if x is a left child of x.parent
    */
   private static boolean isLeftChild(IAVLNode x) {
	   IAVLNode Xp = x.getParent();
	   if(Xp.getLeft().getKey() == x.getKey())
		   return true;
	   return false;
   }
   /*
    * @param x != EXT, null
    * return true if x has only left child 
    */
   private static boolean hasOnlyLeftChild(IAVLNode x) {
	   if(x.getLeft().getKey() != -1 && x.getRight().getKey() == -1)
		   return true;
	   return false;
   }
   /*
    * @param x != EXT, null
    * return true if x has only right child 
    */
   private static boolean hasOnlyRightChild(IAVLNode x) {
	   if(x.getRight().getKey() != -1 && x.getLeft().getKey() == -1)
		   return true;
	   return false;
   }

   /**
    * public String min()
    *
    * Returns the info of the item with the smallest key in the tree,
    * or null if the tree is empty
    */
   public String min()
   {
	   if (this.root == null)
		   return null;
	   IAVLNode x = this.root;
	   while(x.getLeft().getKey() != EXT.getKey())
		   x = x.getLeft();
	   return x.getValue();
   }
   /**
    * 
    * returns pointer to smallest node is subtree of x
    * or null if x is null or EXT
    */
   private IAVLNode minNode(IAVLNode x)
   {
	   if (x.getKey() == EXT.getKey() || x == null)
		   return null;
	   while (x.getLeft().getKey() != -1) {
		   x = x.getLeft();
   }
	   return x;
   }

   /**
    * public String max()
    *
    * Returns the info of the item with the largest key in the tree,
    * or null if the tree is empty
    */
   public String max()
   {
	   if(this.root == null)
		   return null;
	   IAVLNode x = this.root;
	   while(x.getRight().getKey() != EXT.getKey())
		   x = x.getRight();
	   return x.getValue();
   }

  /**
   * public int[] keysToArray()
   *
   * Returns a sorted array which contains all keys in the tree,
   * or an empty array if the tree is empty.
   */
  public int[] keysToArray()
  {
      int[] arr = new int[this.root.getSize()];
      if(this.root == null)
    	  return arr;
      sortedKeyArray(this.root, arr, 0);
      return arr;              
  }
  
  /**
   * 
   * @param x != EXT
   * @param arr.size = this.size()
   * @param 0 <= i < arr.size
   * @return i
   */
  private int sortedKeyArray(IAVLNode x, int[] arr, int i) {
	  if(x.getLeft().getKey() != EXT.getKey())
		  i = sortedKeyArray(x.getLeft(),arr,i);
	  arr[i++] = x.getKey();
	  if(x.getRight().getKey() != EXT.getKey())
		  i = sortedKeyArray(x.getRight(),arr,i);
	  return i;
  }

  /**
   * public String[] infoToArray()
   *
   * Returns an array which contains all info in the tree,
   * sorted by their respective keys,
   * or an empty array if the tree is empty.
   */
  public String[] infoToArray()
  {
        String[] arr = new String[this.root.getSize()];
        if(this.root == null)
        	return arr;
        sortedStringArray(this.root, arr, 0);
        return arr;                    
  }
  
  /**
   * recursive function for infoToArray
   * @param x != EXT
   * @param arr.size = this.size()
   * @param 0 <= i < arr.size()
   */
  private int sortedStringArray(IAVLNode x, String[] arr, int i) {
	  if(x.getLeft().getKey() != EXT.getKey())
		  i = sortedStringArray(x.getLeft(),arr,i);
	  arr[i++] = x.getValue();
	  if(x.getRight().getKey() != EXT.getKey())
		  i = sortedStringArray(x.getRight(),arr,i);
	  return i;
  }

   /**
    * public int size()
    *
    * Returns the number of nodes in the tree.
    *
    * precondition: none
    * postcondition: none
    */
   public int size()
   {
	   return this.root.getSize();
   }
   
     /**
    * public int getRoot()
    *
    * Returns the root AVL node, or null if the tree is empty
    *
    * precondition: none
    * postcondition: none
    */
   public IAVLNode getRoot()
   {
	   return this.root;
   }
     /**
    * public string split(int x)
    *
    * splits the tree into 2 trees according to the key x. 
    * Returns an array [t1, t2] with two AVL trees. keys(t1) < x < keys(t2).
	  * precondition: search(x) != null
    * postcondition: none
    */   
   public AVLTree[] split(int x)
   {
	   IAVLNode center = treePosition(this.root, x);
	   AVLTree t1 = new AVLTree();
	   AVLTree t2 = new AVLTree();
	   AVLTree tempTree = new AVLTree();
	   IAVLNode tempNode;
	   IAVLNode parent;
	   
	   if(center.getLeft().getKey() != -1) //x has left child
	   {
		   t1.root = center.getLeft();
		   t1.root.setParent(null);
	   }
	   if(center.getRight().getKey() != -1) // x has right child
	   {
		   t2.root = center.getRight();
		   t2.root.setParent(null);
	   }
	   
	   while (center.getParent() != null)
	   {
		   parent = center.getParent();
		   tempNode = new AVLNode(parent.getKey(), parent.getValue(), 0);
		   if (isLeftChild(center))
		   {
			   if (parent.getRight().getKey()!= -1) //parent have right child of bigger keys
			   {
				   tempTree.root = parent.getRight();
				   tempTree.root.setParent(null);
			   }
			   else
				   tempTree.root = null;
			   this.sumOfjoin += t2.join(tempNode, tempTree);
		   }
		   else
		   {
			   if(parent.getLeft().getKey() != -1) //parent have left child of lower keys
			   {
				   tempTree.root = parent.getLeft();
				   tempTree.root.setParent(null);
			   }
			   else
				   tempTree.root = null;
			   this.sumOfjoin += t1.join(tempNode, tempTree);
		   }
		   center = center.getParent();
	   }
	   AVLTree[] arr = {t1, t2};
	   return arr; 
   }
   
   /**
    * public join(IAVLNode x, AVLTree t)
    *
    * joins t and x with the tree. 	
    * Returns the complexity of the operation (rank difference between the tree and t + 1)
	  * precondition: keys(x,t) < keys() or keys(x,t) > keys()
    * postcondition: none
    */   
   public int join(IAVLNode x, AVLTree t) 
   {
	   if(t.getRoot() == null) {
		   this.insert(x.getKey(), x.getValue());
		   return (this.root.getHeight() + 2); // t height == -1
	   }
	   if(this.root == null) {
		   this.root = t.getRoot();
		   this.insert(x.getKey(), x.getValue());
		   return (this.root.getHeight() + 2);
	   }
	   IAVLNode tHead = t.getRoot();
	   int ans = 0;
	   if(tHead.getKey() > this.root.getKey()) //keys(x,t) > keys()
	   {
		   if(tHead.getHeight() > this.root.getHeight()) // rank(t) > this.rank 
		   {
			   ans = tHead.getHeight() - this.root.getHeight() + 1;
			   joinCase1(x, t);
		   }
		   else {										// rank(t) <= this.rank
			   if(tHead.getHeight() == this.root.getHeight()) {   // rank(t) == this.rank
				   x.setLeft(this.root);
				   x.setRight(tHead);
				   x.setHeight(1 + this.root.getHeight());
				   x.setSize(1 + this.size() + t.size());
				   this.root.setParent(x);
				   tHead.setParent(x);
				   this.root = x;
				   this.root.setParent(null);				   
				   ans = 1;
			   }
			   else {									//rank(t) < this.rank
				   ans = this.root.getHeight() - tHead.getHeight() + 1;
				   joinCase2(x, t);
			   }
		   }
	   }
	   else {								// keys(x,t) < keys()
		   
		   if(tHead.getHeight() > this.root.getHeight()) // rank(t) > this.rank
		   {
			   ans = tHead.getHeight() - this.root.getHeight() + 1;
			   joinCase3(x, t);
		   }
		   else {										// rank(t) <= this.rank
			   if(tHead.getHeight() == this.root.getHeight()) {   // rank(t) == this.rank
				   x.setRight(this.root);
				   x.setLeft(tHead);
				   this.root.setParent(x);
				   tHead.setParent(x);
				   this.root = x;
				   this.root.setParent(null);
				   x.setHeight(1 + this.root.getHeight());
				   x.setSize(1 + this.size() + t.size());
				   ans = 1;
			   }
			   else {									//rank(t) < this.rank
				   ans = this.root.getHeight() - tHead.getHeight() + 1;
				   joinCase4(x, t);
			   }
		   }
	   }
	   return ans; 
   }
   
   public void joinCase1(IAVLNode x, AVLTree t) {  // rank(t) > this.rank && keys(x, t) > keys()
	   IAVLNode tHead = t.getRoot();
	   IAVLNode temp = t.getRoot();
	   while(temp.getHeight() > this.root.getHeight())
		   temp = temp.getLeft();
	   x.setLeft(this.root);
	   x.setRight(temp);
	   x.setSize(this.size() + temp.getSize() + 1);
	   x.setHeight(this.root.getHeight() + 1);
	   this.root.setParent(x);
	   IAVLNode tempP = temp.getParent();
	   x.setParent(tempP);
	   this.root = tHead;
	   tempP.setLeft(x);
	   temp.setParent(x);
	   joinSizeFix(tempP, x.getSize() - temp.getSize());
	   insertRebalance(tempP);
   }
   
   public void joinCase2(IAVLNode x, AVLTree t) {  // rank(t) < this.rank && keys(x, t) < keys()
	   IAVLNode tHead = t.getRoot();
	   IAVLNode temp = this.root;
	   while(temp.getHeight() > tHead.getHeight())
		   temp = temp.getRight();
	   x.setLeft(temp);
	   x.setRight(tHead);
	   x.setHeight(tHead.getHeight() + 1);
	   x.setSize(temp.getSize() + tHead.getSize() + 1);
	   IAVLNode tempP = temp.getParent();
	   tHead.setParent(x);
	   x.setParent(tempP);
	   tempP.setRight(x);
	   temp.setParent(x);
	   joinSizeFix(tempP, x.getSize() - temp.getSize());
	   insertRebalance(tempP);
   }
   
   public void joinCase3(IAVLNode x, AVLTree t) {  // rank(t) > this.rank && keys(x, t) < keys()
	   IAVLNode tHead = t.getRoot();
	   IAVLNode temp = tHead;
	   while(temp.getHeight() > this.root.getHeight())
		   temp = temp.getRight();
	   x.setLeft(temp);
	   x.setRight(this.root);
	   x.setHeight(this.root.getHeight() + 1);
	   x.setSize(temp.getSize() + this.size() + 1);
	   IAVLNode tempP = temp.getParent();
	   tempP.setRight(x);
	   x.setParent(tempP);
	   this.root.setParent(x);
	   temp.setParent(x);
	   this.root = tHead;
	   joinSizeFix(tempP, x.getSize() - temp.getSize());
	   insertRebalance(tempP);
   }
   
   public void joinCase4(IAVLNode x, AVLTree t) {
	   IAVLNode tHead = t.getRoot();
	   IAVLNode temp = this.root;
	   while(temp.getHeight() > tHead.getHeight())
		   temp = temp.getLeft();
	   x.setLeft(tHead);
	   x.setRight(temp);
	   x.setHeight(tHead.getHeight() + 1);
	   x.setSize(temp.getSize() + tHead.getSize() + 1);
	   IAVLNode tempP = temp.getParent();
	   x.setParent(tempP);
	   tempP.setLeft(x);
	   temp.setParent(x);
	   tHead.setParent(x);
	   joinSizeFix(tempP, x.getSize() - temp.getSize());
	   insertRebalance(tempP);
   }
   
   /**
    * fix size after join
    * @param x != null
    * @param s >= 0
    */
   public void joinSizeFix(IAVLNode x , int s) { 
	   x.setSize(x.getSize() + s);
	  while(x.getParent() != null) {
		   x = x.getParent();
		   x.setSize(x.getSize() + s);
	  }
   }
   
   
   public int forMeasurement() {
	   IAVLNode x = this.root.getLeft();
	   while(x.getRight().getKey() != EXT.getKey())
		   x = x.getRight();
	   return x.getKey();
   }

	/**
	   * public interface IAVLNode
	   * ! Do not delete or modify this - otherwise all tests will fail !
	   */
	public interface IAVLNode{	
		public int getKey(); //returns node's key (for virtuval node return -1)
		public String getValue(); //returns node's value [info] (for virtuval node return null)
		public void setLeft(IAVLNode node); //sets left child
		public IAVLNode getLeft(); //returns left child (if there is no left child return null)
		public void setRight(IAVLNode node); //sets right child
		public IAVLNode getRight(); //returns right child (if there is no right child return null)
		public void setParent(IAVLNode node); //sets parent
		public IAVLNode getParent(); //returns the parent (if there is no parent return null)
		public boolean isRealNode(); // Returns True if this is a non-virtual AVL node
    	public void setHeight(int height); // sets the height of the node
    	public int getHeight(); // Returns the height of the node (-1 for virtual nodes)
    	public void setKey(int k);
    	public void setValue(String v);
    	public void setSize(int size); // sets the size of the node
    	public int getSize(); // returns the size of the node
	}

   /**
   * public class AVLNode
   *
   * If you wish to implement classes other than AVLTree
   * (for example AVLNode), do it in this file, not in 
   * another file.
   * This class can and must be modified.
   * (It must implement IAVLNode)
   */
  public class AVLNode implements IAVLNode{
	  	
	  	private int key;
	  	private String info;
	  	private int height;
	  	private int size;
	  	
	  	private IAVLNode parent;
	  	private IAVLNode left;
	  	private IAVLNode right;
	  	
	  	public AVLNode(int key, String info, int height)
	  	{
	  		this.key = key;
	  		this.info = info;
	  		this.height = height;
	  		this.parent = null;
	  		this.left = null;
	  		this.right = null;
	  		this.size = 0;
	  	}
	  	
	  	public void setKey(int k) {
	  		this.key = k;
	  	}
	  	public void setValue(String v) {
	  		this.info = v;
	  	}
		public int getKey()
		{
			return this.key;
		}
		public String getValue()
		{
			return this.info;
		}
		public void setLeft(IAVLNode node)
		{
			this.left = node;
		}
		public IAVLNode getLeft()
		{
			return this.left;
		}
		public void setRight(IAVLNode node)
		{
			this.right = node;
		}
		public IAVLNode getRight()
		{
			return this.right;
		}
		public void setParent(IAVLNode node)
		{
			this.parent = node;
		}
		public IAVLNode getParent()
		{
			return this.parent;
		}
		// Returns True if this is a non-virtual AVL node
		public boolean isRealNode()
		{
			if(this.info == null)
				return false;
			return true;
		}
	    public void setHeight(int height)
	    {
	      this.height = height;
	    }
	    public int getHeight()
	    {
	      return this.height;
	    }
	    public void setSize(int size)
	    {
	      this.size = size;
	    }
	    public int getSize()
	    {
	      return this.size;
	    }
  	}
}
  

