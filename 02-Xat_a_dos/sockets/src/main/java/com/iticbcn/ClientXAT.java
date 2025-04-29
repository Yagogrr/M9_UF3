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
    
    public ClientXAT() {
        // El constructor no fa res, només inicialitzem variables a connecta()
    }
    
    public void connecta() {
        try {
            // Obrir el socket al servidor
            socket = new Socket(HOST, PORT);
            System.out.println("Client connectat a " + HOST + ":" + PORT);
            
            // Crear els streams de sortida i entrada
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
        // Instanciar ClientXat
        ClientXAT client = new ClientXAT();
        
        // Connectar al servidor
        client.connecta();
        
        try {
            // Crear el FilLectorCX amb l'ObjectOutputStream
            FilLectorCX filLector = new FilLectorCX(client.in);
            System.out.println("Missatge ('" + MSG_SORTIR + "' per tancar): Fil de lectura iniciat");
            
            // Iniciar el fil
            filLector.start();
            
            // Demanar el nom de l'usuari
            Scanner scanner = new Scanner(System.in);
            System.out.print("Rebut: Escriu el teu nom:\n");
            String nom = scanner.nextLine();
            
            // Enviar el nom al servidor
            client.enviarMissatge(nom);
            
            // Enviar missatges rebuts per consola
            String missatge;
            do {
                missatge = scanner.nextLine();
                client.enviarMissatge(missatge);
            } while (!missatge.equalsIgnoreCase(MSG_SORTIR));
            
            // Tancar Scanner
            scanner.close();
            System.out.println("Tancant client...");
            
            // Tancar la connexió
            client.tancarClient();
            System.out.println("Client tancat.");
            System.out.println("El servidor ha tancat la connexió.");
            
        } catch (Exception e) {
            System.err.println("Error en l'execució: " + e.getMessage());
        }
    }
}