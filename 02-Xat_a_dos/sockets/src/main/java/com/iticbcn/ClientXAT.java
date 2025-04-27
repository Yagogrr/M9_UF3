package com.iticbcn;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientXAT {
    public final int PORT = ServidorXAT.PORT;
    public final String HOST = ServidorXAT.HOST;
    private Socket socket;
    private PrintWriter out;
    private DataInputStream entrada;
    private DataOutputStream sortida;

    private void connecta() {
        try {
            socket = new Socket(HOST, PORT);
            entrada = new DataInputStream(socket.getInputStream());
            sortida = new DataOutputStream(socket.getOutputStream());
            System.out.println("Connectat a servidor en " + HOST + ":" + PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enviarMissatge(String msg){
        try {
            sortida.writeUTF(msg);
        } catch (Exception e) {
            System.out.println("Error enviant el missatge");
        }
        
    }

    public void tanca() {
        try {
            socket.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DataOutputStream getSortidaClient(){ return sortida;}

    public static void main(String[] args) throws IOException {
        ClientXAT clientXAT = new ClientXAT();
        clientXAT.connecta();
        FileLectorCX flcx = new FileLectorCX(new ObjectOutputStream(clientXAT.getSortidaClient()));
        flcx.start();
        /*
         * Envie mensajes recividos por la consola
         * con scanner
         */
        clientXAT.tanca();

    }

}
