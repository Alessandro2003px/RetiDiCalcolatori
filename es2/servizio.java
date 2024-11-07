import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class servizio extends Thread {
    private Socket clientSocket;
    
    public servizio(Socket clientSocket){
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        DataInputStream inSock = null;
        DataOutputStream outSock = null;
        String nomeFile = null;
        File file = null;
        int read_bytes = 0; //bytes
        int buffer_size = 4096;
        byte[] buffer = new byte[buffer_size];
        FileOutputStream outFile = null;
        DataOutputStream dest_stream = null;
        long length = 0, b = 0;

        try{
            // TIMEOUT E STREAM INPUT, OUTPUT
            clientSocket.setSoTimeout(30000);

            inSock = new DataInputStream(clientSocket.getInputStream());
            outSock = new DataOutputStream(clientSocket.getOutputStream());
        }catch(Exception e){
            e.printStackTrace();
            //System.exit(1);
            return;
        }

        try {
            // INIZIO SERVIZIO
            while((nomeFile = inSock.readUTF()) != null){
                // NOME FILE
                System.out.println("nome file ricevuto");
                file = new File(nomeFile);
                if(file.exists()){
                    outSock.writeUTF("salta");
                    System.out.println("inviato salta");
                }else{
                    outSock.writeUTF("attiva");
                    System.out.println("inviato attiva");
                    // LUNGHEZZA FILE
                    length = inSock.readLong();
                    System.out.println("lunghezza ricevuta: " + length);
                    // LETTURA FILE BYTE PER BYTE
                    outFile = new FileOutputStream(nomeFile);
                    // Ricevo il file (in linea)
                    dest_stream = new DataOutputStream(outFile);
                    while (b < length) {
                        read_bytes = inSock.read(buffer);
                        dest_stream.write(buffer, 0, read_bytes);
                        b += read_bytes;
                    }
                    dest_stream.flush();
                    System.out.println("Byte trasferiti: " + b);
                    outFile.close();
                    System.out.println("file scritto");
                }
            }
        }catch (Exception e) {
            //System.exit(1);
            e.printStackTrace();
            return;
        }
    }
}
