import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Client {
    private static final String USAGE = "java bankrmi.Client <bank_url>";
    private static final String DEFAULT_BANK_NAME = "Nordea";
    private static final String DEFAULT_SERVER_NAME = "myMarket";
    public static final String DEFAULT_USER_NAME = "Filox";

    private Bank bankobj;

    public SvrBackend getServerobj() {
        return serverobj;
    }

    public Bank getBankobj() {
        return bankobj;
    }

    private SvrBackend serverobj;
    private String bankname, servername;

    public Client(String bankName, String serverName) {
        this.bankname = bankName;
        this.servername = serverName;
        try {
            try {
                LocateRegistry.getRegistry(1099).list();
            } catch (RemoteException e) {
                LocateRegistry.createRegistry(1099);
            }
            // Grab references to remote objects
            bankobj = (Bank) Naming.lookup(bankname);
            serverobj = (SvrBackend) Naming.lookup(servername);
        } catch (Exception e) {
            System.out.println("(Client) The runtime failed: " + e.getMessage());
            e.printStackTrace();
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

        String bankName, serverName;
        Client c;

        if (args.length == 1) {
            bankName = args[0];
            c = new Client(bankName, DEFAULT_SERVER_NAME);
        }
        else if (args.length ==  2) {
            bankName = args[0];
            serverName = args[1];
            c = new Client(bankName, serverName);
        }
        else {
            c = new Client(DEFAULT_BANK_NAME, DEFAULT_SERVER_NAME);
        }

        Login l = new Login();

        ClientMediator cm = ClientMediator.getInstance();
        cm.setClient(c);
    }
}