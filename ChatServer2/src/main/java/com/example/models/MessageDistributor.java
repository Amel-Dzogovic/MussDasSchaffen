package com.example.models;

import com.example.pattern.Observable;

import java.util.ArrayList;
import java.util.List;

public class MessageDistributor extends Observable {
    private static MessageDistributor instance = null;

    public static MessageDistributor getInstance(){
        if(instance==null){
            instance = new MessageDistributor();
        }
        return instance;
    }

    List<Message> messages = new ArrayList<>();


    public MessageDistributor(){

    }

    public void addMassage(Message message){
        if(message==null)
            throw new IllegalArgumentException("No Message");

        messages.add(message);
        notifyAll(message);
    }

}
