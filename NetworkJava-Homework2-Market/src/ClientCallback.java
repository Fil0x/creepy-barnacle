import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientCallback extends Remote {
    public void wish_item_appeared(String itemID, String name, String value, String msg)
            throws RemoteException;
    public void item_sold(String itemID, float amount, String msg) throws RemoteException;

}
