package com.example.pattern;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Observable {
    private List<Observer> observers = new ArrayList<>();

    public Observable(){

    }

    public synchronized int count(){
        return observers.size();
    }

    public synchronized void clear(){
        observers.clear();
    }

    public synchronized void addObserver(Observer observer){
        if(observer == null)
            throw new IllegalArgumentException("Wrong");

        if(observers.contains(observer)==false){
            observers.add(observer);
        }
    }

    public synchronized void removeObserver(Observer observer){
        if(observer == null)
            throw new IllegalArgumentException("Wrong");

        if(observers.contains(observer)==true){
            observers.remove(observer);
        }
    }

    protected synchronized void notifyAll(Object args) {
        Iterator var2 = this.observers.iterator();

        while(var2.hasNext()) {
            Observer observer = (Observer)var2.next();
            observer.notify(this, args);
        }

    }
}
