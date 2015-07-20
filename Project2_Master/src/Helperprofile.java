import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;


public class Helperprofile extends UnicastRemoteObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5037928425963527212L;
	public List<Integer> sentList;
	public int assignport;
	public int heartbeatport;
	public boolean hasreturn;
	public TreeMap<Integer, Integer> sortedMapofHelper;
	
	public Helperprofile(int assignport,int heartbeatport, boolean listreturn) throws RemoteException{
		System.out.println("Helper profile built");
		this.sentList = new LinkedList<Integer>();
		this.assignport = assignport;
		this.heartbeatport = heartbeatport;
		this.hasreturn = listreturn;
		this.sortedMapofHelper = new TreeMap<Integer, Integer>();
		System.out.println("HP: Helper map size "+sortedMapofHelper.size());
	}

}