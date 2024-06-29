package org.example;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.round;

public abstract class Production implements Comparable<Object>{
    public String title;
    List<String> directors;
    List<String> actors;
    List<Genre> genre;
    List<Rating> rating;
    Double sum, numRatings;
    String plot;
    Double averageRating;
    public Production () {
        directors = new ArrayList<>();
        actors = new ArrayList<>();
        genre = new ArrayList<>();
        rating = new ArrayList<>();
        this.averageRating = 0.0;
        this.sum = 0.0;
        this.numRatings = 0.0;
    }
    public void writeLists(List<?> source) {
        // Display each value from a List.
        if(source != null) {
            for(Object aux : source) {
                if(aux instanceof Rating) {
                    System.out.println("= RATING =");
                    System.out.println("User: " + ((Rating) aux).username);
                    System.out.println("Grade: " + ((Rating) aux).grade);
                    System.out.println("Comment: " + ((Rating) aux).comment);
                } else {
                    System.out.println(aux.toString());
                }
            }
        } else {
            System.out.println("-");
        }
    }
    public void computeAvgRating() {
        for(Rating rating : this.rating) {
            this.sum += rating.grade;
            this.numRatings++;
        }
        this.averageRating = (double) round(this.sum/this.numRatings * 100.0);
        this.averageRating /= 100;
    }
    public void displayCommonInfo() {
        // Display common information from both Movies and Series.
        System.out.println("Title: " + (this.title != null ? this.title : "-"));
        System.out.print("Directors: ");
        writeLists(this.directors);
        System.out.print("Actors: ");
        writeLists(this.actors);
        System.out.print("Genres: ");
        writeLists(this.genre);
        System.out.println("Ratings: ");
        writeLists(this.rating);
        System.out.print("Plot: ");
        System.out.println(this.plot != null ? this.plot : "-");
        System.out.print("Average Rating: ");
        System.out.println(this.averageRating != null ? this.averageRating : "-");
    }
    public abstract void displayInfo();
    public abstract int compareTo(@NotNull Object o);
}