package onlinequizsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginPanel extends JPanel {
    private Main main; // Reference to Main
    private JTextField usernameOrEmailField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginPanel(Main main) {
        this.main = main;
        setLayout(new BorderLayout());

        // 🔹 Title
        JLabel titleLabel = new JLabel("Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // 🔹 Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username or Email
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Username or Email:"), gbc);

        gbc.gridx = 1;
        usernameOrEmailField = new JTextField(15);
        formPanel.add(usernameOrEmailField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        formPanel.add(passwordField, gbc);

        // Login Button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginButton = new JButton("Login");
        formPanel.add(loginButton, gbc);

        add(formPanel, BorderLayout.CENTER);

        // 🔹 Action Listener
        loginButton.addActionListener(this::handleLogin);
    }

    /**
     * Handle login button click
     */
    private void handleLogin(ActionEvent e) {
        String usernameOrEmail = usernameOrEmailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (usernameOrEmail.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username/email and password.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            // SQL: allow login with either username or email
            String sql = "SELECT id, username, role FROM users WHERE (username = ? OR email = ?) AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, usernameOrEmail);
            stmt.setString(2, usernameOrEmail);
            stmt.setString(3, password); // Use hashed passwords in production
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("id");
                String userName = rs.getString("username");
                String role = rs.getString("role");

                if ("student".equalsIgnoreCase(role)) {
                    main.openStudentDashboard(userId, userName);
                } else if ("instructor".equalsIgnoreCase(role)) {
                    main.openInstructorDashboard(userId, userName); // Pass ID & username
                } else {
                    JOptionPane.showMessageDialog(this, "Unknown role: " + role);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username/email or password.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Login error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
