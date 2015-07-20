import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class AssignChunks implements Runnable {

//	File file;
	FileReader fileread;
	BufferedReader buffRead;
	List<Integer> list, tempList;
	Set<String> keySet;
	String helperIP;
	Helperprofile profileTemp;
	TreeMap <Integer, Integer> tempMap;
	boolean fromDeadQueue;
	boolean fromFile;
	
	public AssignChunks(){
		list =new ArrayList<Integer>();

		tempMap = new TreeMap<Integer, Integer>();
		fromDeadQueue =false;
		fromFile = false;
	}
	
	@Override
	public void run() {
				
		//Check the queue if it's full then go to sleep
		//Else Read the file, and create a chunk
		//enter the chunk in a queue on the server 
		System.out.println("AssignChunk: Started.");
		int counter = 0;
		String num;
		try {
			fileread =  new FileReader(Server.inputFile);
			buffRead = new BufferedReader(fileread);
			
			//Repeat till all jobs are assigned
			while(Server.assignChunks == true){
				
				//Read till the end of the file
				while( (num = buffRead.readLine()) != null ){
					
						//Check the deadlist at the start of every new chunk
						if( (fromFile == false) && Server.deadLists.size() > 0 ){
							
									fromFile = false;
									
									System.out.println("AssignChunks: Deadlist contains few elements "+ Server.deadLists.size());
									Helperprofile deadProfile = Server.deadLists.peek();
									TreeMap<Integer, Integer> treeFromDeadProfile = deadProfile.sortedMapofHelper;
									List<Integer> listFromDeadProfile = new ArrayList<Integer>();
									while (! helperAvailable()) {
										try {
			//								System.out.println("Waiting for a helper to be available");
											Thread.sleep(500);
											
										} catch (InterruptedException e) {
											System.out.println(e.getMessage());
										}
									}
									
									
									
									profileTemp.sortedMapofHelper = mergeMaps(profileTemp.sortedMapofHelper, treeFromDeadProfile);
									if(!deadProfile.hasreturn){
										
										for(Integer i : deadProfile.sentList)
											listFromDeadProfile.add(i);
										
										profileTemp.sentList = listFromDeadProfile;
									}
									
									
									profileTemp.hasreturn =false;
									Server.deadLists.poll();
									new Thread(new Sendchunks(helperIP,profileTemp)).start();
									
								
						}
					
						//Deadlist is zero - Proceed with making new chunk
						else{
							
									fromFile = true;
									counter++;
									list.add(Integer.parseInt(num) );
									
									if (counter == 100000){
											
											fromFile = false;
											
											while (! helperAvailable()) {
													try {
														Thread.sleep(500);
													} catch (InterruptedException e) {
														System.out.println(e.getMessage());
													}
											}
											
											ArrayList<Integer> temporary = new ArrayList<>();
											for (Integer integer : list)
													temporary.add(integer);

											
											profileTemp.sentList = temporary;
											counter = 0;
											
											profileTemp.hasreturn =false;
											new Thread(new Sendchunks(helperIP,profileTemp)).start();
											list.clear();
										
									}
						}
				}
				
				//Check for last chunk from the file
				if(list.size() >0 ){
					
						while (! helperAvailable()) {
								try {
									Thread.sleep(500);
								} catch (InterruptedException e) {
									System.out.println(e.getMessage());
								}
						}
				
						ArrayList<Integer> temporary = new ArrayList<>();
						for (Integer integer : list)
								temporary.add(integer);

				
						profileTemp.sentList = temporary;
						counter = 0;
						
						profileTemp.hasreturn =false;
						new Thread(new Sendchunks(helperIP,profileTemp)).start();
						list.clear();
						
				}
				
				//Check for pending jobs
				while(Server.deadLists.size()>0){	
					
						
						System.out.println("AssignChunks: Deadlist contains few elements "+ Server.deadLists.size());
						Helperprofile deadProfile = Server.deadLists.peek();
						TreeMap<Integer, Integer> treeFromDeadProfile = deadProfile.sortedMapofHelper;
						List<Integer> listFromDeadProfile = new ArrayList<Integer>();
						while (! helperAvailable()) {
								try {
									Thread.sleep(500);
									
								} catch (InterruptedException e) {
									System.out.println(e.getMessage());
								}
						}
						
						
						
						profileTemp.sortedMapofHelper = mergeMaps(profileTemp.sortedMapofHelper, treeFromDeadProfile);
						if(!deadProfile.hasreturn){
							
								for(Integer i : deadProfile.sentList)
									listFromDeadProfile.add(i);
								
								profileTemp.sentList = listFromDeadProfile;
						}
						
						
						profileTemp.hasreturn =false;
						Server.deadLists.poll();
						new Thread(new Sendchunks(helperIP,profileTemp)).start();

			
				}
				
				
				if(num == null && Server.deadLists.size() == 0 ){
					Server.assignChunks = false;
					System.out.println("All jobs have been assigned.");
				}
				
				
			}

		
		
		//Start the final merge
		finalMerge();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public  void finalMerge() throws IOException {
		
		boolean allFinished = false;
		
		Set<String> tempSet = Server.helperProfileTable.keySet();
		String _ip = "";
		int numOfHelpers = tempSet.size();
		int counter;
		
		while(!allFinished){
			//Check if the helpers have finished their work
			counter = 0;
			for(String ip :  tempSet){
				
				if(!Server.helperProfileTable.get(ip).hasreturn)
					break;
				
				_ip = ip;
				++counter;
			}
			
			if(counter == tempSet.size() && Server.helperProfileTable.get(_ip).hasreturn)
				allFinished =true;
			
			try{
				Thread.sleep(500);
			}
			catch(Exception objEx){
				System.out.println(objEx.getMessage());
			}
		}
		
		System.out.println("Final merge started.");
		
		Server.finalMerge =true;
		
		TreeMap<Integer, Integer> finalTreeMap = null;
		
		boolean firstVal = true;
		for(String ip : tempSet){
			if (firstVal){
				finalTreeMap = Server.helperProfileTable.get(ip).sortedMapofHelper;
				firstVal = false;
			}
			else{
				finalTreeMap = mergeMaps(finalTreeMap, Server.helperProfileTable.get(ip).sortedMapofHelper);
			}
				
		}
			
		System.out.println("Final merge completed.");
		
		finalWrite(finalTreeMap);
		
		
	}

	public void finalWrite(TreeMap<Integer, Integer> finalTreeMap) throws IOException {
		
		long total = 0;
		System.out.println("Sorting ended. File writing begun.");
		
		File output = new File(Server.outputDir+"/sortedFile.txt");
		FileWriter writer = new FileWriter(output);
		BufferedWriter buff = new BufferedWriter(writer, 1024*1024);
		for(Map.Entry<Integer, Integer> i : finalTreeMap.entrySet()){
			
//			System.out.println("FileWrite: "  +i.getKey().toString());
			total = total + i.getKey();
			for(int j = 0; j < i.getValue(); ++j){
				buff.write(i.getKey().toString());
				buff.newLine();
			}
		}
		
		double avg = total/finalTreeMap.size();
		
		System.out.println("Average of the given input file is: "+avg);
		
		buff.close();
		
		System.out.println("Sorted file written.");
	}

	public boolean helperAvailable() {
		
		if (Server.helperProfileTable.size() == 0) 
			return false;
		else {
			
			keySet = Server.helperProfileTable.keySet();
			for (String tempkey : keySet) {
				
				if (Server.helperProfileTable.get(tempkey).hasreturn == true) {
					helperIP = tempkey;
					profileTemp = Server.helperProfileTable.get(tempkey);
					return true;
				}
			}
		}
		return false;
	}
	
	public TreeMap<Integer, Integer> mergeMaps(TreeMap<Integer, Integer> A,
			TreeMap<Integer, Integer> B) {
		for (Map.Entry<Integer, Integer> i : B.entrySet()) {
			if (A.containsKey(i.getKey()))
				A.put(i.getKey(), A.get(i.getKey()) + i.getValue());
			else
				A.put(i.getKey(), i.getValue());
		}
		return A;
	}



}
