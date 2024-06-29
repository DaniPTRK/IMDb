package org.example;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Series extends Production {
    int releaseYear, numSeasons;
    Map<String, List<Episode>> seriesDictionary;
    public Series(){
        super();
        seriesDictionary = new LinkedHashMap<>();
    }
    @Override
    public void displayInfo() {
        // Display info for CLI mode.
        super.displayCommonInfo();
        System.out.print("Release Year: ");
        System.out.println(this.releaseYear != 0 ? this.releaseYear: "-");
        System.out.println("Content: ");
        for(Map.Entry<String, List<Episode>> entry : seriesDictionary.entrySet()) {
            System.out.println("===" + entry.getKey() + "===");
            for(Episode aux : entry.getValue()) {
                System.out.println("Episode: " + aux.name);
                System.out.println("Duration: " + aux.duration);
            }
        }
        System.out.print("\n");
    }
    @Override
    public int compareTo(@NotNull Object o){
        if(o instanceof Series aux) {
            return this.title.compareTo(aux.title);
        } else if(o instanceof Movie aux) {
            return this.title.compareTo(aux.title);
        } else if(o instanceof Actor aux) {
            return this.title.compareTo(aux.name);
        }
        return 1;
    }
}