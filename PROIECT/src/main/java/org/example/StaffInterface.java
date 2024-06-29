package org.example;

import java.util.List;

public interface StaffInterface<T extends Comparable<Object>> {
    void addProductionSystem(T p);
    void addActorSystem(T a);
    void removeProductionSystem(String name, List<User<Comparable<Object>>> users);
    void removeActorSystem(String name, List<User<Comparable<Object>>> users);
    void updateProduction(Production p);
    void updateActor(Actor a);
}
