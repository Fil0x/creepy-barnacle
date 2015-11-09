import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Client {
    private static final String USAGE = "java bankrmi.Client <bank_url>";
    private static final String DEFAULT_BANK_NAME = "Nordea";
    private static final String DEFAULT_SERVER_NAME = "MyMarket";
    public static final String DEFAULT_USER_NAME = "Filox";

    private Bank bankobj;

    public SvrBackend getServerobj() {
        return serverobj;
    }

    public Bank getBankobj() {
        return bankobj;
    }

    private SvrBackend serverobj;
    private String bankname, servername, username;

    public Client(String bankName, String serverName, String userName) {
        this.bankname = bankName;
        this.servername = serverName;
        this.username = userName;
        try {
            try {
                LocateRegistry.getRegistry(1099).list();
            } catch (RemoteException e) {
                LocateRegistry.createRegistry(1099);
            }
            // Grab references to remote objects
            bankobj = (Bank) Naming.lookup(bankname);
            serverobj = (SvrBackend) Naming.lookup(servername);
            // Register a callback
            ClientCallback c = new ClientCallbackImpl();
            serverobj.register(this.username, c);
            // Create a bank account
            Account acc;
            try {
                acc = bankobj.newAccount(this.username);
                // Free money
                acc.deposit(5000);
            }
            catch (RejectedException r) {
                System.out.println("(Client) Account " + username + " already exists." );
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("(Client) The runtime failed: " + e.getMessage());
            System.exit(0);
        }
        System.out.println("(Client) Connected to bank: " + bankname);
        System.out.println("(Client) Connected to server: " + servername);
    }

    public static void main(String[] args) {
        if ((args.length > 2) || (args.length > 0 && args[0].equals("-h"))) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String bankName, serverName, userName;
        Client c;

        if (args.length == 1) {
            userName = args[0];
            c = new Client(DEFAULT_BANK_NAME, DEFAULT_SERVER_NAME, userName);
        }
        else if (args.length == 2) {
            userName = args[0];
            bankName = args[1];
            c = new Client(bankName, DEFAULT_SERVER_NAME, userName);
        }
        else if (args.length ==  3) {
            userName = args[0];
            bankName = args[1];
            serverName = args[2];
            c = new Client(bankName, serverName, userName);
        }
        else {
            userName = DEFAULT_USER_NAME;
            c = new Client(DEFAULT_BANK_NAME, DEFAULT_SERVER_NAME, userName);
        }

        ClientMediator cm = ClientMediator.getInstance();
        cm.setClient(c);

        ClientForm cf = new ClientForm(userName, c);

        cm.setClientForm(cf);
    }
}