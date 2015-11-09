
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Account extends Remote {
    float getBalance() throws RemoteException;

    void deposit(float value) throws RemoteException, RejectedException;

    void withdraw(float value) throws RemoteException, RejectedException;
}