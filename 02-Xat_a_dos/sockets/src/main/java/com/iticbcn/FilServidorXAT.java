package com.iticbcn;

import java.io.ObjectInputStream;

public class FilServidorXAT extends Thread {
    private ObjectInputStream ois;

    public FilServidorXAT(String nom, ObjectInputStream oie){
        setName(nom);
        this.ois = oie;
    }

    @Override
    public void run(){
        try {
            String mensaje;
            do {
                mensaje = (String) ois.readObject();
            } while(!mensaje.equals(ServidorXAT.MSG_SORIR));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
