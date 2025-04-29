package com.iticbcn;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientXAT { private static final int PORT = 9999;
    private static final String HOST = "localhost";
    private static final String MSG_SORTIR = "sortir";
    
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    
    public void connecta() {
        try {
            socket = new Socket(HOST, PORT);
            System.out.println("Client connectat a " + HOST + ":" + PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Flux d'entrada i sortida creat.");
        } catch (IOException e) {
            System.err.println("Error al connectar: " + e.getMessage());
        }
    }
    
    public void enviarMissatge(String missatge) {
        try {
            out.writeObject(missatge);
            out.flush();
            System.out.println("Enviant missatge: " + missatge);
        } catch (IOException e) {
            System.err.println("Error al enviar missatge: " + e.getMessage());
        }
    }
    
    public void tancarClient() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Client tancat.");
            }
        } catch (IOException e) {
            System.err.println("Error al tancar el client: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        ClientXAT client = new ClientXAT();
        client.connecta();
        try {
            FilLectorCX filLector = new FilLectorCX(client.in);
            System.out.println("Missatge ('" + MSG_SORTIR + "' per tancar): Fil de lectura iniciat");
            filLector.start();
            Scanner scanner = new Scanner(System.in);
            System.out.print("Rebut: Escriu el teu nom:\n");
            String nom = scanner.nextLine();
            client.enviarMissatge(nom);
            String missatge;
            do {
                missatge = scanner.nextLine();
                client.enviarMissatge(missatge);
            } while (!missatge.equalsIgnoreCase(MSG_SORTIR));
            scanner.close();
            System.out.println("Tancant client...");
            client.tancarClient();
            System.out.println("Client tancat.");
            System.out.println("El servidor ha tancat la connexió.");
            
        } catch (Exception e) {
            System.err.println("Error en l'execució: " + e.getMessage());
        }
    }
}