package org.example;

import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

public class IMDB {
    static IMDB obj = null;

    public List<User<Comparable<Object>>> users;

    public List<Actor> actors;

    public List<Request> requests;

    public List<Production> productions;

    // Applying Singleton pattern to restrict the number of IMDB instances to one.
    private IMDB(){
        users = new ArrayList<>();
        actors = new ArrayList<>();
        requests = new ArrayList<>();
        productions = new ArrayList<>();
    }

    public static IMDB getInstance() {
        if(obj == null) {
            obj = new IMDB();
        }
        return obj;
    }
    public void addUnknownActor(Production prod, String name) {
        // Add unknown actor inside the DataBase under the contribution of all admins.
        Actor unkActor = new Actor();
        unkActor.name = name;
        if(prod instanceof Movie) {
            unkActor.acting.add(new Pair<>(prod.title, "Movie"));
        } else {
            unkActor.acting.add(new Pair<>(prod.title, "Series"));
        }
        Admin.globalContributionActors.add(unkActor);
        obj.actors.add(unkActor);
    }
    public int doParse() {
        // Extract data from JSONs.
        JSONParser parser = new JSONParser();
        try {
            int OK;
            JSONObject aux, info, cred;
            JSONArray notifications, perform, ratings, genres, actors, directors, favActors, favProductions, conActors,
                    conProductions;
            // Parse from actors.json.
            Object parsval = parser.parse(new FileReader("./POO-TEMA-2023-INPUT/actors.json"));
            JSONArray arr = (JSONArray) parsval;
            for (Object actorObject : arr) {
                aux = (JSONObject) actorObject;
                ratings = (JSONArray)  aux.get("ratings");
                perform = (JSONArray) aux.get("performances");
                Actor act = new Actor();
                act.name = aux.get("name").toString();
                if(aux.containsKey("biography")) {
                    act.biography = aux.get("biography").toString();
                }
                for (Object actingObject : perform) {
                    JSONObject perf = (JSONObject) actingObject;
                    act.acting.add(new Pair<>(perf.get("title").toString(), perf.get("type").toString()));
                }
                // Add ratings inside the Database.
                if(ratings != null) {
                    for(Object rateObject: ratings) {
                        JSONObject auxrate = (JSONObject) rateObject;
                        Rating rate = new Rating();
                        rate.username = auxrate.get("username").toString();
                        rate.comment = auxrate.get("comment").toString();
                        rate.grade = parseInt(auxrate.get("rating").toString());
                        act.rating.add(rate);
                    }
                }
                obj.actors.add(act);
            }
            // Parse from productions.json.
            parsval = parser.parse(new FileReader("./POO-TEMA-2023-INPUT/production.json"));
            arr = (JSONArray) parsval;
            Production content;
            for (Object prodObject : arr) {
                aux = (JSONObject) prodObject;
                actors = (JSONArray) aux.get("actors");
                directors = (JSONArray) aux.get("directors");
                ratings = (JSONArray)  aux.get("ratings");
                genres = (JSONArray) aux.get("genres");
                // Downcast Production variable in order to cover Movies & Series.
                if(aux.get("type").equals("Movie")) {
                    content = new Movie();
                } else {
                    content = new Series();
                }
                // Check if the keys exist. If they do, add them inside the Database.
                if(aux.containsKey("title")) {
                    content.title = aux.get("title").toString();
                }
                if(aux.containsKey("plot")) {
                    content.plot = aux.get("plot").toString();
                }
                if(aux.containsKey("averageRating")) {
                    content.averageRating = Double.valueOf(aux.get("averageRating").toString());
                }
                for(Object directorObject : directors) {
                    content.directors.add(directorObject.toString());
                }
                for(Object actorObject : actors) {
                    content.actors.add(actorObject.toString());
                }
                // Add ratings & genre inside the Database.
                for(Object rateObject: ratings) {
                    JSONObject auxrate = (JSONObject) rateObject;
                    Rating rate = new Rating();
                    rate.username = auxrate.get("username").toString();
                    rate.comment = auxrate.get("comment").toString();
                    rate.grade = parseInt(auxrate.get("rating").toString());
                    content.rating.add(rate);
                }
                for(Object genreObject: genres) {
                    String auxgen = (String) genreObject;
                    content.genre.add(Genre.valueOf(auxgen.toUpperCase()));
                }
                // Add info that's specific for Movies.
                if((aux.containsKey("duration"))&&(content instanceof Movie)) {
                    ((Movie) content).duration = aux.get("duration").toString();
                }
                if (aux.containsKey("releaseYear")) {
                    if(content instanceof Movie) {
                        ((Movie) content).releaseYear = parseInt(aux.get("releaseYear").toString());
                    } else {
                        ((Series) content).releaseYear = parseInt(aux.get("releaseYear").toString());
                    }
                }
                // Add info that's specific for Series.
                if((aux.containsKey("numSeasons"))&&(content instanceof Series)) {
                    ((Series) content).numSeasons = parseInt(aux.get("numSeasons").toString());
                }
                if(content instanceof Series) {
                    // Go through each season.
                    JSONObject seasons = (JSONObject) aux.get("seasons");
                    int i;
                    for(i = 1; i <= ((Series) content).numSeasons; i++) {
                        JSONArray actualseason = (JSONArray) seasons.get("Season " + i);
                        for(Object epObject: actualseason) {
                            JSONObject episodes = (JSONObject) epObject;
                            Episode ep = new Episode();
                            ep.duration = episodes.get("duration").toString();
                            ep.name = episodes.get("episodeName").toString();
                            ((Series) content).seriesDictionary.computeIfAbsent("Season " + i,
                                    k -> new ArrayList<>()).add(ep);
                        }
                    }
                }
                obj.productions.add(content);
            }
            // Add unknown actors from productions inside actors DataBase.
            for (Production prodChecker : obj.productions) {
                for(String actorChecker : prodChecker.actors) {
                    OK = 0;
                    for(Actor actorComparer : obj.actors) {
                        if (actorChecker.equals(actorComparer.name)) {
                            OK = 1;
                            break;
                        }
                    }
                    if(OK == 0) {
                        addUnknownActor(prodChecker, actorChecker);
                    }
                }
            }
            // Parse from accounts.json.
            parsval = parser.parse(new FileReader("./POO-TEMA-2023-INPUT/accounts.json"));
            arr = (JSONArray) parsval;
            for (Object userObject : arr) {
                aux = (JSONObject) userObject;
                info = (JSONObject) aux.get("information");
                cred = (JSONObject) info.get("credentials");
                User<Comparable<Object>> user = UserFactory.factory(AccountType.valueOf(aux.get("userType").toString()));
                assert user != null;
                user.username = aux.get("username").toString();
                // Use Builder Pattern in order to store user information.
                user.userInfo = new User.Information.InformationBuilder()
                        .age(parseInt(info.get("age").toString()))
                        .name(info.get("name").toString())
                        .sex(info.get("gender").toString())
                        .birth(info.get("birthDate").toString())
                        .country(info.get("country").toString())
                        .credsEmail(cred.get("email").toString())
                        .credsPassword(cred.get("password").toString())
                        .build();
                if(aux.containsKey("notifications")) {
                    notifications = (JSONArray) aux.get("notifications");
                    for(Object notif : notifications) {
                        String notify = (String) notif;
                        user.userNotif.add(notify);
                    }
                }
                if(aux.containsKey("favoriteActors")) {
                    favActors = (JSONArray) aux.get("favoriteActors");
                    for(Object favAct : favActors) {
                        String favActor = (String) favAct;
                        for(Actor actor : obj.actors) {
                            if(actor.name.equals(favActor)) {
                                user.addToFav(actor);
                            }
                        }
                    }
                }
                if(aux.containsKey("favoriteProductions")) {
                    favProductions = (JSONArray) aux.get("favoriteProductions");
                    for(Object favProd : favProductions) {
                        String favProduction = (String) favProd;
                        for(Production production : obj.productions) {
                            if(production.title.equals(favProduction)) {
                                user.addToFav(production);
                            }
                        }
                    }
                }
                if(aux.containsKey("actorsContribution")) {
                    conActors = (JSONArray) aux.get("actorsContribution");
                    for(Object conAct : conActors) {
                        String conActor = (String) conAct;
                        for(Actor actor : obj.actors) {
                            if(actor.name.equals(conActor)) {
                                ((Staff<Comparable<Object>>) user).addActorSystem(actor);
                            }
                        }
                    }
                }
                if(aux.containsKey("productionsContribution")) {
                    conProductions = (JSONArray) aux.get("productionsContribution");
                    for(Object conProd : conProductions) {
                        String conProduction = (String) conProd;
                        for(Production production : obj.productions) {
                            if(production.title.equals(conProduction)) {
                                ((Staff<Comparable<Object>>) user).addProductionSystem(production);
                            }
                        }
                    }
                }
                if(user instanceof Regular) {
                    user.accType = AccountType.Regular;
                    user.exp = parseInt(aux.get("experience").toString());
                    obj.users.add(user);
                } else if(user instanceof Contributor) {
                    user.accType = AccountType.Contributor;
                    user.exp = parseInt(aux.get("experience").toString());
                    obj.users.add(user);
                } else if(user instanceof Admin) {
                    user.accType = AccountType.Admin;
                    user.exp = -1;
                    obj.users.add(user);
                }
            }
            // Get every rating.
            for (Production prod : obj.productions) {
                prod.computeAvgRating();
                for(Rating rate : prod.rating) {
                    for(User<Comparable<Object>> user : obj.users) {
                        if(user.username.equals(rate.username)) {
                            user.givenRatings.add(new Pair<>(prod.title,rate));
                        }
                    }
                }
            }
            for (Actor act : obj.actors) {
                act.computeAvgRating();
                for(Rating rate : act.rating) {
                    for(User<Comparable<Object>> user : obj.users) {
                        if(user.username.equals(rate.username)) {
                            user.givenRatings.add(new Pair<>(act.name,rate));
                        }
                    }
                }
            }
            // Parse from requests.json.
            parsval = parser.parse(new FileReader("./POO-TEMA-2023-INPUT/requests.json"));
            arr = (JSONArray) parsval;
            for (Object reqObject : arr) {
                aux = (JSONObject) reqObject;
                Request req = new Request();
                req.type = RequestType.valueOf(aux.get("type").toString());
                if(aux.containsKey("actorName")) {
                    req.actorName = aux.get("actorName").toString();
                }
                if(aux.containsKey("movieTitle")) {
                    req.titleProduction = aux.get("movieTitle").toString();
                }
                req.createdDate = LocalDateTime.parse(aux.get("createdDate").toString(),
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                req.userSender = aux.get("username").toString();
                req.description = aux.get("description").toString();
                for(User<Comparable<Object>> userSender : obj.users) {
                    if(req.userSender.equals(userSender.username)) {
                        userSender.createRequest(req, obj.users);
                    }
                }
                obj.requests.add(req);
            }
            // Sort actors & productions.
            obj.actors.sort(Comparator.comparing(o -> o.name));
            obj.productions.sort(Comparator.comparing(o -> o.title));
        }
        catch (ParseException | IOException e) {
            return -1;
        }
        return 0;
    }

    public int chooseCLIGUI(Scanner cli, int OK) {
        // Choose between CLI & GUI.
        String buffer;

        System.out.println("Welcome to IMDB! Choose between CLI & GUI.");
        System.out.println("1) CLI \n2) GUI");
        while (OK == 0) {
            // Wait for proper input.
            try {
                buffer = cli.nextLine();
                if(buffer.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting...");
                    break;
                }else if(parseInt(buffer) == 1) {
                    OK = 1;
                } else if (parseInt(buffer) == 2) {
                    OK = 2;
                } else {
                    throw new InvalidCommandException();
                }
            } catch (NumberFormatException | InvalidCommandException e) {
                if(e instanceof NumberFormatException) {
                    System.out.println("Input must be an integer value. If you want to exit, type \"exit\".");
                } else {
                    System.out.println("Input must be either 1 or 2.");
                }
            }
        }
        return OK;
    }

    public User<Comparable<Object>> authCLI(Scanner cli) {
        // Authentication process for CLI.
        User<Comparable<Object>> currentUser = null;
        int OK;
        String email, pass;
        System.out.println("Welcome back to IMDB CLI Edition! Enter your credentials!");
        System.out.println("If you want to exit, just write \"exit\" inside the terminal.");
        OK = 0;
        while(OK == 0) {
            // Get input.
            System.out.print("email: ");
            email = cli.nextLine();
            if(email.equals("exit")) {
                break;
            }
            System.out.print("password: ");
            pass = cli.nextLine();
            if(pass.equals("exit")) {
                break;
            }
            // Search for the user inside the DB.
            for(User<Comparable<Object>> user : obj.users) {
                if((user.userInfo.getCreds().getEmail().equals(email))&&
                        (user.userInfo.getCreds().getPassword().equals(pass))) {
                    OK = 1;
                    currentUser = user;
                    break;
                }
            }
            if(OK == 0) {
                System.out.println("Credentials wrong! Please, enter your credentials.");
            }
        }
        return currentUser;
    }
    public int getChoice(Scanner cli, int min, int max) {
        // Ask for a proper integer value in CLI, between min and max.
        String buffer;
        int choice = 0;
        buffer = null;
        while(buffer == null) {
            buffer = cli.nextLine();
            try {
                if((buffer.equalsIgnoreCase("exit"))||(buffer.equalsIgnoreCase("done"))) {
                    choice = -1;
                } else {
                    choice = parseInt(buffer);
                    if ((choice < min) || (choice > max)) {
                        throw new InvalidCommandException();
                    }
                }
            } catch (NumberFormatException | InvalidCommandException e) {
                System.out.println("Input must be an integer value between 1 and "+ max + ".");
                buffer = null;
            }
        }
        return choice;
    }

    public void setTitleCLI(@NotNull Scanner cli, @NotNull Production prod) {
        // Set title for Production.
        System.out.print("Title: ");
        String title = cli.nextLine();
        // Change the name of the subject inside the given rating into the new name.
        for(User<Comparable<Object>> user : obj.users) {
            for(Pair<String,Rating> rate : user.givenRatings) {
                if(rate.getFirst().equals(prod.title)) {
                    user.givenRatings.add(new Pair<>(title, rate.getSecond()));
                    user.givenRatings.remove(new Pair<>(prod.title, rate.getSecond()));
                    break;
                }
            }
        }
        prod.title = title;
    }
    public void setPlotCLI(@NotNull Scanner cli, @NotNull Production prod) {
        // Set plot for Production.
        System.out.print("Plot: ");
        prod.plot = cli.nextLine();
    }
    public void setDirectorsCLI(Scanner cli, Production prod) {
        // Set directors for Production up until user says "done".
        // This strategy is used for setting info for all information that needs to be stored in lists, maps etc.
        String buffer;
        System.out.println("Directors:");
        System.out.println("Finish the list of directors by writing \"done\".");
        buffer = null;
        while(buffer == null) {
            buffer = cli.nextLine();
            if(!buffer.equalsIgnoreCase("done")) {
                prod.directors.add(buffer);
                buffer = null;
            }
        }
    }
    public void setActorsCLI(Scanner cli, Production prod) {
        // Build Actors of production class using CLI.
        int OK;
        String buffer;
        System.out.println("Actors:");
        System.out.println("Finish the list of actors by writing \"done\".");
        buffer = null;
        // Get the name of the actors. If the actor isn't inside the database, set as unknown.
        while(buffer == null) {
            OK = 0;
            buffer = cli.nextLine();
            if(!buffer.equalsIgnoreCase("done")) {
                prod.actors.add(buffer);
                for(Actor bufCheck : actors) {
                    if(bufCheck.name.equals(buffer)) {
                        if(prod instanceof Movie) {
                            bufCheck.acting.add(new Pair<>(prod.title,"Movie"));
                        } else {
                            bufCheck.acting.add(new Pair<>(prod.title,"Series"));
                        }
                        OK = 1;
                        break;
                    }
                }
                if(OK == 0) {
                    addUnknownActor(prod, buffer);
                }
                buffer = null;
            }
        }
    }
    public void setGenresCLI(Scanner cli, Production prod) {
        // Build Genres of Production class using CLI.
        int i = 1, choice = 0;
        int[] genreSelected = new int[14];
        Genre[] genre = {Genre.ACTION, Genre.ADVENTURE, Genre.COMEDY, Genre.DRAMA, Genre.HORROR, Genre.SF,
                Genre.FANTASY, Genre.ROMANCE, Genre.MYSTERY, Genre.THRILLER, Genre.CRIME, Genre.BIOGRAPHY, Genre.WAR,
                Genre.COOKING};
        System.out.println("Genres:");
        System.out.println("If you want to stop picking genres, write \"done\".");
        System.out.println("Pick from the following genres: ");
        System.out.println(i++ + ") Action");
        System.out.println(i++ + ") Adventure");
        System.out.println(i++ + ") Comedy");
        System.out.println(i++ + ") Drama");
        System.out.println(i++ + ") Horror");
        System.out.println(i++ + ") SF");
        System.out.println(i++ + ") Fantasy");
        System.out.println(i++ + ") Romance");
        System.out.println(i++ + ") Mystery");
        System.out.println(i++ + ") Thriller");
        System.out.println(i++ + ") Crime");
        System.out.println(i++ + ") Biography");
        System.out.println(i++ + ") War");
        System.out.println(i + ") Cooking");
        while(choice == 0) {
            choice = getChoice(cli, 1, i);
            if(choice > 0) {
                if(genreSelected[choice-1]==0) {
                    genreSelected[choice-1]=1;
                    prod.genre.add(genre[choice-1]);
                }
                choice = 0;
            }
        }
    }
    public void setReleaseYear(Scanner cli, Production prod) {
        // Build ReleaseYear of Production class using CLI.
        String buffer;
        System.out.print("Release year: ");
        buffer = null;
        while(buffer == null) {
            buffer = cli.nextLine();
            try {
                if (prod instanceof Movie) {
                    ((Movie) prod).releaseYear = parseInt(buffer);
                } else {
                    ((Series) prod).releaseYear = parseInt(buffer);
                }
            } catch (NumberFormatException e) {
                System.out.println("The release year must be an integer.");
                buffer = null;
            }
        }
    }
    public void setDurationCLI(@NotNull Scanner cli, Production prod) {
        // Build Duration of Movies class using CLI.
        System.out.print("Duration(set in minutes): ");
        String buffer = null;
        int dur = -1;
        while(buffer == null) {
            buffer = cli.nextLine();
            try {
                dur = parseInt(buffer);
            } catch (NumberFormatException e) {
                System.out.println("The duration must be an integer.");
                buffer = null;
            }
        }
        ((Movie) prod).duration = dur + " minutes";
    }
    public void setSeasonsCLI(Scanner cli, Production prod) {
        // Build Seasons of Series class using CLI.
        /*
        * This implementation completely erases the history of old seasons, meaning the user must send all data about
        * seasons.
        * */
        String buffer;
        int OK , OK2;
        System.out.print("Number of seasons: ");
        buffer = null;
        // Sanity checks.
        while(buffer == null) {
            buffer = cli.nextLine();
            try {
                ((Series) prod).numSeasons = parseInt(buffer);
            } catch (NumberFormatException e) {
                System.out.println("The number of seasons must be a integer.");
                buffer = null;
            }
        }
        // For each season, wait for at least one episode & one duration.
        for(int i = 1; i<=((Series) prod).numSeasons; i++) {
            OK = 0;
            OK2 = 0;
            System.out.println("Season " + i);
            System.out.println("Episodes: ");
            System.out.println("Finish the list of episodes by writing \"done\".");
            while (OK2 == 0) {
                Episode ep = new Episode();
                while (ep.name == null) {
                    System.out.print("Title: ");
                    ep.name = cli.nextLine();
                    if (ep.name.equalsIgnoreCase("done")) {
                        if (OK == 1) {
                            OK2 = 1;
                        } else {
                            System.out.println("Add at least one episode.");
                            ep.name = null;
                        }
                    }
                }
                // If episode title is given, there must be a duration.
                if (OK2 != 1) {
                    System.out.print("Duration(set in minutes): ");
                    int dur = -1;
                    buffer = null;
                    while(buffer == null) {
                        buffer = cli.nextLine();
                        try {
                            dur = parseInt(buffer);
                        } catch (NumberFormatException e) {
                            if(buffer.equalsIgnoreCase("done")) {
                                System.out.println("Please finish the details of the episode.");
                            } else {
                                System.out.println("The duration must be an integer.");
                            }
                            buffer = null;
                        }
                    }
                    ep.duration = dur + " minutes";
                    OK = 1;
                    ((Series) prod).seriesDictionary.computeIfAbsent("Season " + i,
                            k -> new ArrayList<>()).add(ep);
                }
            }
        }
    }
    public Production buildProduction(Scanner cli, int mode) {
        // Build a Production.
        Production prod;
        if(mode == 1) {
            prod = new Movie();
        } else {
            prod = new Series();
        }
        System.out.println("Provide information about the production.");
        obj.setTitleCLI(cli, prod);
        obj.setPlotCLI(cli, prod);
        obj.setDirectorsCLI(cli, prod);
        obj.setActorsCLI(cli, prod);
        prod.averageRating = 0.0;
        obj.setGenresCLI(cli, prod);
        obj.setReleaseYear(cli, prod);
        if(prod instanceof Movie) {
            obj.setDurationCLI(cli, prod);
        } else {
            obj.setSeasonsCLI(cli, prod);
        }
        return prod;
    }
    public void setNameCLI(@NotNull Scanner cli, @NotNull Actor act) {
        // Set actor name.
        System.out.print("Name: ");
        String name = cli.nextLine();
        // Change the name of the subject inside the given rating into the new name.
        for(User<Comparable<Object>> user : obj.users) {
            for(Pair<String,Rating> rate : user.givenRatings) {
                if(rate.getFirst().equals(act.name)) {
                    user.givenRatings.add(new Pair<>(name, rate.getSecond()));
                    user.givenRatings.remove(new Pair<>(act.name, rate.getSecond()));
                    break;
                }
            }
        }
        act.name = name;
    }
    public void setBiographyCLI(@NotNull Scanner cli, @NotNull Actor act) {
        // Set actor's biography.
        System.out.print("Biography: ");
        act.biography = cli.nextLine();
    }
    public void setActingCLI(Scanner cli, Actor act) {
        // Set actor's acting history.
        // The CLI only supports adding new performances.
        // The GUI is able to also delete old performances if they are omitted.
        String title = null;
        int OK = 0, OK2, OK3;
        System.out.println("Acting: ");
        System.out.println("Finish the list by writing \"done\".");
        while(title == null) {
            System.out.print("Title: ");
            title = cli.nextLine();
            if(!title.equalsIgnoreCase("done")) {
                OK2 = 0;
                for(Production prod : obj.productions) {
                    OK3 = 0;
                    if(prod.title.equals(title)) {
                        OK = 1;
                        OK2 = 1;

                        //Check if actor played in the given production.
                        for(String name : prod.actors) {
                            if(name.equals(act.name)) {
                                OK3 = 1;
                                break;
                            }
                        }

                        // If actor hasn't played, add the new acting.
                        if(OK3 == 0) {
                            if(prod instanceof Movie) {
                                prod.actors.add(act.name);
                                act.acting.add(new Pair<>(title, "Movie"));
                            } else {
                                prod.actors.add(act.name);
                                act.acting.add(new Pair<>(title, "Series"));
                            }
                        }
                        break;
                    }
                }
                // Print if given production can't be found.
                if(OK2 == 0) {
                    System.out.println("Couldn't find the given production.");
                }
                title = null;
            } else {
                if(OK == 0) {
                    System.out.println("In order to add an actor, you must add at least one performance.");
                }
            }
        }
    }
    public Actor buildActor (Scanner cli) {
        // Build Actor using CLI.
        Actor act = new Actor();
        System.out.println("Provide information about the actor.");
        obj.setNameCLI(cli, act);
        obj.setBiographyCLI(cli, act);
        obj.setActingCLI(cli, act);
        return act;
    }
    public void modifySystemCLI(Scanner cli, User<Comparable<Object>> currentUser) {
        // Logic behind adding/deleting a production/actor.
        int i, choice;
        String buffer;
        System.out.println("Write \"add\" if you wish to add a new production/actor inside the system.");
        System.out.println("Write \"delete\" if you wish to delete one of your contributions.");
        buffer = null;
        while(buffer == null) {
            buffer = cli.nextLine();
            if(buffer.equalsIgnoreCase("add")) {
                // Add new contribution.
                System.out.println("What would you like to add?");
                System.out.println("1) Movie");
                System.out.println("2) Series");
                System.out.println("3) Actor");
                choice = getChoice(cli, 1,3);
                if(choice < 0) {
                    break;
                }
                if(choice != 3) {
                    Production prod = buildProduction(cli,choice);
                    ((Staff<Comparable<Object>>) currentUser).addProductionSystem(prod);
                    obj.productions.add(prod);
                    obj.productions.sort(Comparator.comparing(o -> o.title));
                } else {
                    Actor act = buildActor(cli);
                    ((Staff<Comparable<Object>>) currentUser).addActorSystem(act);
                    obj.actors.add(act);
                    obj.actors.sort(Comparator.comparing(o -> o.name));
                }
            } else if (buffer.equalsIgnoreCase("delete")) {
                // Delete old contribution.
                List<Object> content = new ArrayList<>();
                System.out.println("You have the following contributions:");
                i = 0;
                for(Object o : ((Staff<Comparable<Object>>) currentUser).contributions) {
                    content.add(o);
                    if(o instanceof Production) {
                        System.out.println(++i + ") " + ((Production) o).title);
                    } else {
                        System.out.println(++i + ") " + ((Actor) o).name);
                    }
                }
                if(currentUser instanceof Admin) {
                    for(Production o : Admin.globalContributionProductions) {
                        content.add(o);
                        System.out.println(++i + ") " + o.title);
                    }
                    for(Actor o : Admin.globalContributionActors) {
                        content.add(o);
                        System.out.println(++i + ") " + o.name);
                    }
                }
                if(i == 0) {
                    System.out.println("Seems like there are no contributions :(");
                } else {
                    System.out.println("Please write the index of the content which you'd like to delete.");
                    choice = getChoice(cli, 1, i);
                    if (choice < 0) {
                        break;
                    }
                    if (content.get(choice - 1) instanceof Production) {
                        obj.productions.remove((Production) content.get(choice - 1));
                        ((Staff<Comparable<Object>>) currentUser)
                                .removeProductionSystem(((Production) content.get(choice - 1)).title, obj.users);
                    } else {
                        obj.actors.remove((Actor) content.get(choice - 1));
                        ((Staff<Comparable<Object>>) currentUser)
                                .removeActorSystem(((Actor) content.get(choice - 1)).name, obj.users);
                    }
                    if(currentUser instanceof Admin) {
                        if(content.get(choice-1) instanceof Production) {
                            for(Production o : Admin.globalContributionProductions) {
                                if(o.equals(content.get(choice-1))) {
                                    obj.productions.remove(o);
                                    Admin.globalContributionProductions.remove(o);
                                    break;
                                }
                            }
                        } else {
                            for(Actor o : Admin.globalContributionActors) {
                                if(o.equals(content.get(choice-1))) {
                                    for(Production prod : obj.productions) {
                                        for(String actName : prod.actors) {
                                            if(o.name.equals(actName)) {
                                                prod.actors.remove(actName);
                                                break;
                                            }
                                        }
                                    }
                                    obj.actors.remove(o);
                                    Admin.globalContributionActors.remove(o);
                                    break;
                                }
                            }
                        }
                    }
                    for(User<Comparable<Object>> user : obj.users) {
                        for(Object fav : user.favorites) {
                            if(fav.equals(content.get(choice-1))) {
                                if((fav instanceof Production)||(fav instanceof Actor)) {
                                    user.favorites.remove(fav);
                                    break;
                                }
                            }
                        }
                    }
                }
            } else if (!buffer.equalsIgnoreCase("exit")) {
                buffer = null;
                System.out.println("Please only use the keywords \"add\", \"delete\" or \"exit\".");
            }
        }
    }
    public void removeRequests(Request r, User<Comparable<Object>> currentUser) {
        // Retract a request.
        currentUser.removeRequest(r, users);
        currentUser.createdRequests.remove(r);
        obj.requests.remove(r);
    }
    private int RatingComparator(Rating o1, Rating o2) {
        // Comparator used to sort ratings by user experience.
        int exp1 = 0, exp2 = 0;
        for (User<Comparable<Object>> user : obj.users) {
            if (user.username.equals(o1.username)) {
                exp1 = user.exp;
            } else if (user.username.equals(o2.username)) {
                exp2 = user.exp;
            }
        }
        if (exp1 > exp2) {
            return -1;
        } else if (exp1 == exp2) {
            return 0;
        }
        return 1;
    }
    public void sortRatings(Comparable<Object> content) {
        // Sort ratings.
        if(content instanceof Production) {
            ((Production) content).rating.sort(this::RatingComparator);
        } else if(content instanceof Actor) {
            ((Actor) content).rating.sort(this::RatingComparator);
        }
    }
    public void solveRequestsCLI(Scanner cli, User<Comparable<Object>> currentUser) {
        // Logic behind solving requests.
        int i, choice, index = 1;
        String buffer;
        Request toSolve = new Request();
        i = 0;
        // Show requests.
        System.out.println("You have the following requests.");
        if(currentUser instanceof Admin) {
            for(Request r : Admin.RequestsHolder.requests) {
                System.out.println(++i + ") " + r.displayRequest());
            }
        } else {
            for(Request r : ((Staff<Comparable<Object>>)currentUser).requests) {
                System.out.println(++i + ") " + r.displayRequest());
            }
        }
        if(i == 0) {
            System.out.println("Hooray! You have no requests to solve! :D");
        } else {
            System.out.println("In order to select a request, just write its index inside the terminal.");
            choice = getChoice(cli, 1, i);
            if(choice > 0) {
                if (currentUser instanceof Admin) {
                    for (Request r : Admin.RequestsHolder.requests) {
                        if (index == choice) {
                            toSolve = r;
                            break;
                        }
                        index++;
                    }
                } else {
                    for (Request r : ((Staff<Comparable<Object>>) currentUser).requests) {
                        if (index == choice) {
                            toSolve = r;
                            break;
                        }
                        index++;
                    }
                }
                // After selecting a request, user can solve/reject.
                System.out.println("Selected request:\n");
                System.out.println(toSolve.displayRequest());
                System.out.println("If you wish to solve this request, write \"solve\", " +
                        "else write \"reject\".");
                buffer = null;
                User<Comparable<Object>> auxUser = null;
                while (buffer == null) {
                    buffer = cli.nextLine();
                    if ((buffer.equalsIgnoreCase("solve")) ||
                            (buffer.equalsIgnoreCase("reject"))) {
                        if (currentUser instanceof Admin) {
                            Admin.RequestsHolder.removeRequest(toSolve);
                            for (User<Comparable<Object>> user : obj.users) {
                                if (user instanceof Admin) {
                                    toSolve.removeObserver(user);
                                } else if (user.username.equals(toSolve.userSender)) {
                                    auxUser = user;
                                }
                            }
                        } else {
                            ((Staff<Comparable<Object>>) currentUser).requests.remove(toSolve);
                            toSolve.removeObserver(currentUser);
                            for (User<Comparable<Object>> user : obj.users) {
                                if (user.username.equals(toSolve.userSender)) {
                                    auxUser = user;
                                    break;
                                }
                            }
                        }
                        // Notify sender.
                        toSolve.addObserver(auxUser);
                        if (buffer.equalsIgnoreCase("solve")) {
                            toSolve.notifyObservers("Request sent at " + toSolve.createdDate + " has been solved.");
                            if (auxUser != null) {
                                auxUser.expStrat = new createIssueExpStrategy();
                                auxUser.exp = auxUser.executeStrategy(auxUser.exp);
                            }
                        } else {
                            toSolve.notifyObservers("Request sent at " + toSolve.createdDate + " has been rejected.");
                        }
                    } else if (!buffer.equalsIgnoreCase("exit")) {
                        System.out.println("Please use the keywords \"solve\", \"reject\" or \"exit\".");
                        buffer = null;
                    }
                }
            }
        }
    }
    public void updateInfoCLI(Scanner cli, User<Comparable<Object>> currentUser) {
        // Logic behind updating contributions.
        int i = 0, choice;
        List<Comparable<Object>> content = new ArrayList<>();
        // Show all the contributions.
        System.out.println("You have the following contributions: ");
        for(Comparable<Object> obj : ((Staff<Comparable<Object>>) currentUser).contributions) {
            if(obj instanceof Actor) {
                System.out.println(++i + ") " + ((Actor) obj).name);
            } else {
                System.out.println(++i + ") " + ((Production) obj).title);
            }
            content.add(obj);
        }
        if(currentUser instanceof Admin) {
            for(Actor obj : Admin.globalContributionActors) {
                System.out.println(++i + ") " + obj.name);
                content.add(obj);
            }
            for(Production obj : Admin.globalContributionProductions) {
                System.out.println(++i + ") " + obj.title);
                content.add(obj);
            }
        }
        // Let the user choose what to modify.
        System.out.println("Choose which contribution you'd like to update.");
        choice = getChoice(cli, 1, i);
        if(choice > 0) {
            Comparable<Object> cont = content.get(choice-1);
            i = 0;
            if(cont instanceof Actor) {
                System.out.println("You have chosen " + ((Actor)cont).name);
                ((Actor) cont).displayInfo();
                System.out.println("Choose what you'd like to change.");
                System.out.println(++i + ") Name");
                System.out.println(++i + ") Biography");
                System.out.println(++i + ") Acting");
                choice = getChoice(cli, 1, i);
                if(choice > 0) {
                    switch (choice) {
                        case 1:
                            obj.setNameCLI(cli, (Actor) cont);
                            break;
                        case 2:
                            obj.setBiographyCLI(cli, (Actor) cont);
                            break;
                        case 3:
                            obj.setActingCLI(cli, (Actor) cont);
                            break;
                    }
                }
            } else {
                System.out.println("You have chosen " + ((Production) cont).title);
                ((Production) cont).displayInfo();
                System.out.println("Choose what you'd like to change.");
                System.out.println(++i + ") Title");
                System.out.println(++i + ") Plot");
                System.out.println(++i + ") Directors");
                System.out.println(++i + ") Actors");
                System.out.println(++i + ") Genres");
                System.out.println(++i + ") Release Year");
                if(cont instanceof Movie) {
                    System.out.println(++i + ") Duration");
                }
                if(cont instanceof Series) {
                    System.out.println(++i + ") Seasons");
                }
                choice = getChoice(cli, 1, i);
                switch (choice) {
                    case 1:
                        System.out.println("New: ");
                        obj.setTitleCLI(cli, (Production) cont);
                        break;
                    case 2:
                        System.out.println("New: ");
                        obj.setPlotCLI(cli, (Production) cont);
                        break;
                    case 3:
                        System.out.println("New: ");
                        obj.setDirectorsCLI(cli, (Production) cont);
                        break;
                    case 4:
                        System.out.println("New: ");
                        obj.setActorsCLI(cli, (Production) cont);
                        break;
                    case 5:
                        System.out.println("New: ");
                        obj.setGenresCLI(cli, (Production) cont);
                        break;
                    case 6:
                        obj.setReleaseYear(cli, (Production) cont);
                        break;
                    case 7:
                        if(cont instanceof Movie) {
                            obj.setDurationCLI(cli, (Movie) cont);
                        } else if(cont instanceof Series) {
                            obj.setSeasonsCLI(cli, (Series) cont);
                        }
                        break;
                }
            }
        }
    }
    public void notifyReview(Rating rate, List<Rating> ratings, String title) {
        // Notifications for Ratings.
        int OK = 0;
        for(Rating obsRate : ratings) {
            for(User<Comparable<Object>> user : obj.users) {
                if(user.username.equals(obsRate.username)) {
                    rate.addObserver(user);
                    break;
                }
            }
        }
        // Search for people who have this content as a favorite.
        for(User<Comparable<Object>> user : obj.users) {
            for(Object o : user.favorites) {
                if(((o instanceof Production) && (((Production) o).title.equals(title))) ||
                        ((o instanceof Actor) && (((Actor) o).name.equals(title)))) {
                    rate.addObserver(user);
                    if(user instanceof Contributor<Comparable<Object>>) {
                        for(Object o2 : ((Contributor<Comparable<Object>>) user).contributions) {
                            if(((o2 instanceof Production) && (((Production) o2).title.equals(title))) ||
                                    ((o2 instanceof Actor) && (((Actor) o2).name.equals(title)))) {
                                OK = 1;
                                break;
                            }
                        }
                    }
                }
            }
        }

        // Search for the person who contributed.
        if(OK == 0) {
            for(User<Comparable<Object>> user : obj.users) {
                if(!(user instanceof Regular)) {
                    for(Object o : ((Staff<Comparable<Object>>) user).contributions) {
                        if(((o instanceof Production) && (((Production) o).title.equals(title))) ||
                                ((o instanceof Actor) && (((Actor) o).name.equals(title)))) {
                            rate.addObserver(user);
                            OK = 1;
                            break;
                        }
                    }
                }
                if(OK == 1) {
                    break;
                }
            }
        }
        // Check if content is managed by the admins.
        if(OK == 0) {
            for(User<Comparable<Object>> user : obj.users) {
                if(user instanceof Admin) {
                    rate.addObserver(user);
                }
            }
        }
    }
    public void createRatingCLI(Scanner cli, User<Comparable<Object>> currentUser, Comparable<Object> content, int val) {
        // Get essential info for a rating.
        Rating rate = new Rating();
        rate.username = currentUser.username;
        System.out.print("Grade: ");
        rate.grade = getChoice(cli, 1,10);
        System.out.print("Comment: ");
        rate.comment = cli.nextLine();
        if(content instanceof Production) {
            // Get & notify observers.
            notifyReview(rate, ((Production) content).rating, ((Production) content).title);
            rate.notifyObservers("New review has been added for " +
                    ((Production) content).title + " by " + currentUser.username + " -> " + rate.grade);
            // Add rating and compute the average grade.
            ((Production) content).rating.add(rate);
            ((Production) content).computeAvgRating();
            if(val == 0) {
                // Add new rating inside.
                currentUser.givenRatings.add(new Pair<>(((Production) content).title,rate));
            } else {
                // Refresh deleted rating.
                for(Pair<String, Rating> givenRate : currentUser.givenRatings) {
                    if(givenRate.getFirst().equals((((Production) content).title))) {
                        currentUser.givenRatings.remove(givenRate);
                        currentUser.givenRatings.add(new Pair<>(((Production) content).title,rate));
                        break;
                    }
                }
            }
        } else {
            // Get & notify observers.
            notifyReview(rate, ((Actor) content).rating, ((Actor) content).name);
            rate.notifyObservers("New review has been added for " +
                    ((Actor) content).name + " by " + currentUser.username + " -> " + rate.grade);
            // Add rating and compute the average grade.
            ((Actor) content).rating.add(rate);
            ((Actor) content).computeAvgRating();
            if(val == 0) {
                // Add new rating inside.
                currentUser.givenRatings.add(new Pair<>(((Actor) content).name, rate));
            } else {
                // Refresh deleted rating.
                for(Pair<String, Rating> givenRate : currentUser.givenRatings) {
                    if(givenRate.getFirst().equals((((Actor) content).name))) {
                        currentUser.givenRatings.remove(givenRate);
                        currentUser.givenRatings.add(new Pair<>(((Actor) content).name,rate));
                        break;
                    }
                }
            }
        }
        if(val == 0) {
            // User just made a review.
            currentUser.expStrat = new addReviewExpStrategy();
            currentUser.exp = currentUser.executeStrategy(currentUser.exp);
        }
    }
    public void deleteRating(User<Comparable<Object>> currentUser, String buffer, int choice, int mode) {
        // Delete a rating.
        for(Pair<String, Rating> ratePair : currentUser.givenRatings) {
            if(ratePair.getFirst().equals(buffer)) {
                currentUser.givenRatings.remove(ratePair);
                if(mode == 0) {
                    // This only happens if the user decided to remove their own rating.
                    currentUser.givenRatings.add(new Pair<>(buffer, null));
                }
                break;
            }
        }
        // Compute the ratings.
        if(choice == 1) {
            for(Production prod : obj.productions) {
                if(prod.title.equals(buffer)) {
                    prod.computeAvgRating();
                }
            }
        } else {
            for(Actor act : obj.actors) {
                if(act.name.equals(buffer)) {
                    act.computeAvgRating();
                }
            }
        }
    }
    public int setAgeCLI(Scanner cli) {
        // Set age for user.
        int age = -1;
        System.out.print("Age: ");
        while(age < 0) {
            age = getChoice(cli, 1,99);
        }
        return age;
    }
    public String setFirstNameCLI(Scanner cli) {
        // Set first name.
        System.out.print("First Name: ");
        return cli.nextLine();
    }
    public String setLastNameCLI(Scanner cli) {
        // Set last name.
        System.out.print("Last Name: ");
        return cli.nextLine();
    }
    public String setGenderCLI(Scanner cli) {
        // Set gender.
        int choice;
        String gender = null;
        System.out.println("Gender: ");
        System.out.println("1) Male");
        System.out.println("2) Female");
        System.out.println("3) Not specified");
        choice = -1;
        while(choice < 0) {
            choice = getChoice(cli, 1,3);
            if(choice < 0) {
                System.out.println("Can't use \"exit\" at this point.");
            } else {
                if(choice == 1) {
                    gender = "Male";
                } else if(choice == 2) {
                    gender = "Female";
                } else {
                    gender = "Not Specified";
                }
            }
        }
        return gender;
    }
    public String setBirthDateCLI(Scanner cli) {
        // Set birthdate.
        int birthYear, birthMonth, birthDay;
        String birthDate = "";
        System.out.println("Birth Year: ");
        birthYear = getChoice(cli, 1900, 2017);
        System.out.println("Birth Month: ");
        birthMonth = getChoice(cli, 1, 12);
        System.out.println("Birth Day:");
        birthDay = getChoice(cli, 1,31);
        // Format birthdate.
        birthDate += birthYear;
        if(birthMonth < 10) {
            birthDate += "-0" + birthMonth;
        } else {
            birthDate += "-" + birthMonth;
        }
        if(birthDay < 10) {
            birthDate += "-0" +birthDay;
        } else {
            birthDate += "-" + birthDay;
        }
        return birthDate;
    }
    public String setCountryCLI(Scanner cli) {
        // Set Country;
        System.out.println("Country: ");
        return cli.nextLine();
    }
    public String setNameCLI(Scanner cli) {
        // Set name.
        System.out.print("Name: ");
        return cli.nextLine();
    }
    public User<Comparable<Object>> goCLI(Scanner cli, @NotNull User<Comparable<Object>> currentUser, int i) {
        // Menu implementation for CLI.
        int choice = 0, OK = 0;
        String buffer = null, auxbuffer;
        System.out.println("Hello user " + currentUser.username + "!");
        System.out.println("Username: " + currentUser.username);
        System.out.println("Experience: " + (currentUser.exp != -1 ? currentUser.exp : "-"));
        System.out.println("Choose action: ");
        System.out.println(i++ + ") View your own details");
        System.out.println(i++ + ") View productions details");
        System.out.println(i++ + ") View actors details");
        System.out.println(i++ + ") View notifications");
        System.out.println(i++ + ") Search for actor/movie/series");
        System.out.println(i++ + ") Add/Delete/View actor/movie/series to/from favorites");
        if(!currentUser.accType.equals(AccountType.Admin)) {
            System.out.println(i++ + ") Add/Delete request");
        }
        if(!currentUser.accType.equals(AccountType.Regular)) {
            System.out.println(i++ + ") Add/Delete actor/movies/series from system");
            System.out.println(i++ + ") Solve a request");
            System.out.println(i++ + ") Update Production/Actor details");
        }
        if(currentUser.accType.equals(AccountType.Regular)) {
            System.out.println(i++ + ") Add/Delete a review from a production/actor");
        }
        if(currentUser.accType.equals(AccountType.Admin)) {
            System.out.println(i++ + ") Add/Delete/Modify a user from system");
        }
        System.out.println(i + ") Logout");
        while(buffer == null) {
            // Get command based on number.
            buffer = cli.nextLine();
            try {
                choice = parseInt(buffer);
                if((choice < 0) || (choice > i)) {
                    throw new InvalidCommandException();
                }
            } catch (InvalidCommandException | NumberFormatException e) {
                if(buffer.equalsIgnoreCase("exit")) {
                    System.out.println("Please logout before exiting.");
                } else {
                    System.out.println("Input must be an integer value between 1 and " + i + ".");
                }
                buffer = null;
            }
        }
        // Execute the corresponding command.
        switch (choice) {
            case 1:
                // Display info about the user.
                System.out.println("Name: " + currentUser.userInfo.getName());
                System.out.println("Age: " + currentUser.userInfo.getAge());
                System.out.println("Gender: " + currentUser.userInfo.getSex());
                System.out.println("Country: " + currentUser.userInfo.getCountry());
                System.out.println("Birthdate: " + currentUser.userInfo.getBirth());
                break;
            case 2:
                // Display Productions.
                for(Production prodChoice : obj.productions) {
                    obj.sortRatings(prodChoice);
                    prodChoice.displayInfo();
                }
                break;
            case 3:
                // Display Actors.
                for(Actor actorChoice : obj.actors) {
                    obj.sortRatings(actorChoice);
                    actorChoice.displayInfo();
                }
                break;
            case 4:
                // Display notifications.
                if(currentUser.userNotif.isEmpty()) {
                    System.out.println("Seems like you have no notifications :(");
                } else {
                    for(String notification : currentUser.userNotif) {
                        System.out.println(notification);
                    }
                }
                break;
            case 5:
                // Search for a Movie/Series/Actor.
                System.out.println("What would you like to search?");
                System.out.println("1) Production");
                System.out.println("2) Actor");
                choice = getChoice(cli, 1, 2);
                if(choice < 0) {
                    break;
                }
                System.out.print("Please provide the name of the ");
                if(choice == 1) {
                    System.out.println("production.");
                } else {
                    System.out.println("actor.");
                }
                buffer = cli.nextLine();
                if(buffer.equalsIgnoreCase("exit")) {
                    break;
                }
                if (choice == 1) {
                    for(Production searchProd : obj.productions) {
                        if(buffer.equalsIgnoreCase(searchProd.title)) {
                            OK = 1;
                            obj.sortRatings(searchProd);
                            searchProd.displayInfo();
                            break;
                        }
                    }
                } else {
                    for(Actor searchActor : obj.actors) {
                        if(buffer.equalsIgnoreCase(searchActor.name)) {
                            OK = 1;
                            obj.sortRatings(searchActor);
                            searchActor.displayInfo();
                            break;
                        }
                    }
                }
                if(OK == 0) {
                    System.out.println("Couldn't find the desired production/actor.");
                }
                break;
            case 6:
                // Add/Remove a production/actor from favorite list.
                System.out.println("Write \"add\" if you wish to add a new production/actor inside the favorite list.");
                System.out.println("Write \"remove\" if you wish to remove a production/actor from the favorite list.");
                System.out.println("Write \"view\" if you wish to see your favorite list.");
                buffer = null;
                while(buffer == null) {
                    buffer = cli.nextLine();
                    if((buffer.equalsIgnoreCase("add"))
                            || (buffer.equalsIgnoreCase("remove"))) {
                        System.out.println("Please provide the name of the production/actor which you wish to " +
                                buffer + ".");
                        String buffer2 = cli.nextLine();
                        for(Production prod : obj.productions) {
                            if(buffer2.equals(prod.title)) {
                                OK = 1;
                                if(buffer.equalsIgnoreCase("add")) {
                                    if(currentUser.addToFav(prod)) {
                                        System.out.println("Production has been successfully added.");
                                    } else {
                                        System.out.println("Production could not be added.");
                                    }
                                } else {
                                    if(currentUser.removeFromFav(prod)) {
                                        System.out.println("Production has been successfully removed.");
                                    } else {
                                        System.out.println("Production doesn't exist inside the favorite list.");
                                    }
                                }
                                break;
                            }
                        }
                        for(Actor actor : obj.actors) {
                            if(buffer2.equals(actor.name)) {
                                OK = 1;
                                if(buffer.equalsIgnoreCase("add")) {
                                    if(currentUser.addToFav(actor)) {
                                        System.out.println("Actor has been successfully added.");
                                    } else {
                                        System.out.println("Actor could not be added.");
                                    }
                                } else {
                                    if (currentUser.removeFromFav(actor)) {
                                        System.out.println("Actor has been successfully removed.");
                                    } else {
                                        System.out.println("Actor doesn't exist inside the favorite list.");
                                    }
                                }
                                break;
                            }
                        }
                        if(OK == 0) {
                            System.out.println("Sorry, the production/actor doesn't exist yet inside the DataBase. " +
                                    "Maybe create a request?");
                        }
                    } else if(buffer.equalsIgnoreCase("view")){
                        System.out.println(currentUser.username + "'s Favorite List");
                        for(Object content : currentUser.favorites) {
                            if(content instanceof Production) {
                                ((Production) content).displayInfo();
                            } else {
                                ((Actor) content).displayInfo();
                            }
                        }
                    } else if (!buffer.equalsIgnoreCase("exit")){
                        System.out.println("Please only use the keywords \"add\", \"remove\", \"view\" or \"exit\".");
                        buffer = null;
                    }
                }
                break;
            case 7:
                if(currentUser instanceof Admin) {
                    // Add/Delete a production/actor from the system.
                    modifySystemCLI(cli, currentUser);
                } else {
                    // Create/Retract a request.
                    System.out.println("Write \"create\" if you wish to create a request.");
                    System.out.println("Write \"retract\" if you wish to retract a request.");
                    buffer = null;
                    while(buffer == null) {
                        buffer = cli.nextLine();
                        if(buffer.equalsIgnoreCase("create")) {
                            System.out.println("Please choose the topic of the request.");
                            System.out.println("1) Actor issue");
                            System.out.println("2) Movie issue");
                            System.out.println("3) Delete account");
                            System.out.println("4) Others");
                            choice = getChoice(cli,1,4);
                            if(choice < 0) {
                                break;
                            }
                            Request newReq = new Request();
                            newReq.userSender = currentUser.username;
                            newReq.createdDate = LocalDateTime.now();

                            // Ask for info depending on the request choice.
                            switch(choice) {
                                case 1:
                                    newReq.type = RequestType.ACTOR_ISSUE;
                                    System.out.println("Please provide the name of the actor.");
                                    while(newReq.actorName == null) {
                                        newReq.actorName = cli.nextLine();
                                        for(Actor reqActor : obj.actors) {
                                            if(newReq.actorName.equals(reqActor.name)) {
                                                OK = 1;
                                                if(currentUser instanceof Contributor) {
                                                    for(Object o : ((Contributor<Comparable<Object>>) currentUser)
                                                            .contributions) {
                                                        if ((o instanceof Actor) && (((Actor) o).name)
                                                                .equals(reqActor.name)) {
                                                            OK = 2;
                                                            break;
                                                        }
                                                    }
                                                }
                                                break;
                                            }
                                        }
                                        if(OK == 0) {
                                            System.out.println("Actor doesn't exist inside the database.");
                                            System.out.println("Please pick a different actor.");
                                            newReq.actorName = null;
                                        } else if (OK == 2) {
                                            System.out.println("You can't pick an actor that you have added.");
                                            System.out.println("Please pick a different actor.");
                                            newReq.actorName = null;
                                        }
                                    }
                                    break;
                                case 2:
                                    newReq.type = RequestType.MOVIE_ISSUE;
                                    System.out.println("Please provide the name of the production.");
                                    while(newReq.titleProduction == null) {
                                        newReq.titleProduction = cli.nextLine();
                                        for(Production reqProduction : obj.productions) {
                                            if(newReq.titleProduction.equals(reqProduction.title)) {
                                                OK = 1;
                                                if(currentUser instanceof Contributor) {
                                                    for(Object o : ((Contributor<Comparable<Object>>) currentUser)
                                                            .contributions) {
                                                        if ((o instanceof Production) && (((Production) o).title)
                                                                .equals(reqProduction.title)) {
                                                            OK = 2;
                                                            break;
                                                        }
                                                    }
                                                }
                                                break;
                                            }
                                        }
                                        if(OK == 0) {
                                            System.out.println("Production doesn't exist inside the database.");
                                            System.out.println("Please pick a different production.");
                                            newReq.titleProduction = null;
                                        } else if (OK == 2) {
                                            System.out.println("You can't pick a production you have added.");
                                            System.out.println("Please pick a different production.");
                                            newReq.titleProduction = null;
                                        }
                                    }
                                    break;
                                case 3:
                                    newReq.type = RequestType.DELETE_ACCOUNT;
                                    break;
                                case 4:
                                    newReq.type = RequestType.OTHERS;
                                    break;
                            }
                            // Ask for descriptions.
                            System.out.println("Please provide a description.");
                            newReq.description = cli.nextLine();
                            currentUser.createRequest(newReq, obj.users);
                            obj.requests.add(newReq);
                        } else if (buffer.equalsIgnoreCase("retract")) {
                            // Display current requests sent by user.
                            i = 0;
                            if(currentUser.createdRequests.isEmpty()) {
                                System.out.println("There are no requests sent.");
                            } else {
                                for(Request r : currentUser.createdRequests) {
                                    System.out.println(++i + ") " + r.displayRequest());
                                }
                                System.out.println("Choose which request to retract.");
                                choice = getChoice(cli, 1, i);
                                i = 1;
                                for(Request r : currentUser.createdRequests) {
                                    if(i == choice) {
                                        obj.removeRequests(r, currentUser);
                                        break;
                                    }
                                }
                            }
                        } else if(!buffer.equalsIgnoreCase("exit")) {
                            System.out.println("Please only use the keywords \"create\", \"retract\" or \"exit\".");
                            buffer = null;
                        }
                    }
                }
                break;
            case 8:
                if(currentUser instanceof Regular) {
                    // Add/Delete a review for a Production/Actor.
                    int value = 0;
                    System.out.println("Add/Delete a review for:");
                    System.out.println("1) Production");
                    System.out.println("2) Actor");
                    choice = getChoice(cli, 1, 2);
                    if(choice > 0) {
                        System.out.println("Write \"add\" if you wish to add a review.");
                        System.out.println("Write \"delete\" if you wish to delete a review.");
                        buffer = null;
                        while(buffer == null) {
                            buffer = cli.nextLine();
                            if(buffer.equalsIgnoreCase("add")) {
                                System.out.print("Write the name of the ");
                                if(choice == 1) {
                                    System.out.print("production ");
                                } else {
                                    System.out.print("actor ");
                                }
                                System.out.println("which you wish to review.");
                                buffer = cli.nextLine();
                                for(Pair<String, Rating> ratePair : currentUser.givenRatings) {
                                    if(ratePair.getFirst().equals(buffer)) {
                                        if(ratePair.getSecond()!=null) {
                                            // Rating exists.
                                            value = 1;
                                        } else {
                                            // Rating existed. Don't give experience.
                                            value = -1;
                                        }
                                    }
                                }
                                if(value == 1) {
                                    System.out.println("Rating already exists. If you wish to modify it, " +
                                            "delete it first.");
                                } else {
                                    if(choice == 1) {
                                        for(Production prod : obj.productions) {
                                            if(buffer.equals(prod.title)) {
                                                obj.createRatingCLI(cli, currentUser, prod, value);
                                                obj.sortRatings(prod);
                                            }
                                        }
                                    } else {
                                        for(Actor act : obj.actors) {
                                            if(buffer.equals(act.name)) {
                                                obj.createRatingCLI(cli, currentUser, act, value);
                                                obj.sortRatings(act);
                                            }
                                        }
                                    }
                                }
                            } else if(buffer.equalsIgnoreCase("delete")) {
                                // Delete a review from an actor/production.
                                System.out.print("Write the name of the ");
                                if(choice == 1) {
                                    System.out.print("production ");
                                } else {
                                    System.out.print("actor ");
                                }
                                System.out.println("from which you wish to delete your rating.");
                                buffer = cli.nextLine();
                                deleteRating(currentUser,buffer, choice,0);
                                System.out.println("Action completed.");
                            } else if (!buffer.equalsIgnoreCase("exit")) {
                                System.out.println("Please only use the keywords \"add\", \"delete\" or \"exit\".");
                                buffer = null;
                            }
                        }
                    }
                } else if(currentUser instanceof Contributor) {
                    // Add/Delete productions/actors from the system.
                    obj.modifySystemCLI(cli, currentUser);
                } else {
                    // View and solve requests.
                    obj.solveRequestsCLI(cli, currentUser);
                }
                break;
            case 9:
                if(currentUser instanceof Regular) {
                    // Log out for Regulars.
                    currentUser = currentUser.logout();
                } else if(currentUser instanceof Contributor){
                    // View and solve requests.
                    obj.solveRequestsCLI(cli, currentUser);
                } else {
                    // Update information about Productions/Actors.
                    obj.updateInfoCLI(cli, currentUser);
                }
                break;
            case 10:
                if(currentUser instanceof Contributor) {
                    // Update information about Productions/Actors.
                    obj.updateInfoCLI(cli, currentUser);
                } else {
                    // Add/Delete a user.
                    System.out.println("Write \"add\" if you wish to add an user, \"delete\" to erase an existing one, " +
                            "or \"modify\" to modify an existing user.");
                    buffer = null;
                    while(buffer == null) {
                        buffer = cli.nextLine();
                        if(buffer.equalsIgnoreCase("add")) {
                            System.out.println("What kind of user do you wish to create?");
                            System.out.println("Use the keywords \"Admin\", \"Contributor\" or \"Regular\".");
                            buffer = null;
                            while(buffer == null) {
                                buffer = cli.nextLine();
                                if((buffer.equals("Admin"))||(buffer.equals("Contributor"))
                                        ||(buffer.equals("Regular"))) {
                                    User<Comparable<Object>> user = UserFactory.factory(AccountType.valueOf(buffer));
                                    String firstname, lastname, name, gender, birthDate, country, email = null;
                                    String regex = "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)" +
                                            "*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
                                    Pattern pattern = Pattern.compile(regex);
                                    int age;
                                    while(OK == 0) {
                                        // Extract data and make sure it's proper for use.
                                        age = setAgeCLI(cli);
                                        firstname = setFirstNameCLI(cli);
                                        lastname = setLastNameCLI(cli);
                                        name = firstname + " " + lastname;
                                        gender = setGenderCLI(cli);
                                        birthDate = setBirthDateCLI(cli);
                                        country = setCountryCLI(cli);
                                        while(email == null) {
                                            System.out.println("Email: ");
                                            email = cli.nextLine();
                                            if(!pattern.matcher(email).matches()) {
                                                System.out.println("Email doesn't exist. Use a proper email.");
                                                email = null;
                                            }
                                            for(User<Comparable<Object>> userChecker : obj.users) {
                                                if(userChecker.userInfo.getCreds().getEmail().equals(email)) {
                                                    System.out.println("Email is already being used.");
                                                    email = null;
                                                    break;
                                                }
                                            }
                                        }
                                        try {
                                            if(((Admin<Comparable<Object>>) currentUser).addUser(user, age,
                                                    birthDate, firstname, lastname, name, gender,
                                                    country, email) == 0) {
                                                throw new InformationIncompleteException();
                                            } else {
                                                OK = 1;
                                            }
                                        } catch(InformationIncompleteException e) {
                                            System.out.println("Please fill all fields with proper information.");
                                        }
                                        if(OK == 1) {
                                            // Validate username.
                                            ((Admin<Comparable<Object>>) currentUser).validateUser(obj.users, user,
                                                    firstname, lastname);
                                        }
                                    }
                                    if(user instanceof Admin<Comparable<Object>>) {
                                        user.accType = AccountType.Admin;
                                        user.exp = -1;
                                    } else {
                                        if(user instanceof Contributor<Comparable<Object>>) {
                                            user.accType = AccountType.Contributor;
                                        } else {
                                            assert user != null;
                                            user.accType = AccountType.Regular;
                                        }
                                        user.exp = 0;
                                    }
                                    System.out.println("User" + user.username + "has been successfully added.");
                                    System.out.println("Generated password is " + user.userInfo.getCreds().
                                            getPassword());
                                    obj.users.add(user);
                                } else if(!buffer.equals("exit")) {
                                    System.out.println("Please use the keywords \"Admin\", \"Contributor\"," +
                                            " \"Regular\" or \"exit\".");
                                    buffer = null;
                                }
                            }
                        } else if((buffer.equalsIgnoreCase("delete"))
                            || (buffer.equalsIgnoreCase("modify"))) {
                            System.out.print("Write the username of the user you wish to ");
                            if(buffer.equalsIgnoreCase("delete")) {
                                System.out.println("delete.");
                            } else {
                                System.out.println("modify.");
                            }
                            auxbuffer = buffer;
                            buffer = cli.nextLine();
                            for (User<Comparable<Object>> user : obj.users) {
                                if (user.username.equals(buffer)) {
                                    if((!user.username.equals(currentUser.username)) &&
                                            (user.accType.equals(AccountType.Admin))) {
                                        System.out.print("You're not allowed to ");
                                        if(auxbuffer.equalsIgnoreCase("delete")) {
                                            System.out.print("delete ");
                                        } else {
                                            System.out.print("modify ");
                                        }
                                        System.out.println("an admin.");
                                    } else {
                                        if(auxbuffer.equalsIgnoreCase("delete")) {
                                            // Remove user from database.
                                            if(user.username.equals(currentUser.username)) {
                                                System.out.println("You can't delete yourself");
                                            } else {
                                                ((Admin<Comparable<Object>>) currentUser).deleteUserCLI(user, obj);
                                                obj.users.remove(user);
                                            }
                                        } else {
                                            // Change something about the user's profile.
                                            System.out.println("What would you wish to modify?");
                                            System.out.println("1) Age");
                                            System.out.println("2) Name");
                                            System.out.println("3) Gender");
                                            System.out.println("4) Birth Date");
                                            System.out.println("5) Country");
                                            choice = getChoice(cli, 1,5);
                                            if(choice > 0) {
                                                switch (choice) {
                                                    case 1:
                                                        user.userInfo.setAge(obj.setAgeCLI(cli));
                                                        break;
                                                    case 2:
                                                        user.userInfo.setName(obj.setNameCLI(cli));
                                                        break;
                                                    case 3:
                                                        user.userInfo.setSex(obj.setGenderCLI(cli));
                                                        break;
                                                    case 4:
                                                        user.userInfo.setBirth(obj.setBirthDateCLI(cli));
                                                        break;
                                                    case 5:
                                                        user.userInfo.setCountry(obj.setCountryCLI(cli));
                                                        break;
                                                }
                                            }
                                        }
                                    }
                                    break;
                                }
                            }
                        } else if(!buffer.equals("exit")) {
                            System.out.println("Please only use the keywords \"add\", \"delete\" or \"exit\".");
                            buffer = null;
                        }
                    }
                }
                break;
            case 11:
                // Log out for Staff.
                currentUser = currentUser.logout();
                break;
        }
        if(currentUser != null) {
            System.out.println("Press [Enter] to get back to the menu.");
            while (!buffer.isEmpty()) {
                buffer = cli.nextLine();
            }
        }
        return currentUser;
    }
    public void goGUI() {
        new AuthGUI(obj);
    }
    public void run() {
        // First, parse the data from JSONs.
        int works = doParse();
        if(works!=0) {
            System.out.println("Something went wrong while parsing the data.");
        } else {
            // Now, let the user decide between command line interface and graphical user interface.
            int mode = 0;
            User<Comparable<Object>> currentUser;
            Scanner cli = new Scanner(System.in);

            mode = chooseCLIGUI(cli, mode);
            while(mode == 1) {
                // CLI auth process.
                currentUser = authCLI(cli);
                if(currentUser == null) {
                    mode = 0;
                } else {
                    // CLI interface.
                    while(currentUser != null) {
                        currentUser = goCLI(cli, currentUser, 1);
                    }
                }
            }
            while(mode == 2) {
                // GUI auth process.
                goGUI();
                mode = 0;
            }
        }
    }

    public static void main(String[] args) {
        getInstance();
        obj.run();
    }
}
