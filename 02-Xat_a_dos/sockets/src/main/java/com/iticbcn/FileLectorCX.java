package com.iticbcn;

import java.io.ObjectOutputStream;

public class FileLectorCX extends Thread{
    private ObjectOutputStream oos;
    public FileLectorCX(ObjectOutputStream oos){
        this.oos = oos;
    }
    
    @Override
    public void run(){
        
    }
}
