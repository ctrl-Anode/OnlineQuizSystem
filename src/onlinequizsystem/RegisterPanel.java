package onlinequizsystem;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Random;

public class RegisterPanel extends JPanel {
	private static final long serialVersionUID = 1L;

    private Main mainFrame;
    private JTextField fullnameField;
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;

    public RegisterPanel(Main mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("User Registration", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        formPanel.add(new JLabel("Fullname:"));
        fullnameField = new JTextField();
        formPanel.add(fullnameField);

        formPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        formPanel.add(usernameField);
        
                JLabel label = new JLabel("Email:");
                formPanel.add(label);
        emailField = new JTextField();
        formPanel.add(emailField);

        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        formPanel.add(new JLabel("Role:"));
        roleBox = new JComboBox<>(new String[]{"Student", "Instructor"});
        formPanel.add(roleBox);

        JButton registerBtn = new JButton("Register");
        JButton backBtn = new JButton("Back");

        formPanel.add(registerBtn);
        formPanel.add(backBtn);

        add(formPanel, BorderLayout.CENTER);

        // Button Actions
        registerBtn.addActionListener(e -> registerUser());
        backBtn.addActionListener(e -> mainFrame.showPanel(Main.LANDING_PANEL));
    }

    private void registerUser() {
        String full_name = fullnameField.getText();
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String role = roleBox.getSelectedItem().toString();

        if (full_name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String query = "INSERT INTO users (full_name, username, email, password, role, instructor_code) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, full_name);
            stmt.setString(2, username);
            stmt.setString(3, email);
            stmt.setString(4, password);
            stmt.setString(5, role);

            String instructorCode = null;

            if (role.equals("Instructor")) {
                instructorCode = generateInstructorCode();
                JOptionPane.showMessageDialog(this, "Your Instructor Code: " + instructorCode);
            }

            stmt.setString(6, instructorCode);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Registration successful!");
            mainFrame.showPanel(Main.LANDING_PANEL);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private String generateInstructorCode() {
        Random rand = new Random();
        int code = 100000 + rand.nextInt(900000); 
        return String.valueOf(code);
    }
}
