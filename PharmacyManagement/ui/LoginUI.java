package ui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginUI() {
        setTitle("Login");
        setLayout(new GridLayout(3, 2));

        // Username field
        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);

        // Password field
        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        // Login button
        loginButton = new JButton("Login");
        add(loginButton);

        // Placeholder for grid alignment
        add(new JLabel());

        // Action listener for login
        loginButton.addActionListener(e -> login());

        setSize(300, 150);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // Validate credentials
        if (validateLogin(username, password)) {
            JOptionPane.showMessageDialog(this, "Login Successful!");
            new MainUI(); // Proceed to your main UI
            this.dispose(); // Close login UI
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials. Please try again.");
        }
    }

    private boolean validateLogin(String username, String password) {
        System.out.println("Trying login with: " + username + " / " + password);

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/pharmacy_db", "root", "#Prince@18")) {

            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, password);

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    System.out.println("User found in database.");
                    return true;
                } else {
                    System.out.println("No matching user in database.");
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to database: " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        new LoginUI();
    }
}
