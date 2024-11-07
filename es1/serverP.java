import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class serverP {
    public static void main(String[] args){
        ServerSocket servSocket = null;
        Socket clientSocket;
        int port = 2000;

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
            // CREAZIONE SERVERSOCKET
            servSocket = new ServerSocket(port);
            servSocket.setReuseAddress(true);
            System.out.println("ServerSocket creata");
            while (true) {
                // ACCETTO CONNESSIONE E START THREAD
                clientSocket = servSocket.accept();
                System.out.println("Connessione accettata");
                servizio s = new servizio(clientSocket);
                s.start();
            }
        }catch(Exception e){
            e.printStackTrace();
            System.exit(4);
        }
    }
}
