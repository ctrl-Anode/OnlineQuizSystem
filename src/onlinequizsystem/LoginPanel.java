package onlinequizsystem;

import javax.swing.JPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.awt.event.ActionListener;

public class LoginPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private Main main; 
    private JTextField usernameOrEmailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton backButton;

    public LoginPanel(Main main) {
    	setPreferredSize(new Dimension(904, 531));
        this.main = main;
        setLayout(null); 
        setBackground(new Color(245, 222, 179));

        JLabel titleLabel = new JLabel("Online Quiz System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Baskerville Old Face", Font.BOLD, 30));
        titleLabel.setBounds(139, 23, 600, 40);
        add(titleLabel);

        JLabel lblSignIn = new JLabel("Sign In", SwingConstants.CENTER);
        lblSignIn.setFont(new Font("Arial", Font.BOLD, 25));
        lblSignIn.setBounds(139, 113, 600, 30);
        add(lblSignIn);

        JLabel label = new JLabel("Username or Email:");
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setBounds(250, 252, 150, 25);
        add(label);

        usernameOrEmailField = new JTextField(15);
        usernameOrEmailField.setFont(new Font("Arial", Font.PLAIN, 12));
        usernameOrEmailField.setBounds(400, 252, 200, 25);
        add(usernameOrEmailField);

        JLabel label_1 = new JLabel("Password:");
        label_1.setFont(new Font("Arial", Font.BOLD, 12));
        label_1.setBounds(250, 295, 150, 25);
        add(label_1);

        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 12));
        passwordField.setBounds(400, 295, 200, 25);
        add(passwordField);

        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 12));
        loginButton.setBounds(400, 355, 100, 30);
        add(loginButton);

        backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 9));
        backButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
        });
        backButton.setBounds(817, 10, 57, 25);
        add(backButton);

        JLabel footer = new JLabel("BCRV 2025 Online Quiz System", SwingConstants.CENTER);
        footer.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 12));
        footer.setBounds(247, 444, 400, 30);
        add(footer);

        loginButton.addActionListener(this::handleLogin);
        backButton.addActionListener(e -> main.showPanel(Main.LANDING_PANEL));
    }

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
            stmt.setString(3, password); 
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
