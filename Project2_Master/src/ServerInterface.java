import java.io.FileNotFoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote{
	public void helperAlive(String ip, int assignport, int hbport) throws RemoteException, FileNotFoundException;
	}
