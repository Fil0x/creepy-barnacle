import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.StringTokenizer;

public class Client {
    private static final String USAGE = "java bankrmi.Client <bank_url>";
    private static final String DEFAULT_BANK_NAME = "Nordea";
    private static final String DEFAULT_SERVER_NAME = "MyMarket";
    public static final String DEFAULT_USER_NAME = "Filox";

    private Account account;
    private Bank bankobj;

    public SvrBackend getServerobj() {
        return serverobj;
    }

    public Bank getBankobj() {
        return bankobj;
    }

    private SvrBackend serverobj;
    private String bankname, servername, username;
    private String clientname;

    private static enum CommandName {
        newAccount, getAccount, deleteAccount, deposit, withdraw, balance, quit, help, list
    }

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

    public Client() {
        this(DEFAULT_BANK_NAME, DEFAULT_SERVER_NAME, DEFAULT_USER_NAME);
    }

    public void run() {
        BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.print(clientname + "@" + bankname + ">");
            try {
                String userInput = consoleIn.readLine();
                execute(parse(userInput));
            } catch (RejectedException re) {
                System.out.println(re);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Command parse(String userInput) {
        if (userInput == null) {
            return null;
        }

        StringTokenizer tokenizer = new StringTokenizer(userInput);
        if (tokenizer.countTokens() == 0) {
            return null;
        }

        CommandName commandName = null;
        String userName = null;
        float amount = 0;
        int userInputTokenNo = 1;

        while (tokenizer.hasMoreTokens()) {
            switch (userInputTokenNo) {
                case 1:
                    try {
                        String commandNameString = tokenizer.nextToken();
                        commandName = CommandName.valueOf(CommandName.class, commandNameString);
                    } catch (IllegalArgumentException commandDoesNotExist) {
                        System.out.println("Illegal command");
                        return null;
                    }
                    break;
                case 2:
                    userName = tokenizer.nextToken();
                    break;
                case 3:
                    try {
                        amount = Float.parseFloat(tokenizer.nextToken());
                    } catch (NumberFormatException e) {
                        System.out.println("Illegal amount");
                        return null;
                    }
                    break;
                default:
                    System.out.println("Illegal command");
                    return null;
            }
            userInputTokenNo++;
        }
        return new Command(commandName, userName, amount);
    }

    void execute(Command command) throws RemoteException, RejectedException {
        if (command == null) {
            return;
        }

        switch (command.getCommandName()) {
            case list:
                try {
                    for (String accountHolder : bankobj.listAccounts()) {
                        System.out.println(accountHolder);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                return;
            case quit:
                System.exit(0);
            case help:
                for (CommandName commandName : CommandName.values()) {
                    System.out.println(commandName);
                }
                return;

        }

        // all further commands require a name to be specified
        String userName = command.getUserName();
        if (userName == null) {
            userName = clientname;
        }

        if (userName == null) {
            System.out.println("name is not specified");
            return;
        }

        switch (command.getCommandName()) {
            case newAccount:
                clientname = userName;
                bankobj.newAccount(userName);
                return;
            case deleteAccount:
                clientname = userName;
                bankobj.deleteAccount(userName);
                return;
        }

        // all further commands require a Account reference
        Account acc = bankobj.getAccount(userName);
        if (acc == null) {
            System.out.println("No account for " + userName);
            return;
        } else {
            account = acc;
            clientname = userName;
        }

        switch (command.getCommandName()) {
            case getAccount:
                System.out.println(account);
                break;
            case deposit:
                account.deposit(command.getAmount());
                break;
            case withdraw:
                account.withdraw(command.getAmount());
                break;
            case balance:
                System.out.println("balance: $" + account.getBalance());
                break;
            default:
                System.out.println("Illegal command");
        }
    }

    private class Command {
        private String userName;
        private float amount;
        private CommandName commandName;

        private String getUserName() {
            return userName;
        }

        private float getAmount() {
            return amount;
        }

        private CommandName getCommandName() {
            return commandName;
        }

        private Command(Client.CommandName commandName, String userName, float amount) {
            this.commandName = commandName;
            this.userName = userName;
            this.amount = amount;
        }
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