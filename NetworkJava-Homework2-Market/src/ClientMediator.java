import java.rmi.RemoteException;
import java.util.HashMap;

public class ClientMediator {
    private Client client;
    private ClientForm clientForm;

    public ClientMediator(Client client, ClientForm clientForm) {
        this.client = client;
        this.clientForm = clientForm;
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
            result =client.getServerobj().all_items();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void placeWishList(String name, String itemName, float maxValue){
        try {
            client.getServerobj().place_wish(name, itemName, maxValue);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public void buy(String name, String itemID) {
        try {
            client.getServerobj().buy(name, itemID);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}