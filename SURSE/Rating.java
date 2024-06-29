package org.example;

import java.util.ArrayList;
import java.util.List;

public class Rating implements Subject {
    List<Observer> observers = new ArrayList<>();
    public String username, comment;
    public int grade;
    @Override
    public void addObserver(Observer o) {
        this.observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        this.observers.remove(o);
    }

    @Override
    public void notifyObservers(String message) {
        for(Observer o : this.observers) {
            o.update(message);
        }
    }
}
