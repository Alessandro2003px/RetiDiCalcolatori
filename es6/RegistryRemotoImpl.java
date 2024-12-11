
/**
 * 	Implementazione del Registry Remoto.
 *	Metodi descritti nelle interfacce.  
 */
import java.util.ArrayList;
//import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
//import java.rmi.RMISecurityManager;

public class RegistryRemotoImpl extends UnicastRemoteObject implements RegistryRemotoServer {
	ArrayList<String> tags = new ArrayList<String>();
	// num. entry [nomelogico][ref]
	final int tableSize = 100;
	// Tabella: la prima colonna contiene i nomi, la seconda i riferimenti remoti
	Object[][] table = new Object[tableSize][2];

	public RegistryRemotoImpl() throws RemoteException {
		super();
		for (int i = 0; i < tableSize; i++) {
			table[i][0] = null;
			table[i][1] = null;
		}
	}

	/** Aggiunge la coppia nella prima posizione disponibile */
	public synchronized boolean aggiungi(String nomeLogico, Remote riferimento)
			throws RemoteException {
		// Cerco la prima posizione libera e la riempio
		boolean risultato = false;
		if ((nomeLogico == null) || (riferimento == null))
			return risultato;
		for (int i = 0; i < tableSize; i++)
			if (table[i][0] == null) {
				table[i][0] = nomeLogico;
				table[i][1] = riferimento;
				risultato = true;
				break;
			}
		return risultato;
	}

	/** Restituisce il riferimento remoto cercato, oppure null */
	public synchronized Remote cerca(String nomeLogico) throws RemoteException {
		Remote risultato = null;
		if (nomeLogico == null)
			return null;
		for (int i = 0; i < tableSize; i++)
			if (table[i][0] != null && nomeLogico.equals((String) table[i][0])) {
				risultato = (Remote) table[i][1];
				break;
			}
		return risultato;
	}
	public synchronized Remote[] cercaTutti(String nomeLogico) throws RemoteException {
		int cont = 0;
		if (nomeLogico == null)
			return new Remote[0];
		for (int i = 0; i < tableSize; i++)
			if (table[i][0] != null && nomeLogico.equals((String) table[i][0]))
				cont++;
		Remote[] risultato = new Remote[cont];
		// Ora lo uso come indice per il riempimento
		cont = 0;
		for (int i = 0; i < tableSize; i++)
			if (table[i][0] != null && nomeLogico.equals((String) table[i][0]))
				risultato[cont++] = (Remote) table[i][1];
		return risultato;
	}

	/** Restituisce tutti i riferimenti corrispondenti ad un nome logico */
	public synchronized Object[][] restituisciTutti() throws RemoteException {
		int cont = 0;
		for (int i = 0; i < tableSize; i++)
			if (table[i][0] != null)
				cont++;
		Object[][] risultato = new Object[cont][2];
		// Ora lo uso come indice per il riempimento
		cont = 0;
		for (int i = 0; i < tableSize; i++)
			if (table[i][0] != null) {
				risultato[cont][0] = table[i][0];
				risultato[cont][1] = table[i][1];
			}
		return risultato;
	}

	/** Elimina la prima entry corrispondente al nome logico indicato */
	public synchronized boolean eliminaPrimo(String nomeLogico)
			throws RemoteException {
		boolean risultato = false;
		if (nomeLogico == null)
			return risultato;
		for (int i = 0; i < tableSize; i++)
			if (nomeLogico.equals((String) table[i][0])) {
				table[i][0] = null;
				table[i][1] = null;
				risultato = true;
				break;
			}
		return risultato;
	}

	public synchronized boolean eliminaTutti(String nomeLogico)
			throws RemoteException {
		boolean risultato = false;
		if (nomeLogico == null)
			return risultato;
		for (int i = 0; i < tableSize; i++)
			if (nomeLogico.equals((String) table[i][0])) {
				if (risultato == false)
					risultato = true;
				table[i][0] = null;
				table[i][1] = null;
			}
		return risultato;
	}
}