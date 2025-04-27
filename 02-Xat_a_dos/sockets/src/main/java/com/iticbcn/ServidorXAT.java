package com.iticbcn;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorXAT {
    public final static int PORT = 9999;
    public final static String HOST = "localhost";
    public final static String MSG_SORIR = "sortir";
    private ServerSocket srvSocket;
    private Socket clientSocket;
    private String nomClient;
    private DataInputStream entradaClient;
    private DataOutputStream sortidaClient;

    public void iniciarServidor() {
        try {
            srvSocket = new ServerSocket(PORT);
            System.out.println("Servidor en marxa a "+HOST+":"+PORT);
            System.out.println("Esperant connexions a "+HOST+":"+PORT);
            clientSocket = srvSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getNom() {
        try {
            entradaClient = new DataInputStream(clientSocket.getInputStream());
            sortidaClient = new DataOutputStream(clientSocket.getOutputStream());
            nomClient = entradaClient.readUTF();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public void pararServidor() {
        try {
            clientSocket.close();
            srvSocket.close();
            System.out.println("Servidor tancat.");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public DataInputStream getEntradaClient(){ return entradaClient;}

    public static void main(String[] args) throws IOException,InterruptedException{
        ServidorXAT servidorXAT = new ServidorXAT();
        servidorXAT.iniciarServidor();
        servidorXAT.getNom();
        FilServidorXAT fsx = new FilServidorXAT(HOST, new ObjectInputStream(servidorXAT.getEntradaClient()));
        fsx.start();
        /*
         * Enviar mensajes que lee desde la consola hasta la salida
         */
        fsx.join();
        servidorXAT.pararServidor();
    }
}