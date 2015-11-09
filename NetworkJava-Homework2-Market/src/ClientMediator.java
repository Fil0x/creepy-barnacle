import java.rmi.RemoteException;
import java.util.HashMap;

public class ClientMediator {
    private static ClientMediator instance = null;
    private Client client;
    private ClientForm clientForm;

    public void setClient(Client client) {
        this.client = client;
    }

    public void setClientForm(ClientForm clientForm) {
        this.clientForm = clientForm;
    }

    protected ClientMediator() {}

    public static ClientMediator getInstance() {
        if (instance == null)
            instance = new ClientMediator();
        return instance;
    }

    public void unregister(String name) {
        try {
            this.client.getServerobj().unregister(name);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void sell(String name, String itemName, float value){
        try {
            client.getServerobj().sell(name, itemName, value);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, Item> refresh(){
        HashMap<String, Item> result = null;
        try {
            result = client.getServerobj().all_items();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean placeWishList(String name, String itemName, float maxValue){
        try {
            client.getServerobj().place_wish(name, itemName, maxValue);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public void update_balance() {
        clientForm.update_status_label(this.getBalance(clientForm.getName()));
    }

    public void append_to_log(String msg) {
        clientForm.append_to_log(msg);
    }

    public void update_table() {
        clientForm.refresh_table(this.refresh());
    }

    public Item buy(String name, String itemID) {
        try {
            return client.getServerobj().buy(name, itemID);
        } catch (RemoteException e) {
            return null;
        }
    }

    public float getBalance(String name) {
        try {
            return client.getBankobj().getAccount(name).getBalance();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }
}