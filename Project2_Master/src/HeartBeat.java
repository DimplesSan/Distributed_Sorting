import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Set;


public class HeartBeat implements Runnable{

	Set<String> keySet;
	String currentIP;
	int currentPort;
	Registry rgs;
	HeartBeatInterface hbInter;
	
	@Override
	public void run() {

			while(true && !Server.finalMerge){
				
					if(Server.helperProfileTable.size() == 0){
						try {
							Thread.sleep(2500);
						} catch (Exception e) {
							System.out.println(e.getMessage());
						}
					}
					else{
						
						keySet = Server.helperProfileTable.keySet();
						
						for (String helperIpAddr : keySet) {
							
							currentIP = helperIpAddr;
							currentPort = Server.helperProfileTable.get(currentIP).heartbeatport;
							try{
								
								rgs = LocateRegistry.getRegistry(currentIP, currentPort);
								hbInter = (HeartBeatInterface) rgs.lookup("HeartBeat");
								hbInter.isAlive();
								
								Thread.sleep(1000);

							} catch(Exception e){
								
								System.err.println("Node with IP: "+currentIP+" is down!");
								
								//Add the helper profile to the list of failed activites 
								Server.deadLists.add(Server.helperProfileTable.get(currentIP));
								
								//Remove the helper from the list of helpers that are alive. 
								Server.helperProfileTable.remove(currentIP);
								
//								spawn a child thread that tries to restart the current ip
//								new Thread().start();
							}
						}
					}
			}
			
			System.out.println("Heartbeat check terminated.");
			
			
	}

}
