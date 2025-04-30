package com.iticbcn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    public final String DIR_ARRIBADA = "C:\\Users\\Yago\\AppData\\Local\\Temp"; // o el equivalente en Windows
    public final int PORT = Servidor.PORT;
    public final String HOST = Servidor.HOST;
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public void connectar() {
        try {
            socket = new Socket(HOST, PORT);
            System.out.println("Connectant a -> " + HOST + ":" + PORT);
            System.out.println("Connexio acceptada: " + socket.getInetAddress().getHostAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void rebreFitxers() {
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
            String nomFitxer = "";
            
            while (!nomFitxer.equals("sortir")) {
                System.out.print("Nom del fitxer a rebre ('sortir' per sortir): ");
                nomFitxer = bf.readLine();
                
                if (nomFitxer.equals("sortir") || nomFitxer.isBlank()) {
                    break;
                }
                
                try {
                    // Enviar el nom del fitxer al servidor
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(nomFitxer);
                    oos.flush();
                    
                    // Rebre el contingut del fitxer
                    ois = new ObjectInputStream(socket.getInputStream());
                    byte[] contingutFitxer = (byte[]) ois.readObject();
                    
                    if (contingutFitxer == null) {
                        System.out.println("Error rebent el fitxer del servidor");
                        continue;
                    }
                    
                    // Extreure només el nom del fitxer sense la ruta
                    String nomFitxerSenseRuta = new File(nomFitxer).getName();
                    String fitxerDestino = DIR_ARRIBADA + File.separator + nomFitxerSenseRuta;
                    
                    // Guardar el fitxer
                    FileOutputStream fos = new FileOutputStream(fitxerDestino);
                    fos.write(contingutFitxer);
                    fos.close();
                    
                    System.out.println("Fitxer rebut i guardat com: " + fitxerDestino);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            System.out.println("sortir");
            System.out.println("Sortint...");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tancarConnexio() {
        try {
            if (ois != null) ois.close();
            if (oos != null) oos.close();
            if (socket != null) socket.close();
            System.out.println("Connexio tancada.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.connectar();
        client.rebreFitxers();
        client.tancarConnexio();
    }
}