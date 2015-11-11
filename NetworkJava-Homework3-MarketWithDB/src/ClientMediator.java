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
        clientForm.update_status_label(this.getUserInfo(clientForm.getName()));
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
        } catch (RejectedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getUserInfo(String name) {
        try {
            float balance = client.getBankobj().getAccount(name).getBalance();
            int[] metrics = client.getServerobj().getMetrics(name);

            if (metrics == null) {
                return "Balance: " + balance + " SEK";
            }
            else {
                return "Balance: " + balance + " SEK, Purchases: " + metrics[0] + ", Sales: " + metrics[1];
            }
        } catch (RemoteException | RejectedException e) {
            e.printStackTrace();
        }
        return "Error retrieving info";
    }

    private void register_callback(String username) {
        try {
            // Register a callback
            ClientCallback c = new ClientCallbackImpl();
            this.client.getServerobj().register(username, c);
            // Create a bank account
            Account acc;
            acc = this.client.getBankobj().newAccount(username);
            // Free money
            acc.deposit(5000);
        }
        catch (RejectedException r) {
            System.out.println("(Client) Account " + username + " already exists." );
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean login(String username, String password){
        try {
            if(client.getServerobj().login(username,password)) {
                ClientForm cf = new ClientForm(username, client);
                this.setClientForm(cf);
                this.register_callback(username);
                clientForm.setVisible(true);
                return true;
            }
            return false;
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void logout(String username, boolean showLogin) {
        unregister(username);
        if(showLogin) {
            clientForm.setVisible(false);
            clientForm.dispose();
            new Login();
        }
    }

    public boolean register(String username, String password){
        try {
            if(client.getServerobj().create_client(username,password)) {
                this.register_callback(username);
                return true;
            }
            else
                return false;
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }
}