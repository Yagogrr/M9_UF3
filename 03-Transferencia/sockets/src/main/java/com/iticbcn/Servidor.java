package com.iticbcn;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
            System.out.println("Acceptant connexions en -> " + HOST + ":" + PORT);
            System.out.println("Esperant connexio...");
            clientSocket = srvSocket.accept();
            System.out.println("Connexio acceptada: " + clientSocket.getInetAddress().getHostAddress());
            return clientSocket;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void enviarFitxers() {
        try {
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
            
            System.out.println("Esperant el nom del fitxer del client...");
            String nomFitxer = (String) ois.readObject();
            
            if (nomFitxer == null || nomFitxer.equals("sortir")) {
                System.out.println("Nom del fitxer buit o nul. Sortint...");
                return;
            }
            
            System.out.println("Nomfitxer rebut: " + nomFitxer);
            
            try {
                Fitxer fitxer = new Fitxer(nomFitxer);
                byte[] contingut = fitxer.getContingut();
                System.out.println("Contingut del fitxer a enviar: " + contingut.length + " bytes");
                
                // Enviar el contingut del fitxer
                oos.writeObject(contingut);
                oos.flush();
                System.out.println("Fitxer enviat al client: " + nomFitxer);
            } catch (IOException e) {
                System.out.println("Error llegint el fitxer del client: " + e.getMessage());
                oos.writeObject(null);
                oos.flush();
            }
            
            ois.close();
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void tancarConnexio(Socket socket) {
        try {
            if (socket != null) {
                socket.close();
            }
            if (clientSocket != null) {
                System.out.println("Tancant connexió amb el client: " + clientSocket.getInetAddress().getHostAddress());
                clientSocket.close();
            }
            if (srvSocket != null) {
                srvSocket.close();
                System.out.println("Servidor tancat.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        Socket socket = servidor.connectar();
        servidor.enviarFitxers();
        servidor.tancarConnexio(socket);
    }
}