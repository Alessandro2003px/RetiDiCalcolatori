import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.*;


class Client {
    public static void main(String[] args) {
        final int REGISTRYPORT = 1099;
		String registryHost = null; // host remoto con registry
		String serviceName = "";
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

		// Controllo dei parametri della riga di comando
		if (args.length != 2) {
			System.out.println("Sintassi: RMI_Registry_IP ServiceName");
			System.exit(1);
		}
		registryHost = args[0];
		serviceName = args[1];

		System.out.println("Invio richieste a " + registryHost + " per il servizio di nome " + serviceName);

        // Connessione al servizio RMI remoto
		try {
			String completeName = "//" + registryHost + ":" + REGISTRYPORT + "/" + serviceName;
			RemOp serverRMI = (RemOp) Naming.lookup(completeName);
			System.out.println("ClientRMI: Servizio \"" + serviceName + "\" connesso");

			System.out.println("\nRichieste di servizio fino a fine file");

			String service;
			System.out.print("Servizio (C=Conta righe, E=Elimina righe): ");

			/* ciclo accettazione richieste utente */
			while ((service = stdIn.readLine()) != null) {

				if (service.equals("C")) {
                    System.out.print("Nome file: ");
                    String fileName = stdIn.readLine();
                    System.out.print("Parole minime: ");
                    String Min = stdIn.readLine();
					int min = Integer.parseInt(Min);
                    int count = serverRMI.conta_righe(fileName, min);
                    System.out.println("Nel file "+fileName+" ci sono "+count+" righe con almeno "+min+" parole");
                } else if(service.equals("E")) {
                    System.out.print("Nome file: ");
                    String fileName = stdIn.readLine();
                    System.out.print("Riga cancellare: ");
                    String Line = stdIn.readLine();
					int line = Integer.parseInt(Line);	
                    String mix = serverRMI.elimina_righe(fileName, line);
                    String[] splitW = mix.split(" ");
                    System.out.println("Nel file "+splitW[1]+" ci sono "+splitW[0]+" righe");
                } else {
					System.out.println("Scelta non valida");
				}
				System.out.print("Servizio (C=Conta righe, E=Elimina righe): ");

			} // while (!EOF), fine richieste utente

		} catch (NotBoundException nbe) {
			System.err.println("ClientRMI: il nome fornito non risulta registrato; " + nbe.getMessage());
			nbe.printStackTrace();
			System.exit(1);
        } catch (RemoteException re) {
            System.err.println("Errore invocazione metodo server: "+re.getMessage());
            System.exit(1);
        } catch (Exception e) {
			System.err.println("ClientRMI: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
    }
}
