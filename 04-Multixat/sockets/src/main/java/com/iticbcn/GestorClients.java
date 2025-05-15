package com.iticbcn;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GestorClients extends Thread {
    private Socket cliente;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private ServidorXat servidorXat;
    private String nombre;
    private boolean salir;
    
    public GestorClients(Socket cliente, ServidorXat servidorXat) {
        this.cliente = cliente;
        this.servidorXat = servidorXat;
        this.salir = false;
        
        try {
            this.oos = new ObjectOutputStream(cliente.getOutputStream());
            this.ois = new ObjectInputStream(cliente.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String getNom() {
        return nombre;
    }
    
    @Override
    public void run() {
        try {
            String mensaje;
            
            while (!salir) {
                try {
                    mensaje = (String) ois.readObject();
                    procesaMensaje(mensaje);
                } catch (Exception e) {
                    System.out.println("Error rebent missatge del client: " + e.getMessage());
                    salir = true;
                }
            }
            
            cliente.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void enviarMissatge(String remitente, String mensaje) {
        try {
            oos.writeObject(mensaje);
            oos.flush();
        } catch (Exception e) {
            System.out.println("Error enviant missatge a " + nombre + ": " + e.getMessage());
        }
    }
    
    public void procesaMensaje(String mensaje) {
        String codigo = Missatge.getCodiMissatge(mensaje);
        if (codigo == null) return;
        
        String[] partes = Missatge.getPartsMissatge(mensaje);
        
        switch (codigo) {
            case Missatge.CODI_CONECTAR:
                if (partes.length >= 2) {
                    nombre = partes[1];
                    servidorXat.afegirClient(this);
                }
                break;
                
            case Missatge.CODI_SORTIR_CLIENT:
                salir = true;
                servidorXat.eliminarClient(nombre);
                break;
                
            case Missatge.CODI_SORTIR_TOTS:
                salir = true;
                servidorXat.finalitzarXat();
                break;
                
            case Missatge.CODI_MSG_PERSONAL:
                if (partes.length >= 3) {
                    String destinatario = partes[1];
                    String msg = partes[2];
                    servidorXat.enviarMissatgePersonal(destinatario, nombre, msg);
                }
                break;
                
            case Missatge.CODI_MSG_GRUP:
                if (partes.length >= 2) {
                    String msg = partes[1];
                    servidorXat.enviarMissatgeGrup(nombre + ": " + msg);
                }
                break;
                
            default:
                System.out.println("Codi d'operació desconegut: " + codigo);
        }
    }
}