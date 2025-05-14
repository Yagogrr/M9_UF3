package com.iticbcn;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * Clase ClientXat que implementa la interfaz de usuario para el cliente del chat
 */
public class ClientXat implements Runnable {
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private boolean sortir;
    
    public ClientXat() {
        this.sortir = false;
    }
    
    public void connecta() {
        try {
            socket = new Socket(ServidorXat.HOST, ServidorXat.PORT);
            System.out.println("Client connectat a " + ServidorXat.HOST + ":" + ServidorXat.PORT);
            
            // Inicializar ObjectOutputStream primero para evitar bloqueos
            oos = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Flux d'entrada i sortida creat.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void enviarMissatge(String missatge) {
        try {
            if (oos != null) {
                System.out.println("Enviant missatge: " + missatge);
                oos.writeObject(missatge);
                oos.flush();
            } else {
                System.out.println("oos null. Sortint...");
            }
        } catch (IOException e) {
            System.out.println("Error enviant missatge: " + e.getMessage());
        }
    }
    
    public void tancarClient() {
        try {
            System.out.println("Tancant client...");
            if (ois != null) {
                ois.close();
                System.out.println("Flux d'entrada tancat.");
            }
            if (oos != null) {
                oos.close();
                System.out.println("Flux de sortida tancat.");
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void run() {
        try {
            System.out.println("DEBUG: Iniciant rebuda de missatges...");
            ois = new ObjectInputStream(socket.getInputStream());
            
            while (!sortir) {
                try {
                    String missatgeCru = (String) ois.readObject();
                    String codi = Missatge.getCodiMissatge(missatgeCru);
                    String[] parts = Missatge.getPartsMissatge(missatgeCru);
                    
                    if (codi == null || parts == null) continue;
                    
                    switch (codi) {
                        case Missatge.CODI_SORTIR_TOTS:
                            sortir = true;
                            break;
                            
                        case Missatge.CODI_MSG_PERSONAL:
                            if (parts.length >= 3) {
                                String remitent = parts[1];
                                String missatge = parts[2];
                                System.out.println("Missatge de (" + remitent + "): " + missatge);
                            }
                            break;
                            
                        case Missatge.CODI_MSG_GRUP:
                            if (parts.length >= 2) {
                                System.out.println("Missatge de grup: " + parts[1]);
                            }
                            break;
                            
                        default:
                            System.out.println("Codi desconegut rebut: " + codi);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Error rebent missatge. Sortint...");
                    sortir = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            tancarClient();
        }
    }
    
    public void ajuda() {
        System.out.println("---------------------");
        System.out.println("Comandes disponibles:");
        System.out.println("1.- Conectar al servidor (primer pass obligatori)");
        System.out.println("2.- Enviar missatge personal");
        System.out.println("3.- Enviar missatge al grup");
        System.out.println("4.- (o línia en blanc)-> Sortir del client");
        System.out.println("5.- Finalitzar tothom");
        System.out.println("---------------------");
    }
    
    public String getLinea(Scanner sc, String missatge, boolean obligatori) {
        String linea = "";
        do {
            System.out.print(missatge);
            linea = sc.nextLine().trim();
            if (linea.isEmpty() && obligatori) {
                System.out.println("Aquest camp és obligatori");
            } else {
                break;
            }
        } while (obligatori);
        
        return linea;
    }
    
    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        client.connecta();
        
        // Iniciar thread para recibir mensajes
        Thread threadRebre = new Thread(client);
        threadRebre.start();
        
        client.ajuda();
        
        Scanner sc = new Scanner(System.in);
        String opcio;
        
        while (!client.sortir) {
            opcio = sc.nextLine();
            
            if (opcio.isEmpty()) {
                client.sortir = true;
                continue;
            }
            
            switch (opcio) {
                case "1":  // Conectar
                    String nom = client.getLinea(sc, "Introdueix el nom: ", true);
                    client.enviarMissatge(Missatge.getMissatgeConectar(nom));
                    client.ajuda();
                    break;
                    
                case "2":  // Mensaje personal
                    String destinatari = client.getLinea(sc, "Destinatari:: ", true);
                    String missatge = client.getLinea(sc, "Missatge a enviar: ", true);
                    client.enviarMissatge(Missatge.getMissatgePersonal(destinatari, missatge));
                    client.ajuda();
                    break;
                    
                case "3":  // Mensaje al grupo
                    String msgGrup = client.getLinea(sc, "Missatge a enviar al grup: ", true);
                    client.enviarMissatge(Missatge.getMissatgeGrup(msgGrup));
                    client.ajuda();
                    break;
                    
                case "4":  // Salir cliente
                    client.enviarMissatge(Missatge.getMissatgeSortirClient("Adéu"));
                    client.sortir = true;
                    break;
                    
                case "5":  // Finalizar todos
                    client.enviarMissatge(Missatge.getMissatgeSortirTots("Adéu"));
                    client.sortir = true;
                    break;
                    
                default:
                    System.out.println("Opció no vàlida");
                    client.ajuda();
            }
        }
        
        sc.close();
        client.tancarClient();
    }
}
