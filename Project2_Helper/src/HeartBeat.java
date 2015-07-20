import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class HeartBeat extends UnicastRemoteObject implements HeartBeatInterface{

    HeartBeat() throws RemoteException{

    }

    public static void main(String [] args){
        try{
            HeartBeat objHeartBeat = new HeartBeat();

            Registry objReg = LocateRegistry.createRegistry(6658);
            objReg.rebind("HeartBeat",objHeartBeat);
        }
        catch(Exception objEx){
            objEx.getMessage();
        }
        System.out.println("Heart beat started at port 6658");
    }

    @Override
    public boolean isAlive() throws RemoteException{
        return true;
    }
}

interface HeartBeatInterface extends Remote{
    public boolean isAlive() throws RemoteException;
}