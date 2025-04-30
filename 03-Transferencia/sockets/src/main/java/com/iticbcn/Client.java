package com.iticbcn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public final String DIR_ARRIBADA = "C:\\Users\\Yago\\AppData\\Local\\Temp";
    public final int PORT = Servidor.PORT;
    public final String HOST = Servidor.HOST;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Socket socket;
    private PrintWriter out;
    private byte[] contenidoFichero;

    public void connecta(){
        try {
            socket = new Socket(HOST,PORT);
            System.out.println("Connectat a servidor en "+HOST+":"+PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void rebreFitxers(){
        try {
            String mensaje = "-1";
            while(mensaje.equals("sortir")||mensaje.isBlank()){
                try(BufferedReader bf = new BufferedReader(new InputStreamReader(System.in))){
                    System.out.print("Escriba la ruta del fichero mi tete");
                    mensaje = bf.readLine();
                    if(mensaje.equals("sortir")||mensaje.isBlank()){
                        break;
                    }
                    //inanciar OOS y OIS
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    ois =  new ObjectInputStream(socket.getInputStream());
    
                    //enviar ruta
                    oos.writeObject(mensaje); 
                    oos.flush();
                    oos.close();
    
                    //recibir datos
                    byte[] contenidoFichero = (byte[]) ois.readObject();
                    this.contenidoFichero = contenidoFichero;
                    ois.close();
                } 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void tanca(){
        try {
            socket.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public static void main(String[] args) {
        Client client = new Client();
        client.connecta();
        client.rebreFitxers();
        client.tanca();
    }
    
}
