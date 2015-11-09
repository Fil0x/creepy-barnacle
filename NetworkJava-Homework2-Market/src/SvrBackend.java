
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface SvrBackend extends Remote {
    void register(String name, ClientCallback callbackClientObj) throws RemoteException;

    void unregister(String name) throws RemoteException;

    String sell(String name, String itemName, float price) throws RemoteException;

    Item buy(String name, String itemID) throws RemoteException;

    HashMap<String, Item> all_items() throws RemoteException;

    void place_wish(String name, String itemname, float max_price) throws RemoteException;
}