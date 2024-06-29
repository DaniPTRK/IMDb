package org.example;


import kotlin.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public abstract class Staff<T extends Comparable<Object>> extends User<T> implements StaffInterface<T>{
    public List<Request> requests;
    public SortedSet<T> contributions;
    public Staff() {
        super();
        contributions = new TreeSet<>();
        requests = new ArrayList<>();
    }
    public void addProductionSystem(T p) {
        // Add production & increase exp.
        contributions.add(p);
        if(this instanceof Contributor) {
            this.expStrat = new increaseProductionExpStrategy();
            this.exp = this.executeStrategy(this.exp);
        }
    }
    public void addActorSystem(T a) {
        // Add actor & increase exp.
        contributions.add(a);
        if(this instanceof Contributor) {
            this.expStrat = new increaseProductionExpStrategy();
            this.exp = this.executeStrategy(this.exp);
        }
    }
    public void removeProductionSystem(String name, List<User<Comparable<Object>>> users) {
        String username;
        for(T production : contributions) {
            if((production instanceof Production prod)&&((Production)production).title.equals(name)) {
                // Remove all ratings associated to this production.
                for(Rating rate : prod.rating) {
                    username = rate.username;
                    for(User<Comparable<Object>> user : users) {
                        if(user.username.equals(username)) {
                            user.givenRatings.remove(new Pair<>(prod.title, rate));
                        }
                    }
                }
                contributions.remove(production);
                break;
            }
        }
    }
    public void removeActorSystem(String name, List<User<Comparable<Object>>> users) {
        for(T actor : contributions) {
            if((actor instanceof Actor act)&&((Actor)actor).name.equals(name)) {
                // Remove all ratings associated to this actor.
                for(Rating rate : act.rating) {
                    username = rate.username;
                    for(User<Comparable<Object>> user : users) {
                        if(user.username.equals(username)) {
                            user.givenRatings.remove(new Pair<>(act.name, rate));
                        }
                    }
                }
                contributions.remove(actor);
                break;
            }
        }
    }
    public void updateProduction(Production p) {
        for(T production : contributions) {
            if((production instanceof Production prod)&&(production.equals(p))) {
                prod = p;
            }
        }
    }
    public void updateActor(Actor a) {
        for(T actor : contributions) {
            if((actor instanceof Actor act)&&(actor.equals(a))) {
                act = a;
            }
        }
    }
}
