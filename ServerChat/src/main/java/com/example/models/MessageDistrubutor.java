package com.example.models;

import com.example.pattern.Observable;

import java.util.ArrayList;
import java.util.List;

public class MessageDistrubutor extends Observable {
    private static MessageDistrubutor instance = null;

    public static MessageDistrubutor getInstance(){
        if(instance == null){
            instance = new MessageDistrubutor();
        }
        return instance;
    }

    private List<Message> messages = new ArrayList<>();

    private MessageDistrubutor(){

    }

    public void addMessage(Message message){
        if(message == null)
            throw new IllegalArgumentException("Wrong");

        messages.add(message);
        notifyAll(message);
    }
}
