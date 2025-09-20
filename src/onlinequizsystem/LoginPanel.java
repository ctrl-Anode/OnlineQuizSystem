package onlinequizsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginPanel extends JPanel {
    private static final long serialVersionUID = 1L;
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

        // Username or Email Label
        GridBagConstraints gbcLabelUser = new GridBagConstraints();
        gbcLabelUser.gridx = 0;
        gbcLabelUser.gridy = 0;
        gbcLabelUser.insets = new Insets(10, 10, 10, 10);
        gbcLabelUser.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Username or Email:"), gbcLabelUser);

        // Username or Email Field
        GridBagConstraints gbcFieldUser = new GridBagConstraints();
        gbcFieldUser.gridx = 1;
        gbcFieldUser.gridy = 0;
        gbcFieldUser.insets = new Insets(10, 10, 10, 10);
        gbcFieldUser.fill = GridBagConstraints.HORIZONTAL;
        usernameOrEmailField = new JTextField(15);
        formPanel.add(usernameOrEmailField, gbcFieldUser);

        // Password Label
        GridBagConstraints gbcLabelPass = new GridBagConstraints();
        gbcLabelPass.gridx = 0;
        gbcLabelPass.gridy = 1;
        gbcLabelPass.insets = new Insets(10, 10, 10, 10);
        gbcLabelPass.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Password:"), gbcLabelPass);

        // Password Field
        GridBagConstraints gbcFieldPass = new GridBagConstraints();
        gbcFieldPass.gridx = 1;
        gbcFieldPass.gridy = 1;
        gbcFieldPass.insets = new Insets(10, 10, 10, 10);
        gbcFieldPass.fill = GridBagConstraints.HORIZONTAL;
        passwordField = new JPasswordField(15);
        formPanel.add(passwordField, gbcFieldPass);

        // Login Button
        GridBagConstraints gbcButton = new GridBagConstraints();
        gbcButton.gridx = 0;
        gbcButton.gridy = 2;
        gbcButton.gridwidth = 2;
        gbcButton.insets = new Insets(15, 10, 10, 10);
        gbcButton.anchor = GridBagConstraints.CENTER;
        loginButton = new JButton("Login");
        formPanel.add(loginButton, gbcButton);

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
                    main.openInstructorDashboard(userId, userName);
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
