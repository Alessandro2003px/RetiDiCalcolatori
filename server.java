import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class server {
    public static void main(String[] args){
        ServerSocket servSocket = null;
        Socket clientSocket = null;
        DataInputStream inSock = null;
        DataOutputStream outSock = null;
        int port = 2000;
        int length = 0;
        String nomeFile = null;
        File file = null;
        byte[] bytes = null;
        FileOutputStream outFile = null;


        //CONTROLLO ARGOMENTI
        if(args.length != 1){
            System.out.println("Numero args (1) errato");
            System.exit(1);
        }

        try {
            port = Integer.parseInt(args[0]);
            if(port < 1024 || port > 65535){
                System.out.println("Porta deve essere compresa tra 1024 e 65535");
                System.exit(2);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(3);
        }

        try {
            servSocket = new ServerSocket(port);
            servSocket.setReuseAddress(true);
            System.out.println("ServerSocket creata");
            while (true) {
                clientSocket = servSocket.accept();
                clientSocket.setSoTimeout(30000);
                System.out.println("Connessione accettata");

                inSock = new DataInputStream(clientSocket.getInputStream());
                outSock = new DataOutputStream(clientSocket.getOutputStream());

                while(true){
                    try {
                        nomeFile = inSock.readUTF();
                        System.out.println("nome file ricevuto");
                    } catch (IOException e) {
                        clientSocket.close();
                        break;
                    }
                    file = new File(nomeFile);
                    if(file.exists()){
                        outSock.writeUTF("salta");
                        System.out.println("inviato salta");
                        //file.delete();
                    }else{
                        outSock.writeUTF("attiva");
                        System.out.println("inviato attiva");
                        length = inSock.readInt();
                        System.out.println("lunghezza ricevuta: " + length);
                        bytes = new byte[length];
                        //bisogna leggere a pezzi (se length eccessiva, può non esserci spazio sufficiente in memoria)
                        inSock.read(bytes, 0, length);
                        System.out.println("file letto");
                        outFile = new FileOutputStream(nomeFile);
                        outFile.write(bytes);
                        outFile.close();
                        System.out.println("file scritto");
                    }
                }

                clientSocket.close();
            }
            //servSocket.close();
        }catch (Exception e) {
            e.printStackTrace();
            System.exit(4);
        }
    }
}
