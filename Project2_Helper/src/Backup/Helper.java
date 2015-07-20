import java.io.FileNotFoundException;
import java.net.*;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class Helper extends UnicastRemoteObject implements HelperService {

    public static final long serialVersionUID = 1L;
    
    public Registry masterReg, currentHelperReg;
    public final int portNumOfHelper = 6657;
    public String ipOfCurrentHelper, masterip;
    public static long chunkCounter;
    public ArrayList<Item> list;
    
    public Helper() throws RemoteException{
    	
    	bindHelperTOReg();
        ipOfCurrentHelper = "10.10.10.121";
        masterip = "10.10.10.19";

//        try{
//            ipOfCurrentHelper = InetAddress.getLocalHost().getHostAddress(); //Helper IP
    		
            System.out.println("Helper IP is: "+ipOfCurrentHelper);

//        }
//        catch(UnknownHostException objUHE){
//            System.out.println(objUHE.getMessage());
//        }
            
    }

    //Constructor for starting the helper from the Master
    public Helper(String ipAddrHelper, String ipAddrMaster) throws AccessException, RemoteException {
    	
    	bindHelperTOReg();
    	ipOfCurrentHelper = ipAddrHelper;
    	masterip = ipAddrMaster;

   	
	}

    //Constructor for starting the Helper from it's own command line 
	public Helper(String ipAddrHelper) throws AccessException, RemoteException{
		
		bindHelperTOReg();
	    ipOfCurrentHelper = ipAddrHelper;
		masterip = "10.10.10.19";
	}

	public void bindHelperTOReg()throws AccessException, RemoteException{
		
        currentHelperReg = LocateRegistry.createRegistry(portNumOfHelper);
        currentHelperReg.rebind("Helper", this);
        chunkCounter = 0;

	}
	
	
	
	public static void main (String [] args){

    	System.out.println("Helper Started");
    	Helper objHelper;
    	try{
    		
        	if(args.length == 2 )
        		objHelper= new Helper(args[0],args[1]);//If starting the helper from the server - ipOfCurrentHelper & ipOfMaster
        		
        	else if(args.length == 1)    		
        		objHelper= new Helper(args[0]);//If starting directly from the command line
        	else
        		objHelper= new Helper();//Starting helper with default value
        	
        	
            objHelper.start();
            
    	}catch(Exception objEx){
    		System.out.println(objEx.getMessage());
    	}
    	

    }

	
	
	
    public void start() throws RemoteException,NotBoundException, UnknownHostException, FileNotFoundException{
        
        masterReg = LocateRegistry.getRegistry(masterip, 6656);
        ServerInterface objServer = (ServerInterface)masterReg.lookup("master");
        System.out.println("Master Lookup completed at ip: "+ masterip +" on port: "+ 6656);
        
        objServer.helperAlive(ipOfCurrentHelper,portNumOfHelper, 6658);
        System.out.println("Master Notified");
    }

    
    
    
    @Override
    public TreeMap<Integer, Integer> sort(List<Integer> list, TreeMap<Integer, Integer> tree) throws RemoteException{
    	
    	System.out.println("Sort method: Incoming list size is: "+ list.size());
    	
		for (Integer i : list) {
			if(tree.containsKey(i))
				tree.put(i, tree.get(i)+1);
			else
				tree.put(i,1);
		}
		
		System.out.println("Chunk number "+ chunkCounter + "added to treemap.");
		
		return tree;
	}
    
    @Override
    public ArrayList<Item> sortWithBinaryInsertion(List<Integer> ipList, ArrayList<Item> sortedItemList) throws RemoteException{

    	list = sortedItemList;

    	for(int num : ipList)
    		this.add(num);
    		
		return list;

    }
    
	public void add(int input){
		
		if(list.size() == 0){
			list.add(new Item(input, 1));
			return;
		}
		int index = getIndex(input);
		if(index == list.size()){
			list.add(list.size(), new Item(input, 1));
			return;
		}
		if(list.get(index).key == input)
			++list.get(index).value;
		else
			list.add(index, new Item(input, 1));
	}
	public int getIndex(int input){
		if(list.size() == 0)
			return 0;
		int low = 0;
		int high = list.size() - 1;
		int mid;
		
		while(true){
			mid = (high +low) / 2;
			if(list.get(mid).key == input)
				return mid;
			if(low == mid)
				if(list.get(mid).key > input)
					return mid;
			if(list.get(mid).key < input){
				low = mid + 1;
				if(low > high)
					return mid +1;
			}
			else if(low > high)
				return mid;
			else
				high = mid - 1;
			}
		}
    
	
	
	
	
	
	
	
	
	
	
	
//-------------------------------------------------------------------------------------------------------------------------------------    
//Code for 3 way partionting and sorting the chunks using merge sort for the base case file size 
    @Override
//    public ChunkWrapper partition(List<Integer> list, int pivot1, int pivot2) throws RemoteException {
//
//        System.out.println("Partition Started for pivots: "+ pivot1+" " +pivot2);
//
//        ArrayList<Integer> part1 = new ArrayList<Integer>();
//        ArrayList<Integer> part2 = new ArrayList<Integer>();
//        ArrayList<Integer> part3 = new ArrayList<Integer>();
//        ArrayList<Integer> partitionedArrList = new ArrayList<Integer>();
//
//        int minOfTwoPivots = pivot1;
//        int maxOfTwoPivots = pivot2;
//
//        if(pivot1 < pivot2) {
//            minOfTwoPivots = pivot1;
//            maxOfTwoPivots = pivot2;
//        }
//        else {
//            minOfTwoPivots = pivot2;
//            maxOfTwoPivots = pivot1;
//        }
//
//        for(int num:list) {
//            if (num < minOfTwoPivots)
//                part1.add(num);
//
//            else {
//                if (num < maxOfTwoPivots)
//                    part2.add(num);
//
//                else
//                    part3.add(num);
//
//            }
//        }
//
//        ChunkWrapper objChunk = new ChunkWrapper(part1,part2, part3);
//        //opArrQ.add(objChunk);
//
//        System.out.println("Partition Completed for pivots: "+ pivot1+" " +pivot2);
//        return objChunk;
//    }

    public List<Integer> sortList(List<Integer> input) throws RemoteException{
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int i : input)
			list.add(i);
		list = merge(list);
		input.clear();
		for(int i : list)
			input.add(i);
		return input;
	}
	private ArrayList<Integer> merge(ArrayList<Integer> list){
		if(list.size() == 1)
			return list;
		ArrayList<Integer> A = new ArrayList<Integer>();
		ArrayList<Integer> B = new ArrayList<Integer>();
		for(int i = 0; i < list.size(); ++i){
			if(i < list.size()/2)
				A.add(list.get(i));
			else
				B.add(list.get(i));
		}
		A = merge(A);
		B = merge(B);
		return mergeSort(A,B);
	}
	private ArrayList<Integer> mergeSort(ArrayList<Integer> A, ArrayList<Integer> B){
		ArrayList<Integer> C = new ArrayList<Integer>();
		int i, j, k;
		i = j = 0;
		while(i < A.size() && j < B.size()){
			if(A.get(i) < B.get(j)){
				C.add(A.get(i));
				++i;
			}
			else{
				C.add(B.get(j));
				++j;
			}
		}
		if(i < A.size()){
			for(int x = i; x < A.size(); ++x)
				C.add(A.get(x));
		}
		else
			for(int y = j; y < B.size(); ++y)
				C.add(B.get(y));
		return C;
	}
	
}
