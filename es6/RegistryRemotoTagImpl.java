import java.rmi.Naming;
//import java.rmi.Remote;
import java.rmi.RemoteException;

public class RegistryRemotoTagImpl extends RegistryRemotoImpl implements RegistryRemotoTagServer{
    final int tot = 200;
	Object[][] tagTable = new Object[tot][2];

    public RegistryRemotoTagImpl() throws RemoteException {
        super();
        for(int i=0; i<tot; i++) {
			tagTable[i][0] = null;
			tagTable[i][1] = null;
		}
		tags.add("Congresso");
		tags.add("Programma");
		tags.add("Matematica");
    }

    public boolean associaTag(String nome_logico_server, String tag) throws RemoteException{
        boolean res = false;
        if(nome_logico_server == null)
            return res;
        boolean ok = false;
        for(int i=0; i<tableSize; i++) {
            if(((String)(table[i][0])).equals(nome_logico_server)) {
                ok = true;
                break;
            }
        }
        if(ok == false)
            return false;
        ok = false;
        for(String s : tags) {
            if(s.equals(tag)){
                ok = true;
                break;
            }
        }
        if(ok == false)
            return false;
        ok = false;
        for(int i=0; i<tot; i++) {
            if(tagTable[i][0]==null) {
                tagTable[i][0] = nome_logico_server;
                tagTable[i][1] = tag;
                ok = true;
                break;
            }
        }
        return ok;
    }

    public String[] cercaTag(String tag) throws RemoteException {
		int cont = 0;
		if (tag == null)
			return new String[0];
		for (int i = 0; i < tot; i++)
			if (tagTable[i][0] != null && tag.equals((String) tagTable[i][1])){
                cont++;
            }
		String[] risultato = new String[cont];
		// Ora lo uso come indice per il riempimento
		cont = 0;
		for (int i = 0; i < tot; i++)
			if (tagTable[i][0] != null && tag.equals((String) tagTable[i][1])){
                risultato[cont++] = (String) tagTable[i][0];
            }
		return risultato;
	}

    // Avvio del Server RMI
	public static void main(String[] args) {

		int registryRemotoPort = 1099;
		String registryRemotoHost = "localhost";
		String registryRemotoName = "RegistryRemoto";

		// Controllo dei parametri della riga di comando
		if (args.length != 0 && args.length != 1) {
			System.out.println("Sintassi: ServerImpl [registryPort]");
			System.exit(1);
		}
		if (args.length == 1) {
			try {
				registryRemotoPort = Integer.parseInt(args[0]);
			} catch (Exception e) {
				System.out.println("Sintassi: ServerImpl [registryPort], registryPort intero");
				System.exit(2);
			}
		}

		// Impostazione del SecurityManager
		/*if (System.getSecurityManager() == null)
			System.setSecurityManager(new RMISecurityManager());
*/
		// Registrazione del servizio RMI
		String completeName = "//" + registryRemotoHost + ":" + registryRemotoPort
				+ "/" + registryRemotoName;
		try {
			RegistryRemotoTagImpl serverRMI = new RegistryRemotoTagImpl();
			Naming.rebind(completeName, serverRMI);
			System.out.println("Server RMI: Servizio \"" + registryRemotoName
					+ "\" registrato");
		} catch (Exception e) {
			System.err.println("Server RMI \"" + registryRemotoName + "\": "
					+ e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}
}