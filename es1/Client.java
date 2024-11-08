import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

public class Client{

	public static void main(String[] args) {
		InetAddress addr = null;
		int port = -1;
		String fileName=null;
		try {
			if (args.length == 3) {
		   		addr = InetAddress.getByName(args[0]);
		   	 	port = Integer.parseInt(args[1]);
		    	if(args[2].endsWith(".txt")) {
		    		fileName=args[2];
		    	}
		    	else {
		    		System.out.println("Usage: java wrong file extension (must be .txt)");
			    	System.exit(1);
			}
			} else {
				System.out.println("Usage: java Client serverIP serverPort file");
			    	System.exit(1);
			}
		} catch (UnknownHostException e) {
			System.out.println("Problemi nella determinazione dell'endpoint del server : ");
			e.printStackTrace();
			System.out.println("Client: interrompo...");
			System.exit(2);
		}
		DatagramSocket socket = null;
		DatagramPacket packet = null;
		byte[] buf = new byte[256];
		try {
			socket = new DatagramSocket();
			socket.setSoTimeout(30000);
			packet = new DatagramPacket(buf, buf.length, addr, port);
			System.out.println("\nClient: avviato");
			System.out.println("Creata la socket: " + socket);
		} catch (SocketException e) {
			System.out.println("Problemi nella creazione della socket: ");
			e.printStackTrace();
			System.out.println("Client: interrompo...");
			System.exit(1);
		}

		byte[] data = null;
		ByteArrayOutputStream boStream = new ByteArrayOutputStream();
		DataOutputStream doStream = new DataOutputStream(boStream);
		try{
			doStream.writeUTF(fileName);
			data=boStream.toByteArray();
			packet.setData(data);
			socket.send(packet);
		}
		catch(Exception e){
			System.out.println("Problemi nell'invio della richiesta: ");
					e.printStackTrace();
					System.exit(1);
		}

		int portS=-1;
		ByteArrayInputStream biStream = null;
		DataInputStream diStream = null;

		try{
			socket.receive(packet);
			biStream = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
			diStream = new DataInputStream(biStream);
			portS = Integer.parseInt(diStream.readUTF());
		}catch(Exception e){
			System.out.println("Problemi nella ricezione della porta del RowSwap: ");
			e.printStackTrace();
			System.exit(1);
		}
		if(portS==-1) {
			System.out.println("File non trovato, impossibile trovare la porta dello SwapRow. ");
			System.exit(2);
		}
		packet.setPort(portS);
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		String righe = null;
		System.out.println("Inserire righe da cambiare e nome file destinazione(separato da spazio,per terminare ctrl D):");
		try {
			while ((righe = stdIn.readLine()) != null) {
				try {
					boStream = new ByteArrayOutputStream();
       					doStream = new DataOutputStream(boStream);
					//doStream.flush();
					doStream.writeUTF(righe);
					data=boStream.toByteArray();
					packet.setData(data);
					socket.send(packet);
					
				} catch (Exception e) {
					e.printStackTrace();
					throw new Exception("Problemi nell'invio della richiesta allo RowSwapper: ");
				}
				try{
					//Senza setData -> EOF EXCEPTION lanciata da diStream2.readUTF()
					packet.setData(buf);
					packet.setLength(256);
					socket.receive(packet);
					ByteArrayInputStream biStream2= new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
					DataInputStream diStream2 = new DataInputStream(biStream2);
					String result = diStream2.readUTF();
					System.out.println(result);
				}catch(Exception e){
					e.printStackTrace();
					throw new Exception("Problemi nella ricezione dell'esito: ");
				}
				System.out.println("Inserire righe da cambiare e nome file destinazione(separato da spazio,per terminare ctrl D):");
			}
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
			System.exit(1);
		}
	
		System.out.println("Client: termino...");
		socket.close();
	}
}
