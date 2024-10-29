
import java.net.*;
import java.io.*;

public class clientDir {

	public static void main(String[] args) throws IOException {

        InetAddress addr = null;
		int port = -1;
		int dimMin=0;
        File dir=new File(args[2]);
		try{ //check args
			if(args.length == 4){
				addr = InetAddress.getByName(args[0]);
				port = Integer.parseInt(args[1]);
                dimMin=Integer.parseInt(args[3]);
                if(!dir.isDirectory()){
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
        }
        catch(Exception e){
            System.out.println("Problemi nella creazione della socket: ");
            e.printStackTrace();
            System.out
                .print("\n^D(Unix)/^Z(Win)+invio per uscire, oppure immetti nome file: ");
            // il client continua l'esecuzione riprendendo dall'inizio del ciclo
            System.exit(3);

        }

        // creazione stream di input/output su socket
        try{
            inSock = new DataInputStream(socket.getInputStream());
            outSock = new DataOutputStream(socket.getOutputStream());
        }
        catch(IOException e){
            System.out
                .println("Problemi nella creazione degli stream su socket: ");
            e.printStackTrace();
            System.out
                .print("\n^D(Unix)/^Z(Win)+invio per uscire, oppure immetti nome file: ");            // il client continua l'esecuzione riprendendo dall'inizio del ciclo
                System.exit(4);

            }
            //Long.SIZE;
                        int dim;
            File[] listoFiles=dir.listFiles();
            for(File file:listoFiles){
                if(file.isFile()){
                    if((dim=((int)file.length()))>dimMin){

                        try{
                            inFile = new FileInputStream(file.getName());
                        }
                        
                        catch(FileNotFoundException e){
                            e.printStackTrace();
                            socket.shutdownOutput();
                            System.exit(5);

                        } 

                        try{
                            outSock.writeUTF(file.getName());
                            System.out.println("Inviato il nome del file " + file.getName());
                        }
                        catch(Exception e){
                            System.out.println("Problemi nell'invio del nome di " + file.getName()
                                + ": ");
                            e.printStackTrace();
                            System.out
                                  .print("\n^D(Unix)/^Z(Win)+invio per uscire, oppure immetti nome file: ");
                            socket.shutdownOutput();
                            System.exit(5);
                        }
                            String res=inSock.readUTF();
                            if(res.equals("attiva")){
                                outSock.writeLong(dim);
                                FileUtility.trasferisci_a_byte_file_binario(new DataInputStream(inFile), outSock);
                                inFile.close(); 
                            }
                            else if(res.equals("salta")){
                                    continue;
                            }
                            else{
                                System.out
                                  .print("bad server");
                            socket.shutdownOutput();
                            System.exit(5);
                            }







                    }




                }
            }
            socket.close();
            





    }

}