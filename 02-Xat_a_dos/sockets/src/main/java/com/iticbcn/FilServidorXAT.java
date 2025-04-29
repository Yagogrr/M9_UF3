package com.iticbcn;

import java.io.IOException;
import java.io.ObjectInputStream;

public class FilServidorXAT extends Thread {
    private static final String MSG_SORTIR = "sortir";
    
    private ObjectInputStream in;
    
    public FilServidorXAT(String nom, ObjectInputStream in) {
        super(nom);
        this.in = in;
    }
    
    @Override
    public void run() {
        try {
            String missatge;
            // Rebre missatges fins que es rebi MSG_SORTIR
            do {
                missatge = (String) in.readObject();
                System.out.println("Missatge ('" + "sortir" + "' per tancar): " + missatge);
            } while (!missatge.equalsIgnoreCase(MSG_SORTIR));
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al rebre missatges: " + e.getMessage());
        }
    }
}