
import java.net.*;
import java.io.*;

public class clientDir {

	public static void main(String[] args) throws IOException {

        InetAddress addr = null;
		int port = -1;
		int dimMin=0;
        File dir=null;
		try{ //check args
			if(args.length == 4){
                dir=new File(args[2]);
				addr = InetAddress.getByName(args[0]);
				port = Integer.parseInt(args[1]);
                if (port < 1024 || port > 65535) {
                    System.out.println("Usage: java MPutFileClient serverAddr serverPort dir minFileSize");
                    System.exit(1);
                }
                dimMin=Integer.parseInt(args[3]);
                if((!dir.exists())||(!dir.isDirectory())){
                    System.out.println("dir is not dir ");
                    System.exit(1);
                }
			} else{
				System.out.println("Usage: java PutFileClient serverAddr serverPort direttorio dimMax");
				System.exit(1);
			}
		} //try
		// Per esercizio si possono dividere le diverse eccezioni
		catch(Exception e){
			System.out.println("Problemi, i seguenti: ");
			e.printStackTrace();
			System.out.println("Usage: java PutFileClient serverAddr serverPort");
			System.exit(2);
		}

        Socket socket = null;
		FileInputStream inFile = null;
		DataInputStream inSock = null;
		DataOutputStream outSock = null;
		//String nomeFile = null;

        try{
            socket = new Socket(addr, port);
            socket.setSoTimeout(30000);
            System.out.println("Creata la socket: " + socket);
            inSock = new DataInputStream(socket.getInputStream());
            outSock = new DataOutputStream(socket.getOutputStream());
        }
        catch(Exception e){
            System.out.println("Problemi nella creazione della socket: ");
            e.printStackTrace();
            System.out.print("\n^D(Unix)/^Z(Win)+invio per uscire, oppure immetti nome file: ");
            // il client continua l'esecuzione riprendendo dall'inizio del ciclo
            System.exit(3);
        }

            //Long.SIZE;
            long dim;
            File[] listoFiles=dir.listFiles();
            System.out.println("len: "+listoFiles.length);
            for(File file:listoFiles){
                System.out.println("["+file.getName()+"]");
                if(file.isFile()){
                    if((dim=((long)file.length()))>dimMin){
                        try{
                            outSock.writeUTF(file.getName());
                            System.out.println("Inviato il nome del file " + file.getName());
                            String res=inSock.readUTF();
                            if(res.equals("attiva")){
                                System.out.println("Inviato file " + file.getName());
                                outSock.writeLong(dim);
                                inFile = new FileInputStream(file.getAbsolutePath());
                                FileUtility.trasferisci_a_byte_file_binario(new DataInputStream(inFile), outSock);
                                inFile.close(); 
                            }
                            else if(res.equals("salta")){
                                System.out.println("saltato file " + file.getName());
                                continue;
                            }
                            else{
                                System.out.print("bad server");
                                socket.shutdownOutput();
                                System.exit(5);
                            }
                        }
                        catch(Exception e){
                            e.printStackTrace();
                            socket.shutdownOutput();
                            System.exit(5);
                        }
                    }
                }
            }
            System.out.println("chiusura socket");
            socket.close();
    }
}
