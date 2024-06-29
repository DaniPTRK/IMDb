package org.example;

import javax.swing.*;
import java.awt.*;

public class AuthGUI extends JFrame {
    JTextField email;
    JTextField password;
    JLabel creds;
    JPanel panel;
    public AuthGUI(IMDB obj) {
        // Authentication process & design.
        super("Authentication Page IMDB");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800,540);

        GridBagConstraints gbc = new GridBagConstraints();

        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(57, 25, 0));

        Font font = new Font(Font.SANS_SERIF,Font.PLAIN,32);
        Font sFont = new Font(Font.SANS_SERIF,Font.PLAIN,16);

        ImageIcon logoIcon = new ImageIcon("./POO-TEMA-2023-INPUT/imdb1_0.png");
        JLabel logoLabel = new JLabel(logoIcon);
        JLabel logoText = new JLabel("Authentication");
        logoText.setForeground(Color.ORANGE);
        logoText.setFont(font);
        logoText.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel emailLabel = new JLabel("Email: ");
        emailLabel.setFont(sFont);
        emailLabel.setForeground(Color.ORANGE);

        emailLabel.setHorizontalAlignment(SwingConstants.CENTER);
        email = new JTextField();
        email.setBackground(new Color(64, 49, 10));
        email.setForeground(Color.ORANGE);
        email.setFont(sFont);
        email.setHorizontalAlignment(SwingConstants.CENTER);
        email.setPreferredSize(new Dimension(300,100));

        JLabel passwordLabel = new JLabel("Password: ");
        passwordLabel.setFont(sFont);
        passwordLabel.setForeground(Color.ORANGE);

        password = new JTextField();
        password.setBackground(new Color(64, 49, 10));
        password.setForeground(Color.ORANGE);
        password.setPreferredSize(new Dimension(300,100));
        password.setHorizontalAlignment(SwingConstants.CENTER);
        password.setFont(sFont);

        JButton auth = new JButton("Login");
        auth.setBackground(new Color(140, 105, 14));
        auth.setForeground(Color.ORANGE);
        auth.setFont(sFont);
        auth.setPreferredSize(new Dimension(250, 80));
        auth.addActionListener(e -> authenticate(obj));

        creds = new JLabel("Credentials wrong! Please, enter your credentials.");
        creds.setForeground(Color.ORANGE);
        creds.setFont(sFont);
        creds.setHorizontalAlignment(SwingConstants.CENTER);
        creds.setVisible(false);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0,20,20,0);
        panel.add(logoLabel, gbc);
        gbc.gridx++;
        panel.add(logoText, gbc);
        gbc.gridx--;
        gbc.gridy++;
        panel.add(emailLabel, gbc);
        gbc.gridx++;
        panel.add(email, gbc);
        gbc.gridy++;
        gbc.gridx--;
        panel.add(passwordLabel, gbc);
        gbc.gridx++;
        panel.add(password, gbc);
        gbc.gridy++;
        gbc.gridx--;
        panel.add(creds, gbc);
        gbc.gridx++;
        panel.add(auth, gbc);
        setLocationRelativeTo(null);
        getContentPane().add(panel);
        setVisible(true);
    }
    public void authenticate(IMDB obj) {
        int OK = 0;
        // Search for the user inside the database.
        User<Comparable<Object>> currentUser;
        String emailString = email.getText();
        String passString = password.getText();
        for(User<Comparable<Object>> user : obj.users) {
            if((user.userInfo.getCreds().getEmail().equals(emailString))&&(user.userInfo.getCreds().getPassword().
                    equals(passString))) {
                OK = 1;
                currentUser = user;
                dispose();
                new WelcomePage(obj, currentUser);
                break;
            }
        }
        if(OK == 0) {
            creds.setVisible(true);
        }
    }
}
