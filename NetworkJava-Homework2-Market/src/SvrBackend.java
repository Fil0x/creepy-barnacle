
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SvrBackend extends Remote {
    public void register(String name, ClientCallback callbackClientObj) throws RemoteException;

    public void unregister(String name, ClientCallback callbackClientObj) throws RemoteException;

    public void sell(String name, String itemID) throws RemoteException;

    public void buy(String name, String itemID) throws RemoteException;

    public void all_items(String name) throws RemoteException; // Probably not void

    public void place_wish(String name, String itemname, String max_price) throws RemoteException;
}