package org.example;

import org.jetbrains.annotations.NotNull;

public class Movie extends Production {
    String duration;
    int releaseYear = 0;
    public Movie() {
        super();
    }
    @Override
    public void displayInfo() {
        // Display info for CLI mode.
        super.displayCommonInfo();
        System.out.print("Duration: ");
        System.out.println(this.duration != null ? this.duration : "-");
        System.out.print("Release Year: ");
        System.out.println(this.releaseYear != 0 ? this.releaseYear: "-");
        System.out.print("\n");
    }
    @Override
    public int compareTo(@NotNull Object o){
        if(o instanceof Movie aux) {
            return this.title.compareTo(aux.title);
        } else if(o instanceof Series aux) {
            return this.title.compareTo(aux.title);
        } else if(o instanceof Actor aux){
            return this.title.compareTo(aux.name);
        }
        return 1;
    }
}
