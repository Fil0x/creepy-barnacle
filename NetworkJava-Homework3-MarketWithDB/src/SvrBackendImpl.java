import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;


public class SvrBackendImpl extends UnicastRemoteObject implements SvrBackend {

    private HashMap<String, ClientCallback> clients;
    private HashMap<String, ArrayList<Wish>> wishlist;
    private Bank rmi_bank;


    protected SvrBackendImpl() throws RemoteException {
        super();
        this.clients = new HashMap<>();
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

            // Add it to the database
            DBMediator dm = DBMediator.getInstance();
            dm.sell(item_id, name, item_name, price);

            this.check_wishlist(name, new_item);
            return item_id;
        }
        else {
            System.out.println("(ServerBackend) Client: " + name + " not registered");
            return null;
        }
    }

    private void check_wishlist(String buyer, Item new_item) {
        for(String name: this.wishlist.keySet()){
            for(Wish w: this.wishlist.get(name)) {
                if(buyer.equals(name)) continue;
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
    public Item buy(String name, String itemID) throws RemoteException, RejectedException {
        // Does the client have an account in the bank?
        Account buyer_acc = this.rmi_bank.getAccount(name);
        if(buyer_acc == null) {
            System.out.println("(ServerBackend) Client " + name + " doesn't have an account.");
            return null;
        }
        // Check if he wants to buy his own item
        Item item = DBMediator.getInstance().getItem(itemID);
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
            DBMediator.getInstance().deleteItem(itemID);
            // Increase the counters
            DBMediator.getInstance().incrementPurchases(name);
            DBMediator.getInstance().incrementSales(item.getName());
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
        DBMediator dm = DBMediator.getInstance();
        return dm.getAllItems();
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

    @Override
    public boolean login(String username, String password) throws RemoteException {
        return DBMediator.getInstance().login(username, password);
    }

    @Override
    public boolean create_client(String username, String password) throws RemoteException {
        return DBMediator.getInstance().createClient(username, password);
    }

    @Override
    public int[] getMetrics(String username) throws RemoteException {
        return DBMediator.getInstance().getUserMetrics(username);
    }
}
