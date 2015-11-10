import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Bank extends Remote {

    Account newAccount(String name) throws RemoteException, RejectedException;

    Account getAccount(String name) throws RemoteException, RejectedException;

    boolean deleteAccount(String name) throws RemoteException, RejectedException;

    String[] listAccounts() throws RemoteException;
}