import java.util.*;
import java.io.*;

class NodePoint //Defines the structure of the nodepointand all its fields.
{
    int data;
    String key;
    NodePoint previousNode;
	NodePoint nextNode;
	NodePoint parent;
    NodePoint child;
    boolean childCut = false;
    int deg = 0;

    NodePoint(int d, String k) // Parameterized constructor, to initialize a node point with data and key given as arguments.
    {
        data = d;
        key = k;
        previousNode = this;  // Initializing the pointer to the previous nodepoint of heap as this one being initialized
        nextNode = this;  // Initializing the pointer to the next nodepoint of heap as this one being initialized

    }

}

class HelpFuncs{//Functions used to help with main commands like inser, deleteMax etc.
    public static NodePoint concatinate(NodePoint x, NodePoint y) {//To concatinate to nodepoints amd return the bigger one
		if (x == null && y == null) {
			return null;
		} else if (x != null && y == null) { 
			return x;
		} else if (x == null && y != null) { 
			return y;
		} else {
			NodePoint xnew = x.nextNode; 
			x.nextNode = y.nextNode;
			x.nextNode.previousNode = x;
			y.nextNode = xnew;
			y.nextNode.previousNode = y;

			if(x.data>y.data){
               return x;
            }
            else{
                return y;
            }
		}
    }
    
}

class HeapCommands{// The primary commands to implement and maintain the fibonacci heap
	
	public NodePoint max = null;// Initializing the max pointer of the heap to null.

    public Queue q = new LinkedList();
    HashMap<String, NodePoint> hash; //HashMap builtin implementation of java.
    int size = 0;

    public NodePoint insert(int value, String key) { //To insert new values to the fibonacciheap.
		NodePoint ans = new NodePoint(value, key);
		max = HelpFuncs.concatinate(max, ans);
		++size;

		return ans; //returns the new nodepoint object with particular value and key.
    }
    
    public void increaseKey(NodePoint nodePoint, int val) { // To increment the nodepoint value by 1 and then call the cascadeCut function if new value is greter than its parent 
		nodePoint.data += val;

		if (nodePoint.parent != null && nodePoint.data >= nodePoint.parent.data){
			CascadeCut(nodePoint);
		}
		if (nodePoint.data >= max.data){
			max = nodePoint; //Updating the max pointer of the heap.
		}
    }

    public static HeapCommands merge(HeapCommands x, HeapCommands y) {// To merge the two heaps x and y and return the result.
		HeapCommands result = new HeapCommands();
		result.max = HelpFuncs.concatinate(x.max, y.max);// getting the max of the two max pointers of x and y through the concatinate helper function.
		result.size = x.size + y.size;

		x.size = y.size = 0;
		x.max = null;
		y.max = null; 
		
		return result;
    }
    private void CascadeCut(NodePoint nodePoint) { //To implement cascading cut of nodepoints according to their childCut value.
		nodePoint.childCut = false;

		if (nodePoint.parent == null)
			return;

		if (nodePoint.nextNode != nodePoint) { 
			nodePoint.nextNode.previousNode = nodePoint.previousNode;
			nodePoint.previousNode.nextNode = nodePoint.nextNode;
		}

		if (nodePoint.parent.child == nodePoint) {
			if (nodePoint.nextNode != nodePoint) {
				nodePoint.parent.child = nodePoint.nextNode;
			}else {
				nodePoint.parent.child = null;
			}
		}

		nodePoint.parent.deg-=1; //after removal of that nodepoint, reduce degree by 1
        nodePoint.previousNode = nodePoint.nextNode = nodePoint;
		max = HelpFuncs.concatinate(max, nodePoint);//finding the max of the current max and the new nodepoint through the concatenate function

        if (nodePoint.parent.childCut == true)//recursively calling and removing subtrees rooted at nodepoint with true childcut
			CascadeCut(nodePoint.parent);
		else
			nodePoint.parent.childCut = true; //converting the false value of the parent's childCut to true.

		nodePoint.parent = null;
    }
    
