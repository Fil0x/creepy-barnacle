import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class SvrBackendImpl extends UnicastRemoteObject implements SvrBackend {
    protected SvrBackendImpl() throws RemoteException {
        super();
    }

    @Override
    public void register(String name) throws RemoteException {

    }

    @Override
    public void unregister(String name) throws RemoteException {

    }

    @Override
    public void sell(String name, String itemID) throws RemoteException {

    }

    @Override
    public void buy(String name, String itemID) throws RemoteException {

    }

    @Override
    public void all_items(String name) throws RemoteException {

    }

    @Override
    public void place_wish(String name, String itemname, String max_price) throws RemoteException {

    }
}
