import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientCallback extends Remote {
    void wish_item_appeared(String itemID, String name, float value, String msg)
            throws RemoteException;
    void item_sold(String itemname, float amount, String msg) throws RemoteException;

}
