import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RegistryRemotoTagServer extends RegistryRemotoServer,  RegistryRemotoTagClient {
    public boolean associaTag(Remote nome_logico_server, String tag) throws RemoteException;
}