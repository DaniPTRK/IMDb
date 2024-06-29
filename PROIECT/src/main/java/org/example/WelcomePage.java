package org.example;

import javax.swing.*;
import java.awt.*;

public class WelcomePage extends JFrame {
    JPanel panel;
    GridBagConstraints gbc = new GridBagConstraints();
    Color colorBack = new Color(64, 49, 10);
    Color colorPanel = new Color(57, 25, 0);
    public void stylize(JButton button, Font font) {
        button.setBackground(colorBack);
        button.setForeground(Color.ORANGE);
        button.setFont(font);
        button.setHorizontalAlignment(SwingConstants.CENTER);
    }
    public void stylize(JLabel label, Font font) {
        label.setForeground(Color.ORANGE);
        label.setFont(font);
        label.setHorizontalAlignment(SwingConstants.CENTER);
    }
    public WelcomePage(IMDB obj, User<Comparable<Object>> currentUser) {
        // Welcome page process & design.
        super("IMDB Welcome Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024,600);
        Font font = new Font(Font.SANS_SERIF,Font.PLAIN,24);
        Font sFont = new Font(Font.SANS_SERIF,Font.PLAIN,16);

        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(colorPanel);

        JLabel welcome = new JLabel("Welcome back, " + currentUser.username + "!");
        stylize(welcome, font);

        ImageIcon logoIcon = new ImageIcon("./POO-TEMA-2023-INPUT/imdb1_0.png");
        JLabel logoLabel = new JLabel(logoIcon);

        JButton menu = new JButton("Main Menu");
        stylize(menu, sFont);
        menu.setPreferredSize(new Dimension(200, 60));
        menu.addActionListener(e -> goGUI(obj, currentUser));

        JButton search = new JButton("Search content");
        stylize(search, sFont);
        search.setPreferredSize(new Dimension(200,60));
        search.addActionListener(e -> Action(obj, currentUser, 3));

        JButton actButton = new JButton("View actors");
        stylize(actButton, sFont);
        actButton.setPreferredSize(new Dimension(200,60));
        actButton.addActionListener(e -> Action(obj, currentUser, 1));

        JButton logout = new JButton("Logout");
        stylize(logout, sFont);
        logout.setPreferredSize(new Dimension(200,60));
        logout.addActionListener(e -> logout(obj));

        JButton recommendations = new JButton("Recommendations");
        stylize(recommendations, sFont);
        recommendations.setPreferredSize(new Dimension(200,60));
        recommendations.addActionListener(e -> recommendPage(obj, currentUser));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0,20,20,0);
        gbc.gridwidth = 4;
        panel.add(welcome, gbc);
        gbc.gridwidth = 2;
        gbc.gridx++;
        gbc.gridy++;
        panel.add(menu, gbc);
        gbc.gridx+=1;
        panel.add(search, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 1;
        gbc.gridy++;
        panel.add(actButton, gbc);
        gbc.gridx++;
        panel.add(logoLabel, gbc);
        gbc.gridx++;
        panel.add(recommendations, gbc);
        gbc.gridy++;
        gbc.gridx--;
        panel.add(logout, gbc);

        setLocationRelativeTo(null);
        getContentPane().add(panel);
        setVisible(true);
    }

    void recommendPage(IMDB obj, User<Comparable<Object>> currentUser) {
        // Send the user to the recommendations page.
        dispose();
        new ActionGUI(obj, currentUser, 12, 1);
    }
    void goGUI(IMDB obj, User<Comparable<Object>> currentUser) {
        // Send the user to his main menu.
        dispose();
        new MenuGUI(obj, currentUser);
    }
    void Action(IMDB obj, User<Comparable<Object>> user, int sit) {
        // Show all the actors inside the DataBase.
        dispose();
        new ActionGUI(obj, user, sit,1);
    }
    void logout(IMDB obj) {
        // Send the user back to authentication.
        dispose();
        new AuthGUI(obj);
    }
}
