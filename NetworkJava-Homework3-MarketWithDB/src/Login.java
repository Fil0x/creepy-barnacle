import sun.rmi.runtime.Log;

import javax.sound.midi.Soundbank;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login extends JFrame{

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;

    public Login(){
        initComponents();
    }

    private void initComponents(){
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setTitle("Login");
        this.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password");
        statusLabel = new JLabel(" ");

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        JButton registerButton = new JButton("Register");
        JButton loginButton = new JButton("Login");
        JButton cancelButton = new JButton("Cancel");

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        mainPanel.add(usernameLabel, c);

        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 2;
        mainPanel.add(usernameField, c);

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        mainPanel.add(passwordLabel, c);

        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 2;
        mainPanel.add(passwordField, c);

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        mainPanel.add(registerButton, c);

        c.gridx = 1;
        c.gridy = 2;
        mainPanel.add(loginButton, c);

        c.gridx = 2;
        c.gridy = 2;
        mainPanel.add(cancelButton, c);

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 3;
        mainPanel.add(statusLabel, c);

        loginButton.addActionListener(new ButtonClickListener());
        cancelButton.addActionListener(new ButtonClickListener());
        registerButton.addActionListener(new ButtonClickListener());

        this.add(mainPanel, BorderLayout.CENTER);
        this.pack();
        this.setLocationRelativeTo(null); // center the window
        this.setVisible(true);
    }

    private class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String choice = e.getActionCommand();

            ClientMediator clientMediator = ClientMediator.getInstance();
            String username = Login.this.usernameField.getText();
            String password = String.valueOf(Login.this.passwordField.getPassword());

            if (choice.equals("Register")){
                if(clientMediator.register(username,password)) {
                    statusLabel.setText("Registered. Now press Login...");
                    System.out.println("(Login) Successfully registered account " + username);
                }
                else {
                    statusLabel.setText("Invalid username/password(8 chars min)");
                }
            } else if (choice.equals("Login")) {
                if(clientMediator.login(username, password)) {
                    System.out.println("LOGGED IN !!");
                    Login.this.setVisible(false);
                    Login.this.dispose();
                }
                else {
                    System.out.println("(Login) Invalid credentials.");
                    statusLabel.setText("Invalid Credentials");
                }
            }
            else if (choice.equals("Cancel")) {
                System.exit(0);
            }
            else {
                throw new IllegalArgumentException("(Login) Invalid choice.");
            }

        }
    }
}
