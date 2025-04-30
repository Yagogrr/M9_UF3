package com.iticbcn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public final int PORT = Servidor.PORT;
    public final String HOST = Servidor.HOST;
    private Socket socket;
    private PrintWriter out;
    private void connecta(){
        try {
            socket = new Socket(HOST,PORT);
            System.out.println("Connectat a servidor en "+HOST+":"+PORT);
            out = new PrintWriter(socket.getOutputStream(),true);
        } catch (IOException e) {
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

    private void envia(String msg){
        out.println(msg);
        System.out.println("Enviat al servidor: "+msg);
    }
    public static void main(String[] args) {
        Client client = new Client();
        client.connecta();
        client.envia("Prova d'enviament 1");
        client.envia("Prova d'enviament 2");
        client.envia("Adeu!");
        String tecla= "a";
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(System.in))) {
            while(!tecla.isBlank()){
                System.err.println("Prem Enter per tancar el client...");
                tecla = bf.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
       
        client.tanca();
        
    }
    
}
