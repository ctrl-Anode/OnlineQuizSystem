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
        landingPanel.setBackground(new Color(245, 245, 250));

        // Title
        JLabel title = new JLabel("Welcome to Online Quiz System", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(44, 62, 80));
        title.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10));
        landingPanel.add(title, BorderLayout.NORTH);

        // Center panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        JLabel subtitle = new JLabel("Test your knowledge and track your progress", JLabel.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(new Color(100, 100, 100));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(subtitle);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 40))); // spacing

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 40, 20));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 80, 10, 80));

        JButton loginBtn = createStyledButton("Login", new Color(52, 152, 219));
        JButton registerBtn = createStyledButton("Register", new Color(46, 204, 113));

        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);

        centerPanel.add(buttonPanel);

        landingPanel.add(centerPanel, BorderLayout.CENTER);

        // Footer
        JLabel footer = new JLabel("2025 Online Quiz System", JLabel.CENTER);
        footer.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        footer.setForeground(new Color(120, 120, 120));
        footer.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        landingPanel.add(footer, BorderLayout.SOUTH);

        // Actions
        loginBtn.addActionListener(e -> showPanel(LOGIN_PANEL));
        registerBtn.addActionListener(e -> showPanel(REGISTER_PANEL));

        return landingPanel;
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
