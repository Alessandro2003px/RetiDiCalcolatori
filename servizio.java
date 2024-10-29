import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class servizio extends Thread{
    private Socket clientSocket;
    
    public servizio(Socket clientSocket){
        this.clientSocket = clientSocket;
    }

    @Override
    public void run(){
        DataInputStream inSock = null;
        DataOutputStream outSock = null;
        String nomeFile = null;
        File file = null;
        int b = 0;
        FileOutputStream outFile = null;
        int length = 0;

        try {
                // TIMEOUT E STREAM INPUT, OUTPUT
                clientSocket.setSoTimeout(30000);

                inSock = new DataInputStream(clientSocket.getInputStream());
                outSock = new DataOutputStream(clientSocket.getOutputStream());

                // INIZIO SERVIZIO
                while(true){
                    // NOME FILE
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
                    }else{
                        outSock.writeUTF("attiva");
                        System.out.println("inviato attiva");
                        // LUNGHEZZA FILE
                        length = inSock.readInt();
                        System.out.println("lunghezza ricevuta: " + length);
                        // LETTURA FILE BYTE PER BYTE
                        //NO THREAD SAFE -> DA CORREGGERE
                        outFile = new FileOutputStream(nomeFile);
                        for(int i = 0; i < length; i++){
                            if((b = inSock.read()) >= 0)
                                outFile.write(b);
                        }
                        System.out.println("file letto");
                        outFile.close();
                        System.out.println("file scritto");
                    }
                }
        } catch(SocketTimeoutException e){
            try {
                clientSocket.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
