import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemOp extends Remote {

public int conta_righe(String nome_file, int min_parole) throws RemoteException;

public String elimina_righe(String nome_file, int riga) throws RemoteException;
}
