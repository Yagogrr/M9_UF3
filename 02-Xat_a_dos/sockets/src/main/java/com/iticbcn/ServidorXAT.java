package com.iticbcn;
import java.io.*;
import java.net.*;

public class ServidorXAT {
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    private static final String MSG_SORTIR = "sortir";
    
    private ServerSocket serverSocket;
    
    public ServidorXAT() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor iniciat a " + HOST + ":" + PORT);
        } catch (IOException e) {
            System.err.println("Error al crear el servidor: " + e.getMessage());
        }
    }
    
    public void iniciarServidor() {
        try {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connectat: " + clientSocket.getInetAddress().getHostAddress());
            
            // Crear streams
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            
            // Rebre el nom del client
            String nomClient = getNom(in);
            System.out.println("Nom rebut: " + nomClient);
            
            // Instanciar i iniciar el fil per gestionar la comunicació
            FilServidorXAT filServidor = new FilServidorXAT(nomClient, in);
            System.out.println("Fil de xat creat.");
            filServidor.start();
            System.out.println("Fil de " + nomClient + " iniciat");
            
            // Enviament de missatges des del servidor
            BufferedReader consola = new BufferedReader(new InputStreamReader(System.in));
            String missatge;
            
            do {
                missatge = consola.readLine();
                out.writeObject("Rebut: " + missatge);
                out.flush();
                System.out.println("Missatge ('" + MSG_SORTIR + "' per tancar): " + missatge);
            } while (!missatge.equalsIgnoreCase(MSG_SORTIR));
            
            // Esperar a què finalitzi el fil
            filServidor.join();
            System.out.println("Fil de xat finalitzat.");
            
            // Tancar la connexió
            clientSocket.close();
            System.out.println("Servidor aturat.");
            
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            System.err.println("Error en la comunicació: " + e.getMessage());
        }
    }
    
    private String getNom(ObjectInputStream in) throws IOException, ClassNotFoundException {
        return (String) in.readObject();
    }
    
    public void pararServidor() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error al tancar el servidor: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        // Crear instància de ServidorXat
        ServidorXAT servidor = new ServidorXAT();
        
        // Iniciar el servidor
        servidor.iniciarServidor();
    }
}