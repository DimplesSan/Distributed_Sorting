import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;




import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;


public class Server extends UnicastRemoteObject implements ServerInterface{
	
	public static String pathOfPropFile;
	public Registry rgs;
	public String ownIP;
	public int ownPort;
	private static final long serialVersionUID = 1L;
	public static File objUnsortedFile;
	public static String inputFile, outputDir;
	
	public static ConcurrentHashMap<String, Helperprofile> helperProfileTable;
	static ConcurrentLinkedQueue<Helperprofile> deadLists;
	
	public static boolean assignChunks, finalMerge;
	
	public Server(String pathHelperDetails, String masterIP) throws RemoteException{
		
		ownPort = 6656;
		rgs = LocateRegistry.createRegistry(ownPort);
		rgs.rebind("master", this);
//		ownIP = Inet4Address.getLocalHost().getHostAddress();
		ownIP = masterIP;
		pathOfPropFile = pathHelperDetails;
		helperProfileTable =  new ConcurrentHashMap<String, Helperprofile>();
		deadLists = new ConcurrentLinkedQueue<Helperprofile>();
		assignChunks = true;
		finalMerge =false;
	}
	
	public static void main(String[] args)throws NumberFormatException, IOException, NotBoundException{
		
		if(args.length != 2)
			System.out.println("Execute the server with following args: \n 1) path of the properties file \n 2) IP address of master");
		else{
			Server objServer =  new Server(args[0], args[1]);
//			Server objServer =  new Server("/home/pi/Group4/Project2_Master/src/HelperDetails.properties", 
//											"10.10.10.104");
			
			//Accept file inputs from the user who is running the server
			Scanner scan = new Scanner(System.in);
			System.out.println("Give path for input file: ");
			inputFile = scan.nextLine();
			System.out.println("Give path for output files: ");
			outputDir = scan.nextLine();
			
//			objServer.intiHelpers(pathOfPropFile);
			
			System.out.println("Server started.");
//			start a thread that checks if the helpers are alive
			new Thread(new HeartBeat()).start();
			
			//start to assign chunks
			new Thread(new AssignChunks()).start();
			
			
			//tempTest Code
//			LinkedList<Integer>temp = new  LinkedList<Integer>();
//			TreeMap< Integer, Integer> tempMap = new TreeMap<Integer, Integer>();
//			
//			BufferedReader objBr = new BufferedReader(new FileReader(inputFile));
//			for(int i=0; i<1000; ++i){
//				temp.add(Integer.parseInt(objBr.readLine()) );
//			}
//			System.out.println("Start of test"+ tempMap +" ___ " +temp.size() );
//			Registry rgs = LocateRegistry.getRegistry("10.10.10.104",6657);
//			HelperService hps = (HelperService) rgs.lookup("Helper");
//			
//			TreeMap<Integer, Integer> opMap= hps.sort(temp, tempMap);
//			
//			System.out.println("Returned map:"+ opMap.size());
			
		}
		
	}

	@Override
	public void helperAlive(String ip, int assignport, int hbport)throws RemoteException, FileNotFoundException {
		
		System.out.println("Helper alive at: " +ip + " Port: " +assignport +" with heartbeat port: "+ hbport  );
		new Thread(new HelperAlive(ip, assignport, hbport)).start();	// A new thread for every time a helper registers with the client
	}
	
	public static void intiHelpers(String propFilePath){

		try{
			JSch jsch=new JSch();
			Session session;
			
			Properties objHelperDetails = new  Properties();
			objHelperDetails.load((new FileInputStream(new File(propFilePath))) ); 	//Read from the Helper Details properties file
			System.out.println("Helper details properties file read.");
			
			Enumeration enuKeys = objHelperDetails.keys();
			int counter = 0;
			while(enuKeys.hasMoreElements()){	// Repeat the following for each of the helpers from the list in the properties file
				
				String key = (String)enuKeys.nextElement();
				String helperDetails = objHelperDetails.getProperty(key).trim();
				System.out.println("Details are:"+ helperDetails);
				
				String[] arrDetails = helperDetails.split("#");		//Extract details of the helper

				String ipAddr = arrDetails[0];
				String usrNm =  arrDetails[1];
				String passWrd =  arrDetails[2];
				String command =  arrDetails[3];
				
				session = jsch.getSession(usrNm, ipAddr, 22);
				session.setPassword(passWrd);
				
				boolean successFlag = execInitCommand(session,command, counter);	//Execute the specified command 
				if(!successFlag){
					System.out.println("ERROR: Could not initiate helper with the ip: "+ ipAddr);
				}
					
				else{
					System.out.println("Helper with ip: " + ipAddr + " initialized.");
					counter = counter + 1;
				}
					
				
				session.disconnect();
			}
			
		}catch(Exception objEx){
			System.out.println(objEx.getMessage());
		}
		
	}
	
	public static boolean execInitCommand(Session session, String command, int ctr){
		boolean flag = true;
		try {
				Properties config = new Properties();
				config.put("StrictHostKeyChecking", "no");
				session.setConfig(config);
			
				session.connect();
				ChannelExec channel =(ChannelExec) session.openChannel("exec");

				BufferedReader in  = new BufferedReader(new InputStreamReader(channel.getInputStream()));
			
				channel.setCommand(command);	
				channel.connect();
				String msg=null;  
//				while((msg=in.readLine())!=null){	//Msg from the command line of the helper
//				if(ctr % 2 == 0)
//					System.out.println(in.readLine());
//				else{
//					System.out.println(in.readLine());
//					System.out.println(in.readLine());
//				}
					
//			   }
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			flag =false;	
		}
		
		return flag;
	}
}
