import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServerImpl extends UnicastRemoteObject implements RemOp {
    // Costruttore
    public ServerImpl() throws RemoteException {
        super();
    }

    public int conta_righe(String nome_file, int min_parole) throws RemoteException{
        BufferedReader bReader = null;
        String line;
        int count = 0;

        //check file di testo (per convenzione .txt)
        if(!nome_file.endsWith(".txt")) throw new RemoteException("No file .txt");

        //conto le parole tramite metodo split della classe String
        //altrimenti si potrebbe leggere a carattere
        try {
            bReader = new BufferedReader(new FileReader(nome_file));
            while((line = bReader.readLine()) != null){
                if(line.split(" ").length > min_parole) count++; 
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException(e.getMessage());
        }finally{
            try {
                bReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return count;
    }

	public String elimina_righe(String nome_file, int riga) throws RemoteException{
        PrintWriter writer = null;
        BufferedReader bReader = null;
        String line;
        int count = 1;
        File file, new_file;

        //check file di testo (per convenzione .txt)
        if(!nome_file.endsWith(".txt")) throw new RemoteException("No file .txt");
        if(riga <= 0) throw new RemoteException("Riga negativa o pari a 0");

        try {
            writer = new PrintWriter("temp.txt");
            bReader = new BufferedReader(new FileReader(nome_file));
            while((line = bReader.readLine()) != null){
                System.out.println(line);
                if(count != riga)
                    writer.write(line+"\n");
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException(e.getMessage());
        }finally{
            writer.close();
            try {
                bReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(count < riga) throw new RemoteException("File troppo piccolo per la riga");

        file = new File(nome_file);
        file.delete();
        new_file = new File("temp.txt");
        new_file.renameTo(file);

        return (count-1) + " " + nome_file;
    }

	// Avvio del Server RMI
	public static void main(String[] args) {
		final int REGISTRYPORT = 1099;
		String registryHost = "localhost";
		String serviceName = "Server"; // lookup name...

		// Registrazione del servizio RMI
		String completeName = "//" + registryHost + ":" + REGISTRYPORT + "/" + serviceName;
		try {
			ServerImpl serverRMI = new ServerImpl();
			Naming.rebind(completeName, serverRMI);
			System.out.println("Server RMI: Servizio \"" + serviceName + "\" registrato");
		} catch (Exception e) {
			System.err.println("Server RMI \"" + serviceName + "\": " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}
}
