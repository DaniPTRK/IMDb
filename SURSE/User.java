package org.example;

import kotlin.Pair;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public abstract class User<T extends Comparable<Object>> implements Observer {
    Information userInfo;
    AccountType accType;
    String username;
    int exp = 0;
    List<String> userNotif;
    List<Request> createdRequests;
    List<Pair<String,Rating>> givenRatings;
    public SortedSet<T> favorites;
    ExperienceStrategy expStrat;
    public User() {
        this.userNotif = new ArrayList<>();
        this.favorites = new TreeSet<>();
        this.createdRequests = new ArrayList<>();
        this.givenRatings = new ArrayList<>();
    }
    boolean addToFav(T favorite) {
        // Add to favorites.
        return favorites.add(favorite);
    }
    boolean removeFromFav(T favorite) {
        // Remove from favorites.
        return favorites.remove(favorite);
    }

    @Override
    public void update(String message) {
        userNotif.add(message);
    }

    public boolean createRequest(Request r, List<User<Comparable<Object>>> users) {
        // Find the receiving end of a request.
        if((r.type.equals(RequestType.DELETE_ACCOUNT)) || (r.type.equals(RequestType.OTHERS))) {
            r.userReceiver = "ADMIN";
            Admin.RequestsHolder.addRequest(r);
            for(User<Comparable<Object>> admins : users) {
                if(admins instanceof Admin) {
                    r.addObserver(admins);
                }
            }
        } else {
            int OK = 0;
            for(User<Comparable<Object>> userReceiver : users) {
                if(userReceiver instanceof Staff<Comparable<Object>> userStaff){
                    for(Object cont : userStaff.contributions) {
                        if ((cont instanceof Production) && ((Production) cont).title.equals(r.titleProduction)) {
                            r.userReceiver = userReceiver.username;
                            ((Staff<Comparable<Object>>) userReceiver).requests.add(r);
                            r.addObserver(userReceiver);
                            OK = 1;
                            break;
                        }
                        if ((cont instanceof Actor) && ((Actor) cont).name.equals(r.actorName)) {
                            r.userReceiver = userReceiver.username;
                            ((Staff<Comparable<Object>>) userReceiver).requests.add(r);
                            r.addObserver(userReceiver);
                            OK = 1;
                            break;
                        }
                    }
                    if((OK == 0) && (userReceiver instanceof Admin)) {
                        if(r.type.equals(RequestType.MOVIE_ISSUE)) {
                            for(Production cont : Admin.globalContributionProductions) {
                                if (cont.title.equals(r.actorName)) {
                                    r.userReceiver = "ADMIN";
                                    Admin.RequestsHolder.requests.add(r);
                                    for(User<Comparable<Object>> admins : users) {
                                        if(admins instanceof Admin) {
                                            r.addObserver(admins);
                                        }
                                    }
                                    OK = 1;
                                    break;
                                }
                            }
                        } else {
                            for(Actor cont : Admin.globalContributionActors) {
                                if (cont.name.equals(r.actorName)) {
                                    r.userReceiver = "ADMIN";
                                    Admin.RequestsHolder.requests.add(r);
                                    for(User<Comparable<Object>> admins : users) {
                                        if(admins instanceof Admin) {
                                            r.addObserver(admins);
                                        }
                                    }
                                    OK = 1;
                                    break;
                                }
                            }
                        }
                    }
                }
                if(OK == 1) {
                    break;
                }
            }
        }
        if(r.userSender.equals(r.userReceiver)) {
            return false;
        } else {
            createdRequests.add(r);
            r.notifyObservers("New request pending from " + r.userSender + " received at " + r.createdDate + ".");
            return true;
        }
    }

    public void removeRequest(Request r, List<User<Comparable<Object>>> users) {
        // Eliminate solved/retracted request.
        if(r.userReceiver.equals("ADMIN")) {
            Admin.RequestsHolder.removeRequest(r);
        } else {
            for(User<Comparable<Object>> userReceiver : users) {
                if(r.userReceiver.equals(userReceiver.username)) {
                    ((Staff<Comparable<Object>>) userReceiver).requests.remove(r);
                    break;
                }
            }
        }
        createdRequests.remove(r);
        r.notifyObservers("Request from " + r.userSender + " has been solved/removed.");
    }

    public int executeStrategy(int a) {
        // Execute Strategy Pattern to calculate experience.
        return this.expStrat.calculateExperience(a);
    }
    User<T> logout() {
        // Logout.
        return null;
    }
    public static class Information {
        private Credentials creds;
        private String name, country, sex;
        private int age;
        private LocalDateTime birth;

        private Information(InformationBuilder builder) {
            this.creds = new Credentials();
            this.name = builder.name;
            this.country = builder.country;
            this.sex = builder.sex;
            this.age = builder.age;
            this.birth = builder.birth;
            this.creds.setEmail(builder.creds.getEmail());
            this.creds.setPassword(builder.creds.getPassword());
        }

        // Applying Builder pattern.
        public static class InformationBuilder {
            private final Credentials creds = new Credentials();
            private String name;
            private String country;
            private String sex;
            private int age;
            private LocalDateTime birth;
            public InformationBuilder name(String name) {
                this.name = name;
                return this;
            }
            public InformationBuilder country(String country) {
                this.country = country;
                return this;
            }
            public InformationBuilder sex(String sex) {
                if (sex.equals("Male")) {
                    this.sex = "M";
                } else if(sex.equals("Female")) {
                    this.sex = "F";
                }
                else {
                    this.sex = "N";
                }
                return this;
            }
            public InformationBuilder age(int age) {
                this.age = age;
                return this;
            }
            public InformationBuilder birth(String birth) {
                LocalDate ld = LocalDate.parse(birth, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                this.birth = LocalDateTime.of(ld, LocalTime.of(0,0));
                return this;
            }
            public InformationBuilder credsEmail(String email) {
                this.creds.setEmail(email);
                return this;
            }
            public InformationBuilder credsPassword(String password) {
                this.creds.setPassword(password);
                return this;
            }
            public Information build() {
                return new Information(this);
            }
        }

        public void setCreds(Credentials creds) {
            this.creds = creds;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public void setSex(String sex) {
            if (sex.equals("Male")) {
                this.sex = "M";
            } else if(sex.equals("Female")) {
                this.sex = "F";
            }
            else {
                this.sex = "N";
            }
        }

        public void setAge(int age) {
            this.age = age;
        }

        public void setBirth(String birth) {
            LocalDate ld = LocalDate.parse(birth, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            this.birth = LocalDateTime.of(ld, LocalTime.of(0,0));
        }

        public Credentials getCreds() {
            return creds;
        }

        public String getName() {
            return name;
        }

        public String getCountry() {
            return country;
        }

        public String getSex() {
            return sex;
        }

        public int getAge() {
            return age;
        }

        public LocalDateTime getBirth() {
            return birth;
        }

    }
}

