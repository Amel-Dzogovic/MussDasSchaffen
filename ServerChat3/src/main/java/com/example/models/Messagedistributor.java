package com.example.models;

import com.example.pattern.Observable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Messagedistributor extends Observable {
    private static Messagedistributor instance = null;

    public static Messagedistributor getInstance(){
        if(instance==null){
            instance=new Messagedistributor();
        }
        return instance;
    }

    List<Message> messages = new ArrayList<>();


    public Messagedistributor(){

    }
    public void addMessage(Message message){
        if(message==null)
            throw new IllegalArgumentException();

        messages.add(message);
        notifyAll(message);
    }
}
