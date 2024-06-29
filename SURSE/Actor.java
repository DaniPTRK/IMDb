package org.example;


import kotlin.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.round;

public class Actor implements Comparable<Object> {
    public String name, biography;
    List<Pair<String,String>> acting;
    Double averageRating;
    Double sum, numRatings;
    List<Rating> rating;

    public Actor() {
        this.acting = new ArrayList<>();
        this.rating = new ArrayList<>();
        this.averageRating = 0.0;
        this.sum = 0.0;
        this.numRatings = 0.0;
    }

    @Override
    public int compareTo(@NotNull Object o) {
        if(o instanceof Movie aux) {
            return this.name.compareTo(aux.title);
        } else if(o instanceof Series aux) {
            return this.name.compareTo(aux.title);
        } else if(o instanceof Actor aux){
            return this.name.compareTo(aux.name);
        }
        return 1;
    }
    public void computeAvgRating() {
        // Compute the average grade obtained from the ratings.
        for(Rating rating : this.rating) {
            this.sum += rating.grade;
            this.numRatings++;
        }
        this.averageRating = (double) round(this.sum/this.numRatings * 100.0);
        this.averageRating /= 100;
    }
    public void displayInfo() {
        // Display info about an Actor.
        System.out.println("Name: " + this.name);
        System.out.println("Biography: " + (this.biography != null ? this.biography : "-"));
        System.out.println("Acting: ");
        for(Pair<String,String> auxAct : this.acting) {
            System.out.println("Title: " + auxAct.getFirst()+ "\nType: " + auxAct.getSecond());
        }
        for(Rating rate : this.rating) {
            System.out.println("= RATING =");
            System.out.println("User: " + rate.username);
            System.out.println("Grade: " + rate.grade);
            System.out.println("Comment: " + rate.comment);
        }
        System.out.println("AverageRating: " + (this.sum != 0 ? this.averageRating : "0"));
        System.out.print("\n");
    }
}
