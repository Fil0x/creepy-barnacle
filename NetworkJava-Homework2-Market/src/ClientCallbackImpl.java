import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientCallbackImpl extends UnicastRemoteObject
        implements ClientCallback {
    public ClientCallbackImpl() throws RemoteException {
        super();
    }

    @Override
    public void wish_item_appeared(String itemID, String name, float value, String msg) throws RemoteException {
        System.out.println("(ClientCallback) Item on wishlist appeared: " +  msg);
        ClientMediator c = ClientMediator.getInstance();
        c.append_to_log("Wishlist item appeared: " + name + "[" + itemID + "]@" + value);
        c.append_to_log("Refreshing...");
        c.update_table();
    }

    @Override
    public void item_sold(String itemname, float amount, String msg) throws RemoteException {
        System.out.println("(ClientCallback) [" + itemname + "] was sold for " + amount +  " to " + msg);
        ClientMediator c = ClientMediator.getInstance();
        c.append_to_log("Sold [" + itemname + "] for " + amount + " SEK");
        c.update_balance();
        c.update_table();
    }
}
