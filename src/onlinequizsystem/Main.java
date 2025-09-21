package onlinequizsystem;

import java.awt.EventQueue;
import java.awt.*;
import javax.swing.*;

public class Main extends JFrame {

    private static final long serialVersionUID = 1L;

    public static final String LANDING_PANEL = "LandingPanel";
    public static final String LOGIN_PANEL = "LoginPanel";
    public static final String REGISTER_PANEL = "RegisterPanel";
    public static final String STUDENT_DASHBOARD = "StudentDashboard";
    public static final String INSTRUCTOR_DASHBOARD = "InstructorDashboard";

    private CardLayout cardLayout;
    private JPanel contentPane;  
    private JPanel mainPanel;

    private StudentDashboard studentDashboard;
    private InstructorDashboard instructorDashboard;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Main frame = new Main();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Create the frame.
     */
    public Main() {
        setTitle("Online Quiz System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1000, 600);

        contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add LandingPage directly inside Main.java
        mainPanel.add(createLandingPage(), LANDING_PANEL);
        mainPanel.add(new LoginPanel(this), LOGIN_PANEL);
        mainPanel.add(new RegisterPanel(this), REGISTER_PANEL);

        contentPane.add(mainPanel, BorderLayout.CENTER);

        showPanel(LANDING_PANEL); 
    }

    /**
     * Show a specific panel in the card layout
     */
    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }

    /**
     * Open Student Dashboard
     */
    public void openStudentDashboard(int studentId, String studentUsername) {
        if (studentDashboard != null) {
            mainPanel.remove(studentDashboard);
        }
        studentDashboard = new StudentDashboard(this, studentId, studentUsername);
        mainPanel.add(studentDashboard, STUDENT_DASHBOARD);
        showPanel(STUDENT_DASHBOARD);
    }

    /**
     * Open Instructor Dashboard
     */
    public void openInstructorDashboard(int instructorId, String instructorUsername) {
        if (instructorDashboard != null) {
            mainPanel.remove(instructorDashboard);
        }
        instructorDashboard = new InstructorDashboard(this, instructorId, instructorUsername);
        mainPanel.add(instructorDashboard, INSTRUCTOR_DASHBOARD);
        showPanel(INSTRUCTOR_DASHBOARD);
    }

    /**
     * Landing Page Panel (merged here instead of a separate class)
     */
    private JPanel createLandingPage() {
        JPanel landingPanel = new JPanel(new BorderLayout());
        landingPanel.setBackground(new Color(247, 249, 252));

        // Title Section
        JPanel titleSection = new JPanel();
        titleSection.setLayout(new BoxLayout(titleSection, BoxLayout.Y_AXIS));
        titleSection.setOpaque(false);
        titleSection.setBorder(BorderFactory.createEmptyBorder(50, 30, 30, 30));

        // App Icon
        JLabel iconLabel = new JLabel("🎓", JLabel.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Main Title
        JLabel title = new JLabel("Online Quiz System", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(new Color(44, 62, 80));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtitle
        JLabel subtitle = new JLabel("Test your knowledge and track your progress", JLabel.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(new Color(127, 140, 141));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        titleSection.add(iconLabel);
        titleSection.add(Box.createRigidArea(new Dimension(0, 20)));
        titleSection.add(title);
        titleSection.add(Box.createRigidArea(new Dimension(0, 10)));
        titleSection.add(subtitle);

        landingPanel.add(titleSection, BorderLayout.NORTH);

        // Center panel with buttons
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 100, 80, 100));

        // Features section
        JPanel featuresPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        featuresPanel.setOpaque(false);
        featuresPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));

        // Student features
        JPanel studentFeatures = createFeatureCard("👨‍🎓", "For Students", 
            new String[]{"Join instructor classes", "Take interactive quizzes", "View detailed results", "Track your progress"});
        
        // Instructor features  
        JPanel instructorFeatures = createFeatureCard("👨‍🏫", "For Instructors", 
            new String[]{"Create and manage quizzes", "Monitor student progress", "View detailed analytics", "Manage student enrollment"});

        featuresPanel.add(studentFeatures);
        featuresPanel.add(instructorFeatures);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 0, 50));

        JButton loginBtn = createModernLandingButton("🔐 Sign In", new Color(41, 128, 185), "Access your account");
        JButton registerBtn = createModernLandingButton("👥 Create Account", new Color(46, 204, 113), "Join our platform");

        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);

        centerPanel.add(featuresPanel);
        centerPanel.add(buttonPanel);

        landingPanel.add(centerPanel, BorderLayout.CENTER);

        // Footer
        JLabel footer = new JLabel("© 2025 Online Quiz System - Empowering Education", JLabel.CENTER);
        footer.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        footer.setForeground(new Color(149, 165, 166));
        footer.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        landingPanel.add(footer, BorderLayout.SOUTH);

        // Actions
        loginBtn.addActionListener(e -> showPanel(LOGIN_PANEL));
        registerBtn.addActionListener(e -> showPanel(REGISTER_PANEL));

        return landingPanel;
    }

    private JPanel createFeatureCard(String icon, String title, String[] features) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 221, 225), 1),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        // Header
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        
        JLabel iconLabel = new JLabel(icon, JLabel.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(iconLabel);
        header.add(Box.createRigidArea(new Dimension(0, 10)));
        header.add(titleLabel);

        // Features list
        JPanel featuresList = new JPanel();
        featuresList.setLayout(new BoxLayout(featuresList, BoxLayout.Y_AXIS));
        featuresList.setOpaque(false);
        featuresList.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        for (String feature : features) {
            JLabel featureLabel = new JLabel("✓ " + feature);
            featureLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            featureLabel.setForeground(new Color(127, 140, 141));
            featureLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            featuresList.add(featureLabel);
            featuresList.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        card.add(header, BorderLayout.NORTH);
        card.add(featuresList, BorderLayout.CENTER);

        return card;
    }

    private JButton createModernLandingButton(String text, Color bgColor, String tooltip) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText(tooltip);
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    /**
     * Helper for styled buttons
     */
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createLineBorder(bgColor.darker(), 2));
        button.setContentAreaFilled(true);
        return button;
    }
}
