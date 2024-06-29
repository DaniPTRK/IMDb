package org.example;

import javax.swing.*;
import java.awt.*;

public class MenuGUI extends JFrame {
    JPanel mainPanel, panel, profilePanel;
    public void stylize(JButton button, Font font) {
        button.setBackground(new Color(64, 49, 10));
        button.setForeground(Color.ORANGE);
        button.setFont(font);
        button.setHorizontalAlignment(SwingConstants.CENTER);
    }
    public void stylize(JLabel label, Font font) {
        label.setForeground(Color.ORANGE);
        label.setFont(font);
        label.setHorizontalAlignment(SwingConstants.CENTER);
    }
    public MenuGUI(IMDB obj, User<Comparable<Object>> currentUser) {
        //GUI for the main menu.
        super("IMDB Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024,720);
        Font font = new Font(Font.SANS_SERIF,Font.PLAIN,32);
        Font sFont = new Font(Font.SANS_SERIF,Font.PLAIN,16);

        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(new Color(57, 25, 0));
        GridBagConstraints gbc = new GridBagConstraints();

        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(57, 25, 0));

        profilePanel = new JPanel();
        profilePanel.setLayout(new GridBagLayout());
        profilePanel.setBackground(new Color(57, 25, 0));


        JLabel profile = new JLabel("= Profile =");
        stylize(profile, sFont);
        JLabel name = new JLabel("Username: " + currentUser.username);
        stylize(name, sFont);
        JLabel type = new JLabel("Role: " + currentUser.accType);
        stylize(type, sFont);
        JLabel exp = new JLabel("Experience: " + (currentUser.exp > 0 ? currentUser.exp : "-" ));
        stylize(exp, sFont);
        JLabel welcome = new JLabel("Main Menu");
        stylize(welcome, font);

        // Buttons for each action.
        JButton info = new JButton("See your information");
        stylize(info, sFont);
        JButton production = new JButton("See all productions");
        stylize(production, sFont);
        JButton actor = new JButton("See all actors");
        stylize(actor, sFont);
        JButton notif = new JButton("View all notifications");
        stylize(notif, sFont);
        JButton search = new JButton("Search for production/actor");
        stylize(search, sFont);
        JButton favManager = new JButton("View favorites");
        stylize(favManager, sFont);
        JButton createReq = new JButton("Create/Retract a request");
        stylize(createReq, sFont);
        JButton addSystem = new JButton("Add/Delete a production/actor from system");
        stylize(addSystem, sFont);
        JButton requests = new JButton("Solve a request");
        stylize(requests, sFont);
        JButton updateSystem = new JButton("Update Production/Actor details");
        stylize(updateSystem, sFont);
        JButton addReview = new JButton("Add/Delete a review from a production/actor");
        stylize(addReview, sFont);
        JButton addUser = new JButton("Add/Delete a user from system");
        stylize(addUser, sFont);
        JButton backTo = new JButton("Back to Main Page");
        stylize(backTo, sFont);

        ImageIcon logoIcon = new ImageIcon("./POO-TEMA-2023-INPUT/imdb1_0.png");
        JLabel logoLabel = new JLabel(logoIcon);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0,0,10,0);
        mainPanel.add(profilePanel, gbc);
        gbc.gridx++;
        mainPanel.add(welcome, gbc);
        gbc.gridx++;
        mainPanel.add(logoLabel);
        gbc.gridx--;
        gbc.gridy++;
        mainPanel.add(panel, gbc);


        gbc.gridx = 0;
        gbc.gridy = 0;

        profilePanel.add(profile, gbc);
        gbc.gridy++;
        profilePanel.add(name, gbc);
        gbc.gridy++;
        profilePanel.add(type, gbc);
        gbc.gridy++;
        profilePanel.add(exp, gbc);

        gbc.gridx = 0;
        gbc.gridy = 0;

        panel.add(info, gbc);
        gbc.gridy++;
        panel.add(production, gbc);
        gbc.gridy++;
        panel.add(actor, gbc);
        gbc.gridy++;
        panel.add(notif,gbc);
        gbc.gridy++;
        panel.add(search, gbc);
        gbc.gridy++;
        panel.add(favManager, gbc);
        gbc.gridy++;
        if(!(currentUser instanceof Admin)) {
            panel.add(createReq, gbc);
            gbc.gridy++;
        }
        if(!(currentUser instanceof Regular)) {
            panel.add(addSystem, gbc);
            gbc.gridy++;
            panel.add(requests, gbc);
            gbc.gridy++;
            panel.add(updateSystem, gbc);
        } else {
            panel.add(addReview, gbc);
        }
        gbc.gridy++;
        if(currentUser instanceof Admin) {
            panel.add(addUser, gbc);
            gbc.gridy++;
        }
        panel.add(backTo, gbc);
        gbc.gridy++;

        production.addActionListener(e -> Action(obj, currentUser, 0));
        actor.addActionListener(e -> Action(obj, currentUser, 1));
        notif.addActionListener(e -> Action(obj, currentUser, 2));
        search.addActionListener(e -> Action(obj, currentUser, 3));
        favManager.addActionListener(e -> Action(obj, currentUser, 4));
        createReq.addActionListener(e -> Action(obj, currentUser, 5));
        addSystem.addActionListener(e -> Action(obj, currentUser, 6));
        requests.addActionListener(e -> Action(obj, currentUser, 7));
        updateSystem.addActionListener(e -> Action(obj, currentUser, 8));
        addReview.addActionListener(e -> Action(obj, currentUser, 9));
        addUser.addActionListener(e -> Action(obj, currentUser, 10));
        info.addActionListener(e -> Action(obj, currentUser, 11));
        backTo.addActionListener(e -> backTo(obj, currentUser));

        setLocationRelativeTo(null);
        getContentPane().add(mainPanel);
        setVisible(true);
    }
    void Action(IMDB obj, User<Comparable<Object>> user, int sit) {
        // Execute the action given. Ret indicates that, if the user presses "exit", he will be sent back to this menu.
        dispose();
        new ActionGUI(obj, user, sit, 0);
    }
    void backTo(IMDB obj, User<Comparable<Object>> user) {
        // Return to the welcome page.
        dispose();
        new WelcomePage(obj, user);
    }
}
