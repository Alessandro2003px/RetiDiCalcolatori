import java.rmi.Remote;
import java.rmi.RemoteException;

public class RegistryRemotoTagImpl extends RegistryRemotoImpl implements RegistryRemotoTagServer {
    public RegistryRemotoTagImpl() throws RemoteException {
        super();
    }

    public boolean associaTag(Remote nome_logico_server, String tag) throws RemoteException{
        boolean res = false;
        if(nome_logico_server == null)
            return res;
            boolean ok = false;
        for(int i=0; i<tableSize; i++) {
            if(table[i][0] == nome_logico_server) {
                ok = true;
                break;
            }
        }
        if(ok == false)
            return false;
        ok = false;
        for(String s : tags) {
            if(s.equals(tag))
                ok = true;
        }
        for(int i=0; i<tot; i++) {
            if(tagTable[i][0]==null) {
                tagTable[i][0] = nome_logico_server;
                tagTable[i][1] = tag;
            }
        }
        return ok;
    }
}