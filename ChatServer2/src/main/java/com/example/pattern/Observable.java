package com.example.pattern;

import java.util.ArrayList;
import java.util.List;

public abstract class Observable {
    List<Observer> observers = new ArrayList<>();

    public synchronized int count(){
        return observers.size();
    }

    public synchronized void clear(){
        observers.clear();
    }

    public synchronized void addObserver(Observer observer){
        if(observer==null)
            throw new IllegalArgumentException("Empty observer");

        if(observers.contains(observer)==false){
            observers.add(observer);
        }
    }

    public synchronized void removeObserver(Observer observer){
        if(observer==null)
            throw new IllegalArgumentException("Empty observer");

        if(observers.contains(observer)){
            observers.remove(observer);
        }
    }

    public void notifyAll(Object args){
        for(Observer observer : observers){
            observer.notify(this,args);
        }
    }
}
