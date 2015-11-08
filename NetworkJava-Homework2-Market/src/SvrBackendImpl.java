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
    public synchronized void unregister(String name, ClientCallback callbackClientObj) throws RemoteException {
        if(this.clients.remove(name, callbackClientObj)) {
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
            String itemid = Utilities.generateItemId();
            Item new_item = new Item(name, item_name, itemid, price);
            items.put(itemid, new_item);

            this.check_wishlist(new_item);
            return itemid;
        }
        else {
            System.out.println("(ServerBackend) Client: " + name + "not registered");
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
            System.out.println("(ServerBackend) Client " + name + " doesnt have an account.");
            return null;
        }

        Item item = this.items.get(itemID);
        Account seller_acc = this.rmi_bank.getAccount(item.getName());
        try {
            // Complete the transaction
            buyer_acc.withdraw(item.getPrice());
            seller_acc.deposit(item.getPrice());
            // Notify the seller
            ClientCallback c = clients.get(item.getName());
            c.item_sold(item.getItemid(), item.getPrice(), "Item sold to " + name);

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
        if(this.wishlist.get(name) == null) {
            ArrayList<Wish> new_list = new ArrayList<>();
            new_list.add(w);
            this.wishlist.put(name, new_list);
        } else {
            this.wishlist.get(name).add(w);
        }
    }
}
