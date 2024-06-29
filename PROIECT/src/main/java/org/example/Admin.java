package org.example;

import kotlin.Pair;

import java.util.*;

public class Admin<T extends Comparable<Object>> extends Staff<T> {
    static List<Actor> globalContributionActors = new ArrayList<>();
    static List<Production> globalContributionProductions = new ArrayList<>();
    public Admin() {
        super();
    }
    public String generatePass() {
        // Generate a random password.
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        StringBuilder pass = new StringBuilder();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopq" +
                        "rstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:'\",<.>/?";
        for(int i = 0; i<12; i++) {
            pass.append(characters.charAt(random.nextInt(characters.length())));
        }
        return pass.toString();
    }
    public String generateUser(String firstname, String lastname) {
        // Generating actual username.
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        return firstname + "_" + lastname + "_" + random.nextInt(10000);
    }
    public void validateUser(List<User<Comparable<Object>>> users, User<Comparable<Object>> user,
                             String firstname, String lastname) {
        // Generate an unique username.
        int OK = 0;
        while(OK == 0) {
            OK = 1;
            user.username = this.generateUser(firstname.toLowerCase(), lastname.toLowerCase());
            for(User<Comparable<Object>> userValidate : users) {
                if(userValidate.username.equals(user.username)) {
                    OK = 0;
                    break;
                }
            }
        }
    }
    public int addUser(User<Comparable<Object>> user, int age, String birthDate, String firstname,
                       String lastname, String name, String gender, String country, String email) {
        if((birthDate.isEmpty())||(firstname.isEmpty())||(lastname.isEmpty())||(name.isEmpty())||(gender.isEmpty())||
                (country.isEmpty())||(email.isEmpty())) {
            return 0;
        }
        // Create an user.
        String password = generatePass();
        System.out.println(password);
        user.userInfo = new User.Information.InformationBuilder()
                .age(age)
                .name(name)
                .sex(gender)
                .birth(birthDate)
                .country(country)
                .credsEmail(email)
                .credsPassword(password)
                .build();
        return 1;
    }
    public void deleteUserCLI(User<Comparable<Object>> user, IMDB obj) {
        // Delete an user.
        int choice = 1;
        // Get all of his contributions sent to admins.
        if(user instanceof Contributor<Comparable<Object>>) {
            for(Object o : ((Contributor<Comparable<Object>>) user).contributions) {
                if (o instanceof Production) {
                    Admin.globalContributionProductions.add((Production) o);
                } else {
                    Admin.globalContributionActors.add((Actor) o);
                }
            }
        } else {
            // Remove all of user's ratings.
            while (!user.givenRatings.isEmpty()) {
                Pair<String,Rating> ratingPair = user.givenRatings.get(0);
                for(Actor act : obj.actors) {
                    if(ratingPair.getFirst().equals(act.name)) {
                        choice = 2;
                        break;
                    }
                }
                obj.deleteRating(user, ratingPair.getFirst(), choice, 1);
            }
        }
        while(!user.createdRequests.isEmpty()) {
            // Remove every request made by the user.
            Request r = user.createdRequests.get(0);
            obj.removeRequests(r, user);
        }
    }
    public static class RequestsHolder {
        // Implementation of requests holder.
        static List<Request> requests = new ArrayList<>();
        static void addRequest(Request request) {
            requests.add(request);
        }
        static void removeRequest(Request request) {
            requests.remove(request);
        }
    }
}
