package onlinequizsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

    // Modern color palette
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 73, 94);
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);
    private static final Color BACKGROUND_COLOR = new Color(247, 249, 252);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color FIELD_BORDER = new Color(189, 195, 199);

    public RegisterPanel(Main mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        // Create main container with proper margins
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setOpaque(false);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        
        // Registration Card
        JPanel registrationCard = createRegistrationCard();
        
        // Footer Panel
        JPanel footerPanel = createFooterPanel();

        mainContainer.add(headerPanel, BorderLayout.NORTH);
        mainContainer.add(registrationCard, BorderLayout.CENTER);
        mainContainer.add(footerPanel, BorderLayout.SOUTH);

        add(mainContainer, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

        // Registration icon
        JLabel iconLabel = new JLabel("👥", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        
        // Title
        JLabel titleLabel = new JLabel("Create Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(TEXT_COLOR);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Join our quiz platform today", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(127, 140, 141));

        JPanel titleContainer = new JPanel();
        titleContainer.setLayout(new BoxLayout(titleContainer, BoxLayout.Y_AXIS));
        titleContainer.setOpaque(false);
        titleContainer.add(iconLabel);
        titleContainer.add(Box.createRigidArea(new Dimension(0, 8)));
        titleContainer.add(titleLabel);
        titleContainer.add(Box.createRigidArea(new Dimension(0, 5)));
        titleContainer.add(subtitleLabel);

        headerPanel.add(titleContainer, BorderLayout.CENTER);
        return headerPanel;
    }

    private JPanel createRegistrationCard() {
        JPanel cardPanel = new JPanel();
        cardPanel.setBackground(CARD_COLOR);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 2, new Color(0, 0, 0, 30)),
                BorderFactory.createMatteBorder(0, 0, 0, 0, new Color(0, 0, 0, 0))
            ),
            BorderFactory.createEmptyBorder(35, 35, 35, 35)
        ));

        cardPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Full Name Field
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 15, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel fullNamePanel = createFieldPanel("👤", "Full Name");
        fullnameField = (JTextField) fullNamePanel.getComponent(1);
        cardPanel.add(fullNamePanel, gbc);

        // Username Field
        gbc.gridy = 1;
        JPanel userPanel = createFieldPanel("🔤", "Username");
        usernameField = (JTextField) userPanel.getComponent(1);
        cardPanel.add(userPanel, gbc);

        // Email Field
        gbc.gridy = 2;
        JPanel emailPanel = createFieldPanel("📧", "Email Address");
        emailField = (JTextField) emailPanel.getComponent(1);
        cardPanel.add(emailPanel, gbc);

        // Password Field
        gbc.gridy = 3;
        JPanel passPanel = createPasswordFieldPanel("🔒", "Password");
        passwordField = (JPasswordField) passPanel.getComponent(1);
        cardPanel.add(passPanel, gbc);

        // Role Selection
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 25, 0);
        JPanel rolePanel = createRolePanel();
        cardPanel.add(rolePanel, gbc);

        // Register Button
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 0, 0);
        JButton registerBtn = createStyledButton("Create Account", ACCENT_COLOR);
        registerBtn.addActionListener(e -> registerUser());
        cardPanel.add(registerBtn, gbc);

        return cardPanel;
    }

    private JPanel createFieldPanel(String icon, String placeholder) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        // Icon label
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        iconLabel.setOpaque(true);
        iconLabel.setBackground(new Color(236, 240, 241));
        
        // Text field
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FIELD_BORDER, 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT_COLOR);
        
        // Add placeholder behavior
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_COLOR);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });

        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(400, 45));
        
        return panel;
    }

    private JPanel createPasswordFieldPanel(String icon, String placeholder) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        // Icon label
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        iconLabel.setOpaque(true);
        iconLabel.setBackground(new Color(236, 240, 241));
        
        // Password field
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FIELD_BORDER, 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT_COLOR);
        field.setEchoChar('●');

        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(400, 45));
        
        return panel;
    }

    private JPanel createRolePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        // Icon label
        JLabel iconLabel = new JLabel("🎭");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        iconLabel.setOpaque(true);
        iconLabel.setBackground(new Color(236, 240, 241));
        
        // Role ComboBox
        roleBox = new JComboBox<>(new String[]{"Student", "Instructor"});
        roleBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FIELD_BORDER, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        roleBox.setBackground(Color.WHITE);
        roleBox.setForeground(TEXT_COLOR);

        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(roleBox, BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(400, 45));
        
        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));

        // Back button
        JButton backButton = createStyledButton("← Back to Home", SECONDARY_COLOR);
        backButton.addActionListener(e -> mainFrame.showPanel(Main.LANDING_PANEL));
        
        // Login link
        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loginPanel.setOpaque(false);
        
        JLabel loginLabel = new JLabel("Already have an account? ");
        loginLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        loginLabel.setForeground(new Color(127, 140, 141));
        
        JLabel loginLink = new JLabel("Sign in here");
        loginLink.setFont(new Font("Segoe UI", Font.BOLD, 12));
        loginLink.setForeground(PRIMARY_COLOR);
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainFrame.showPanel(Main.LOGIN_PANEL);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                loginLink.setForeground(PRIMARY_COLOR.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                loginLink.setForeground(PRIMARY_COLOR);
            }
        });
        
        loginPanel.add(loginLabel);
        loginPanel.add(loginLink);

        footerPanel.add(backButton, BorderLayout.WEST);
        footerPanel.add(loginPanel, BorderLayout.CENTER);

        return footerPanel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(400, 45));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    private void registerUser() {
        String fullName = fullnameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = roleBox.getSelectedItem().toString();

        // Check for placeholder text and empty fields
        if (fullName.equals("Full Name") || fullName.isEmpty() ||
            username.equals("Username") || username.isEmpty() ||
            email.equals("Email Address") || email.isEmpty() ||
            password.isEmpty()) {
            showStyledMessage("All fields are required!", "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Basic email validation
        if (!email.contains("@") || !email.contains(".")) {
            showStyledMessage("Please enter a valid email address!", "Invalid Email", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String query = "INSERT INTO users (full_name, username, email, password, role, instructor_code) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, fullName);
            stmt.setString(2, username);
            stmt.setString(3, email);
            stmt.setString(4, password);
            stmt.setString(5, role);

            String instructorCode = null;

            if (role.equals("Instructor")) {
                instructorCode = generateInstructorCode();
                showStyledMessage("Registration successful!\nYour Instructor Code: " + instructorCode + 
                                "\nPlease save this code for future reference.", 
                                "Registration Complete", JOptionPane.INFORMATION_MESSAGE);
            } else {
                showStyledMessage("Registration successful!\nWelcome to the Quiz System!", 
                                "Registration Complete", JOptionPane.INFORMATION_MESSAGE);
            }

            stmt.setString(6, instructorCode);
            stmt.executeUpdate();

            mainFrame.showPanel(Main.LANDING_PANEL);
        } catch (Exception ex) {
            ex.printStackTrace();
            showStyledMessage("Registration failed: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String generateInstructorCode() {
        Random rand = new Random();
        int code = 100000 + rand.nextInt(900000); 
        return String.valueOf(code);
    }

    private void showStyledMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}
