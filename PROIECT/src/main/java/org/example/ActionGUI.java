package org.example;

import kotlin.Pair;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

public class ActionGUI extends JFrame {
    Color colorBack = new Color(64, 49, 10);
    Color colorPanel = new Color(57, 25, 0);
    public void stylize(JButton button, Font font) {
        // Stylization of a button.
        button.setBackground(colorBack);
        button.setForeground(Color.ORANGE);
        button.setFont(font);
        button.setHorizontalAlignment(SwingConstants.CENTER);
    }
    public void stylize(JLabel label, Font font) {
        // Stylization of a label.
        label.setForeground(Color.ORANGE);
        label.setFont(font);
        label.setHorizontalAlignment(SwingConstants.CENTER);
    }
    public void stylizeGenerate(JTextField field) {
        // Stylization for fields that are used for generating users.
        field.setBackground(colorBack);
        field.setForeground(Color.ORANGE);
        field.setPreferredSize(new Dimension(170, 30));
    }
    public String writeLists(List<?> source) {
        // Class used for displaying information about Productions.
        StringBuilder res = new StringBuilder();
        if(source != null) {
            for(Object aux : source) {
                if(aux instanceof Rating) {
                    res.append("= RATING =\n");
                    res.append("User: ").append(((Rating) aux).username).append("\n");
                    res.append("Grade: ").append(((Rating) aux).grade).append("\n");
                    res.append("Comment: ").append(((Rating) aux).comment).append("\n");
                } else {
                    res.append(aux.toString()).append("\n");
                }
            }
        } else {
            res.append(("-") + "\n");
        }
        return res.toString();
    }
    public ActionGUI(IMDB obj, User<Comparable<Object>> currentUser, int sit, int ret) {
        // This class is used whenever a user wants to execute an action that's displayed inside their menu.
        // This is also used for displaying recommendations, action which can only be called from the welcome page.
        super("IMDB Workbench");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Font font = new Font(Font.SANS_SERIF,Font.PLAIN,32);
        Font sFont = new Font(Font.SANS_SERIF,Font.PLAIN,16);
        Font ssFont = new Font(Font.SANS_SERIF,Font.PLAIN,12);
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(colorPanel);
        JButton back = new JButton("EXIT");
        back.setPreferredSize(new Dimension(200,60));
        back.addActionListener(e -> backToMenu(obj,currentUser,ret));
        stylize(back,sFont);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(colorPanel);

        JLabel title = null, situation, select;

        JScrollPane scrollPane, scrollPaneinfo, scrollSearch;
        JTextArea infoProd;
        JList<String> view;
        Vector<String> names;
        JTextField searchBar;
        JButton search, add, delete, review, modify, updateProd, updateActor;

        Dimension buttonDims = new Dimension(200,60);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0,20,20,0);

