package org.example;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Request implements Subject {
    List<Observer> observers = new ArrayList<>();
    RequestType type;
    LocalDateTime createdDate;
    String actorName, titleProduction, description, userSender, userReceiver;

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
    public String displayRequest() {
        // Display info about the request.
        String res = "";
        res += "Type: " + type + "\n";
        res += "Date: " + createdDate + "\n";
        res += "From: " + userSender + "\n";
        res += "To: " + userReceiver + "\n";
        if(type.equals(RequestType.ACTOR_ISSUE)) {
            res += "Actor Name: " + actorName + "\n";
        } else if(type.equals(RequestType.MOVIE_ISSUE)){
            res += "Title Production: " + titleProduction + "\n";
        }
        res += ("Description: " + description);
        return res;
    }
}