
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface SvrBackend extends Remote {
    public void register(String name, ClientCallback callbackClientObj) throws RemoteException;

    public void unregister(String name, ClientCallback callbackClientObj) throws RemoteException;

    public String sell(String name, String itemName, float price) throws RemoteException;

    public Item buy(String name, String itemID) throws RemoteException;

    public HashMap<String, Item> all_items() throws RemoteException;

    public void place_wish(String name, String itemname, float max_price) throws RemoteException;
}