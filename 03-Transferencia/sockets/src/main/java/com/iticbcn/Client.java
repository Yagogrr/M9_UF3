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
    public final String DIR_ARRIBADA = "C:\\Users\\Yago\\AppData\\Local\\Temp"; // Directorio temporal del sistema
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
            
            // Mostrar el directorio donde se guardarán los archivos
            System.out.println("Els fitxers es guardaran a: " + new File(DIR_ARRIBADA).getAbsolutePath());
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
                    // Crear los streams si no existen
                    if (oos == null) {
                        oos = new ObjectOutputStream(socket.getOutputStream());
                    }
                    
                    // Enviar el nom del fitxer al servidor
                    oos.writeObject(nomFitxer);
                    oos.flush();
                    
                    // Crear el input stream si no existe
                    if (ois == null) {
                        ois = new ObjectInputStream(socket.getInputStream());
                    }
                    
                    // Rebre el contingut del fitxer
                    byte[] contingutFitxer = (byte[]) ois.readObject();
                    
                    if (contingutFitxer == null) {
                        System.out.println("Error rebent el fitxer del servidor");
                        continue;
                    }
                    
                    // Extreure només el nom del fitxer sense la ruta
                    String nomFitxerSenseRuta = new File(nomFitxer).getName();
                    String fitxerDestino = DIR_ARRIBADA + File.separator + nomFitxerSenseRuta;
                    
                    // Asegurar que el directorio existe
                    File dirDestino = new File(DIR_ARRIBADA);
                    if (!dirDestino.exists()) {
                        dirDestino.mkdirs();
                    }
                    
                    // Guardar el fitxer
                    FileOutputStream fos = new FileOutputStream(fitxerDestino);
                    fos.write(contingutFitxer);
                    fos.close();
                    
                    System.out.println("Fitxer rebut i guardat com: " + new File(fitxerDestino).getAbsolutePath());
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
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
            // Enviar mensaje de salida al servidor si los streams ya están creados
            if (oos != null) {
                oos.writeObject("sortir");
                oos.flush();
                oos.close();
            }
            if (ois != null) ois.close();
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