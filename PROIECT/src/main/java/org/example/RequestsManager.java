package org.example;

import java.util.List;

public interface RequestsManager {
    boolean createRequest(Request r, List<User<Comparable<Object>>> users);
    void removeRequest(Request r, List<User<Comparable<Object>>> users);
}
