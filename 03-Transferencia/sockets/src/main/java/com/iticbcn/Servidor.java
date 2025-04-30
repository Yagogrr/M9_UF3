package com.iticbcn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    public final static int PORT = 9999;
    public final static String HOST = "localhost";
    private ServerSocket srvSocket;
    private Socket clientSocket;

    public Socket connectar() {
        try {
            srvSocket = new ServerSocket(PORT);
            System.out.println("Servidor en marxa a "+HOST+":"+PORT);
            System.out.println("Esperant connexions a "+HOST+":"+PORT);
            return clientSocket = srvSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void enviarFitxers() {
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {
            String msg;
            while ((msg = bf.readLine()) != null) { 
                //TODO
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void tanca(Socket socket) {
        try {
            socket.close();
            clientSocket.close();
            srvSocket.close();
            System.out.println("Servidor tancat.");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        servidor.connectar();
        servidor.enviarFitxers();
        servidor.tanca(null);
    }
}