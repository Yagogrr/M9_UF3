package com.iticbcn;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;

public class FilLectorCX extends Thread{
    private ObjectInputStream stream;
    
    public FilLectorCX(ObjectInputStream stream) {
        this.stream = stream;
    }
    
    @Override
    public void run() {
        try {
            String missatge;
            while (true) {
                missatge = (String) stream.readObject();
                System.out.println("Missatge ('" + "sortir" + "' per tancar): " + missatge);
            }
        } catch (EOFException e) {
            System.out.println("El servidor ha tancat la connexió.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al rebre missatges del servidor: " + e.getMessage());
        }
    }
}