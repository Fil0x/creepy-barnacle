import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class BankImpl extends UnicastRemoteObject implements Bank {

    public static final String TABLE_NAME = "ACCOUNT";
    private PreparedStatement createAccountStatement;
    private PreparedStatement findAccountStatement;
    private PreparedStatement deleteAccountStatement;
    private Map<String, Account> accounts = new HashMap<>();
    private String datasource;

    public BankImpl(String datasource)
            throws RemoteException, ClassNotFoundException, SQLException {
        super();
        this.datasource = datasource;
        Connection connection = createDatasource();
        prepareStatements(connection);
    }

    private Connection createDatasource() throws ClassNotFoundException, SQLException {
        Connection connection = getConnection();
        boolean exist = false;
        int tableNameColumn = 3;
        DatabaseMetaData dbm = connection.getMetaData();
        for (ResultSet rs = dbm.getTables(null, null, null, null); rs.next();) {
            if (rs.getString(tableNameColumn).equals(TABLE_NAME)) {
                exist = true;
                rs.close();
                break;
            }
        }
        if (!exist) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + TABLE_NAME
                    + " (name VARCHAR(32) PRIMARY KEY, balance FLOAT not null)");
        }
        return connection;
    }

    private Connection getConnection()
                throws ClassNotFoundException, SQLException {

        Class.forName("org.mariadb.jdbc.Driver");
        return DriverManager.getConnection(
                        "jdbc:mariadb://localhost:3306/" + datasource, "root", "root");

    }

    private void prepareStatements(Connection connection) throws SQLException {
        createAccountStatement = connection.prepareStatement("INSERT INTO "
                + TABLE_NAME + " VALUES (?, 0)");
        findAccountStatement = connection.prepareStatement("SELECT * from "
                + TABLE_NAME + " WHERE NAME = ?");
        deleteAccountStatement = connection.prepareStatement("DELETE FROM "
                + TABLE_NAME
                + " WHERE name = ?");
    }

    @Override
    public synchronized String[] listAccounts() {
        return accounts.keySet().toArray(new String[1]);
    }

    @Override
    public synchronized Account newAccount(String name) throws RemoteException,
            RejectedException {
        AccountImpl account = (AccountImpl) accounts.get(name);
        if (account != null) {
            System.out.println("Account [" + name + "] exists!!!");
            throw new RejectedException("Rejected: Bank:  Account for: "
                    + name + " already exists: " + account);
        }
        ResultSet result;
        try {
            findAccountStatement.setString(1, name);
            result = findAccountStatement.executeQuery();

            if (result.next()) {
                // account exists, instantiate, put in cache and throw exception.
                account = new AccountImpl(name, result.getFloat("balance"),
                        getConnection());
                accounts.put(name, account);
                throw new RejectedException("Rejected: Account for: " + name
                        + " already exists");
            }
            result.close();

            // create account.
            createAccountStatement.setString(1, name);
            int rows = createAccountStatement.executeUpdate();
            if (rows == 1) {
                account = new AccountImpl(name, getConnection());
                accounts.put(name, account);
                System.out.println("Bank: Account: " + account
                        + " has been created for " + name);
                return account;
            } else {
                throw new RejectedException("Cannot create an account for " + name);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RejectedException("Cannot create an account for " + name, e);
        }
    }

    @Override
    public synchronized Account getAccount(String name) throws RemoteException,
            RejectedException {
        if (name == null) {
            return null;
        }

        Account acct = accounts.get(name);
        if (acct == null) {
            try {
                findAccountStatement.setString(1, name);
                ResultSet result = findAccountStatement.executeQuery();
                if (result.next()) {
                    acct = new AccountImpl(result.getString("name"),
                            result.getFloat("balance"), getConnection());
                    result.close();
                    accounts.put(name, acct);
                } else {
                    return null;
                }
            } catch (SQLException | ClassNotFoundException e) {
                throw new RejectedException("Unable to find account for " + name, e);
            }
        }
        return acct;
    }

    @Override
    public synchronized boolean deleteAccount(String name) throws RejectedException {
        if (!hasAccount(name)) {
            return false;
        }
        accounts.remove(name);
        try {
            deleteAccountStatement.setString(1, name);
            int rows = deleteAccountStatement.executeUpdate();
            if (rows != 1) {
                throw new RejectedException("Unable to delete account..." + name);
            }
        } catch (SQLException e) {
            System.out.println("Unable to delete account for " + name + ": "
                    + e.getMessage());
            throw new RejectedException("Unable to delete account..." + name, e);
        }
        System.out.println("Bank: Account for " + name + " has been deleted");
        return true;
    }

    private boolean hasAccount(String name) {
        return accounts.get(name) != null;
    }
}