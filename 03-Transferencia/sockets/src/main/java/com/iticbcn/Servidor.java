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
            // Crear los streams una sola vez
            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
            
            String nomFitxer = null;
            
            while (true) {
                System.out.println("Esperant el nom del fitxer del client...");
                try {
                    nomFitxer = (String) ois.readObject();
                    
                    // Comprobar si el cliente quiere salir
                    if (nomFitxer == null || nomFitxer.equals("sortir") || nomFitxer.isBlank()) {
                        System.out.println("Nom del fitxer buit o nul. Sortint...");
                        break;
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
                } catch (Exception e) {
                    System.out.println("Error rebent el nom del fitxer o connexió tancada pel client");
                    break;
                }
            }
            
            // Cerrar streams
            try {
                ois.close();
                oos.close();
            } catch (Exception e) {
                System.out.println("Error tancant els streams: " + e.getMessage());
            }
            
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