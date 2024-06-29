package org.example;

public interface Subject {

    public abstract void addObserver(Observer o);
    public abstract void removeObserver(Observer o);
    public abstract void notifyObservers(String message);
}
