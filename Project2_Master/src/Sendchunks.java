import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;

public class Sendchunks extends UnicastRemoteObject implements Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5923262536387584467L;
	Helperprofile objHelperProfile;
	String ip;
	int port;	

	public Sendchunks(String helperIp, Helperprofile _objHelperProfile) throws RemoteException{

		this.objHelperProfile = _objHelperProfile;
		this.ip = helperIp;
		this.port = _objHelperProfile.assignport;
		
	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		try{
				System.out.println("Send chunks : Ip of helper: "+ip + " and "+port  );
				Registry rgs = LocateRegistry.getRegistry(ip, port);
				HelperService hps = (HelperService) rgs.lookup("Helper");
			
				//Server.deadLists.add(profile);
				System.out.println("Sending list of size "+objHelperProfile.sentList.size());
				System.out.println("Map size "+objHelperProfile.sortedMapofHelper.size());
				objHelperProfile.sortedMapofHelper = hps.sortChunks(objHelperProfile.sentList, objHelperProfile.sortedMapofHelper);
				objHelperProfile.hasreturn = true;
				objHelperProfile.sentList.clear();
				Server.helperProfileTable.put(ip,  objHelperProfile);
	
				System.out.println("Returned map of size"+objHelperProfile.sortedMapofHelper.size());
			
		} catch(Exception e){
			System.err.println("Helper "+ip+" has died " + e.getMessage() );
			e.printStackTrace();
			Server.deadLists.add(objHelperProfile);
		}
		
	}

}
