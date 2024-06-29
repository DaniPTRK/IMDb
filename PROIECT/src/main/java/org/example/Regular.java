package org.example;


import java.util.List;

public class Regular<T extends Comparable<Object>> extends User<T> implements RequestsManager {
    public boolean createRequest(Request r, List<User<Comparable<Object>>> users) {
        return super.createRequest(r, users);
    }

    public void removeRequest(Request r, List<User<Comparable<Object>>> users) {
        super.removeRequest(r, users);
    }
    public Regular() {
        super();
    }
}