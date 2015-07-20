import java.rmi.RemoteException;
import java.util.TreeMap;


public class HelperAlive implements Runnable{


	//Map<String, Integer> aliveHelper;
	String ip;
	int assignport;
	int hbport;

	public HelperAlive(String ip, int assignport, int hbport) {
		super();
		this.ip = ip;
		this.assignport = assignport;
		this.hbport = hbport;
	}


	@Override
	public void run() {
		
		Helperprofile profile = null;
		try {
			profile = new Helperprofile(assignport, hbport, true);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Server.helperProfileTable.put(ip, profile);
		System.out.println("Helper with ip: "+ ip +" added to the helper profile table. Current size of table: " +Server.helperProfileTable.size() );
	}
}
