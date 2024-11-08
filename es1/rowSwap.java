import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.StringTokenizer;

public class rowSwap extends Thread{
    private DatagramSocket socket = null;
    private DatagramPacket packet = null;
    private byte[] buf = new byte[256];
    private int port;
    private String filename;
    public rowSwap(String filename,int port){
            this.port=port;
            this.filename=filename;
    }
    @Override
    public void run(){

        try {
			socket = new DatagramSocket(port);
			packet = new DatagramPacket(buf, buf.length);
			System.out.println("Creato rowswap, socket: " + socket);
		}
        catch (SocketException e) {
			System.out.println("Problemi nella creazione della socket: ");
			e.printStackTrace();
			System.exit(1);
		}

        File file= new File(this.filename);
                int numLinea1 = -1;
                int numLinea2 = -1;
			String richiesta = null;
			ByteArrayInputStream biStream = null;
			DataInputStream diStream = null;
			StringTokenizer st = null;
			ByteArrayOutputStream boStream = null;
			DataOutputStream doStream = null;
			String linea = null;
			byte[] data = null;
            String nomeFileRicevuto=null;
            String dataSend=null;
        while(true){

            try {
                packet.setData(buf);
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

            try{
                biStream = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
				diStream = new DataInputStream(biStream);
				richiesta = diStream.readUTF();
                st = new StringTokenizer(richiesta);
                nomeFileRicevuto = st.nextToken();
                System.out.println(nomeFileRicevuto);
                numLinea1 = Integer.parseInt(st.nextToken());
                System.out.println("l1="+numLinea1);
                numLinea2 = Integer.parseInt(st.nextToken());
                System.out.println("l2="+numLinea2);
                
            }
            catch(Exception e){
            
                dataSend="parsing fallito";
                System.err.println(e);
            }
            if(numLinea1<1||numLinea2<1)
            {          
                  dataSend="numlinea negativo";
            }
        else{

        
            BufferedReader userInput=null;
            try {
                //FileInputStream inputStream=new FileInputStream(file);
                userInput = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                
            } catch (FileNotFoundException e) {
                //  Auto-generated catch block
                e.printStackTrace();
            }
            int i=1;
            String SString1=null,SString2=null;

            try {
                File fileout=new File(nomeFileRicevuto);
                PrintWriter out = new PrintWriter(fileout);
                
                while((linea = userInput.readLine()) != null)   {
                    if(i==numLinea1){
                        SString1=linea;
                    }
                    else if(i==numLinea2)
                    SString2=linea;
                    i++;
                 
                }
                userInput.close();
                userInput = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                if(numLinea1>=i||numLinea2>=i){
                    dataSend="num linea non valido";

                }
                else{
                    i=1;

                 while((linea = userInput.readLine()) != null)   {
                    if(i==numLinea1){
                        out.println(SString2);
                    }
                    else if(i==numLinea2){
                        out.println(SString1);

                    }
                    else{
                        out.println(linea);
                    }
                    
                    
                    
                    
                    i++;
                 }
                 dataSend="positivo";
                }
                

                    out.flush();  // Flush data to the file before closing

                        out.close();

            } catch (IOException e) {
                //  Auto-generated catch block
                e.printStackTrace();
            }
        }
        try{
            boStream = new ByteArrayOutputStream();
            doStream = new DataOutputStream(boStream);
            doStream.writeUTF(dataSend);
            data = boStream.toByteArray();
            packet.setData(data, 0, data.length);
            socket.send(packet);
            System.out.println("datatosend="+dataSend);
        }
        catch (IOException e) {
            //  Auto-generated catch block
            e.printStackTrace();
        }
                    
        }
      

    }
    
}




/*import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.io.*;
import java.util.StringTokenizer;

public class rowSwap extends Thread{
    private DatagramSocket socket = null;
    private DatagramPacket packet = null;
    private byte[] buf = new byte[256];
    private int port;
    private String filename;
    public rowSwap(String filename,int port){
            this.port=port;
            this.filename=filename;
    }
    @Override
    public void run(){

        try {
			socket = new DatagramSocket(port);
			packet = new DatagramPacket(buf, buf.length);
			System.out.println("Creato rowswap, socket: " + socket);
		}
        catch (SocketException e) {
			System.out.println("Problemi nella creazione della socket: ");
			e.printStackTrace();
			System.exit(1);
		}

        File file= new File(this.filename);
                int numLinea1 = -1;
                int numLinea2 = -1;
			String richiesta = null;
			ByteArrayInputStream biStream = null;
			DataInputStream diStream = null;
			StringTokenizer st = null;
			ByteArrayOutputStream boStream = null;
			DataOutputStream doStream = null;
			String linea = null;
			byte[] data = null;
            String nomeFileRicevuto=null;
            String dataSend=null;
        while(true){

            try {
                packet.setData(buf);
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

            try{
                biStream = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
				diStream = new DataInputStream(biStream);
				richiesta = diStream.readUTF();
                st = new StringTokenizer(richiesta);
                nomeFileRicevuto = st.nextToken();
                System.out.print(nomeFileRicevuto);
                numLinea1 = Integer.parseInt(st.nextToken());
                System.out.print(numLinea1);
                numLinea2 = Integer.parseInt(st.nextToken());
                System.out.print(numLinea2);
                
            }
            catch(Exception e){
            
                dataSend="parsing fallito";
                System.err.println(e);
            }
            if(numLinea1<1||numLinea2<1)
            {          
                  dataSend="numlinea negativo";
            }
        else{

        
            BufferedReader userInput=null;
            try {
                //FileInputStream inputStream=new FileInputStream(file);
                userInput = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            } catch (FileNotFoundException e) {
                //  Auto-generated catch block
                e.printStackTrace();
            }
            int i=1;
            String SString1=null,SString2=null;

            try {
                File fileout=new File(nomeFileRicevuto);
                PrintWriter out = new PrintWriter(fileout);
                while((linea = userInput.readLine()) != null)   {
                    if(i==numLinea1){
                        SString1=linea;
                    }
                    else if(i==numLinea2)
                    SString2=linea;
                    i++;
                 
                }
                if(numLinea1>i||numLinea2>i){
                    dataSend="num linea non valido";

                }
                else{
                    i=1;

                 while((linea = userInput.readLine()) != null)   {
                    if(i==numLinea1){
                        out.write(SString2);
                    }
                    else if(i==numLinea2){
                        out.write(SString1);

                    }
                    else{
                        out.write(linea);
                    }
                    
                    
                    
                    
                    i++;
                 }
                 dataSend="risultato positivo";
                }
                


                        out.close();

            } catch (IOException e) {
                //  Auto-generated catch block
                e.printStackTrace();
            }
        }
        try{
            boStream = new ByteArrayOutputStream();
            doStream = new DataOutputStream(boStream);
            doStream.writeUTF(dataSend);
            data = boStream.toByteArray();
            packet.setData(data, 0, data.length);
            socket.send(packet);
        }
        catch (IOException e) {
            //  Auto-generated catch block
            e.printStackTrace();
        }
                    
        }
      

    }
    
}
*/