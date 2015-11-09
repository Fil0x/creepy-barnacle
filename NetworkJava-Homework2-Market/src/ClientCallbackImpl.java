import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientCallbackImpl extends UnicastRemoteObject
        implements ClientCallback {
    public ClientCallbackImpl() throws RemoteException {
        super();
    }

    @Override
    public void wish_item_appeared(String itemID, String name, float value, String msg) throws RemoteException {
        System.out.println("(ClientCallback) Received: " +  msg);
    }

    @Override
    public void item_sold(String itemID, float amount, String msg) throws RemoteException {
        System.out.println("(ClientCallback) Received: " +  msg);
    }
}