    public NodePoint deleteMax() {// To delete that element which currently has the max value in the heap and then adjusting the rest of the heap
		if (max == null){
			throw new NoSuchElementException("Empty heap Exception"); // base case
		}
		size-=1; //decreasing the size by 1.

		NodePoint temp = max;

		if (max.nextNode == max) { 
			max = null;
		} else { 
			max.previousNode.nextNode = max.nextNode;//exchanging the value of the max, its previous and next nodes
			max.nextNode.previousNode = max.previousNode;
			max = max.nextNode;
		}

		if (temp.child != null) {
			NodePoint curr = temp.child;
			do {
				curr.parent = null;
				curr = curr.nextNode;
			} while (curr != temp.child);
		}

		
		max = HelpFuncs.concatinate(max, temp.child); //updating current max through concatination

		if (max == null) 
			return temp;

		/* To implement the pairwise combine, the below code is used.
		   The Concatenate function is called multiple times.
		*/
		List<NodePoint> treeT = new ArrayList<NodePoint>();

		List<NodePoint> cnode = new ArrayList<NodePoint>();

		for (NodePoint curr = max; cnode.isEmpty() || cnode.get(0) != curr; curr = curr.nextNode)
			cnode.add(curr);

		for (NodePoint curr : cnode) {
			while (true) {//infinite loop until no same degree trees(root nodePoints) are left.
				while (curr.deg >= treeT.size()){
					treeT.add(null);
				}

				if (treeT.get(curr.deg) == null) {
					treeT.set(curr.deg, curr);
					break;
				}

				NodePoint other = treeT.get(curr.deg);
				treeT.set(curr.deg, null); 

				NodePoint min = (other.data < curr.data) ? other : curr;
                NodePoint max = (other.data < curr.data) ? curr : other;
                
				min.nextNode.previousNode = min.previousNode;
                min.previousNode.nextNode = min.nextNode;
                
				min.nextNode = min.previousNode = min;
				max.child = HelpFuncs.concatinate(max.child, min);
				min.parent = max;

				min.childCut = false;
				max.deg+=1;
				curr = max;
			}
			if (curr.data >= max.data){
				max = curr;
			}
		}
		return temp;//return the deleted max
	}
}

public class hashtagcounter {
    public static void main(String[] args) throws Exception {

		HeapCommands obj = new HeapCommands(); //initaillizing object to implement the heap commands
		obj.hash = new HashMap<String, NodePoint>();
		String s;

		File fname = new File(args[0]);//name of the file to be read from(input file)
		FileInputStream fstream = new FileInputStream(fname);
		BufferedReader breader = new BufferedReader(new InputStreamReader(fstream));
		FileReader freader = new FileReader(fname);
		
		if(args.length != 1 ){//For the case when there is an output file specified
			FileWriter w = new FileWriter(new File(args[1]));//Name of the file to write the output
			PrintWriter pw = new PrintWriter(w);
			while ((s = breader.readLine()) != null && !s.equals("STOP")) {//stop when there is a STOP in the line
				String[] sarr = s.split(" ");
				if (s.indexOf('#') != -1) {// when there is a hashtag in the line
					sarr[0] = sarr[0].substring(1);
					int val = Integer.parseInt(sarr[1]);
					if (obj.hash.containsKey(sarr[0])) {
						obj.increaseKey(obj.hash.get(sarr[0]).nextNode, val);
					} else {
						NodePoint ins = obj.insert(val, sarr[0]);
						NodePoint pointerNode = new NodePoint(-1, null);
						pointerNode.nextNode = ins;
						obj.hash.put(sarr[0], pointerNode);
					}
				} 
				else{
					Integer n = Integer.parseInt(sarr[0]); 
					for (int i = 0; i < n; i++) {
						NodePoint currMax = obj.deleteMax();//deleting the first n max elements 
						pw.write(currMax.key);//writing in the file
						if (i < n-1 ) {
							pw.write(",");
						}
					obj.q.add(currMax);
					}
					while (!obj.q.isEmpty()) {//Adjusting the rest of the heap
						NodePoint ins = (NodePoint) obj.q.remove();
						NodePoint res = obj.insert(ins.data, ins.key);
						obj.hash.get(ins.key).nextNode = res;
					}

					pw.println();
					pw.flush();
				}
			}
		}
				
		else{//For the case when there is no output file specified
			while ((s = breader.readLine()) != null && !s.equals("STOP")) {
				String[] sarr = s.split(" ");
				if (s.indexOf('#') != -1) {//when there is a hashtag in the line
					sarr[0] = sarr[0].substring(1);
					int val = Integer.parseInt(sarr[1]);
					if (obj.hash.containsKey(sarr[0])) {
						obj.increaseKey(obj.hash.get(sarr[0]).nextNode, val);
					} else {
						NodePoint ins = obj.insert(val, sarr[0]);
						NodePoint pointerNode = new NodePoint(-1, null);
						pointerNode.nextNode = ins;
						obj.hash.put(sarr[0], pointerNode);
					}
				} 
				else{
					Integer n = Integer.parseInt(sarr[0]);
					for (int i = 0; i < n; i++) {
						NodePoint currMax = obj.deleteMax();//deleting the first n max elements 
						System.out.print(currMax.key);//Printing on the console
						if (i < n-1) {
							System.out.print(",");
						}
						obj.q.add(currMax);
					}
					System.out.println();
					while (!obj.q.isEmpty()) {//Adjusting the rest of the heap.
						NodePoint ins = (NodePoint) obj.q.remove();
						NodePoint res = obj.insert(ins.data, ins.key);
						obj.hash.get(ins.key).nextNode = res;
					}
				}
			}

		}
		breader.close();//Closing the readers
		freader.close();
	}	
}