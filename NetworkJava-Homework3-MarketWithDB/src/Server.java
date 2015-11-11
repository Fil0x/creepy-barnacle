import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.sql.SQLException;

public class Server {
    private static final int REGISTRY_PORT_NUMBER = 1099;
    private static final String USAGE = "java bankjdbc.Server [rmi-URL of a bank] "
            + "[database] [dbms: access, derby, pointbase, cloudscape, mysql]";
    private static final String BANK = "Nordea";
    private static final String SERVER = "myMarket";
    private static final String DATASOURCE = "market";

    public Server(String bankName, String serverName) {
        try {
            Bank bankobj = new BankImpl(DATASOURCE);
            SvrBackendImpl serverobj = new SvrBackendImpl();
            serverobj.set_bank(bankobj);

            // Register the newly created object at rmiregistry.
            try {
                LocateRegistry.getRegistry(REGISTRY_PORT_NUMBER).list();
            } catch (RemoteException e) {
                LocateRegistry.createRegistry(REGISTRY_PORT_NUMBER);
            }
            java.rmi.Naming.rebind(bankName, bankobj);
            Naming.rebind(serverName, serverobj);

            System.out.println(bankobj + " is ready.");
            System.out.println(serverobj + " is ready.");
        } catch (RemoteException | MalformedURLException |
                ClassNotFoundException | SQLException e) {
            System.out.println("Failed to start bank server.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        if (args.length > 2|| (args.length > 0 && args[0].equalsIgnoreCase("-h"))) {
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