package com.iticbcn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    public final static int PORT = 7777;
    public final static String HOST = "localhost";
    private ServerSocket srvSocket;
    private Socket clientSocket;

    public void connecta() {
        try {
            srvSocket = new ServerSocket(PORT);
            System.out.println("Servidor en marxa a "+HOST+":"+PORT);
            System.out.println("Esperant connexions a "+HOST+":"+PORT);
            clientSocket = srvSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void repDades() {
        System.out.println("Client connectat: "+clientSocket.getInetAddress());
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {
            String msg;
            while ((msg = bf.readLine()) != null) { //per cada misatge, faig un print
                System.out.println("Rebut: "+msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void tanca() {
        try {
            clientSocket.close();
            srvSocket.close();
            System.out.println("Servidor tancat.");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        servidor.connecta();
        servidor.repDades();
        servidor.tanca();
    }
}