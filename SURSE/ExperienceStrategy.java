package org.example;

interface ExperienceStrategy {
    public int calculateExperience(int a);
}

class addReviewExpStrategy implements ExperienceStrategy {
    // Return 1 exp point for adding a review.
    public int calculateExperience(int a) {
        return a+1;
    }
}
class createIssueExpStrategy implements ExperienceStrategy {
    // Return 2 exp points for adding a review.
    public int calculateExperience(int a) {
        return a+2;
    }
}
class increaseProductionExpStrategy implements ExperienceStrategy {
    // Return 3 exp points for adding a Production/Actor.
    public int calculateExperience(int a) {
        return a+3;
    }
}