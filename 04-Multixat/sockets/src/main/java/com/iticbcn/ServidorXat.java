package com.iticbcn;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

/**
 * Clase ServidorXat que gestiona las conexiones de los clientes y la distribución de mensajes
 */
public class ServidorXat {
    public final static int PORT = 9999;
    public final static String HOST = "localhost";
    public final static String MSG_SORTIR = "sortir";
    
    private Hashtable<String, GestorClients> clients;
    private boolean sortir;
    private ServerSocket serverSocket;
    
    public ServidorXat() {
        clients = new Hashtable<>();
        sortir = false;
    }
    
    public void servidorAEscoltar() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor iniciat a " + HOST + ":" + PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void pararServidor() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void finalitzarXat() {
        enviarMissatgeGrup(MSG_SORTIR);
        clients.clear();
        sortir = true;
        pararServidor();
        System.out.println("Tancant tots els clients.");
        System.out.println("DEBUG: multicast sortir");
        System.exit(0);
    }
    
    public void afegirClient(GestorClients gestorClient) {
        clients.put(gestorClient.getNom(), gestorClient);
        enviarMissatgeGrup("Entra: " + gestorClient.getNom());
        System.out.println(gestorClient.getNom() + " connectat.");
        System.out.println("DEBUG: multicast Entra: " + gestorClient.getNom());
    }
    
    public void eliminarClient(String nom) {
        if (clients.containsKey(nom)) {
            clients.remove(nom);
            System.out.println(nom + " desconnectat.");
        }
    }
    
    public void enviarMissatgeGrup(String missatge) {
        System.out.println("DEBUG: multicast " + missatge);
        String missatgeCodificat = Missatge.getMissatgeGrup(missatge);
        for (GestorClients client : clients.values()) {
            client.enviarMissatge("Servidor", missatgeCodificat);
        }
    }
    
    public void enviarMissatgePersonal(String destinatari, String remitent, String missatge) {
        if (clients.containsKey(destinatari)) {
            String missatgeCodificat = Missatge.getMissatgePersonal(remitent, missatge);
            clients.get(destinatari).enviarMissatge(remitent, missatgeCodificat);
            System.out.println("Missatge personal per (" + destinatari + ") de (" + remitent + "): " + missatge);
        }
    }
    
    public static void main(String[] args) {
        ServidorXat servidor = new ServidorXat();
        servidor.servidorAEscoltar();
        
        while (!servidor.sortir) {
            try {
                Socket clientSocket = servidor.serverSocket.accept();
                System.out.println("Client connectat: " + clientSocket.getInetAddress());
                
                GestorClients gestorClient = new GestorClients(clientSocket, servidor);
                gestorClient.start();
            } catch (IOException e) {
                if (!servidor.sortir) {
                    e.printStackTrace();
                }
            }
        }
        
        servidor.pararServidor();
    }
}