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

		// controllo argomenti input: in numero pari oppure 1 argomento (porta)
		if ( (args.length-1) % 2 != 0 || args.length == 0) {
			System.out.println("Usage: java server portaServer file1 port1 ... fileN portN");
			System.exit(1);
		}

		//controllo argomento porta discovery server
		try {
			port = Integer.parseInt(args[0]);
			// controllo che la porta sia nel range consentito 1024-65535
			if (port < 1024 || port > 65535) {
				System.out.println("Porta fuori range consentito (1025 - 65534)");
				System.exit(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(2);
		}
		
		String nome=null;
		File file=null;
		int portSwapper=-1;
		//in caso di arg errati si esce. La soluzione del prof è più flessibile ma illeggibile
		for(int i=1;i<args.length;i=i+2){
			nome = args[i];
			file = new File(nome);
			if(!file.exists()){
				System.out.println("file inesistente ");
				System.exit(3);
			}
			try {
				portSwapper = Integer.parseInt(args[i+1]);
				// controllo che la porta sia nel range consentito 1024-65535
				if (portSwapper < 1024 || portSwapper > 65535) {
					System.out.println("Una delle porte fuori range consentito (1025 - 65534)");
					System.exit(3);
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(3);
			}
		}

		for(int i=1;i<args.length;i=i+2){
			rowSwap rowSwap = new rowSwap(args[i],Integer.parseInt(args[i+1]));
			rowSwap.start();
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
			ByteArrayOutputStream boStream = null;
			DataOutputStream doStream = null;
			byte[] data = null;
			String portToSend=null; //risposta

			while (true) {
				System.out.println("\nIn attesa di richieste...");
				// ricezione del datagramma
				try {
					packet.setData(buf); //presente sia in invio che lettura della risposta altrimenti crasha
					//packet.setLength(256);
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
					biStream = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
					diStream = new DataInputStream(biStream);
					richiesta = diStream.readUTF();

					System.out.println("Richiesto server per nome file: " + richiesta);
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
					portToSend="-1";
					for(int i=1;i<args.length;i=i+2){
						if(args[i].equalsIgnoreCase(richiesta)){
							portToSend=args[i+1] + "";
						}
					}
					packet.setData(buf); //presente sia in invio che lettura della risposta altrimenti crasha
					//packet.setLength(256);
					boStream = new ByteArrayOutputStream();
					doStream = new DataOutputStream(boStream);
					doStream.writeUTF(portToSend);
					data = boStream.toByteArray();
					boStream.close();
					doStream.close();
					packet.setData(data, 0, data.length);
					socket.send(packet);
				}
				catch (Exception e) {
					System.err.println("Problemi nell'invio della risposta: "
				      + e.getMessage());
					e.printStackTrace();
					continue;
					// il server continua a fornire il servizio ricominciando dall'inizio
					// del ciclo
				}
			} // while
		}catch (Exception e) {
			// qui catturo le eccezioni non catturate all'interno del while
			// in seguito alle quali il server termina l'esecuzione
			e.printStackTrace();
		}

		System.out.println("Discovery server: termino...");
		socket.close();
	}
}
