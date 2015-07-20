import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.TreeMap;

interface HelperService extends Remote {

    public Queue opArrQ = new ArrayDeque<ArrayList <Integer> >();

//    public ChunkWrapper partition(List<Integer> list, int pivot1, int pivot2) throws RemoteException;
    public List<Integer> sortList(List<Integer> list) throws RemoteException;
    
    public TreeMap<Integer, Integer> sortChunks(List<Integer> list, TreeMap<Integer, Integer> tree) throws RemoteException;
    public ArrayList<Item> sortWithBinaryInsertion(List<Integer> ipList, ArrayList<Item> sortedItemList) throws RemoteException;
    
}

