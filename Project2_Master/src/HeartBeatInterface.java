import java.rmi.Remote;
import java.rmi.RemoteException;

interface HeartBeatInterface extends Remote{
    public boolean isAlive() throws RemoteException;
}