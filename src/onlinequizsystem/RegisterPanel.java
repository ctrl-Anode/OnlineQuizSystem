package onlinequizsystem;

import javax.swing.JPanel;
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
        setPreferredSize(new Dimension(904, 531));
        setLayout(null); 
        setBackground(new Color(245, 222, 179));

        JLabel lblFullname = new JLabel("Full Name:");
        lblFullname.setFont(new Font("Arial", Font.BOLD, 12));
        lblFullname.setBounds(289, 180, 79, 25);
        add(lblFullname);

        fullnameField = new JTextField(20);
        fullnameField.setFont(new Font("Arial", Font.PLAIN, 12));
        fullnameField.setBounds(368, 180, 200, 25);
        add(fullnameField);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(new Font("Arial", Font.BOLD, 12));
        lblUsername.setBounds(289, 220, 79, 25);
        add(lblUsername);

        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 12));
        usernameField.setBounds(368, 220, 200, 25);
        add(usernameField);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(new Font("Arial", Font.BOLD, 12));
        lblEmail.setBounds(289, 260, 79, 25);
        add(lblEmail);

        emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 12));
        emailField.setBounds(368, 260, 200, 25);
        add(emailField);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Arial", Font.BOLD, 12));
        lblPassword.setBounds(289, 300, 79, 25);
        add(lblPassword);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 12));
        passwordField.setBounds(368, 300, 200, 25);
        add(passwordField);

        JLabel lblRole = new JLabel("Role:");
        lblRole.setFont(new Font("Arial", Font.BOLD, 12));
        lblRole.setBounds(289, 340, 79, 25);
        add(lblRole);

        roleBox = new JComboBox<>(new String[]{"Student", "Instructor"});
        roleBox.setFont(new Font("Arial", Font.PLAIN, 12));
        roleBox.setBounds(368, 340, 200, 25);
        add(roleBox);

        JButton registerBtn = new JButton("Sign Up");
        registerBtn.setFont(new Font("Arial", Font.BOLD, 12));
        registerBtn.setBounds(400, 390, 100, 30);
        add(registerBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Arial", Font.PLAIN, 9));
        backBtn.setBounds(817, 10, 57, 25);
        add(backBtn);
        
        JLabel titleLabel_1 = new JLabel("Online Quiz System", SwingConstants.CENTER);
        titleLabel_1.setFont(new Font("Baskerville Old Face", Font.BOLD, 30));
        titleLabel_1.setBounds(139, 23, 600, 40);
        add(titleLabel_1);
        
        JLabel lblSignUp = new JLabel("Sign Up", SwingConstants.CENTER);
        lblSignUp.setFont(new Font("Arial", Font.BOLD, 25));
        lblSignUp.setBounds(139, 113, 600, 30);
        add(lblSignUp);
        
        JLabel footer = new JLabel("BCRV 2025 Online Quiz System", SwingConstants.CENTER);
        footer.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 12));
        footer.setBounds(247, 444, 400, 30);
        add(footer);

        registerBtn.addActionListener(e -> registerUser());
        backBtn.addActionListener(e -> mainFrame.showPanel(Main.LANDING_PANEL));
    }

    private void registerUser() {
        String full_name = fullnameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String roleText = (String) roleBox.getSelectedItem();
        String role = "student";
        if ("Instructor".equalsIgnoreCase(roleText)) role = "instructor";

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
            if ("instructor".equals(role)) {
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
