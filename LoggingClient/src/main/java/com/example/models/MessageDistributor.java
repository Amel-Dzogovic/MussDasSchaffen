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

    private List<Message> messages= new ArrayList<>();

    private MessageDistributor(){

    }

    public void addMessage(Message message){
        if(message == null)
            throw new IllegalArgumentException("Wrong Message");

        messages.add(message);
        notifyAll(message);
    }
}
