
/**
 * ClientCongresso.java
 *
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;
//import java.rmi.RMISecurityManager;

class ClientCongresso {

	// Avvio del Client RMI
	public static void main(String[] args) {
		int registryRemotoPort = 1099;
		String registryRemotoHost = null;
		String registryRemotoName = "RegistryRemoto";
		//String serviceName = "ServerCongresso";
		String tag = "congresso";
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

		// Controllo dei parametri della riga di comando
		if (args.length != 1 && args.length != 2) {
			System.out.println("Sintassi: ClientCongresso NomeHostRegistryRemoto [registryPort], registryPort intero");
			System.exit(1);
		}
		registryRemotoHost = args[0];
		if (args.length == 2) {
			try {
				registryRemotoPort = Integer.parseInt(args[1]);
			} catch (Exception e) {
				System.out
						.println(
								"Sintassi: ClientCongresso NomeHostRegistryRemoto [registryPort], registryPort intero");
				System.exit(1);
			}
		}

		// Impostazione del SecurityManager
		/*if (System.getSecurityManager() == null)
			System.setSecurityManager(new RMISecurityManager());*/

		// Connessione al servizio RMI remoto
		try {
			String completeRemoteRegistryName = "//" + registryRemotoHost + ":"
					+ registryRemotoPort + "/" + registryRemotoName;
			RegistryRemotoTagClient registryRemoto = (RegistryRemotoTagClient) Naming.lookup(completeRemoteRegistryName);
			String service;

			System.out.println("Inserisci tag, CTRL-D per uscire");
			while((tag = stdIn.readLine()) != null){
				String[] servizi = registryRemoto.cercaTag(tag);
				if(servizi.length == 0){
					System.out.println("Non sono presenti servizi per questo tag");
				}else{
					System.out.println("Per quel tag sono presenti questi servizi:");
					for(String s : servizi){
						System.out.println(s);
					}
					System.out.println("Quale servizio desideri?");
					while((service = stdIn.readLine()) != null){
						ServerCongresso serverRMI = (ServerCongresso) registryRemoto.cerca(service);
						if(serverRMI == null){
							System.out.println("Servizio " + service + " inesistente");
							System.out.println("Quale servizio desideri?");
							continue;
						}
						System.out.println("ClientRMI: Servizio \"" + service + "\" connesso");
	
						System.out.println("\nRichieste di servizio fino a fine file");
	
						System.out.println("Servizio (R=Registrazione, P=Programma del congresso), CTRL-D per tornare alla home: ");
	
						while ((service = stdIn.readLine()) != null) {
	
							if (service.equals("R")) {
	
								boolean ok = false;
								int g = 0;
								System.out.print("Giornata (1-3)? ");
								while (ok != true) {
									g = Integer.parseInt(stdIn.readLine());
									if (g < 1 || g > 3) {
										System.out.println("Giornata non valida");
										System.out.print("Giornata (1-3)? ");
										continue;
									} else
										ok = true;
								}
								ok = false;
								String sess = null;
								System.out.print("Sessione (S1 - S12)? ");
	
								while (ok != true) {
									sess = stdIn.readLine();
									if (!sess.equals("S1") && !sess.equals("S2") && !sess.equals("S3")
											&& !sess.equals("S4") && !sess.equals("S5")
											&& !sess.equals("S6") && !sess.equals("S7")
											&& !sess.equals("S8") && !sess.equals("S9")
											&& !sess.equals("S10") && !sess.equals("S11")
											&& !sess.equals("S12")) {
										System.out.println("Sessione non valida");
										System.out.print("Sessione (S1 - S12)? ");
										continue;
									} else
										ok = true;
								}
	
								System.out.print("Speaker? ");
								String speak = stdIn.readLine();
	
								// Tutto corretto
								if (serverRMI.registrazione(g, sess, speak) == 0)
									System.out.println("Registrazione di " + speak
											+ " effettuata per giornata " + g + " sessione " + sess);
								else
									System.out.println("Sessione piena: giornata" + g + " sessione " + sess);
							} // R
							else if (service.equals("P")) {
								int g = 0;
								boolean ok = false;
								System.out.print("Programma giornata (1-3)? ");
	
								while (ok != true) {
									// intercettare la NumberFormatException
									g = Integer.parseInt(stdIn.readLine());
									if (g < 1 || g > 3) {
										System.out.println("Giornata non valida");
										System.out.print("Programma giornata (1-3)? ");
										continue;
									} else
										ok = true;
								}
								System.out.println("Ecco il programma: ");
								serverRMI.programma(g).stampa();
	
							} // P
							else
								System.out.println("Servizio non disponibile");
	
							System.out.println("Servizio (R=Registrazione, P=Programma del congresso), CTRL-D per tornare alla home: ");
						}// !EOF richieste utente
					}
				}
				System.out.println("Inserisci tag, CTRL-D per uscire");
			}
		} catch (Exception e) {
			System.err.println("ClientRMI: " + e.getMessage());
			e.printStackTrace();
			System.exit(2);
		}
	}
}