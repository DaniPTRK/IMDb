package org.example;

import java.util.List;

public class Contributor<T extends Comparable<Object>> extends Staff<T> implements RequestsManager{

    public boolean createRequest(Request r, List<User<Comparable<Object>>> users) {
        return super.createRequest(r, users);
    }

    public void removeRequest(Request r, List<User<Comparable<Object>>> users) {
        super.removeRequest(r, users);
    }
    public Contributor() {
        super();
    }
}
