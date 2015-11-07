import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;


public class SvrBackendImpl extends UnicastRemoteObject implements SvrBackend {

    private HashMap<String, ClientCallback> clients;
    private String serverName;

    protected SvrBackendImpl(String serverName) throws RemoteException {
        super();
        this.serverName = serverName;
        this.clients = new HashMap<>();
    }

    @Override
    public synchronized void register(String name, ClientCallback callbackClientObj) throws RemoteException {
        if(!this.clients.containsValue(callbackClientObj)) {
            this.clients.put(name, callbackClientObj);
        }
        System.out.println("(ServerBackend) Registered new client: " + name);
    }

    @Override
    public synchronized void unregister(String name, ClientCallback callbackClientObj) throws RemoteException {
        if(this.clients.remove(name, callbackClientObj)) {
            System.out.println("(ServerBackend) Unregistered client: " + name);
        }
        else {
            System.out.println("(ServerBackend) Client " + name + " wasn't registered.");
        }
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
