package onlinequizsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private Main main; // Reference to Main
    private JTextField usernameOrEmailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton backButton;

    // Modern color palette
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 73, 94);
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);
    private static final Color BACKGROUND_COLOR = new Color(247, 249, 252);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color FIELD_BORDER = new Color(189, 195, 199);

    public LoginPanel(Main main) {
        this.main = main;
        setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        // Create main container with proper margins
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setOpaque(false);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // 🔹 Header Panel with icon and title
        JPanel headerPanel = createHeaderPanel();
        
        // 🔹 Login Card Panel
        JPanel loginCard = createLoginCard();
        
        // 🔹 Footer Panel with back button
        JPanel footerPanel = createFooterPanel();

        mainContainer.add(headerPanel, BorderLayout.NORTH);
        mainContainer.add(loginCard, BorderLayout.CENTER);
        mainContainer.add(footerPanel, BorderLayout.SOUTH);

        add(mainContainer, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        // Login icon
        JLabel iconLabel = new JLabel("🔐", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        
        // Title
        JLabel titleLabel = new JLabel("Welcome Back", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_COLOR);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Please sign in to your account", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(127, 140, 141));

        JPanel titleContainer = new JPanel();
        titleContainer.setLayout(new BoxLayout(titleContainer, BoxLayout.Y_AXIS));
        titleContainer.setOpaque(false);
        titleContainer.add(iconLabel);
        titleContainer.add(Box.createRigidArea(new Dimension(0, 10)));
        titleContainer.add(titleLabel);
        titleContainer.add(Box.createRigidArea(new Dimension(0, 5)));
        titleContainer.add(subtitleLabel);

        headerPanel.add(titleContainer, BorderLayout.CENTER);
        return headerPanel;
    }

    private JPanel createLoginCard() {
        JPanel cardPanel = new JPanel();
        cardPanel.setBackground(CARD_COLOR);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 221, 225), 1),
            BorderFactory.createEmptyBorder(40, 40, 40, 40)
        ));
        
        // Add subtle shadow effect
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 2, new Color(0, 0, 0, 30)),
                BorderFactory.createMatteBorder(0, 0, 0, 0, new Color(0, 0, 0, 0))
            ),
            BorderFactory.createEmptyBorder(40, 40, 40, 40)
        ));

        cardPanel.setLayout(new GridBagLayout());

        cardPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Username or Email Field with icon
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel userPanel = createFieldPanel("👤", "Username or Email");
        usernameOrEmailField = (JTextField) userPanel.getComponent(1);
        cardPanel.add(userPanel, gbc);

        // Password Field with icon
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 30, 0);
        JPanel passPanel = createPasswordFieldPanel("🔒", "Password");
        passwordField = (JPasswordField) passPanel.getComponent(1);
        cardPanel.add(passPanel, gbc);

        // Login Button
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        loginButton = createStyledButton("Sign In", PRIMARY_COLOR);
        loginButton.addActionListener(this::handleLogin);
        cardPanel.add(loginButton, gbc);

        return cardPanel;
    }

    private JPanel createFieldPanel(String icon, String placeholder) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        // Icon label with proper sizing
        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        iconLabel.setOpaque(true);
        iconLabel.setBackground(new Color(236, 240, 241));
        iconLabel.setPreferredSize(new Dimension(45, 45));
        iconLabel.setMinimumSize(new Dimension(45, 45));
        iconLabel.setMaximumSize(new Dimension(45, 45));
        
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
        panel.setPreferredSize(new Dimension(350, 45));
        panel.setMaximumSize(new Dimension(350, 45));
        
        return panel;
    }

    private JPanel createPasswordFieldPanel(String icon, String placeholder) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        // Icon label with proper sizing
        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        iconLabel.setOpaque(true);
        iconLabel.setBackground(new Color(236, 240, 241));
        iconLabel.setPreferredSize(new Dimension(45, 45));
        iconLabel.setMinimumSize(new Dimension(45, 45));
        iconLabel.setMaximumSize(new Dimension(45, 45));
        
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
        panel.setPreferredSize(new Dimension(350, 45));
        panel.setMaximumSize(new Dimension(350, 45));
        
        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        // Back button
        backButton = createStyledButton("← Back to Home", SECONDARY_COLOR);
        backButton.addActionListener(e -> main.showPanel(Main.LANDING_PANEL));
        
        // Register link
        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        registerPanel.setOpaque(false);
        
        JLabel registerLabel = new JLabel("Don't have an account? ");
        registerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        registerLabel.setForeground(new Color(127, 140, 141));
        
        JLabel registerLink = new JLabel("Sign up here");
        registerLink.setFont(new Font("Segoe UI", Font.BOLD, 12));
        registerLink.setForeground(PRIMARY_COLOR);
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                main.showPanel(Main.REGISTER_PANEL);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                registerLink.setForeground(PRIMARY_COLOR.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                registerLink.setForeground(PRIMARY_COLOR);
            }
        });
        
        registerPanel.add(registerLabel);
        registerPanel.add(registerLink);

        footerPanel.add(backButton, BorderLayout.WEST);
        footerPanel.add(registerPanel, BorderLayout.CENTER);

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
        button.setPreferredSize(new Dimension(350, 45));
        
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

    /**
     * Handle login button click
     */
    private void handleLogin(ActionEvent e) {
        String usernameOrEmail = usernameOrEmailField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Check for placeholder text
        if (usernameOrEmail.equals("Username or Email") || usernameOrEmail.isEmpty() || password.isEmpty()) {
            showStyledMessage("Please enter both username/email and password.", "Login Error", JOptionPane.ERROR_MESSAGE);
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
                    showStyledMessage("Unknown role: " + role, "Login Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                showStyledMessage("Invalid username/email or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            showStyledMessage("Login error: " + ex.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void showStyledMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}
