import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;

public class Server {
    private static final String USAGE = "java bankrmi.Server <bank_rmi_url>";
    private static final String BANK = "Nordea";
    private static final String SERVER = "MyMarket";

    public Server(String bankName, String serverName) {
        try {
            Bank bankobj = new BankImpl(bankName);
            SvrBackendImpl serverObj = new SvrBackendImpl(serverName);

            // Register the newly created back and SvrBackendImpl objects at rmiregistry.
            try {
                LocateRegistry.getRegistry(1099).list();
            } catch (RemoteException e) {
                LocateRegistry.createRegistry(1099);
            }
            Naming.rebind(bankName, bankobj);
            Naming.rebind(serverName, serverObj);

            serverObj.set_bank(bankobj);

            System.out.println(bankobj + " is ready.");
            System.out.println(serverObj + " is ready.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length > 2 || (args.length > 0 && args[0].equalsIgnoreCase("-h"))) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String bankName, serverName;
        if (args.length == 1) {
            bankName = args[0];
            serverName = SERVER;
        }
        else if(args.length == 2) {
            bankName = args[0];
            serverName = args[1];
        }
        else {
            bankName = BANK;
            serverName = SERVER;
        }

        new Server(bankName, serverName);
    }
}