        // Each case represents a different action chosen by the user.
        switch(sit) {
            case 0:
                // Display productions.
                title = new JLabel("Productions");
                stylize(title, font);

                scrollPane = new JScrollPane();
                scrollPaneinfo = new JScrollPane();
                scrollPane.setPreferredSize(new Dimension(250,250));
                scrollPaneinfo.setPreferredSize(new Dimension(400,250));
                scrollPaneinfo.getVerticalScrollBar().setPreferredSize(new Dimension(1, 0));
                scrollPaneinfo.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 5));
                scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(1, 0));
                scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 5));

                infoProd = new JTextArea();
                infoProd.setBackground(colorBack);
                infoProd.setForeground(Color.ORANGE);
                infoProd.setEnabled(false);

                view = new JList<>();
                view.setBackground(colorBack);
                view.setForeground(Color.ORANGE);
                names = new Vector<>();

                scrollPane.setViewportView(view);
                scrollPaneinfo.setViewportView(infoProd);

                // Go through each Production and display its information.
                for(Production prod : obj.productions) {
                    names.add(prod.title);
                }
                view.setListData(names);
                view.addListSelectionListener(e -> {
                    if(!e.getValueIsAdjusting()) {
                        String selected = view.getSelectedValue();
                        for(Production prod : obj.productions) {
                            if(prod.title.equals(selected)) {
                                infoProd.setText(displayInfoProd(prod));
                            }
                        }
                    }
                });
                gbc.gridx = 0;
                gbc.gridy = 1;
                mainPanel.add(scrollPane, gbc);
                gbc.gridx++;
                mainPanel.add(scrollPaneinfo, gbc);
                break;
            case 1:
                // Display actors.
                title = new JLabel("Actors");
                stylize(title, font);

                scrollPane = new JScrollPane();
                scrollPaneinfo = new JScrollPane();

                scrollPane.setPreferredSize(new Dimension(250,250));
                scrollPaneinfo.setPreferredSize(new Dimension(400,250));
                scrollPaneinfo.getVerticalScrollBar().setPreferredSize(new Dimension(1, 0));
                scrollPaneinfo.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 5));
                scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(1, 0));
                scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 5));

                infoProd = new JTextArea();
                infoProd.setBackground(colorBack);
                infoProd.setForeground(Color.ORANGE);
                infoProd.setEnabled(false);

                view = new JList<>();
                view.setBackground(colorBack);
                view.setForeground(Color.ORANGE);
                names = new Vector<>();

                scrollPane.setViewportView(view);
                scrollPaneinfo.setViewportView(infoProd);

                // Go through each actor inside the database and display their information.
                for(Actor act : obj.actors) {
                    names.add(act.name);
                }
                view.setListData(names);
                view.addListSelectionListener(e -> {
                    if(!e.getValueIsAdjusting()) {
                        String selected = view.getSelectedValue();
                        for(Actor act : obj.actors) {
                            if(act.name.equals(selected)) {
                                infoProd.setText(displayInfoActor(act));
                            }
                        }
                    }
                });
                gbc.gridx = 0;
                gbc.gridy = 1;
                mainPanel.add(scrollPane, gbc);
                gbc.gridx++;
                mainPanel.add(scrollPaneinfo, gbc);
                break;
            case 2:
                // Display notifications.
                title = new JLabel("Notifications");
                stylize(title, font);

                scrollPaneinfo = new JScrollPane();
                scrollPaneinfo.setPreferredSize(new Dimension(300,250));
                scrollPaneinfo.getVerticalScrollBar().setPreferredSize(new Dimension(1, 0));
                scrollPaneinfo.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 5));

                infoProd = new JTextArea();
                infoProd.setBackground(colorBack);
                infoProd.setForeground(Color.ORANGE);
                infoProd.setEnabled(false);

                scrollPaneinfo.setViewportView(infoProd);
                if(currentUser.userNotif.isEmpty()) {
                    infoProd.append("You haven't received any notifications!");
                } else {
                    for(String notif : currentUser.userNotif) {
                        infoProd.append(notif + "\n");
                    }
                }
                gbc.gridx = 0;
                gbc.gridy = 1;
                mainPanel.add(scrollPaneinfo, gbc);
                break;
            case 3:
                // Search for an actor/production.
                situation = new JLabel();
                stylize(situation, ssFont);

                title = new JLabel("IMDB Search");
                stylize(title, font);

                scrollPaneinfo = new JScrollPane();
                scrollPaneinfo.setPreferredSize(new Dimension(320,240));
                scrollSearch = new JScrollPane();
                scrollSearch.setPreferredSize(new Dimension(230,50));
                scrollPaneinfo.getVerticalScrollBar().setPreferredSize(new Dimension(1, 0));
                scrollPaneinfo.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 5));
                scrollSearch.getVerticalScrollBar().setPreferredSize(new Dimension(1, 0));
                scrollSearch.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 5));

                searchBar = new JTextField("Search for an Actor/Production here...");
                searchBar.setPreferredSize(new Dimension(230,50));
                searchBar.setBackground(colorBack);
                searchBar.setForeground(Color.ORANGE);
                scrollSearch.setViewportView(searchBar);

                infoProd = new JTextArea();
                infoProd.setBackground(colorBack);
                infoProd.setForeground(Color.ORANGE);
                infoProd.setEnabled(false);

                search = new JButton("Search");
                search.setPreferredSize(new Dimension(100, 50));
                stylize(search, sFont);
                search.addActionListener(e -> infoProd.setText(searchContent(obj, searchBar.getText())));


                add = new JButton("Add to Favorites");
                add.setPreferredSize(buttonDims);
                stylize(add, sFont);
                add.addActionListener(e -> situation.setText(addtoFav(obj, searchBar.getText(), currentUser)));

                delete = new JButton("Remove from Favorites");
                delete.setPreferredSize(buttonDims);
                stylize(delete, sFont);
                delete.addActionListener(e -> situation.setText(removeFav(obj, searchBar.getText(), currentUser)));


                scrollPaneinfo.setViewportView(infoProd);
                gbc.gridx = 0;
                gbc.gridy = 0;
                buttonPanel.add(add, gbc);
                gbc.gridy++;
                buttonPanel.add(delete, gbc);

                //If user is a Regular, let them review what they search.
                if(currentUser instanceof Regular<Comparable<Object>>) {
                    review = new JButton("Review");
                    stylize(review, sFont);
                    review.setPreferredSize(buttonDims);

                    JLabel finalTitle = title;
                    review.addActionListener(e -> {
                        scrollSearch.setVisible(false);
                        search.setVisible(false);
                        scrollPaneinfo.setVisible(false);
                        situation.setVisible(false);
                        buttonPanel.setVisible(false);
                        finalTitle.setText("Review Manger");
                        addReview(sFont, mainPanel, add, delete, gbc, obj, currentUser, searchBar.getText());
                    });

                    gbc.gridy++;
                    buttonPanel.add(review, gbc);
                }

                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.gridy = 1;
                mainPanel.add(scrollSearch, gbc);
                gbc.gridx++;
                mainPanel.add(search,gbc);
                gbc.gridy++;
                gbc.gridx--;
                gbc.gridwidth = 2;
                mainPanel.add(scrollPaneinfo, gbc);
                gbc.gridy++;
                gbc.fill = 0;
                mainPanel.add(situation, gbc);
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.gridwidth = 1;
                gbc.gridx+=2;
                gbc.gridy--;
                mainPanel.add(buttonPanel, gbc);
                break;
            case 4:
                // Display users favorite list.
                title = new JLabel(currentUser.username + "'s Favorite list");
                stylize(title, font);

                scrollPane = new JScrollPane();
                scrollPaneinfo = new JScrollPane();

                scrollPaneinfo.setPreferredSize(new Dimension(300,250));
                scrollPane.setPreferredSize(new Dimension(300,250));
                scrollPaneinfo.getVerticalScrollBar().setPreferredSize(new Dimension(5,0));
                scrollPaneinfo.getHorizontalScrollBar().setPreferredSize(new Dimension(0,5));
                scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(5,0));
                scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0,5));

                infoProd = new JTextArea();
                infoProd.setBackground(colorBack);
                infoProd.setForeground(Color.ORANGE);
                infoProd.setEnabled(false);

                scrollPaneinfo.setViewportView(infoProd);

                view = new JList<>();
                view.setPreferredSize(new Dimension(300,250));
                view.setBackground(colorBack);
                view.setForeground(Color.ORANGE);
                names = new Vector<>();

                scrollPane.setViewportView(view);

                if(currentUser.favorites.isEmpty()) {
                    infoProd.append("Favorite list is empty :(");
                } else {
                    for(Object o : currentUser.favorites) {
                        if(o instanceof Production) {
                            names.add(((Production) o).title);
                        } else {
                            names.add(((Actor) o).name);
                        }
                    }
                }
                view.setListData(names);
                view.addListSelectionListener(e -> {
                    if(!e.getValueIsAdjusting()) {
                        String selected = view.getSelectedValue();
                        for(Actor act : obj.actors) {
                            if(act.name.equals(selected)) {
                                infoProd.setText(displayInfoActor(act));
                            }
                        }
                        for(Production prod : obj.productions) {
                            if(prod.title.equals(selected)) {
                                infoProd.setText(displayInfoProd(prod));
                            }
                        }
                    }
                });
                gbc.gridx = 0;
                gbc.gridy = 1;
                mainPanel.add(scrollPane, gbc);
                gbc.gridx++;
                mainPanel.add(scrollPaneinfo, gbc);
                break;
            case 5:
                // Let the user create/retract a request.
                title = new JLabel("Request Manager");
                stylize(title,font);

                add = new JButton("Create request");
                add.setPreferredSize(buttonDims);
                stylize(add, sFont);
                delete = new JButton("Retract request");
                delete.setPreferredSize(buttonDims);
                stylize(delete, sFont);

                gbc.gridx = 0;
                gbc.gridy = 1;
                mainPanel.add(add, gbc);
                gbc.gridx++;
                mainPanel.add(delete, gbc);

                add.addActionListener(e -> addNewRequest(sFont, mainPanel, add, delete, gbc, obj, currentUser));
                delete.addActionListener(e -> removeRequest(sFont, mainPanel, add, delete, gbc, obj, currentUser));
                break;
            case 6:
                // Let the user decide between adding or deleting content.
                title = new JLabel("Add/Delete content to system");
                stylize(title,font);

                add = new JButton("Add content");
                add.setPreferredSize(buttonDims);
                stylize(add, sFont);

                delete = new JButton("Delete content");
                delete.setPreferredSize(buttonDims);
                stylize(delete, sFont);

                gbc.gridx = 0;
                gbc.gridy = 1;
                mainPanel.add(add, gbc);
                gbc.gridx++;
                mainPanel.add(delete, gbc);

                add.addActionListener(e -> addSystem(sFont, mainPanel, add, delete, gbc, obj, currentUser));
                delete.addActionListener(e -> removeSystem(sFont, mainPanel, add, delete, gbc, obj, currentUser));
                break;
            case 7:
                // Request manager used by Staff.
                title = new JLabel("Staff Request Manager");
                stylize(title,font);
                select = new JLabel();
                stylize(select, sFont);
                JTextArea info;

                view = new JList<>();
                view.setBackground(colorBack);
                view.setForeground(Color.ORANGE);

                names = new Vector<>();

                JButton solve, reject;
                solve = new JButton("Solve request");
                stylize(solve, sFont);
                solve.setPreferredSize(new Dimension(200, 60));
                reject = new JButton("Reject request");
                reject.setPreferredSize(new Dimension(200, 60));
                stylize(reject, sFont);

                scrollPaneinfo = new JScrollPane();
                scrollPane = new JScrollPane();
                scrollPaneinfo.getVerticalScrollBar().setPreferredSize(new Dimension(5,0));
                scrollPaneinfo.getHorizontalScrollBar().setPreferredSize(new Dimension(0,5));
                scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(5,0));
                scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0,5));

                info = new JTextArea();
                info.setBackground(colorBack);
                info.setForeground(Color.ORANGE);
                info.setEnabled(false);
                scrollPaneinfo.setViewportView(info);
                scrollPane.setViewportView(view);
                scrollPaneinfo.setPreferredSize(new Dimension(300,350));
                scrollPane.setPreferredSize(new Dimension(250,350));

                if((((Staff<Comparable<Object>>) currentUser).requests.isEmpty()) &&
                        ((currentUser instanceof Contributor<Comparable<Object>>)||(Admin.RequestsHolder.requests.isEmpty()))) {
                    info.setText("Hooray! You don't have any requests to solve! :D");
                } else {
                    for(Request r : ((Staff<Comparable<Object>>) currentUser).requests) {
                        names.add("Subject: " + r.type + " sent at " + r.createdDate);
                    }
                    if(currentUser instanceof Admin) {
                        for(Request r : Admin.RequestsHolder.requests) {
                            names.add("Subject: " + r.type + " sent at" + r.createdDate);
                        }
                    }
                    view.setListData(names);
                }

                view.addListSelectionListener(e -> {
                    if(!e.getValueIsAdjusting()) {
                        int selected = view.getSelectedIndex() , i = 0;
                        for(Request r : ((Staff<Comparable<Object>>) currentUser).requests) {
                            if(i == selected) {
                                info.setText(r.displayRequest());
                                i++;
                                break;
                            }
                            i++;
                        }
                        if(i<=selected) {
                            for(Request r : Admin.RequestsHolder.requests) {
                                if(i == selected) {
                                    info.setText(r.displayRequest());
                                    break;
                                }
                                i++;
                            }
                        }
                    }
                });

                solve.addActionListener(e -> select.setText(manageRequest(info, view, obj, currentUser, 0)));
                reject.addActionListener(e -> select.setText(manageRequest(info, view, obj, currentUser, 1)));

                gbc.gridx = 0;
                gbc.gridy = 1;
                mainPanel.add(scrollPane, gbc);
                gbc.gridx++;
                mainPanel.add(scrollPaneinfo, gbc);
                gbc.gridy++;
                gbc.gridx--;
                mainPanel.add(solve, gbc);
                gbc.gridx++;
                mainPanel.add(reject, gbc);
                gbc.gridx--;
                gbc.gridwidth = 2;
                gbc.gridy++;
                mainPanel.add(select, gbc);
                break;
            case 8:
                // Update content from system.
                title = new JLabel("Update content from system");
                stylize(title,font);

                updateProd = new JButton("Update a Production");
                updateProd.setPreferredSize(buttonDims);
                stylize(updateProd, sFont);
                updateActor = new JButton("Update an Actor");
                updateActor.setPreferredSize(buttonDims);
                stylize(updateActor, sFont);

                gbc.gridx = 0;
                gbc.gridy = 1;
                mainPanel.add(updateProd, gbc);
                gbc.gridx++;
                mainPanel.add(updateActor, gbc);

                updateProd.addActionListener(e -> updateProd(sFont, mainPanel, updateProd, updateActor, gbc, obj,
                        currentUser));
                updateActor.addActionListener(e -> updateActor(sFont, mainPanel, updateProd, updateActor, gbc, obj,
                        currentUser));
                break;
            case 9:
                // Let the user decide between adding or removing a review they've made.
                title = new JLabel("Review Manager");
                stylize(title,font);

                add = new JButton("Add Review");
                add.setPreferredSize(buttonDims);
                stylize(add, sFont);
                delete = new JButton("Remove Review");
                delete.setPreferredSize(buttonDims);
                stylize(delete, sFont);

                gbc.gridx = 0;
                gbc.gridy = 1;
                mainPanel.add(add, gbc);
                gbc.gridx++;
                mainPanel.add(delete, gbc);

                add.addActionListener(e -> addReview(sFont, mainPanel, add, delete, gbc, obj,
                        currentUser, null));
                delete.addActionListener(e -> removeReview(sFont, mainPanel, add, delete, gbc, obj,
                        currentUser));
                break;
            case 10:
                // Admin User Manager panel.
                title = new JLabel("User Manager");
                stylize(title,font);

                add = new JButton("Add user");
                add.setPreferredSize(buttonDims);
                stylize(add, sFont);

                modify = new JButton("Modify user");
                modify.setPreferredSize(buttonDims);
                stylize(modify, sFont);

                gbc.gridx = 0;
                gbc.gridy = 1;
                mainPanel.add(add, gbc);
                gbc.gridx++;
                mainPanel.add(modify, gbc);

                add.addActionListener(e -> addUser(sFont, mainPanel, add, modify, gbc, obj, currentUser));
                modify.addActionListener(e -> modifyUser(sFont, mainPanel, add, modify, gbc, obj, currentUser));

                break;
            case 11:
                // Display user information.
                title = new JLabel("User Information");
                stylize(title, font);

                JLabel accType, age, name, country, sex, birthDate, email, userInfo;
                JTextField nameInfo, countryInfo, birthInfo, emailInfo, ageInfo;
                JList<String> accTypeInfo = new JList<>();
                JList<String> sexInfo = new JList<>();
                Vector<String> accTypes = new Vector<>();
                Vector<String> genders = new Vector<>();
                accTypes.add("Admin");
                accTypes.add("Contributor");
                accTypes.add("Regular");
                accTypeInfo.setListData(accTypes);
                accTypeInfo.setBackground(colorPanel);
                accTypeInfo.setForeground(Color.ORANGE);
                genders.add("Male");
                genders.add("Female");
                genders.add("Not Specified");
                sexInfo.setListData(genders);
                sexInfo.setBackground(colorPanel);
                sexInfo.setForeground(Color.ORANGE);

                userInfo = new JLabel("User Information");
                stylize(userInfo, sFont);
                accType = new JLabel("Account Type: ");
                stylize(accType, sFont);
                name = new JLabel("Name: ");
                stylize(name, sFont);
                age = new JLabel("Age: ");
                stylize(age, sFont);
                email = new JLabel("Email: ");
                stylize(email, sFont);
                birthDate = new JLabel("Birthdate: ");
                stylize(birthDate, sFont);
                country = new JLabel("Country: ");
                stylize(country, sFont);
                sex = new JLabel("Gender: ");
                stylize(sex, sFont);
                select = new JLabel();
                stylize(select, sFont);

                nameInfo = new JTextField(currentUser.userInfo.getName());
                stylizeGenerate(nameInfo);
                countryInfo = new JTextField(currentUser.userInfo.getCountry());
                stylizeGenerate(countryInfo);
                birthInfo = new JTextField(currentUser.userInfo.getBirth().toString());
                stylizeGenerate(birthInfo);
                emailInfo = new JTextField(currentUser.userInfo.getCreds().getEmail());
                stylizeGenerate(emailInfo);
                ageInfo = new JTextField(String.valueOf(currentUser.userInfo.getAge()));
                stylizeGenerate(ageInfo);
                if(currentUser instanceof Admin) {
                    accTypeInfo.setSelectedIndex(0);
                } else if(currentUser instanceof Contributor) {
                    accTypeInfo.setSelectedIndex(1);
                } else {
                    accTypeInfo.setSelectedIndex(2);
                }
                if(currentUser.userInfo.getSex().equals("M")) {
                    sexInfo.setSelectedIndex(0);
                } else if (currentUser.userInfo.getSex().equals ("F") ) {
                    sexInfo.setSelectedIndex(1);
                } else {
                    sexInfo.setSelectedIndex(2);
                }

                accTypeInfo.setEnabled(false);
                nameInfo.setEnabled(false);
                countryInfo.setEnabled(false);
                birthInfo.setEnabled(false);
                emailInfo.setEnabled(false);
                ageInfo.setEnabled(false);
                sexInfo.setEnabled(false);

                gbc.gridy = 1;
                gbc.gridx = 0;
                gbc.gridwidth = 2;
                mainPanel.add(userInfo, gbc);
                gbc.gridy++;
                gbc.gridwidth = 1;
                mainPanel.add(accType, gbc);
                gbc.gridx++;
                mainPanel.add(accTypeInfo, gbc);
                gbc.gridx--;
                gbc.gridy++;
                mainPanel.add(name, gbc);
                gbc.gridx++;
                mainPanel.add(nameInfo, gbc);
                gbc.gridx--;
                gbc.gridy++;
                mainPanel.add(age, gbc);
                gbc.gridx++;
                mainPanel.add(ageInfo, gbc);
                gbc.gridx--;
                gbc.gridy++;
                mainPanel.add(email, gbc);
                gbc.gridx++;
                mainPanel.add(emailInfo, gbc);
                gbc.gridx--;
                gbc.gridy++;
                mainPanel.add(birthDate, gbc);
                gbc.gridx++;
                mainPanel.add(birthInfo, gbc);
                gbc.gridx--;
                gbc.gridy++;
                mainPanel.add(country, gbc);
                gbc.gridx++;
                mainPanel.add(countryInfo, gbc);
                gbc.gridx--;
                gbc.gridy++;
                mainPanel.add(sex, gbc);
                gbc.gridx++;
                mainPanel.add(sexInfo, gbc);
                break;
            case 12:
                // Display recommendations for users.
                title = new JLabel("Recommendations");
                stylize(title, font);
                JLabel genre, popularity, length, release, like, act, dir;
                JList<String> genreInfo, popularityInfo, lengthInfo, releaseInfo, likeInfo, recommended;
                JTextField actor, director;
                JTextArea moreInfo;
                JButton addToFav, recommend;

                JScrollPane scrollGenre, scrollGen, scrollInfo;
                scrollGenre = new JScrollPane();
                scrollGenre.getVerticalScrollBar().setPreferredSize(new Dimension(5,0));
                scrollGenre.getHorizontalScrollBar().setPreferredSize(new Dimension(0,5));
                scrollGenre.setPreferredSize(new Dimension(70,250));
                scrollGen = new JScrollPane();
                scrollGen.getVerticalScrollBar().setPreferredSize(new Dimension(5,0));
                scrollGen.getHorizontalScrollBar().setPreferredSize(new Dimension(0,5));
                scrollGen.setPreferredSize(new Dimension(200,250));
                scrollInfo = new JScrollPane();
                scrollInfo.getVerticalScrollBar().setPreferredSize(new Dimension(5,0));
                scrollInfo.getHorizontalScrollBar().setPreferredSize(new Dimension(0,5));
                scrollInfo.setPreferredSize(new Dimension(400,250));


                // Set multiple filters.
                genre = new JLabel("Genre:");
                stylize(genre, sFont);
                popularity = new JLabel("Popularity:");
                stylize(popularity, sFont);
                length = new JLabel("Running time:");
                stylize(length, sFont);
                release = new JLabel("Release:");
                stylize(release, sFont);
                like = new JLabel("Appreciation:");
                stylize(like, sFont);
                act = new JLabel("Preferred actor:");
                stylize(act, sFont);
                dir = new JLabel("Preferred director:");
                stylize(dir, sFont);
                situation = new JLabel();
                stylize(situation, ssFont);

                genreInfo = new JList<>();
                genreInfo.setBackground(colorBack);
                genreInfo.setForeground(Color.ORANGE);
                genreInfo.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

                popularityInfo = new JList<>();
                popularityInfo.setBackground(colorBack);
                popularityInfo.setForeground(Color.ORANGE);

                lengthInfo = new JList<>();
                lengthInfo.setBackground(colorBack);
                lengthInfo.setForeground(Color.ORANGE);

                releaseInfo = new JList<>();
                releaseInfo.setBackground(colorBack);
                releaseInfo.setForeground(Color.ORANGE);

                likeInfo = new JList<>();
                likeInfo.setBackground(colorBack);
                likeInfo.setForeground(Color.ORANGE);

                recommended = new JList<>();
                recommended.setBackground(colorBack);
                recommended.setForeground(Color.ORANGE);

                addToFav = new JButton("Add to Favorties");
                addToFav.setPreferredSize(buttonDims);
                stylize(addToFav, sFont);

                recommend = new JButton("Get recommendations");
                recommend.setPreferredSize(buttonDims);
                stylize(recommend, sFont);

                review = new JButton("Review");
                review.setPreferredSize(buttonDims);
                stylize(review, sFont);

                actor = new JTextField();
                actor.setBackground(colorBack);
                actor.setForeground(Color.ORANGE);
                actor.setPreferredSize(new Dimension(200, 30));

                director = new JTextField();
                director.setBackground(colorBack);
                director.setForeground(Color.ORANGE);
                director.setPreferredSize(new Dimension(200, 30));

                moreInfo = new JTextArea();
                moreInfo.setBackground(colorBack);
                moreInfo.setForeground(Color.ORANGE);
                moreInfo.setEnabled(false);

                scrollGenre.setViewportView(genreInfo);
                scrollGen.setViewportView(recommended);
                scrollInfo.setViewportView(moreInfo);

                Vector<String> values = new Vector<>();
                for(Genre gen : Genre.values()) {
                    values.add(gen.toString());
                }
                genreInfo.setListData(values);
                values = new Vector<>();
                values.add("Popular");
                values.add("Mildly popular");
                values.add("Less popular");
                popularityInfo.setListData(values);

                values = new Vector<>();
                values.add("Long/Series");
                values.add("Medium");
                values.add("Short");
                lengthInfo.setListData(values);

                values = new Vector<>();
                values.add("Recent");
                values.add("2000's Classics");
                values.add("Old School 1900");
                releaseInfo.setListData(values);

                values = new Vector<>();
                values.add("Very liked");
                values.add("Liked");
                values.add("Mixed");
                likeInfo.setListData(values);

                recommend.addActionListener(e -> generateRecom(genreInfo, popularityInfo, lengthInfo, releaseInfo,
                        likeInfo, recommended, actor, director, obj));
                addToFav.addActionListener(e -> {
                    if(recommended.getSelectedIndex()!=-1) {
                        situation.setText(addtoFav(obj, recommended.getSelectedValue(), currentUser));
                    } else {
                        situation.setText("You haven't selected any recommendations");
                    }
                });
                review.addActionListener(e -> {
                    if(recommended.getSelectedIndex()!=-1) {
                        popularity.setVisible(false);
                        popularityInfo.setVisible(false);
                        length.setVisible(false);
                        lengthInfo.setVisible(false);
                        genre.setVisible(false);
                        scrollGenre.setVisible(false);
                        release.setVisible(false);
                        releaseInfo.setVisible(false);
                        like.setVisible(false);
                        likeInfo.setVisible(false);
                        act.setVisible(false);
                        actor.setVisible(false);
                        dir.setVisible(false);
                        director.setVisible(false);
                        scrollGen.setVisible(false);
                        scrollInfo.setVisible(false);
                        situation.setVisible(false);
                        recommend.setVisible(false);
                        addToFav.setVisible(false);
                        review.setVisible(false);
                        addReview(sFont, mainPanel, addToFav, review, gbc, obj, currentUser,
                                recommended.getSelectedValue());
                    } else {
                        situation.setText("You haven't selected any recommendations");
                    }
                });
                recommended.addListSelectionListener(e -> {
                    if(!e.getValueIsAdjusting()) {
                        String selected = recommended.getSelectedValue();
                        for(Production prod : obj.productions) {
                            if(prod.title.equals(selected)) {
                                moreInfo.setText(displayInfoProd(prod));
                                break;
                            }
                        }
                        for(Actor a : obj.actors) {
                            if(a.name.equals(selected)) {
                                moreInfo.setText(displayInfoActor(a));
                                break;
                            }
                        }
                    }
                });


                gbc.gridy = 1;
                gbc.gridx = 0;
                mainPanel.add(popularity, gbc);
                gbc.gridx++;
                mainPanel.add(popularityInfo, gbc);
                gbc.gridy++;
                gbc.gridx--;
                mainPanel.add(length, gbc);
                gbc.gridx++;
                mainPanel.add(lengthInfo, gbc);
                gbc.gridy++;
                gbc.gridx--;
                mainPanel.add(genre, gbc);
                gbc.gridx++;
                mainPanel.add(scrollGenre, gbc);
                gbc.gridy++;
                gbc.gridx--;
                mainPanel.add(release, gbc);
                gbc.gridx++;
                mainPanel.add(releaseInfo, gbc);
                gbc.gridy++;
                gbc.gridx--;
                mainPanel.add(like, gbc);
                gbc.gridx++;
                mainPanel.add(likeInfo, gbc);
                gbc.gridx = 2;
                gbc.gridy = 1;
                mainPanel.add(act, gbc);
                gbc.gridx++;
                gbc.gridwidth = 2;
                mainPanel.add(actor, gbc);
                gbc.gridy++;
                gbc.gridx--;
                gbc.gridwidth = 1;
                mainPanel.add(dir, gbc);
                gbc.gridx++;
                gbc.gridwidth = 2;
                mainPanel.add(director, gbc);
                gbc.gridy++;
                gbc.gridx--;
                gbc.gridwidth = 1;
                mainPanel.add(scrollGen, gbc);
                gbc.gridx++;
                gbc.gridwidth = 2;
                mainPanel.add(scrollInfo, gbc);
                gbc.gridx--;
                gbc.gridy++;
                gbc.gridwidth = 1;
                mainPanel.add(recommend, gbc);
                gbc.gridx++;
                //If the user is a Regular, let them add a review if they wish to.
                if(currentUser instanceof Regular) {
                    mainPanel.add(addToFav, gbc);
                    gbc.gridx++;
                    mainPanel.add(review, gbc);
                } else {
                    gbc.gridwidth = 2;
                    mainPanel.add(addToFav, gbc);
                }
                gbc.gridy++;
                gbc.gridx = 2;
                gbc.gridwidth = 3;
                mainPanel.add(situation, gbc);
                break;
        }

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        assert title != null;
        mainPanel.add(title, gbc);
        gbc.gridwidth = 1;
        gbc.gridx+=2;
        gbc.fill = 0;
        mainPanel.add(back, gbc);
        setSize(1152,720);
        setLocationRelativeTo(null);
        getContentPane().add(mainPanel);
        setVisible(true);
    }
    void backToMenu(IMDB obj, User<Comparable<Object>> currentUser, int ret) {
        // Go back to the main menu.
        dispose();
        if(ret == 0) {
            new MenuGUI(obj, currentUser);
        } else {
            new WelcomePage(obj, currentUser);
        }
    }
    void generateRecom(JList<String> genreInfo, JList<String> popularityInfo, JList<String> lengthInfo,
                         JList<String> releaseInfo, JList<String> likeInfo, JList<String> recommended,
                         JTextField actor,  JTextField director, IMDB obj) {
        // Generate recommendations using multiple filters.
        int avgRate, length, release, avgNum, numRates, sum, releaseYear;
        int[] OK = new int[7];
        String actorName = actor.getText();
        String directorName = director.getText();
        List<String> genreString = genreInfo.getSelectedValuesList();
        List<Genre> genres = new ArrayList<>();
        List<Genre> genresAux = new ArrayList<>();
        List<Production> productions = new ArrayList<>(obj.productions);
        // Number of reviews.
        avgNum = popularityInfo.getSelectedIndex();
        // Length of movie.
        length = lengthInfo.getSelectedIndex();
        // Release date.
        release = releaseInfo.getSelectedIndex();
        // Average rating.
        avgRate = likeInfo.getSelectedIndex();
        // Genres.
        if(!genreString.isEmpty()) {
            for(String genreSt : genreString) {
                genres.add(Genre.valueOf(genreSt));
            }
        }
        // Filtering productions using the given filters.
        Vector<String> recoStr = new Vector<>();
        for(Production prod : productions) {
            Arrays.fill(OK, 1);
            sum = 0;
            // Check if preferred actor is inside the production's actor list.
            if(!actorName.isEmpty()) {
                for(String act : prod.actors) {
                    if(act.equals(actorName)) {
                        OK[0] = 0;
                        break;
                    }
                }
            } else {
                OK[0] = 0;
            }
            // Check if preferred director is inside the production's directors list.
            if(!directorName.isEmpty()) {
                for(String dir : prod.directors) {
                    if(dir.equals(directorName)) {
                        OK[1] = 0;
                        break;
                    }
                }
            } else {
                OK[1] = 0;
            }
            // Check if selected genres are inside the production's genre list.
            if(!genres.isEmpty()) {
                genresAux.addAll(genres);
                for(Genre gen : prod.genre) {
                    genresAux.removeIf(gen::equals);
                }
                if(genresAux.isEmpty()) {
                    OK[2] = 0;
                }
            } else {
                OK[2] = 0;
            }

            // Check for popularity: look at the number of ratings - numRates and compare them to 3 & 5, depending on
            // which state the filter is set.
            if(avgNum != -1) {
                numRates = 0;
                for(Rating ignored : prod.rating) {
                    numRates++;
                }
                switch(avgNum) {
                    case 0:
                        if(numRates >= 5) {
                            OK[3] = 0;
                        }
                        break;
                    case 1:
                        if((numRates >=3)&&(numRates<5)) {
                            OK[3] = 0;
                        }
                        break;
                    case 2:
                        if(numRates<3) {
                            OK[3] = 0;
                        }
                        break;
                }
            } else {
                OK[3] = 0;
            }

            // Check for running time : look at the duration - length and compare it to 100 & 150, depending on which
            // state the filter is set.
            switch(length) {
                case -1:
                    OK[4] = 0;
                    break;
                case 0:
                    if((prod instanceof Series)||((prod instanceof Movie)&&
                            parseInt((((Movie) prod).duration
                                    .substring(0,((Movie) prod).duration.indexOf(" "))))>=150)) {
                        OK[4] = 0;
                    }
                    break;
                case 1:
                    if(prod instanceof Movie) {
                        int index = ((Movie) prod).duration.indexOf(" ");
                        int duration = parseInt((((Movie) prod).duration.substring(0,index)));
                        if((duration<150)&&(duration>=100)) {
                            OK[4] = 0;
                        }
                    }
                    break;
                case 2:
                    if(prod instanceof Movie) {
                        int index = ((Movie) prod).duration.indexOf(" ");
                        int duration = parseInt((((Movie) prod).duration.substring(0,index)));
                        if(duration<100) {
                            OK[4] = 0;
                        }
                    }
                    break;
            }

            // Look at the release year - releaseYear and compare it to 2000 & 2015, depending on which state the filter
            // is set.
            switch(release) {
                case -1:
                    OK[5] =0;
                    break;
                case 0:
                    if(prod instanceof Movie) {
                        releaseYear = ((Movie) prod).releaseYear;
                    } else {
                        releaseYear = ((Series) prod).releaseYear;
                    }
                    if(releaseYear >= 2015) {
                        OK[5] = 0;
                    }
                    break;
                case 1:
                    if(prod instanceof Movie) {
                        releaseYear = ((Movie) prod).releaseYear;
                    } else {
                        releaseYear = ((Series) prod).releaseYear;
                    }
                    if((releaseYear < 2015)&&(releaseYear >= 2000)) {
                        OK[5] = 0;
                    }
                    break;
                case 2:
                    if(prod instanceof Movie) {
                        releaseYear = ((Movie) prod).releaseYear;
                    } else {
                        releaseYear = ((Series) prod).releaseYear;
                    }
                    if(releaseYear < 2000) {
                        OK[5] = 0;
                    }
                    break;
            }

            // Look at the average rating - avgRate and compare it to 7 & 9, depending on which state the filter is set.
            switch(avgRate) {
                case -1:
                    OK[6] = 0;
                    break;
                case 0:
                    if(prod.averageRating >= 9.0) {
                        OK[6] = 0;
                    }
                    break;
                case 1:
                    if((prod.averageRating < 9.0)&&(prod.averageRating > 7.0)) {
                        OK[6] = 0;
                    }
                    break;
                case 2:
                    if(prod.averageRating < 7.0) {
                        OK[6] = 0;
                    }
                    break;
            }
            for(int i = 0; i<7; i++) {
                sum+=OK[i];
            }
            if(sum == 0) {
                recoStr.add(prod.title);
                recoStr.addAll(prod.actors);
            }
        }
        recommended.setListData(recoStr);
    }
    void addReview(Font sFont, JPanel mainPanel, JButton add, JButton delete, GridBagConstraints gbc, IMDB obj,
                   User<Comparable<Object>> currentUser, String prepared) {
        // Window for adding a review.
        add.setVisible(false);
        delete.setVisible(false);

        JPanel secondPanel = new JPanel(new GridBagLayout());
        secondPanel.setBackground(colorPanel);

        JLabel selected, rating, comment, titleReview, situation;
        titleReview = new JLabel("Review");
        stylize(titleReview, sFont);
        selected = new JLabel("Selected content:");
        stylize(selected, sFont);
        rating = new JLabel("Rating:");
        stylize(rating, sFont);
        comment = new JLabel("Comment:");
        stylize(comment, sFont);
        situation = new JLabel();
        stylize(situation, sFont);

        JButton post = new JButton("Post Review");
        stylize(post, sFont);

        int OK;

        JTextField selectedInfo = new JTextField();
        selectedInfo.setPreferredSize(new Dimension(350, 30));
        selectedInfo.setBackground(colorBack);
        selectedInfo.setForeground(Color.ORANGE);
        JSlider ratingInfo = new JSlider(1,10,5);
        ratingInfo.setMajorTickSpacing(1);
        ratingInfo.setPaintTicks(true);
        ratingInfo.setPaintLabels(true);
        ratingInfo.setBackground(colorBack);
        ratingInfo.setForeground(Color.ORANGE);
        ratingInfo.setPreferredSize(new Dimension(350, 60));
        JTextArea commentInfo = new JTextArea();
        commentInfo.setBackground(colorBack);
        commentInfo.setForeground(Color.ORANGE);

        JScrollPane commentPane, contentPane;
        commentPane = new JScrollPane();
        commentPane.getVerticalScrollBar().setPreferredSize(new Dimension(5,0));
        commentPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0,5));
        commentPane.setPreferredSize(new Dimension(350,250));
        contentPane = new JScrollPane();
        contentPane.getVerticalScrollBar().setPreferredSize(new Dimension(5,0));
        contentPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0,5));
        contentPane.setPreferredSize(new Dimension(350,400));

        JList<String> view = new JList<>();
        view.setBackground(colorBack);
        view.setForeground(Color.ORANGE);

        Vector<String> names= new Vector<>();

        // Show only the titles that haven't been rated by the user.
        for(Production prod : obj.productions) {
            OK = 0;
            for(Pair<String, Rating> rate : currentUser.givenRatings) {
                if((prod.title.equals(rate.getFirst()))&&(rate.getSecond()!=null)) {
                    OK =1;
                    break;
                }
            }
            if(OK == 0) {
                names.add(prod.title);
            }
        }
        for(Actor act : obj.actors) {
            OK = 0;
            for(Pair<String, Rating> rate : currentUser.givenRatings) {
                if((act.name.equals(rate.getFirst()))&&(rate.getSecond()!=null)) {
                    OK =1;
                    break;
                }
            }
            if(OK == 0) {
                names.add(act.name);
            }
        }
        view.setListData(names);

        commentPane.setViewportView(commentInfo);
        contentPane.setViewportView(view);
        view.addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()) {
                selectedInfo.setText(view.getSelectedValue());
            }
        });

        post.addActionListener(e -> situation.setText(addNewReview(selectedInfo.getText(), ratingInfo.getValue(),
                commentInfo.getText(), view, obj, currentUser)));

        if(prepared != null) {
            view.setSelectedValue(prepared ,true);
            selectedInfo.setText(prepared);
        }

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        secondPanel.add(titleReview,gbc);
        gbc.gridwidth = 1;
        gbc.gridy++;
        secondPanel.add(selected, gbc);
        gbc.gridx++;
        secondPanel.add(selectedInfo, gbc);
        gbc.gridy++;
        gbc.gridx--;
        secondPanel.add(rating, gbc);
        gbc.gridx++;
        secondPanel.add(ratingInfo, gbc);
        gbc.gridy++;
        gbc.gridx--;
        secondPanel.add(comment, gbc);
        gbc.gridx++;
        secondPanel.add(commentPane, gbc);
        gbc.gridy++;
        secondPanel.add(post, gbc);
        gbc.gridy++;
        secondPanel.add(situation, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(contentPane, gbc);
        gbc.gridx++;
        mainPanel.add(secondPanel, gbc);
    }
    String addNewReview(String title, int grade, String comment, JList<String> view,
                        IMDB obj, User<Comparable<Object>> currentUser) {
        // Add a new review and send a message to inform the user about the result of the operation.
        if(comment.isEmpty()) {
            return "Add a comment";
        }
        Production prodReview = null;
        Actor actReview = null;
        int OK = 0;
        for(Production prod : obj.productions) {
            if(prod.title.equals(title)) {
                OK = 1;
                prodReview = prod;
                break;
            }
        }
        for(Actor act : obj.actors) {
            if(act.name.equals(title)) {
                OK = 1;
                actReview = act;
                break;
            }
        }
        if(OK == 0) {
            return "Couldn't find the given production/actor";
        }
        int status = 0;
        for(Pair<String, Rating> ratePair : currentUser.givenRatings) {
            if(ratePair.getFirst().equals(title)) {
                if(ratePair.getSecond() == null) {
                    // Review existed.
                    status = 1;
                } else {
                    return "Review already exists.";
                }
                break;
            }
        }
        Rating rate = new Rating();
        rate.username = currentUser.username;
        rate.grade = grade;
        rate.comment = comment;

        // Notify about the review & compute the new average rating.
        if(prodReview != null) {
            obj.notifyReview(rate, prodReview.rating, prodReview.title);
            prodReview.rating.add(rate);
            prodReview.computeAvgRating();
            obj.sortRatings(prodReview);
        } else {
            obj.notifyReview(rate, actReview.rating, actReview.name);
            actReview.rating.add(rate);
            actReview.computeAvgRating();
            obj.sortRatings(actReview);
        }

        rate.notifyObservers("New review has been added for " + title + " by " + currentUser.username + " -> "
                + rate.grade);
        // Add this rating inside the list of user's given ratings.
        if(status == 0) {
            currentUser.givenRatings.add(new Pair<>(title, rate));
        } else {
            for(Pair<String, Rating> givenRate : currentUser.givenRatings) {
                if(givenRate.getFirst().equals(title)) {
                    currentUser.givenRatings.remove(givenRate);
                    currentUser.givenRatings.add(new Pair<>(title,rate));
                    break;
                }
            }
        }

        // Update the list with names that haven't been updated.
        Vector<String> names = new Vector<>();
        for(Production prod : obj.productions) {
            OK = 0;
            for(Pair<String, Rating> rateEx : currentUser.givenRatings) {
                if((prod.title.equals(rateEx.getFirst()))&&(rateEx.getSecond()!=null)) {
                    OK =1;
                    break;
                }
            }
            if(OK == 0) {
                names.add(prod.title);
            }
        }
        for(Actor act : obj.actors) {
            OK = 0;
            for(Pair<String, Rating> rateEx : currentUser.givenRatings) {
                if((act.name.equals(rateEx.getFirst()))&&(rateEx.getSecond()!=null)) {
                    OK =1;
                    break;
                }
            }
            if(OK == 0) {
                names.add(act.name);
            }
        }
        view.setListData(names);
        view.setSelectedIndex(-1);

        if(status == 0) {
            // User just made a review.
            currentUser.expStrat = new addReviewExpStrategy();
            currentUser.exp = currentUser.executeStrategy(currentUser.exp);
        }
        return "Review has been added!";
    }
    void removeReview(Font sFont, JPanel mainPanel, JButton add, JButton delete, GridBagConstraints gbc, IMDB obj,
                   User<Comparable<Object>> currentUser) {
        // Window for removing a review.
        add.setVisible(false);
        delete.setVisible(false);

        JPanel secondPanel = new JPanel(new GridBagLayout());
        secondPanel.setBackground(colorPanel);

        JLabel selected, rating, comment, titleReview, situation;
        titleReview = new JLabel("Review");
        stylize(titleReview, sFont);
        selected = new JLabel("Selected content:");
        stylize(selected, sFont);
        rating = new JLabel("Rating:");
        stylize(rating, sFont);
        comment = new JLabel("Comment:");
        stylize(comment, sFont);
        situation = new JLabel();
        stylize(situation, sFont);

        JButton post = new JButton("Delete Review");
        stylize(post, sFont);

        JTextField selectedInfo = new JTextField();
        selectedInfo.setPreferredSize(new Dimension(350, 30));
        selectedInfo.setBackground(colorBack);
        selectedInfo.setForeground(Color.ORANGE);
        JSlider ratingInfo = new JSlider(1,10,5);
        ratingInfo.setEnabled(false);
        ratingInfo.setMajorTickSpacing(1);
        ratingInfo.setPaintTicks(true);
        ratingInfo.setPaintLabels(true);
        ratingInfo.setBackground(colorBack);
        ratingInfo.setForeground(Color.ORANGE);
        ratingInfo.setPreferredSize(new Dimension(350, 60));
        JTextArea commentInfo = new JTextArea();
        commentInfo.setBackground(colorBack);
        commentInfo.setForeground(Color.ORANGE);
        commentInfo.setEnabled(false);

        JScrollPane commentPane, contentPane;
        commentPane = new JScrollPane();
        commentPane.getVerticalScrollBar().setPreferredSize(new Dimension(5,0));
        commentPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0,5));
        commentPane.setPreferredSize(new Dimension(350,250));
        contentPane = new JScrollPane();
        contentPane.getVerticalScrollBar().setPreferredSize(new Dimension(5,0));
        contentPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0,5));
        contentPane.setPreferredSize(new Dimension(350,400));

        JList<String> view = new JList<>();
        view.setBackground(colorBack);
        view.setForeground(Color.ORANGE);

        Vector<String> names= new Vector<>();

        for(Pair<String,Rating> rate : currentUser.givenRatings) {
            if(rate.getSecond()!=null) {
                names.add(rate.getFirst());
            }
        }
        view.setListData(names);

        commentPane.setViewportView(commentInfo);
        contentPane.setViewportView(view);
        view.addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()) {
                String title = view.getSelectedValue();
                if(title != null) {
                    selectedInfo.setText(title);
                    for(Pair<String,Rating> rate : currentUser.givenRatings) {
                        if(title.equals(rate.getFirst())) {
                            commentInfo.setText(rate.getSecond().comment);
                            ratingInfo.setValue(rate.getSecond().grade);
                            break;
                        }
                    }
                }
            }
        });

        post.addActionListener(e -> situation.setText(deleteReview(selectedInfo, ratingInfo,
                commentInfo, view, obj, currentUser)));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        secondPanel.add(titleReview,gbc);
        gbc.gridwidth = 1;
        gbc.gridy++;
        secondPanel.add(selected, gbc);
        gbc.gridx++;
        secondPanel.add(selectedInfo, gbc);
        gbc.gridy++;
        gbc.gridx--;
        secondPanel.add(rating, gbc);
        gbc.gridx++;
        secondPanel.add(ratingInfo, gbc);
        gbc.gridy++;
        gbc.gridx--;
        secondPanel.add(comment, gbc);
        gbc.gridx++;
        secondPanel.add(commentPane, gbc);
        gbc.gridy++;
        secondPanel.add(post, gbc);
        gbc.gridy++;
        secondPanel.add(situation, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(contentPane, gbc);
        gbc.gridx++;
        mainPanel.add(secondPanel, gbc);
    }
    String deleteReview(JTextField selectedInfo, JSlider ratingInfo, JTextArea commentInfo, JList<String> view,
                        IMDB obj, User<Comparable<Object>> currentUser) {
        // Delete a review and send a message to inform the user about the result of the operation.
        if(view.getSelectedIndex() == -1) {
            return "Please select a review";
        }
        int OK = 0;
        for(Pair<String,Rating> rate : currentUser.givenRatings) {
            if((selectedInfo.getText().equals(rate.getFirst()))&&(rate.getSecond()!=null)) {
                OK = 1;
            }
        }
        if(OK == 0) {
            return "Selected content doesn't have a review";
        }
        int choice = 2;
        for(Production prod : obj.productions) {
            if(prod.title.equals(selectedInfo.getText())) {
                choice = 1;
                break;
            }
        }
        // Delete the rating.
        obj.deleteRating(currentUser, selectedInfo.getText(), choice, 0);
        selectedInfo.setText("");
        ratingInfo.setValue(5);
        commentInfo.setText("");
        Vector<String> names = new Vector<>();
        for(Pair<String,Rating> rate : currentUser.givenRatings) {
            if(rate.getSecond()!=null) {
                names.add(rate.getFirst());
            }
        }
        view.setListData(names);
        view.setSelectedIndex(-1);
        return "Review has been deleted";
    }
    void updateProd(Font sFont, JPanel mainPanel, JButton updateProd, JButton updateActor, GridBagConstraints gbc,
                    IMDB obj, User<Comparable<Object>> currentUser) {
        //Update a movie/series.
        updateActor.setVisible(false);
        updateProd.setVisible(false);

        JButton series, movie;
        series = new JButton("Update Series");
        stylize(series, sFont);
        series.setPreferredSize(new Dimension(200,60));
        movie = new JButton("Update Movie");
        stylize(movie, sFont);
        movie.setPreferredSize(new Dimension(200,60));

        series.addActionListener(e -> newProductionCreation(sFont, mainPanel, gbc, obj, currentUser, series, movie, 2));
        movie.addActionListener(e -> newProductionCreation(sFont, mainPanel, gbc, obj, currentUser, series, movie, 3));

        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(series, gbc);
        gbc.gridx++;
        mainPanel.add(movie, gbc);
        gbc.gridy++;
    }
    void updateActor(Font sFont, JPanel mainPanel, JButton updateProd, JButton updateActor, GridBagConstraints gbc,
                     IMDB obj, User<Comparable<Object>> currentUser) {
        // Update actor.
        updateProd.setVisible(false);
        updateActor.setVisible(false);

        JLabel situation, name, biography, acting;
        JScrollPane actingPane, bioPane, actorsPane;
        JTextField nameInfo, bioInfo;

        JTextArea actingInfo;
        JPanel secondPanel = new JPanel(new GridBagLayout());
        secondPanel.setBackground(colorPanel);

        JList<String> view = new JList<>();
        Vector<String> names = new Vector<>();
        JButton update = new JButton("Update");
        update.setPreferredSize(new Dimension(200,60));
        stylize(update, sFont);

        situation = new JLabel();
        stylize(situation, sFont);
        name = new JLabel("Actor's name:");
        stylize(name, sFont);
        biography = new JLabel("Biography:");
        stylize(biography, sFont);
        acting = new JLabel("Acting:");
        stylize(acting, sFont);

        actingPane = new JScrollPane();
        actingPane.setPreferredSize(new Dimension(250,100));
        actingPane.getVerticalScrollBar().setPreferredSize(new Dimension(5,0));
        actingPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0,5));
        bioPane = new JScrollPane();
        bioPane.setPreferredSize(new Dimension(250, 50));
        bioPane.getVerticalScrollBar().setPreferredSize(new Dimension(5,0));
        bioPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0,5));
        actorsPane = new JScrollPane();
        actorsPane.setPreferredSize(new Dimension(150,300));
        actorsPane.getVerticalScrollBar().setPreferredSize(new Dimension(5,0));
        actorsPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0,5));

        nameInfo = new JTextField();
        nameInfo.setBackground(colorBack);
        nameInfo.setForeground(Color.ORANGE);
        nameInfo.setPreferredSize(new Dimension(250, 30));
        bioInfo = new JTextField();
        bioInfo.setBackground(colorBack);
        bioInfo.setForeground(Color.ORANGE);

        actingInfo = new JTextArea();
        actingInfo.setBackground(colorBack);
        actingInfo.setForeground(Color.ORANGE);

        for(Object o : ((Staff<Comparable<Object>>) currentUser).contributions) {
            if(o instanceof Actor) {
                names.add(((Actor) o).name);
            }
        }
        if(currentUser instanceof Admin) {
            for(Actor a : Admin.globalContributionActors) {
                names.add(a.name);
            }
        }
        view.setListData(names);

        view.setListData(names);
        view.setBackground(colorBack);
        view.setForeground(Color.ORANGE);
        view.addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()) {
                String actor = view.getSelectedValue();
                for(Actor act : obj.actors) {
                    if(act.name.equals(actor)) {
                        nameInfo.setText(act.name);
                        bioInfo.setText(act.biography);
                        actingInfo.setText("");
                        for(Pair<String,String> performance : act.acting) {
                            actingInfo.append(performance.getFirst() + "\n");
                        }
                    }
                }
            }
        });

        update.addActionListener(e -> situation.setText(updateDataActor(nameInfo.getText(), bioInfo.getText(),
                actingInfo.getText(), view, obj, currentUser)));

        bioPane.setViewportView(bioInfo);
        actingPane.setViewportView(actingInfo);
        actorsPane.setViewportView(view);

        gbc.gridx = 0;
        gbc.gridy = 0;
        secondPanel.add(name,gbc);
        gbc.gridx++;
        secondPanel.add(nameInfo, gbc);
        gbc.gridx--;
        gbc.gridy++;
        secondPanel.add(biography, gbc);
        gbc.gridx++;
        secondPanel.add(bioPane, gbc);
        gbc.gridx--;
        gbc.gridy++;
        secondPanel.add(acting, gbc);
        gbc.gridx++;
        secondPanel.add(actingPane, gbc);
        gbc.gridx--;
        gbc.gridy++;
        gbc.gridwidth = 2;
        secondPanel.add(update, gbc);
        gbc.gridy++;
        secondPanel.add(situation, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        mainPanel.add(actorsPane, gbc);
        gbc.gridx++;
        mainPanel.add(secondPanel, gbc);
    }
    String updateDataActor(String name, String bio, String acting, JList<String> view, IMDB obj,
                          User<Comparable<Object>> currentUser) {
        // Updating info about an Actor and send a message to inform the user about the result of the operation.
        BufferedReader buffReader = new BufferedReader(new StringReader(acting));
        if(view.getSelectedIndex() == -1) {
            return "No actor has been selected";
        }
        if(name.isEmpty()) {
            return "Please add a name";
        } else if(bio.isEmpty()) {
            return "Please add a bio";
        } else if(acting.isEmpty()) {
            return "Please add at least one performance";
        }
        Actor toChange = new Actor();
        for(Actor act : obj.actors) {
            if(act.name.equals(view.getSelectedValue())) {
                toChange = act;
                break;
            }
        }
        String actingName;
        Vector<String> names = new Vector<>();
        List<Pair<String,String>> acts = new ArrayList<>();
        int OK = 0, OK1;
        try {
            while((actingName = buffReader.readLine()) != null) {
                OK1 = 0;
                for(Production prod : obj.productions) {
                    if(prod.title.equals(actingName)) {
                        OK1 = 1;
                        OK = 1;
                        prod.actors.add(name);
                        if(prod instanceof Movie) {
                            acts.add(new Pair<>(actingName,"Movie"));
                        } else {
                            acts.add(new Pair<>(actingName,"Series"));
                        }
                        break;
                    }
                }
                if(OK1 == 0) {
                    // Check for old performances that aren't inside the DataBase, but have been set as performances
                    // inside the JSON.
                    for(Pair<String,String> unkPerformance : toChange.acting) {
                        if(unkPerformance.getFirst().equals(actingName)) {
                            acts.add(new Pair<>(actingName, unkPerformance.getSecond()));
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            return "Please add a performance";
        }
        // Remove actor from productions.
        for(Production prod : obj.productions) {
            for(String actorName : prod.actors) {
                if(actorName.equals(toChange.name)) {
                    prod.actors.remove(actorName);
                    break;
                }
            }
        }

        if(OK == 0) {
            return "Performance couldn't be found inside the DataBase";
        }

        // Change the name of the subject inside the given rating into the new name.
        for(User<Comparable<Object>> user : obj.users) {
            for(Pair<String,Rating> rate : user.givenRatings) {
                if(rate.getFirst().equals(toChange.name)) {
                    user.givenRatings.add(new Pair<>(name, rate.getSecond()));
                    user.givenRatings.remove(new Pair<>(toChange.name, rate.getSecond()));
                    break;
                }
            }
        }

        toChange.name = name;
        toChange.biography = bio;
        toChange.acting = acts;

        for(Object o : ((Staff<Comparable<Object>>) currentUser).contributions) {
            if(o instanceof Actor) {
                names.add(((Actor) o).name);
            }
        }
        if(currentUser instanceof Admin) {
            for(Actor a : Admin.globalContributionActors) {
                names.add(a.name);
            }
        }
        view.setListData(names);
        view.setSelectedIndex(-1);
        return "Actor " + name + " has been updated";
    }
    void modifyUser(Font sFont, JPanel mainPanel, JButton add, JButton modify, GridBagConstraints gbc,
                    IMDB obj, User<Comparable<Object>> currentUser) {
        // Modify User Data.
        add.setVisible(false);
        modify.setVisible(false);

        JButton delete = new JButton("Delete user"), modify2 = new JButton("Modify data");
        stylize(delete, sFont);
        stylize(modify2, sFont);
        delete.setPreferredSize(new Dimension(200,60));
        modify2.setPreferredSize(new Dimension(200,60));
        JList<String> userview = new JList<>();
        userview.setBackground(colorPanel);
        userview.setForeground(Color.ORANGE);
        Vector<String> names = new Vector<>();
        JScrollPane scrollView = new JScrollPane() ;
        scrollView.getVerticalScrollBar().setPreferredSize(new Dimension(5,0));
        scrollView.getHorizontalScrollBar().setPreferredSize(new Dimension(0,5));
        scrollView.setPreferredSize(new Dimension(150,400));

        JLabel accType, age, name, country, sex, birthDate, email, userInfo, select;
        JTextField credEmail, firstInfo, countryInfo, birthInfo, emailInfo, ageInfo;

        JPanel secondPanel = new JPanel(new GridBagLayout());
        secondPanel.setBackground(colorPanel);

        JList<String> accTypeInfo = new JList<>();
        JList<String> sexInfo = new JList<>();
        Vector<String> accTypes = new Vector<>();
        Vector<String> genders = new Vector<>();
        accTypes.add("Admin");
        accTypes.add("Contributor");
        accTypes.add("Regular");
        accTypeInfo.setListData(accTypes);
        accTypeInfo.setBackground(colorPanel);
        accTypeInfo.setForeground(Color.ORANGE);
        genders.add("Male");
        genders.add("Female");
        genders.add("Not Specified");
        sexInfo.setListData(genders);
        sexInfo.setBackground(colorPanel);
        sexInfo.setForeground(Color.ORANGE);

        userInfo = new JLabel("User Information");
        stylize(userInfo, sFont);
        accType = new JLabel("Account Type: ");
        stylize(accType, sFont);
        name = new JLabel("Name: ");
        stylize(name, sFont);
        age = new JLabel("Age: ");
        stylize(age, sFont);
        email = new JLabel("Email: ");
        stylize(email, sFont);
        birthDate = new JLabel("Birthdate: ");
        stylize(birthDate, sFont);
        country = new JLabel("Country: ");
        stylize(country, sFont);
        sex = new JLabel("Gender: ");
        stylize(sex, sFont);
        select = new JLabel();
        stylize(select, sFont);

        credEmail = new JTextField();
        stylizeGenerate(credEmail);
        firstInfo = new JTextField();
        stylizeGenerate(firstInfo);
        countryInfo = new JTextField();
        stylizeGenerate(countryInfo);
        birthInfo = new JTextField();
        stylizeGenerate(birthInfo);
        emailInfo = new JTextField();
        stylizeGenerate(emailInfo);
        ageInfo = new JTextField();
        stylizeGenerate(ageInfo);

        for(User<Comparable<Object>> user : obj.users) {
            names.add(user.username);
        }
        userview.setListData(names);
        scrollView.setViewportView(userview);

        userview.addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()) {
                String user = userview.getSelectedValue();
                for(User<Comparable<Object>> userSearch : obj.users) {
                    if(userSearch.username.equals(user)) {
                        firstInfo.setText(userSearch.userInfo.getName());
                        ageInfo.setText(String.valueOf(userSearch.userInfo.getAge()));
                        emailInfo.setText(userSearch.userInfo.getCreds().getEmail());
                        birthInfo.setText(String.valueOf(userSearch.userInfo.getBirth()).substring(0,
                                String.valueOf(userSearch.userInfo.getBirth()).indexOf("T")));
                        countryInfo.setText(userSearch.userInfo.getCountry());
                        if(userSearch instanceof Admin) {
                            accTypeInfo.setSelectedIndex(0);
                        } else if(userSearch instanceof Contributor) {
                            accTypeInfo.setSelectedIndex(1);
                        } else {
                            accTypeInfo.setSelectedIndex(2);
                        }
                        if(userSearch.userInfo.getSex().equals("M")) {
                            sexInfo.setSelectedIndex(0);
                        } else if (userSearch.userInfo.getSex().equals ("F") ) {
                            sexInfo.setSelectedIndex(1);
                        } else {
                            sexInfo.setSelectedIndex(2);
                        }
                        break;
                    }
                }
            }
        });

        accTypeInfo.setEnabled(false);
        delete.addActionListener(e -> select.setText(deleteUser(userview, obj, currentUser)));
        modify2.addActionListener(e -> {
            if((accTypeInfo.getSelectedIndex() == 0)&&(!userview.getSelectedValue().equals(currentUser.username))) {
                select.setText("Can't modify an admin");
            } else {
                select.setText(updateUser(firstInfo.getText(), ageInfo.getText(),
                        emailInfo.getText(), birthInfo.getText(), countryInfo.getText(), sexInfo, userview, obj));
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(scrollView, gbc);
        gbc.gridx++;
        mainPanel.add(secondPanel, gbc);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        secondPanel.add(userInfo, gbc);
        gbc.gridwidth = 1;
        gbc.gridy++;
        secondPanel.add(accType, gbc);
        gbc.gridx++;
        secondPanel.add(accTypeInfo, gbc);
        gbc.gridx--;
        gbc.gridy++;
        secondPanel.add(name, gbc);
        gbc.gridx++;
        secondPanel.add(firstInfo, gbc);
        gbc.gridy++;
        gbc.gridx--;
        secondPanel.add(age, gbc);
        gbc.gridx++;
        secondPanel.add(ageInfo, gbc);
        gbc.gridy++;
        gbc.gridx--;
        secondPanel.add(email, gbc);
        gbc.gridx++;
        secondPanel.add(emailInfo, gbc);
        gbc.gridy++;
        gbc.gridx--;
        secondPanel.add(birthDate, gbc);
        gbc.gridx++;
        secondPanel.add(birthInfo, gbc);
        gbc.gridy++;
        gbc.gridx--;
        secondPanel.add(country, gbc);
        gbc.gridx++;
        secondPanel.add(countryInfo, gbc);
        gbc.gridy++;
        gbc.gridx--;
        secondPanel.add(sex, gbc);
        gbc.gridx++;
        secondPanel.add(sexInfo, gbc);
        gbc.gridy++;
        gbc.gridx--;
        secondPanel.add(delete, gbc);
        gbc.gridx++;
        secondPanel.add(modify2, gbc);
        gbc.gridy++;
        gbc.gridx--;
        gbc.gridwidth = 2;
        secondPanel.add(select, gbc);
    }
    String deleteUser(JList<String> userView, IMDB obj, User<Comparable<Object>> currentUser) {
        // Delete user data and send a message to inform the user about the result of the operation.
        if(userView.getSelectedIndex() == -1) {
            return "You haven't selected any users";
        }
        Vector<String> names = new Vector<>();

        String userview = userView.getSelectedValue();
        for(User<Comparable<Object>> user : obj.users) {
            if(user.username.equals(userview)) {
                if(user instanceof Admin) {
                    return "You can't remove an admin";
                }
                ((Admin<Comparable<Object>>) currentUser).deleteUserCLI(user, obj);
                obj.users.remove(user);
                break;
            }
        }

        for(User<Comparable<Object>> user : obj.users) {
            names.add(user.username);
        }

        userView.setListData(names);
        userView.setSelectedIndex(-1);
        return "User has been successfully deleted";
    }
    String updateUser(String name, String age, String email, String birth, String country,
                      JList<String> gender, JList<String> userView, IMDB obj) {
        // Update user data and send a message to inform the user about the result of the operation.
        if(userView.getSelectedIndex() == -1) {
            return "You haven't selected any users";
        }
        if(name.isEmpty()) {
            return "Please add a name";
        } else if(age.isEmpty()) {
            return "Please add an age";
        }else if(email.isEmpty()) {
            return "Please add an email";
        } else if(birth.isEmpty()) {
            return "Please add a birthDate";
        } else if(country.isEmpty()) {
            return "Please add a country";
        } else if(gender.getSelectedIndex() == -1) {
            return "Please add a gender";
        }
        String regex = "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex);
        if(!pattern.matcher(email).matches()) {
            return "Email doesn't exist. Use a proper email";
        }
        try {
            LocalDate ld = LocalDate.parse(birth, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            return "Birthdate format must be yyyy-MM-dd";
        }
        try {
            parseInt(age);
        } catch (NumberFormatException e) {
            return "Age should be an integer";
        }
        String username = userView.getSelectedValue();

        for(User<Comparable<Object>> user : obj.users) {
            if(user.userInfo.getCreds().getEmail().equals(email)) {
                if(!user.username.equals(username)) {
                    return "Email is already used";
                }
            }
        }

        for(User<Comparable<Object>> user : obj.users) {
            if(username.equals(user.username)) {
                user.userInfo.setName(name);
                user.userInfo.setAge(parseInt(age));
                user.userInfo.getCreds().setEmail(email);
                user.userInfo.setBirth(birth);
                user.userInfo.setCountry(country);
                user.userInfo.setSex(gender.getSelectedValue());
            }
        }
        userView.setSelectedIndex(-1);
        return "User has been updated";
    }
    void addUser(Font sFont, JPanel mainPanel, JButton add, JButton modify, GridBagConstraints gbc,
                 IMDB obj, User<Comparable<Object>> currentUser) {
        // Window for adding a new user.
        JLabel accType, age, credentials, username, password, firstname, lastname, country, sex, birthDate, email,
                userInfo, select;
        JTextField credEmail, credPass, firstInfo, lastInfo, countryInfo, birthInfo, emailInfo, ageInfo,
                credUser;
        JList<String> accTypeInfo = new JList<>();
        JList<String> sexInfo = new JList<>();
        Vector<String> accTypes = new Vector<>();
        Vector<String> genders = new Vector<>();
        accTypes.add("Admin");
        accTypes.add("Contributor");
        accTypes.add("Regular");
        accTypeInfo.setListData(accTypes);
        accTypeInfo.setBackground(colorPanel);
        accTypeInfo.setForeground(Color.ORANGE);
        genders.add("Male");
        genders.add("Female");
        genders.add("Not Specified");
        sexInfo.setListData(genders);
        sexInfo.setBackground(colorPanel);
        sexInfo.setForeground(Color.ORANGE);

        JButton generate;

        add.setVisible(false);
        modify.setVisible(false);

        userInfo = new JLabel("User Information");
        stylize(userInfo, sFont);
        credentials = new JLabel("Generated Credentials");
        stylize(credentials, sFont);
        accType = new JLabel("Account Type: ");
        stylize(accType, sFont);
        username = new JLabel("Username: ");
        stylize(username, sFont);
        password = new JLabel("Password: ");
        stylize(password, sFont);
        firstname = new JLabel("First Name: ");
        stylize(firstname, sFont);
        lastname = new JLabel("Last Name:");
        stylize(lastname, sFont);
        age = new JLabel("Age: ");
        stylize(age, sFont);
        email = new JLabel("Email: ");
        stylize(email, sFont);
        birthDate = new JLabel("Birthdate: ");
        stylize(birthDate, sFont);
        country = new JLabel("Country: ");
        stylize(country, sFont);
        sex = new JLabel("Gender: ");
        stylize(sex, sFont);
        select = new JLabel();
        stylize(select, sFont);

        credEmail = new JTextField();
        stylizeGenerate(credEmail);
        credPass = new JTextField();
        stylizeGenerate(credPass);
        credUser = new JTextField();
        stylizeGenerate(credUser);
        credUser.setEditable(false);
        credPass.setEditable(false);
        firstInfo = new JTextField();
        stylizeGenerate(firstInfo);
        lastInfo = new JTextField();
        stylizeGenerate(lastInfo);
        countryInfo = new JTextField();
        stylizeGenerate(countryInfo);
        birthInfo = new JTextField("yyyy-MM-dd");
        stylizeGenerate(birthInfo);
        emailInfo = new JTextField();
        stylizeGenerate(emailInfo);
        ageInfo = new JTextField();
        stylizeGenerate(ageInfo);

        generate = new JButton("Generate User");
        generate.setPreferredSize(new Dimension(200, 60));
        stylize(generate, sFont);

        generate.addActionListener(e -> select.setText(addUserSystem(firstInfo.getText(), lastInfo.getText(),
                ageInfo.getText(), emailInfo.getText(), birthInfo.getText(), countryInfo.getText(), credUser, credPass,
                accTypeInfo, sexInfo, obj, currentUser)));
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        mainPanel.add(userInfo, gbc);
        gbc.gridx+=2;
        mainPanel.add(credentials, gbc);
        gbc.gridx=0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        mainPanel.add(accType, gbc);
        gbc.gridx++;
        mainPanel.add(accTypeInfo, gbc);
        gbc.gridx--;
        gbc.gridy++;
        mainPanel.add(firstname, gbc);
        gbc.gridx++;
        mainPanel.add(firstInfo, gbc);
        gbc.gridx--;
        gbc.gridy++;
        mainPanel.add(lastname, gbc);
        gbc.gridx++;
        mainPanel.add(lastInfo, gbc);
        gbc.gridx--;
        gbc.gridy++;
        mainPanel.add(age, gbc);
        gbc.gridx++;
        mainPanel.add(ageInfo, gbc);
        gbc.gridx--;
        gbc.gridy++;
        mainPanel.add(email, gbc);
        gbc.gridx++;
        mainPanel.add(emailInfo, gbc);
        gbc.gridx--;
        gbc.gridy++;
        mainPanel.add(birthDate, gbc);
        gbc.gridx++;
        mainPanel.add(birthInfo, gbc);
        gbc.gridx--;
        gbc.gridy++;
        mainPanel.add(country, gbc);
        gbc.gridx++;
        mainPanel.add(countryInfo, gbc);
        gbc.gridx--;
        gbc.gridy++;
        mainPanel.add(sex, gbc);
        gbc.gridx++;
        mainPanel.add(sexInfo, gbc);
        gbc.gridx = 2;
        gbc.gridy = 2;
        mainPanel.add(username, gbc);
        gbc.gridx++;
        mainPanel.add(credUser, gbc);
        gbc.gridx--;
        gbc.gridy++;
        mainPanel.add(password, gbc);
        gbc.gridx++;
        mainPanel.add(credPass, gbc);
        gbc.gridy++;
        gbc.gridx--;
        gbc.gridwidth = 2;
        mainPanel.add(generate, gbc);
        gbc.gridy++;
        mainPanel.add(select, gbc);
        gbc.gridwidth = 1;
    }
    String addUserSystem(String firstname, String lastname, String age, String email, String birth, String country,
                         JTextField username, JTextField password, JList<String> type, JList<String> gender,
                         IMDB obj, User<Comparable<Object>> currentUser) {
        // Add a user inside the system and send a message to inform the user about the result of the operation.
        if(firstname.isEmpty()) {
            return "Please add a firstname";
        } else if(lastname.isEmpty()) {
            return "Please add a lastname";
        } else if(age.isEmpty()) {
            return "Please add an age";
        }else if(email.isEmpty()) {
            return "Please add an email";
        } else if(birth.isEmpty()) {
            return "Please add a birthDate";
        } else if(country.isEmpty()) {
            return "Please add a country";
        } else if(gender.getSelectedIndex() == -1) {
            return "Please add a gender";
        } else if(type.getSelectedIndex() == -1) {
            return "Please select an account type";
        }
        String regex = "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        String selectedGender = gender.getSelectedValue();
        String selectedType = type.getSelectedValue();
        Pattern pattern = Pattern.compile(regex);
        if(!pattern.matcher(email).matches()) {
            return "Email doesn't exist. Use a proper email";
        }
        try {
            LocalDate ld = LocalDate.parse(birth, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            return "Birthdate format must be yyyy-MM-dd";
        }
        for(User<Comparable<Object>> user : obj.users) {
            if(user.userInfo.getCreds().getEmail().equals(email)) {
                return "Email is already used";
            }
        }
        try {
            parseInt(age);
        } catch (NumberFormatException e) {
            return "Age should be an integer";
        }
        User<Comparable<Object>> newUser = UserFactory.factory(AccountType.valueOf(selectedType));
        assert newUser != null;
        newUser.userInfo = new User.Information.InformationBuilder()
                .age(parseInt(age))
                .name(firstname+lastname)
                .sex(selectedGender)
                .birth(birth)
                .country(country)
                .credsEmail(email)
                .credsPassword(((Admin<Comparable<Object>>)currentUser).generatePass())
                .build();
        ((Admin<Comparable<Object>>) currentUser).validateUser(obj.users, newUser, firstname, lastname);
        username.setText(newUser.username);
        password.setText(newUser.userInfo.getCreds().getPassword());
        if(newUser instanceof Admin<Comparable<Object>>) {
            newUser.accType = AccountType.Admin;
            newUser.exp = -1;
        } else {
            if(newUser instanceof Contributor<Comparable<Object>>) {
                newUser.accType = AccountType.Contributor;
            } else {
                newUser.accType = AccountType.Regular;
            }
            newUser.exp = 0;
        }
        obj.users.add(newUser);
        return "User has been added successfully";
    }
    void addSystem(Font sFont, JPanel mainPanel, JButton add, JButton delete, GridBagConstraints gbc, IMDB obj,
                   User<Comparable<Object>> currentUser) {
        // Window for adding content inside the system. The user chooses what to add.
        add.setVisible(false);
        delete.setVisible(false);

        JButton production, actor;
        production = new JButton("New Production");
        stylize(production, sFont);
        production.setPreferredSize(new Dimension(200,60));
        actor = new JButton("New Actor");
        stylize(actor, sFont);
        actor.setPreferredSize(new Dimension(200,60));

        production.addActionListener(e -> newProduction(sFont, mainPanel, gbc, obj, currentUser, actor, production));
        actor.addActionListener(e -> newActor(sFont, mainPanel, gbc, obj, currentUser, actor, production));

        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(production, gbc);
        gbc.gridx++;
        mainPanel.add(actor, gbc);
        gbc.gridy++;
    }
    void newProduction(Font sFont, JPanel mainPanel, GridBagConstraints gbc, IMDB obj,
                       User<Comparable<Object>> currentUser, JButton actor, JButton production) {
        // Creating a new Production. User chooses between adding a series or a movie.
        actor.setVisible(false);
        production.setVisible(false);

        JButton series, movie;
        series = new JButton("New Series");
        stylize(series, sFont);
        series.setPreferredSize(new Dimension(200,60));
        movie = new JButton("New Movie");
        stylize(movie, sFont);
        movie.setPreferredSize(new Dimension(200,60));

        series.addActionListener(e -> newProductionCreation(sFont, mainPanel, gbc, obj, currentUser, series, movie, 0));
        movie.addActionListener(e -> newProductionCreation(sFont, mainPanel, gbc, obj, currentUser, series, movie, 1));

        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(series, gbc);
        gbc.gridx++;
        mainPanel.add(movie, gbc);
        gbc.gridy++;
    }
    void newProductionCreation (Font sFont, JPanel mainPanel, GridBagConstraints gbc, IMDB obj,
                   User<Comparable<Object>> currentUser, JButton series, JButton movie, int product) {
        // Page for creating a new production.
        series.setVisible(false);
        movie.setVisible(false);
        setSize(1550, 720);
        setLocationRelativeTo(null);

        JLabel prodName, directors, actors, genre, plot, duration, releaseYear, situation, epTitle, epDur;
        JScrollPane directorsPane, actorsPane, genrePane, epTitlePane, epDurPane, plotPane;
        JButton post = new JButton("Post");
        stylize(post, sFont);
        JTextField prodInfo, plotInfo, durationInfo, releaseYearInfo;
        JTextArea  dirInfo, actInfo, genInfo, episodeTitleInfo, episodeDurationInfo;

        // These following components are dedicated for updating an existing production.
        JLabel update = new JLabel();
        if(product == 2) {
            update.setText("Select Series:");
        } else {
            update.setText("Select Movie:");
        }
        stylize(update, sFont);
        JScrollPane updatingPane = new JScrollPane();
        updatingPane.setPreferredSize(new Dimension(300,150));
        updatingPane.getVerticalScrollBar().setPreferredSize(new Dimension(5,0));
        updatingPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0,5));
        JList<String> updateValues = new JList<>();
        updateValues.setBackground(colorBack);
        updateValues.setForeground(Color.ORANGE);
        Vector<String> names = new Vector<>();
        if((product == 2) || (product == 3)) {
            post.setText("Update");
        }
        for(Object o : ((Staff<Comparable<Object>>) currentUser).contributions) {
            if((product == 2)&&(o instanceof Series)) {
                names.add(((Series) o).title);
            } else if((product == 3)&&(o instanceof Movie)) {
                names.add(((Movie) o).title);
            }
        }
        if(currentUser instanceof Admin) {
            for(Production prod : Admin.globalContributionProductions) {
                if((product == 2)&&(prod instanceof Series)) {
                    names.add(prod.title);
                } else if((product == 3)&&(prod instanceof Movie)) {
                    names.add(prod.title);
                }
            }
        }
        updateValues.setListData(names);
        updatingPane.setViewportView(updateValues);

        directorsPane = new JScrollPane();
        directorsPane.setPreferredSize(new Dimension(300,150));
        directorsPane.getVerticalScrollBar().setPreferredSize(new Dimension(5,0));
        directorsPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0,5));
        actorsPane = new JScrollPane();
        actorsPane.setPreferredSize(new Dimension(300, 150));
        directorsPane.getVerticalScrollBar().setPreferredSize(new Dimension(5,0));
        directorsPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0,5));
        genrePane = new JScrollPane();
        genrePane.setPreferredSize(new Dimension(300, 150));
        directorsPane.getVerticalScrollBar().setPreferredSize(new Dimension(5,0));
        directorsPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0,5));
        epTitlePane = new JScrollPane();
        epTitlePane.setPreferredSize(new Dimension(300, 150));
        epTitlePane.getVerticalScrollBar().setPreferredSize(new Dimension(5,0));
        epTitlePane.getHorizontalScrollBar().setPreferredSize(new Dimension(0,5));
        epDurPane = new JScrollPane();
        epDurPane.setPreferredSize(new Dimension(300, 150));
        epDurPane.getVerticalScrollBar().setPreferredSize(new Dimension(5,0));
        epDurPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0,5));
        plotPane = new JScrollPane();
        plotPane.setPreferredSize(new Dimension(250,30));
        plotPane.getVerticalScrollBar().setPreferredSize(new Dimension(5,0));
        plotPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0,5));

        prodInfo = new JTextField();
        prodInfo.setBackground(colorBack);
        prodInfo.setForeground(Color.ORANGE);
        prodInfo.setPreferredSize(new Dimension(250, 30));
        episodeTitleInfo = new JTextArea("""
                Please use the keyword "Season" inbetween adding episodes as follows:
                Season
                EpTitle11
                EpTitle12
                EpTitle13
                Season
                EpTitle21
                EpTitle22...
                """);
        episodeTitleInfo.setBackground(colorBack);
        episodeTitleInfo.setForeground(Color.ORANGE);
        episodeDurationInfo = new JTextArea("""
                Write here the duration of the corresponding episode from the "Episode Title" area.
                You can just write the numbers or you can use the format "number minutes".
                """);
        episodeDurationInfo.setBackground(colorBack);
        episodeDurationInfo.setForeground(Color.ORANGE);
        dirInfo = new JTextArea();
        dirInfo.setBackground(colorBack);
        dirInfo.setForeground(Color.ORANGE);
        actInfo = new JTextArea();
        actInfo.setBackground(colorBack);
        actInfo.setForeground(Color.ORANGE);
        genInfo = new JTextArea();
        genInfo.setBackground(colorBack);
        genInfo.setForeground(Color.ORANGE);
        plotInfo = new JTextField();
        plotInfo.setBackground(colorBack);
        plotInfo.setForeground(Color.ORANGE);
        durationInfo = new JTextField();
        durationInfo.setBackground(colorBack);
        durationInfo.setForeground(Color.ORANGE);
        durationInfo.setPreferredSize(new Dimension(250, 30));
        releaseYearInfo= new JTextField();
        releaseYearInfo.setBackground(colorBack);
        releaseYearInfo.setForeground(Color.ORANGE);
        releaseYearInfo.setPreferredSize(new Dimension(250, 30));

        directorsPane.setViewportView(dirInfo);
        actorsPane.setViewportView(actInfo);
        genrePane.setViewportView(genInfo);
        epTitlePane.setViewportView(episodeTitleInfo);
        epDurPane.setViewportView(episodeDurationInfo);
        plotPane.setViewportView(plotInfo);

        situation = new JLabel();
        stylize(situation, sFont);
        prodName = new JLabel("Production Name:");
        stylize(prodName, sFont);
        directors = new JLabel("Directors:");
        stylize(directors, sFont);
        actors = new JLabel("Actors:");
        stylize(actors, sFont);
        genre = new JLabel("Genre:");
        stylize(genre, sFont);
        plot = new JLabel("Plot:");
        stylize(plot, sFont);
        duration = new JLabel("Duration: ");
        stylize(duration, sFont);
        releaseYear = new JLabel("Release Year:");
        stylize(releaseYear, sFont);
        epTitle = new JLabel("Episode Titles:");
        stylize(epTitle, sFont);
        epDur = new JLabel("Episode Duration:");
        stylize(epDur, sFont);

        updateValues.addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()) {
                String prodSt = updateValues.getSelectedValue();
                if (updateValues.getSelectedIndex() != -1) {
                    Production productionSelected;
                    if (product == 2) {
                        productionSelected = new Series();
                    } else {
                        productionSelected = new Movie();
                    }
                    for (Object o : ((Staff<Comparable<Object>>) currentUser).contributions) {
                        if (o instanceof Production aux) {
                            if (prodSt.equals(aux.title)) {
                                productionSelected = aux;
                            }
                        }
                    }
                    if (currentUser instanceof Admin) {
                        for (Production prod : Admin.globalContributionProductions) {
                            productionSelected = prod;
                        }
                    }
                    prodInfo.setText(productionSelected.title);
                    dirInfo.setText("");
                    for (String dir : productionSelected.directors) {
                        dirInfo.append(dir + "\n");
                    }
                    actInfo.setText("");
                    for (String act : productionSelected.actors) {
                        actInfo.append(act + "\n");
                    }
                    genInfo.setText("");
                    for (Genre gen : productionSelected.genre) {
                        genInfo.append(gen.toString() + "\n");
                    }
                    plotInfo.setText(productionSelected.plot);
                    if (productionSelected instanceof Movie mov) {
                        durationInfo.setText(mov.duration.substring(0, mov.duration.indexOf(" ")));
                        releaseYearInfo.setText(String.valueOf(mov.releaseYear));
                    } else if (productionSelected instanceof Series ser) {
                        releaseYearInfo.setText(String.valueOf(ser.releaseYear));
                        episodeTitleInfo.setText("");
                        episodeDurationInfo.setText("");
                        for (Map.Entry<String, List<Episode>> entry : ser.seriesDictionary.entrySet()) {
                            episodeTitleInfo.append("Season\n");
                            for (Episode aux : entry.getValue()) {
                                episodeTitleInfo.append(aux.name + "\n");
                                episodeDurationInfo.append(aux.duration + "\n");
                            }
                        }
                    }
                }
            }
        });

        if(product == 0) {
            post.addActionListener(e -> situation.setText(getDataSeries(prodInfo.getText(), dirInfo.getText(),
                    actInfo.getText(), genInfo.getText(), plotInfo.getText(), releaseYearInfo.getText(),
                    episodeTitleInfo.getText(), episodeDurationInfo.getText() ,obj, currentUser)));
        } else if(product == 1){
            post.addActionListener(e -> situation.setText(getDataMovie(prodInfo.getText(), dirInfo.getText(),
                    actInfo.getText(), genInfo.getText(), plotInfo.getText(), durationInfo.getText(),
                    releaseYearInfo.getText(), obj, currentUser)));
        } else if(product == 2) {
            post.addActionListener(e -> situation.setText(updateDataSeries(prodInfo.getText(), dirInfo.getText(),
                    actInfo.getText(), genInfo.getText(), plotInfo.getText(), releaseYearInfo.getText(),
                    episodeTitleInfo.getText(), episodeDurationInfo.getText(), obj, updateValues, currentUser)));
        } else {
            post.addActionListener(e -> situation.setText(updateDataMovie(prodInfo.getText(), dirInfo.getText(),
                    actInfo.getText(), genInfo.getText(), plotInfo.getText(), durationInfo.getText(),
                    releaseYearInfo.getText(), obj, updateValues, currentUser)));
        }

        // An absolutely atrocious way of designing things...
        gbc.gridx = 0;
        gbc.gridy = 1;
        if((product == 0)||(product == 2)) {
            mainPanel.add(prodName, gbc);
            gbc.gridx++;
            mainPanel.add(prodInfo, gbc);
            gbc.gridx++;
            mainPanel.add(plot, gbc);
            gbc.gridx++;
            mainPanel.add(plotPane, gbc);
            gbc.gridx++;
            mainPanel.add(releaseYear, gbc);
            gbc.gridx++;
            mainPanel.add(releaseYearInfo, gbc);
            gbc.gridx = 0;
            gbc.gridy++;
            mainPanel.add(directors, gbc);
            gbc.gridx++;
            mainPanel.add(directorsPane, gbc);
            gbc.gridx++;
            mainPanel.add(actors, gbc);
            gbc.gridx++;
            mainPanel.add(actorsPane, gbc);
            gbc.gridx++;
            mainPanel.add(genre, gbc);
            gbc.gridx++;
            mainPanel.add(genrePane, gbc);
            gbc.gridx = 0;
            gbc.gridy++;
            mainPanel.add(epTitle, gbc);
            gbc.gridx++;
            mainPanel.add(epTitlePane, gbc);
            gbc.gridx++;
            mainPanel.add(epDur, gbc);
            gbc.gridx++;
            mainPanel.add(epDurPane, gbc);
            if(product == 2) {
                gbc.gridx++;
                mainPanel.add(update, gbc);
                gbc.gridx++;
                mainPanel.add(updatingPane, gbc);
            }
            gbc.gridx = 2;
            gbc.gridy++;
            gbc.gridwidth = 2;
            mainPanel.add(post, gbc);
            gbc.gridy++;
            mainPanel.add(situation, gbc);
            gbc.gridwidth = 1;
        } else if((product == 1)||(product == 3)){
            mainPanel.add(prodName, gbc);
            gbc.gridx++;
            mainPanel.add(prodInfo, gbc);
            gbc.gridx++;
            mainPanel.add(plot, gbc);
            gbc.gridx++;
            mainPanel.add(plotPane, gbc);
            gbc.gridx = 0;
            gbc.gridy++;
            mainPanel.add(duration, gbc);
            gbc.gridx++;
            mainPanel.add(durationInfo, gbc);
            gbc.gridx++;
            mainPanel.add(releaseYear, gbc);
            gbc.gridx++;
            mainPanel.add(releaseYearInfo, gbc);
            gbc.gridx = 0;
            gbc.gridy++;
            mainPanel.add(directors, gbc);
            gbc.gridx++;
            mainPanel.add(directorsPane, gbc);
            gbc.gridx++;
            mainPanel.add(actors, gbc);
            gbc.gridx++;
            mainPanel.add(actorsPane, gbc);
            gbc.gridx = 0;
            gbc.gridy++;
            mainPanel.add(genre, gbc);
            gbc.gridx++;
            mainPanel.add(genrePane, gbc);
            if(product == 3) {
                gbc.gridx++;
                mainPanel.add(update, gbc);
                gbc.gridx++;
                mainPanel.add(updatingPane, gbc);
            }
            gbc.gridx = 2;
            gbc.gridy++;
            gbc.gridwidth = 2;
            mainPanel.add(post, gbc);
            gbc.gridy++;
            mainPanel.add(situation, gbc);
            gbc.gridwidth = 1;
        }
    }
    String updateDataMovie(String title, String directors, String actors, String genre, String plot, String duration,
                                       String releaseYear, IMDB obj, JList<String> name,
                           User<Comparable<Object>> currentUser) {
        // Update info about a movie and send a message to inform the user about the result of the operation.
        if(name.getSelectedIndex() == -1) {
            return "No movie has been selected";
        } else if(title.isEmpty()) {
            return "Please add a title";
        } else if (directors.isEmpty()) {
            return "Add at least one director";
        } else if (actors.isEmpty()) {
            return "Add at least one actor";
        } else if (genre.isEmpty()) {
            return "Add at least one genre";
        }
        int aux, aux2;
        try {
            aux = parseInt(releaseYear);
        } catch (NumberFormatException e) {
            return "Release Year must be an integer";
        }
        try {
            aux2 = parseInt(duration);
        } catch (NumberFormatException e) {
            return "Duration must be an integer";
        }
        BufferedReader buffReaderDir = new BufferedReader(new StringReader(directors));
        BufferedReader buffReaderAct = new BufferedReader(new StringReader(actors));
        BufferedReader buffReaderGen = new BufferedReader(new StringReader(genre));
        String auxString, auxTitle = name.getSelectedValue();

        Movie updateMovie = new Movie();
        List<String> listActors = new ArrayList<>();
        List<String> listDirectors = new ArrayList<>();
        List<Genre> listGenre = new ArrayList<>();
        Vector<String> updateName = new Vector<>();
        int OK;

        // Get the movie from the contributions.
        for(Object o : ((Staff<Comparable<Object>>) currentUser).contributions) {
            if((o instanceof Movie)&&(auxTitle.equals(((Movie) o).title))) {
                updateMovie = (Movie) o;
            }
        }
        if(currentUser instanceof Admin) {
            for(Production prod : Admin.globalContributionProductions) {
                if((prod instanceof Movie)&&(auxTitle.equals(prod.title))) {
                    updateMovie = (Movie) prod;
                }
            }
        }


        try {
            while((auxString = buffReaderDir.readLine()) != null) {
                listDirectors.add(auxString);
            }
            while((auxString = buffReaderGen.readLine()) != null) {
                for(Genre gen : Genre.values()) {
                    if(auxString.toUpperCase().equals(gen.toString())) {
                        listGenre.add(gen);
                        break;
                    }
                }
            }
            while((auxString = buffReaderAct.readLine()) != null) {
                listActors.add(auxString);
                OK = 0;
                for(Actor bufCheck : obj.actors) {
                    if(bufCheck.name.equals(auxString)) {
                        bufCheck.acting.remove(new Pair<>(updateMovie.title,"Movie"));
                        bufCheck.acting.add(new Pair<>(title,"Movie"));
                        OK = 1;
                        break;
                    }
                }
                if(OK == 0) {
                    obj.addUnknownActor(updateMovie, auxString);
                }
            }
        } catch (IOException e) {
            return "Please add an actor/director/genre";
        }

        // Check if actor is still inside the actors list. If he isn't erase this performance from his acts.
        for(Actor act : obj.actors) {
            OK = 0;
            for(Pair<String, String> perf : act.acting) {
                if(perf.getFirst().equals(title)) {
                    for(String actName : listActors) {
                        if(actName.equals(act.name)) {
                            OK = 1;
                            break;
                        }
                    }
                    if(OK == 0) {
                        act.acting.remove(new Pair<>(title, "Movie"));
                    }
                }
            }
        }

        // Change the name of the subject inside the given rating into the new name.
        for(User<Comparable<Object>> user : obj.users) {
            for(Pair<String,Rating> rate : user.givenRatings) {
                if(rate.getFirst().equals(updateMovie.title)) {
                    user.givenRatings.add(new Pair<>(title, rate.getSecond()));
                    user.givenRatings.remove(new Pair<>(updateMovie.title, rate.getSecond()));
                    break;
                }
            }
        }

        updateMovie.title = title;
        updateMovie.plot = plot;
        updateMovie.duration = aux2 + " minutes";
        updateMovie.releaseYear = aux;
        updateMovie.directors = listDirectors;
        updateMovie.actors = listActors;
        updateMovie.genre = listGenre;

        for(Object o : ((Staff<Comparable<Object>>) currentUser).contributions) {
            if(o instanceof Movie) {
                updateName.add(((Movie) o).title);
            }
        }
        if(currentUser instanceof Admin) {
            for(Production prod : Admin.globalContributionProductions) {
                if(prod instanceof Movie) {
                    updateName.add(prod.title);
                }
            }
        }

        name.setListData(updateName);

        return "Movie " + updateMovie.title + " has been updated";
    }
    String getDataMovie(String title, String directors, String actors, String genre, String plot, String duration,
                        String releaseYear, IMDB obj, User<Comparable<Object>> currentUser) {
        // Set info about a movie and send a message to inform the user about the result of the operation.
        BufferedReader buffReaderDir = new BufferedReader(new StringReader(directors));
        BufferedReader buffReaderAct = new BufferedReader(new StringReader(actors));
        BufferedReader buffReaderGen = new BufferedReader(new StringReader(genre));
        String auxString;
        int OK;
        Movie mov = new Movie();
        if(title.isEmpty()) {
            return "Please add a title";
        } else if (directors.isEmpty()) {
            return "Add at least one director";
        } else if (actors.isEmpty()) {
            return "Add at least one actor";
        } else if (genre.isEmpty()) {
            return "Add at least one genre";
        }
        try {
            mov.releaseYear = parseInt(releaseYear);
        } catch (NumberFormatException e) {
            return "Release Year must be an integer";
        }
        try {
            mov.duration = parseInt(duration) + " minutes";
        } catch (NumberFormatException e) {
            return "Duration must be an integer";
        }
        mov.title = title;
        mov.plot = plot;
        mov.averageRating = 0.0;
        try {
            while((auxString = buffReaderDir.readLine()) != null) {
                mov.directors.add(auxString);
            }
            while((auxString = buffReaderGen.readLine()) != null) {
                for(Genre gen : Genre.values()) {
                    if(auxString.toUpperCase().equals(gen.toString())) {
                        mov.genre.add(gen);
                        break;
                    }
                }
            }
            while((auxString = buffReaderAct.readLine()) != null) {
                mov.actors.add(auxString);
                OK = 0;
                for(Actor bufCheck : obj.actors) {
                    if(bufCheck.name.equals(auxString)) {
                        bufCheck.acting.add(new Pair<>(mov.title,"Movie"));
                        OK = 1;
                        break;
                    }
                }
                if(OK == 0) {
                    obj.addUnknownActor(mov, auxString);
                }
            }
        } catch (IOException e) {
            return "Please add an actor/director/genre";
        }
        ((Staff<Comparable<Object>>)currentUser).addProductionSystem(mov);
        obj.productions.add(mov);
        obj.productions.sort(Comparator.comparing(o -> o.title));
        return "Movie " + mov.title + " successfully added";
    }
    String updateDataSeries(String title, String directors, String actors, String genre, String plot, String releaseYear,
                            String epTitle, String epDur, IMDB obj, JList<String> name,
                            User<Comparable<Object>> currentUser) {
        if(name.getSelectedIndex() == -1) {
            return "No series has been selected";
        } else if(title.isEmpty()) {
            return "Please add a title";
        } else if (directors.isEmpty()) {
            return "Add at least one director";
        } else if (actors.isEmpty()) {
            return "Add at least one actor";
        } else if (genre.isEmpty()) {
            return "Add at least one genre";
        }

        int aux;
        try {
            aux = parseInt(releaseYear);
        } catch (NumberFormatException e) {
            return "Release Year must be an integer";
        }
        BufferedReader buffReaderDir = new BufferedReader(new StringReader(directors));
        BufferedReader buffReaderAct = new BufferedReader(new StringReader(actors));
        BufferedReader buffReaderGen = new BufferedReader(new StringReader(genre));
        BufferedReader buffReaderTitle = new BufferedReader(new StringReader(epTitle));
        BufferedReader buffReaderDur = new BufferedReader(new StringReader(epDur));
        String auxString, auxString2, auxTitle = name.getSelectedValue();

        Series updateSer = new Series();
        List<String> listActors = new ArrayList<>();
        List<String> listDirectors = new ArrayList<>();
        List<Genre> listGenre = new ArrayList<>();
        Vector<String> updateName = new Vector<>();
        Map<String, List<Episode>> seriesDictionary = new LinkedHashMap<>();
        int OK, numSeasons = 0;

        for(Object o : ((Staff<Comparable<Object>>) currentUser).contributions) {
            if((o instanceof Series)&&(auxTitle.equals(((Series) o).title))) {
                updateSer = (Series) o;
            }
        }
        if(currentUser instanceof Admin) {
            for(Production prod : Admin.globalContributionProductions) {
                if((prod instanceof Series)&&(auxTitle.equals(prod.title))) {
                    updateSer = (Series) prod;
                }
            }
        }


        try {
            while((auxString = buffReaderDir.readLine()) != null) {
                listDirectors.add(auxString);
            }
            while((auxString = buffReaderGen.readLine()) != null) {
                for(Genre gen : Genre.values()) {
                    if(auxString.toUpperCase().equals(gen.toString())) {
                        listGenre.add(gen);
                        break;
                    }
                }
            }
            auxString = buffReaderTitle.readLine();
            while(auxString != null) {
                if(auxString.equalsIgnoreCase("Season")) {
                    numSeasons++;
                    auxString = buffReaderTitle.readLine();
                    auxString2 = buffReaderDur.readLine();
                    while((auxString2!=null)&&(auxString != null)
                            &&(!(auxString.equalsIgnoreCase("Season")))) {
                        Episode ep = new Episode();
                        ep.name = auxString;
                        if(auxString2.contains(" minutes")) {
                            auxString2 = auxString2.substring(0, auxString2.indexOf(" "));
                        }
                        ep.duration = parseInt(auxString2) + " minutes";
                        seriesDictionary.computeIfAbsent("Season " + numSeasons,
                                k -> new ArrayList<>()).add(ep);
                        auxString = buffReaderTitle.readLine();
                        auxString2 = buffReaderDur.readLine();
                    }
                    if(auxString2 == null) {
                        auxString = null;
                    }
                } else {
                    throw new InvalidCommandException();
                }
            }

            while((auxString = buffReaderAct.readLine()) != null) {
                listActors.add(auxString);
                OK = 0;
                for(Actor bufCheck : obj.actors) {
                    if(bufCheck.name.equals(auxString)) {
                        bufCheck.acting.remove(new Pair<>(updateSer.title,"Series"));
                        bufCheck.acting.add(new Pair<>(title,"Movie"));
                        OK = 1;
                        break;
                    }
                }
                if(OK == 0) {
                    obj.addUnknownActor(updateSer, auxString);
                }
            }
        } catch (IOException | NumberFormatException | InvalidCommandException e) {
            if(e instanceof NumberFormatException) {
                return "Episode duration must of format \"number minutes\" or just number";
            } else if (e instanceof InvalidCommandException) {
                return "Please respect the model given for adding episodes";
            }
            return "Please add an actor/director/genre/episode";
        }

        // Check if actor is still inside the actors list. If he isn't erase this performance from his actings.
        for(Actor act : obj.actors) {
            OK = 0;
            for(Pair<String, String> perf : act.acting) {
                if(perf.getFirst().equals(title)) {
                    for(String actName : listActors) {
                        if(actName.equals(act.name)) {
                            OK = 1;
                            break;
                        }
                    }
                    if(OK == 0) {
                        act.acting.remove(new Pair<>(title, "Movie"));
                    }
                    break;
                }
            }
        }

        // Change the name of the subject inside the given rating into the new name.
        for(User<Comparable<Object>> user : obj.users) {
            for(Pair<String,Rating> rate : user.givenRatings) {
                if(rate.getFirst().equals(updateSer.title)) {
                    user.givenRatings.add(new Pair<>(title, rate.getSecond()));
                    user.givenRatings.remove(new Pair<>(updateSer.title, rate.getSecond()));
                    break;
                }
            }
        }

        updateSer.title = title;
        updateSer.plot = plot;
        updateSer.releaseYear = aux;
        updateSer.directors = listDirectors;
        updateSer.actors = listActors;
        updateSer.genre = listGenre;
        updateSer.numSeasons = numSeasons;
        updateSer.seriesDictionary = seriesDictionary;

        for(Object o : ((Staff<Comparable<Object>>) currentUser).contributions) {
            if(o instanceof Series) {
                updateName.add(((Series) o).title);
            }
        }
        if(currentUser instanceof Admin) {
            for(Production prod : Admin.globalContributionProductions) {
                if(prod instanceof Series) {
                    updateName.add(prod.title);
                }
            }
        }

        name.setListData(updateName);

        return "Series " + updateSer.title + " has been updated";
    }
    String getDataSeries(String title, String directors, String actors, String genre, String plot, String releaseYear,
                         String epTitle, String epDur, IMDB obj, User<Comparable<Object>> currentUser) {
        BufferedReader buffReaderDir = new BufferedReader(new StringReader(directors));
        BufferedReader buffReaderAct = new BufferedReader(new StringReader(actors));
        BufferedReader buffReaderGen = new BufferedReader(new StringReader(genre));
        BufferedReader buffReaderTitle = new BufferedReader(new StringReader(epTitle));
        BufferedReader buffReaderDur = new BufferedReader(new StringReader(epDur));
        String auxString, auxString2;
        int OK;
        Series ser = new Series();
        if(title.isEmpty()) {
            return "Please add a title";
        } else if (directors.isEmpty()) {
            return "Add at least one director";
        } else if (actors.isEmpty()) {
            return "Add at least one actor";
        } else if (genre.isEmpty()) {
            return "Add at least one genre";
        }
        ser.title = title;
        ser.plot = plot;
        ser.averageRating = 0.0;
        ser.numSeasons = 0;

        try {
            ser.releaseYear = parseInt(releaseYear);
        } catch (NumberFormatException e) {
            return "Release Year must be an integer";
        }

        try {
            while((auxString = buffReaderDir.readLine()) != null) {
                ser.directors.add(auxString);
            }
            while((auxString = buffReaderGen.readLine()) != null) {
                for(Genre gen : Genre.values()) {
                    if(auxString.toUpperCase().equals(gen.toString())) {
                        ser.genre.add(gen);
                        break;
                    }
                }
            }
            auxString = buffReaderTitle.readLine();
            while(auxString != null) {
                if(auxString.equalsIgnoreCase("Season")) {
                    ser.numSeasons++;
                    auxString = buffReaderTitle.readLine();
                    auxString2 = buffReaderDur.readLine();
                    while((auxString2!=null)&&(auxString != null)
                            &&(!(auxString.equalsIgnoreCase("Season")))) {
                        Episode ep = new Episode();
                        ep.name = auxString;
                        if(auxString2.contains(" minutes")) {
                            auxString2 = auxString2.substring(0, auxString2.indexOf(" "));
                        }
                        ep.duration = parseInt(auxString2) + " minutes";
                        ser.seriesDictionary.computeIfAbsent("Season " + ser.numSeasons,
                                k -> new ArrayList<>()).add(ep);
                        auxString = buffReaderTitle.readLine();
                        auxString2 = buffReaderDur.readLine();
                    }
                    if(auxString2 == null) {
                        auxString = null;
                    }
                } else {
                    throw new InvalidCommandException();
                }
            }
            while((auxString = buffReaderAct.readLine()) != null) {
                ser.actors.add(auxString);
                OK = 0;
                for(Actor bufCheck : obj.actors) {
                    if(bufCheck.name.equals(auxString)) {
                        bufCheck.acting.add(new Pair<>(ser.title,"Series"));
                        OK = 1;
                        break;
                    }
                }
                if(OK == 0) {
                    obj.addUnknownActor(ser, auxString);
                }
            }
        } catch (IOException | NumberFormatException | InvalidCommandException e) {
            if(e instanceof NumberFormatException) {
                return "Episode duration must of format \"number minutes\" or just number";
            } else if ( e instanceof  InvalidCommandException) {
                return "Please respect the model given for adding episodes";
            }
            return "Please add an actor/director/genre/episode";
        }
        ((Staff<Comparable<Object>>)currentUser).addProductionSystem(ser);
        obj.productions.add(ser);
        obj.productions.sort(Comparator.comparing(o -> o.title));
        return "Series " + ser.title + " successfully added";
    }
    void newActor(Font sFont, JPanel mainPanel, GridBagConstraints gbc, IMDB obj,
                  User<Comparable<Object>> currentUser, JButton actor, JButton production) {
        // Window for adding a new actor.
        actor.setVisible(false);
        production.setVisible(false);

        JScrollPane actingPane, bioPane;
        JLabel name, biography, acting, situation;
        JTextArea nameInfo, bioInfo, actingInfo;
        JButton post = new JButton("Post");
        stylize(post, sFont);

        situation = new JLabel();
        stylize(situation, sFont);
        name = new JLabel("Actor's name:");
        stylize(name, sFont);
        biography = new JLabel("Biography:");
        stylize(biography, sFont);
        acting = new JLabel("Acting:");
        stylize(acting, sFont);

        actingPane = new JScrollPane();
        actingPane.setPreferredSize(new Dimension(250,100));
        bioPane = new JScrollPane();
        bioPane.setPreferredSize(new Dimension(250, 50));
        bioPane.getVerticalScrollBar().setPreferredSize(new Dimension(5,0));
        bioPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0,5));

        nameInfo = new JTextArea();
        nameInfo.setPreferredSize(new Dimension(250, 30));
        nameInfo.setBackground(colorBack);
        nameInfo.setForeground(Color.ORANGE);

        bioInfo = new JTextArea();
        bioInfo.setBackground(colorBack);
        bioInfo.setForeground(Color.ORANGE);

        actingInfo = new JTextArea();
        actingInfo.setBackground(colorBack);
        actingInfo.setForeground(Color.ORANGE);

        actingPane.setViewportView(actingInfo);
        bioPane.setViewportView(bioInfo);

        post.addActionListener(e -> situation.setText(getDataActor(nameInfo.getText(), bioInfo.getText(),
                actingInfo.getText(), obj, currentUser)));

        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(name,gbc);
        gbc.gridx++;
        mainPanel.add(nameInfo, gbc);
        gbc.gridx--;
        gbc.gridy++;
        mainPanel.add(biography, gbc);
        gbc.gridx++;
        mainPanel.add(bioPane, gbc);
        gbc.gridx--;
        gbc.gridy++;
        mainPanel.add(acting, gbc);
        gbc.gridx++;
        mainPanel.add(actingPane, gbc);
        gbc.gridy++;
        mainPanel.add(post, gbc);
        gbc.gridy++;
        mainPanel.add(situation, gbc);
    }
    String getDataActor(String name, String bio, String acting, IMDB obj, User<Comparable<Object>> currentUser) {
        // Create a new actor with the given data and return info to user.
        int OK = 0;
        BufferedReader buffReader = new BufferedReader(new StringReader(acting));
        String actingName;
        Actor act = new Actor();
        if(name.isEmpty()) {
            return "Please add a name";
        } else if(bio.isEmpty()) {
            return "Please add a biography";
        } else if(acting.isEmpty()) {
            return "Please add a performance";
        }
        act.name = name;
        act.biography = bio;
        try {
            while((actingName = buffReader.readLine()) != null) {
                for(Production prod : obj.productions) {
                    if(prod.title.equals(actingName)) {
                        OK = 1;
                        prod.actors.add(act.name);
                        if(prod instanceof Movie) {
                            act.acting.add(new Pair<>(actingName,"Movie"));
                        } else {
                            act.acting.add(new Pair<>(actingName,"Series"));
                        }
                        break;
                    }
                }
            }
        } catch (IOException e) {
            return "Please add a performance";
        }
        if(OK == 0) {
            return "Performance couldn't be found inside the DataBase";
        } else {
            ((Staff<Comparable<Object>>)currentUser).addActorSystem(act);
            obj.actors.add(act);
            obj.actors.sort(Comparator.comparing(o -> o.name));
            return "Actor " + act.name  + " successfully to the DataBase";
        }
    }
    void removeSystem(Font sFont, JPanel mainPanel, JButton add, JButton delete, GridBagConstraints gbc, IMDB obj,
                   User<Comparable<Object>> currentUser) {
        // Remove contributions from system.
        add.setVisible(false);
        delete.setVisible(false);
        JLabel select = new JLabel();
        stylize(select, sFont);
        JTextArea info;

        JList<String> view = new JList<>();
        view.setBackground(colorBack);
        view.setForeground(Color.ORANGE);

        Vector<String> names = new Vector<>();

        JButton remove;
        remove = new JButton("Remove contribution");
        stylize(remove, sFont);
        remove.setPreferredSize(new Dimension(200, 60));

        JScrollPane scrollPaneinfo = new JScrollPane();
        JScrollPane scrollPane = new JScrollPane();
        scrollPaneinfo.getVerticalScrollBar().setPreferredSize(new Dimension(5,0));
        scrollPaneinfo.getHorizontalScrollBar().setPreferredSize(new Dimension(0,5));
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(5,0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0,5));

        info = new JTextArea();
        info.setBackground(colorBack);
        info.setForeground(Color.ORANGE);
        info.setEnabled(false);
        scrollPaneinfo.setViewportView(info);
        scrollPane.setViewportView(view);
        scrollPaneinfo.setPreferredSize(new Dimension(300,350));
        scrollPane.setPreferredSize(new Dimension(250,350));

        if((((Staff<Comparable<Object>>) currentUser).contributions.isEmpty()) &&
                ((currentUser instanceof Contributor<Comparable<Object>>)|| (
                        (Admin.globalContributionProductions.isEmpty())&&(Admin.globalContributionActors.isEmpty())))) {
            info.setText("You don't have any contributions... :(");
        } else {
            for(Object o : ((Staff<Comparable<Object>>) currentUser).contributions) {
                if(o instanceof Production) {
                    names.add(((Production) o).title);
                } else {
                    names.add(((Actor) o).name);
                }
            }
            if(currentUser instanceof Admin) {
                for(Actor a : Admin.globalContributionActors) {
                    names.add(a.name);
                }
                for(Production prod : Admin.globalContributionProductions) {
                    names.add(prod.title);
                }
            }
            view.setListData(names);
        }

        view.addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()) {
                int selected = view.getSelectedIndex() , i = 0;
                for(Object o : ((Staff<Comparable<Object>>) currentUser).contributions) {
                    if(i == selected) {
                        if(o instanceof Production) {
                            info.setText(displayInfoProd((Production) o));
                        } else {
                            info.setText(displayInfoActor((Actor) o));
                        }
                    }
                    i++;
                }
                if(i <= selected) {
                    for(Actor a : Admin.globalContributionActors) {
                        if(i == selected) {
                            info.setText(displayInfoActor(a));
                        }
                        i++;
                    }
                    for(Production prod : Admin.globalContributionProductions) {
                        if(i == selected) {
                            info.setText(displayInfoProd(prod));
                        }
                        i++;
                    }
                }
            }
        });

        remove.addActionListener(e -> select.setText(removeContent(info, view, obj, currentUser)));

        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(scrollPane, gbc);
        gbc.gridx++;
        mainPanel.add(scrollPaneinfo, gbc);
        gbc.gridy++;
        mainPanel.add(remove, gbc);
        gbc.gridx--;
        gbc.gridwidth = 2;
        gbc.gridy++;
        mainPanel.add(select, gbc);
    }
    String removeContent(JTextArea info, JList<String> view, IMDB obj, User<Comparable<Object>> currentUser) {
        // Remove contributions from the Database.
        if(view.getSelectedIndex() == -1) {
            return "You haven't selected any contributions!";
        }

        int i = 0, selected = view.getSelectedIndex();
        for(Object o : ((Staff<Comparable<Object>>) currentUser).contributions) {
            if(i == selected) {
                if (o instanceof Production) {
                    obj.productions.remove(o);
                    ((Staff<Comparable<Object>>) currentUser).removeProductionSystem(((Production) o).title, obj.users);
                } else {
                    obj.actors.remove((Actor) o);
                    ((Staff<Comparable<Object>>) currentUser).removeActorSystem(((Actor) o).name, obj.users);
                }
                break;
            }
            i++;
        }

        if(i <= selected) {
            for(Actor o : Admin.globalContributionActors) {
                if(i == selected) {
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
                    i++;
                    break;
                }
                i++;
            }
            for(Production o : Admin.globalContributionProductions) {
                if(i == selected) {
                    obj.productions.remove(o);
                    Admin.globalContributionProductions.remove(o);
                    break;
                }
                i++;
            }
        }

        Vector<String> names = new Vector<>();

        if((((Staff<Comparable<Object>>) currentUser).contributions.isEmpty()) &&
                ((currentUser instanceof Contributor<Comparable<Object>>)|| (
                        (Admin.globalContributionProductions.isEmpty())&&(Admin.globalContributionActors.isEmpty())))) {
            info.setText("You don't have any contributions... :(");
        } else {
            for(Object o : ((Staff<Comparable<Object>>) currentUser).contributions) {
                if(o instanceof Production) {
                    names.add(((Production) o).title);
                } else {
                    names.add(((Actor) o).name);
                }
            }
            if(currentUser instanceof Admin) {
                for(Actor a : Admin.globalContributionActors) {
                    names.add(a.name);
                }
                for(Production prod : Admin.globalContributionProductions) {
                    names.add(prod.title);
                }
            }
        }
        view.setListData(names);

        return "Contribution has been removed";
    }
    String manageRequest(JTextArea info, JList<String> view, IMDB obj, User<Comparable<Object>> currentUser, int type) {
        // Solve or retract (type indicates the action) a user.
        if(view.getSelectedIndex() == -1) {
            return "You haven't selected any requests!";
        }
        User<Comparable<Object>> auxUser = null;
        Request toSolve = new Request();
        int i = 0, selected = view.getSelectedIndex();
        for(Request r : ((Staff<Comparable<Object>>) currentUser).requests) {
            if(i == selected) {
                ((Staff<Comparable<Object>>) currentUser).requests.remove(r);
                toSolve = r;
                break;
            }
            i++;
        }
        if(i<=selected) {
            for(Request r : Admin.RequestsHolder.requests) {
                if(i == selected) {
                    Admin.RequestsHolder.removeRequest(r);
                    toSolve = r;
                    break;
                }
                i++;
            }
        }

        Vector<String> names = new Vector<>();
        if((((Staff<Comparable<Object>>) currentUser).requests.isEmpty()) &&
                ((currentUser instanceof Contributor<Comparable<Object>>)||(Admin.RequestsHolder.requests.isEmpty()))) {
            info.setText("Hooray! You don't have any requests to solve! :D");
        } else {
            for(Request r : ((Staff<Comparable<Object>>) currentUser).requests) {
                names.add("Subject: " + r.type + " sent at " + r.createdDate);
            }
            if(currentUser instanceof Admin) {
                for(Request r : Admin.RequestsHolder.requests) {
                    names.add("Subject: " + r.type + " sent at " + r.createdDate);
                }
            }
        }
        view.setListData(names);

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
            toSolve.removeObserver(currentUser);
            for (User<Comparable<Object>> user : obj.users) {
                if (user.username.equals(toSolve.userSender)) {
                    auxUser = user;
                    break;
                }
            }
        }
        toSolve.addObserver(auxUser);
        if(type == 0) {
            toSolve.notifyObservers("Request sent at " + toSolve.createdDate + " has been solved.");
            if (auxUser != null) {
                auxUser.expStrat = new createIssueExpStrategy();
                auxUser.exp = auxUser.executeStrategy(auxUser.exp);
            }
            return "Request has been solved";
        } else {
            toSolve.notifyObservers("Request sent at " + toSolve.createdDate + " has been rejected.");
            return "Request has been rejected";
        }
    }
    void removeRequest(Font sFont, JPanel mainPanel, JButton add, JButton delete, GridBagConstraints gbc, IMDB obj,
                       User<Comparable<Object>> currentUser) {
        // Window for removing a request.
        JLabel select = new JLabel();
        stylize(select, sFont);

        JScrollPane scrollPane, scrollInfoPane;
        JTextArea info;
        JButton retract;

        JList<String> view = new JList<>();
        view.setBackground(colorBack);
        view.setForeground(Color.ORANGE);

        Vector<String> names = new Vector<>();
        add.setVisible(false);
        delete.setVisible(false);

        scrollInfoPane = new JScrollPane();
        scrollPane = new JScrollPane();
        scrollInfoPane.getVerticalScrollBar().setPreferredSize(new Dimension(5,0));
        scrollInfoPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0,5));
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(5,0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0,5));

        info = new JTextArea();
        info.setBackground(colorBack);
        info.setForeground(Color.ORANGE);
        info.setEnabled(false);
        scrollInfoPane.setViewportView(info);
        scrollPane.setViewportView(view);
        scrollInfoPane.setPreferredSize(new Dimension(300,350));
        scrollPane.setPreferredSize(new Dimension(250,350));

        if(currentUser.createdRequests.isEmpty()) {
            info.setText("You haven't sent any requests!");
        } else {
            for(Request r : currentUser.createdRequests) {
                names.add("Subject: " + r.type + "sent at " + r.createdDate);
            }
            view.setListData(names);
        }

        view.addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()) {
                int selected = view.getSelectedIndex() , i = 0;
                for(Request r : currentUser.createdRequests) {
                    if(i == selected) {
                        info.setText(r.displayRequest());
                        break;
                    }
                    i++;
                }
            }
        });

        retract = new JButton("Retract");
        stylize(retract, sFont);
        retract.addActionListener(e -> select.setText(retractRequest(info, view, obj, currentUser)));

        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(scrollPane, gbc);
        gbc.gridx++;
        mainPanel.add(scrollInfoPane, gbc);
        gbc.gridy++;
        mainPanel.add(retract, gbc);
        gbc.gridy++;
        mainPanel.add(select, gbc);
    }
    String retractRequest(JTextArea info, JList<String> view, IMDB obj, User<Comparable<Object>> currentUser) {
        // Retract a sent request.
        if(view.getSelectedIndex() == -1) {
            return "You haven't selected any requests!";
        }

        int i = 0;
        for(Request r : currentUser.createdRequests) {
            if(i == view.getSelectedIndex()) {
                obj.removeRequests(r, currentUser);
                break;
            }
            i++;
        }

        Vector<String> names = new Vector<>();
        if(currentUser.createdRequests.isEmpty()) {
            info.setText("You haven't sent any requests!");
        } else {
            for(Request r : currentUser.createdRequests) {
                names.add("Subject: " + r.type + " sent at " + r.createdDate);
            }
        }
        view.setListData(names);

        return "Request retracted successfully!";
    }
    void addNewRequest(Font sFont, JPanel mainPanel, JButton add, JButton delete, GridBagConstraints gbc, IMDB obj,
                       User<Comparable<Object>> currentUser) {
        // Window for adding a new request.
        JButton create;
        JLabel criteria, description, titleName, situation;
        JScrollPane scrollPane, scrollDescription, scrollTitle;
        JList<String> view;
        Vector<String> names;
        JTextArea infoDesc, infoTitle;

        add.setVisible(false);
        delete.setVisible(false);
        criteria = new JLabel("Topic of the request");
        stylize(criteria, sFont);
        description = new JLabel("Description ");
        stylize(description, sFont);
        titleName = new JLabel();
        stylize(titleName, sFont);
        titleName.setVisible(false);
        situation = new JLabel();
        stylize(situation, sFont);

        create = new JButton("Create Request");
        stylize(create, sFont);

        scrollPane = new JScrollPane();
        scrollDescription = new JScrollPane();
        scrollTitle = new JScrollPane();
        scrollTitle.setVisible(false);

        view = new JList<>();
        view.setPreferredSize(new Dimension(150,30));
        view.setBackground(colorBack);
        view.setForeground(Color.ORANGE);
        names = new Vector<>();
        names.add("Actor Issue");
        names.add("Movie Issue");
        names.add("Delete Account");
        names.add("Others");

        infoDesc = new JTextArea("Insert description here..");
        infoDesc.setPreferredSize(new Dimension(300,80));
        infoDesc.setBackground(colorBack);
        infoDesc.setForeground(Color.ORANGE);
        scrollDescription.setViewportView(infoDesc);

        infoTitle = new JTextArea("Insert title/name here..");
        infoTitle.setPreferredSize(new Dimension(300,30));
        infoTitle.setBackground(colorBack);
        infoTitle.setForeground(Color.ORANGE);
        infoTitle.setVisible(false);
        scrollTitle.setViewportView(infoTitle);

        scrollPane.setViewportView(view);
        view.setListData(names);
        view.addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()) {
                String selected = view.getSelectedValue();
                if((selected.equals("Actor Issue"))||(selected.equals("Movie Issue"))) {
                    if(selected.equals("Actor Issue")) {
                        titleName.setText("Name of Actor");
                    } else {
                        titleName.setText("Title of Production");
                    }
                    titleName.setVisible(true);
                    infoTitle.setVisible(true);
                    scrollTitle.setVisible(true);
                } else {
                    titleName.setVisible(false);
                    infoTitle.setVisible(false);
                    scrollTitle.setVisible(false);
                }
            }
        });

        create.addActionListener(e -> situation.setText(createRequest(view.getSelectedValue(),
                infoTitle.getText(), infoDesc.getText(), obj, currentUser)));
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(criteria, gbc);
        gbc.gridx++;
        mainPanel.add(scrollPane, gbc);
        gbc.gridx--;
        gbc.gridy++;
        mainPanel.add(titleName, gbc);
        gbc.gridx++;
        mainPanel.add(scrollTitle, gbc);
        gbc.gridx--;
        gbc.gridy++;
        mainPanel.add(description, gbc);
        gbc.gridx++;
        mainPanel.add(scrollDescription, gbc);
        gbc.gridy++;
        mainPanel.add(create, gbc);
        gbc.gridy++;
        mainPanel.add(situation, gbc);
    }
    String createRequest(String type, String title, String desc, IMDB obj, User<Comparable<Object>> currentUser) {
        // Creating a request with the given info.
        if(desc.equals("Insert description here..")) {
            return "Please fill the description";
        }
        Request req = new Request();
        int OK = 0;
        req.userSender = currentUser.username;
        req.createdDate = LocalDateTime.now();
        req.description = desc;
        if(type == null) {
            return "Please select a request type";
        } else if(type.equals("Actor Issue")) {
            req.type = RequestType.ACTOR_ISSUE;
            req.actorName = title;
            for(Actor act : obj.actors) {
                if(act.name.equals(req.actorName)) {
                    OK = 1;
                    if(currentUser instanceof Contributor) {
                        for(Object o : ((Contributor<Comparable<Object>>) currentUser).contributions) {
                            if ((o instanceof Actor) && (((Actor) o).name).equals(req.actorName)) {
                                OK = 2;
                                break;
                            }
                        }
                    }
                    break;
                }
            }
            if(OK == 0) {
                return "Couldn't find the Actor inside the DataBase";
            } else if(OK == 2) {
                return "Can't create a request for an Actor you added";
            }
        } else if(type.equals("Movie Issue")) {
            req.type = RequestType.MOVIE_ISSUE;
            req.titleProduction = title;
            for(Production prod : obj.productions) {
                if(prod.title.equals(req.titleProduction)) {
                    OK = 1;
                    if(currentUser instanceof Contributor) {
                        for(Object o : ((Contributor<Comparable<Object>>) currentUser).contributions) {
                            if ((o instanceof Production) && (((Production) o).title).equals(req.titleProduction)) {
                                OK = 2;
                                break;
                            }
                        }
                    }
                    break;
                }
            }
            if(OK == 0) {
                return "Couldn't find the Production inside the DataBase";
            } else if(OK == 2) {
                return "Can't create a request for a Production which you added";
            }
        } else if(type.equals("Delete Account")) {
            req.type = RequestType.DELETE_ACCOUNT;
        } else if(type.equals("Others")){
            req.type = RequestType.OTHERS;
        }
        currentUser.createRequest(req, obj.users);
        obj.requests.add(req);
        return "Request successfully issued!";
    }
    String searchContent(IMDB obj, String name) {
        // Display the content searched.
        for(Production prod : obj.productions) {
            if(name.equals(prod.title)) {
                return displayInfoProd(prod);
            }
        }
        for(Actor act : obj.actors) {
            if(name.equals(act.name)) {
                return displayInfoActor(act);
            }
        }
        return null;
    }
    String addtoFav(IMDB obj, String name, User<Comparable<Object>> currentUser) {
        // Add to favorites the given name.
        for(Production prod : obj.productions) {
            if(name.equals(prod.title)) {
                if(currentUser.addToFav(prod)) {
                    return name + " has been successfully added\n in your Favorite List.";
                } else {
                    return name + " is already in your Favorite\n List or couldn't be found.";
                }
            }
        }
        for(Actor act : obj.actors) {
            if(name.equals(act.name)) {
                if(currentUser.addToFav(act)) {
                    return name + " has been successfully added\n in your Favorite List.";
                } else {
                    return name + " is already in your Favorite\n List or couldn't be found.";
                }
            }
        }
        return null;
    }
    String removeFav(IMDB obj, String name, User<Comparable<Object>> currentUser) {
        // Remove from favorites the given name.
        for(Production prod : obj.productions) {
            if(name.equals(prod.title)) {
                if(currentUser.removeFromFav(prod)) {
                    return name + " has been successfully removed from your Favorite List.";
                } else {
                    return name + " isn't in your Favorite List or couldn't be found.";
                }
            }
        }
        for(Actor act : obj.actors) {
            if(name.equals(act.name)) {
                if(currentUser.removeFromFav(act)) {
                    return name + " has been successfully removed from your Favorite List.";
                } else {
                    return name + " isn't in your Favorite List or couldn't be found.";
                }
            }
        }
        return null;
    }
    String displayInfoProd(Production prod) {
        // Display info about production.
        StringBuilder res = new StringBuilder();
        res.append("Title: ").append(prod.title).append("\n");
        res.append("Directors: ");
        res.append(writeLists(prod.directors)).append("\n");
        res.append("Actors: ");
        res.append(writeLists(prod.actors)).append("\n");
        res.append("Genres: ");
        res.append(writeLists(prod.genre)).append("\n");
        res.append("Ratings: ");
        res.append(writeLists(prod.rating)).append("\n");
        res.append("Plot: ");
        res.append(prod.plot).append("\n");
        res.append("Average Rating: ");
        res.append(prod.averageRating.toString()).append("\n");
        res.append("Release Year: ");
        if(prod instanceof Movie) {
            res.append(((Movie) prod).releaseYear).append("\n");
            res.append("Duration: ");
            res.append(((Movie) prod).duration).append("\n");
        } else {
            res.append(((Series) prod).releaseYear).append("\n");
            res.append("Content: ");
            for(Map.Entry<String, List<Episode>> entry :
                    ((Series) prod).seriesDictionary.entrySet()) {
                res.append("===").append(entry.getKey()).append("===").append("\n");
                for(Episode aux : entry.getValue()) {
                    res.append("Episode: ").append(aux.name).append("\n");
                    res.append("Duration: ").append(aux.duration).append("\n");
                }
            }
        }
        return res.toString();
    }
    String displayInfoActor(Actor act) {
        // Display info about actor.
        StringBuilder res = new StringBuilder();
            res.append("Name: ").append(act.name).append("\n");
            res.append("Biography: ").append(act.biography != null ? act.biography : "-").append("\n");
            res.append("Acting:\n");
            for(Pair<String,String> auxAct : act.acting) {
                res.append("Title: ").append(auxAct.getFirst()).append("\nType: ").append(auxAct.getSecond()).append("\n");
            }
            res.append("Average Rating: ");
            res.append(act.averageRating).append("\n");
            res.append("Rating:\n");
            res.append(writeLists(act.rating)).append("\n");
        return res.toString();
    }
}
