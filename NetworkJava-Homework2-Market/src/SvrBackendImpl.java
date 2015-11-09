import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;


public class SvrBackendImpl extends UnicastRemoteObject implements SvrBackend {

    private HashMap<String, ClientCallback> clients;
    private HashMap<String, Item> items;
    private HashMap<String, ArrayList<Wish>> wishlist;
    private String serverName;
    private Bank rmi_bank;

    protected SvrBackendImpl(String serverName) throws RemoteException {
        super();
        this.serverName = serverName;
        this.clients = new HashMap<>();
        this.items = new HashMap<>();
        this.wishlist = new HashMap<>();
    }

    public void set_bank(Bank b) { this.rmi_bank = b; }

    @Override
    public synchronized void register(String name, ClientCallback callbackClientObj) throws RemoteException {
        if(!this.clients.containsValue(callbackClientObj)) {
            this.clients.put(name, callbackClientObj);
        }
        System.out.println("(ServerBackend) Registered new client: " + name);
    }

    @Override
    public synchronized void unregister(String name) throws RemoteException {
        if(this.clients.remove(name) != null) {
            System.out.println("(ServerBackend) Unregistered client: " + name);
        }
        else {
            System.out.println("(ServerBackend) Client " + name + " wasn't registered.");
        }
    }

    @Override
    public String sell(String name, String item_name, float price) throws RemoteException {
        // A client wants to put up something for sale
        if(clients.containsKey(name)){
            String item_id = Utilities.generateItemId();
            Item new_item = new Item(name, item_name, item_id, price);
            items.put(item_id, new_item);

            System.out.println("(ServerBackend) New item posted: " + item_name + ", " + price);

            this.check_wishlist(new_item);
            return item_id;
        }
        else {
            System.out.println("(ServerBackend) Client: " + name + " not registered");
            return null;
        }
    }

    private void check_wishlist(Item new_item) {
        for(String name: this.wishlist.keySet()){
            for(Wish w: this.wishlist.get(name)) {
                if(w.isSatisfied(new_item.getItem_name(), new_item.getPrice())){
                    try {
                        clients.get(name).wish_item_appeared(new_item.getItemid(),
                                new_item.getItem_name(), new_item.getPrice(),
                                new_item.getItemid() + ":" + new_item.getItem_name() + "@" + new_item.getPrice());
                    } catch (RemoteException e) {
                        System.out.println("(ServerBackend) Unreachable client " + new_item.getName());
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public Item buy(String name, String itemID) throws RemoteException {
        // Does the client have an account in the bank?
        Account buyer_acc = this.rmi_bank.getAccount(name);
        if(buyer_acc == null) {
            System.out.println("(ServerBackend) Client " + name + " doesn't have an account.");
            return null;
        }
        // Check if he wants to buy his own item
        Item item = this.items.get(itemID);
        if (item.getName().equals(name)) {
            return null;
        }

        Account seller_acc = this.rmi_bank.getAccount(item.getName());
        ClientCallback c = clients.get(item.getName());
        try {
            // Complete the transaction
            buyer_acc.withdraw(item.getPrice());
            seller_acc.deposit(item.getPrice());
            // Remove the item from the available stuff
            System.out.println("(ServerBackend) Removing item: " + itemID);
            this.items.remove(itemID);
            // Notify the seller
            c.item_sold(item.getItem_name(), item.getPrice(), name);

            return item;
        } catch (RejectedException e) {
            System.out.println("(ServerBackend) Client " + name + " not enough credits.");
            return null;
        }
    }

    @Override
    public HashMap<String, Item> all_items() throws RemoteException {
        return this.items;
    }

    @Override
    public void place_wish(String name, String itemname, float max_price) throws RemoteException {
        Wish w = new Wish(itemname, max_price);
        if(!this.wishlist.containsKey(name)) {
            ArrayList<Wish> new_list = new ArrayList<>();
            new_list.add(w);
            this.wishlist.put(name, new_list);
        } else {
            this.wishlist.get(name).add(w);
        }
    }
}
