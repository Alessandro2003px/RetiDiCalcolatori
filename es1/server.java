// LineServer.java

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
public class server {

	// porta nel range consentito 1024-65535!
	// dichiarata come statica perch� caratterizza il server
	//private static final int PORT = 4444;

	public static void main(String[] args) {

		System.out.println("LineServer: avviato");

		DatagramSocket socket = null;
		DatagramPacket packet = null;
		byte[] buf = new byte[256];
		int port = -1;

		// controllo argomenti input: 0 oppure 1 argomento (porta)
		if(args.length==0){
		System.out.println("n. argomenti errato ");
			//e.printStackTrace();
			System.exit(1);
		}
		
		for(int i=1;i<args.length;i=i+2){
			String nome = args[i];
			File file= new File(nome);
			if(!file.exists()){
				System.out.println("file inesistente ");
				System.exit(2);
			}
			try {
			port = Integer.parseInt(args[i+1]);
				// controllo che la porta sia nel range consentito 1024-65535
				if (port < 1024 || port > 65535) {
					System.out.println("Usage: java LineServer [serverPort>1024]");
					System.exit(1);
				}
			} catch (NumberFormatException e) {
				System.out.println("Usage: java LineServer [serverPort>1024]");
				System.exit(1);
			}
		}

		//controllo argomento porta discovery server
		try {
			port = Integer.parseInt(args[0]);
				// controllo che la porta sia nel range consentito 1024-65535
				if (port < 1024 || port > 65535) {
					System.out.println("Usage: java LineServer [serverPort>1024]");
					System.exit(1);
				}
			} catch (NumberFormatException e) {
				System.out.println("Usage: java LineServer [serverPort>1024]");
				System.exit(1);
		}
		for(int i=1;i<args.length;i=i+2){
			rowSwap rowSwap = new rowSwap(args[i],Integer.parseInt(args[i+1]));
			rowSwap.start();
			//rowSwap.run();
		}
				

		try {
			socket = new DatagramSocket(port);
			packet = new DatagramPacket(buf, buf.length);
			System.out.println("Creato discovery, socket: " + socket);
		}
		catch (SocketException e) {
			System.out.println("Problemi nella creazione della socket: ");
			e.printStackTrace();
			System.exit(1);
		}





		try {
			String nomeFile = null;
			String richiesta = null;
			ByteArrayInputStream biStream = null;
			DataInputStream diStream = null;
			//StringTokenizer st = null;
			ByteArrayOutputStream boStream = null;
			DataOutputStream doStream = null;
			//String linea = null;
			byte[] data = null;



			String portToSend=null;
			
			while (true) {
				
				System.out.println("\nIn attesa di richieste...");
				
				// ricezione del datagramma
				try {
					//packet.setData(buf);
					packet.setData(buf); //presente sia in invio che lettura della risposta altrimenti crasha
					packet.setLength(256); //^^^^^^^^^^^^^^^^^^^^^^^
					socket.receive(packet);
				}
				catch (IOException e) {
					System.err.println("Problemi nella ricezione del datagramma: "
							+ e.getMessage());
					e.printStackTrace();
					continue;
					// il server continua a fornire il servizio ricominciando dall'inizio
					// del ciclo
				}

				try {
					//packet.setData(buf); //presente sia in invio che lettura della risposta altrimenti crasha
					//packet.setLength(256); //^^^^^^^^^^^^^^^^^^^^^^^
					biStream = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
					diStream = new DataInputStream(biStream);
					richiesta = diStream.readUTF();
					
					portToSend="-1";
					for(int i=1;i<args.length;i=i+2){
						if(args[i].equalsIgnoreCase(richiesta)){
							portToSend=args[i+1];
						}
					}
				}
				catch (Exception e) {
					System.err.println("Problemi nella lettura della richiesta: "
						+ nomeFile);
					e.printStackTrace();
					continue;
					// il server continua a fornire il servizio ricominciando dall'inizio
					// del ciclo
				}

				// preparazione della linea e invio della risposta
				try {
					packet.setData(buf); //presente sia in invio che lettura della risposta altrimenti crasha
					packet.setLength(256); //^^^^^^^^^^^^^^^^^^^^^^^
					boStream = new ByteArrayOutputStream();
					doStream = new DataOutputStream(boStream);
					doStream.writeUTF(portToSend);
					data = boStream.toByteArray();
					boStream.close();
					doStream.close();
					packet.setData(data, 0, data.length);
					socket.send(packet);
				}
				catch (IOException e) {
					System.err.println("Problemi nell'invio della risposta: "
				      + e.getMessage());
					e.printStackTrace();
					continue;
					// il server continua a fornire il servizio ricominciando dall'inizio
					// del ciclo
				}

			} // while

		}
		// qui catturo le eccezioni non catturate all'interno del while
		// in seguito alle quali il server termina l'esecuzione
		catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("LineServer: termino...");
		socket.close();
	}
}