package onlinequizsystem;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
    private static final long serialVersionUID = 1L;

    public static final String LANDING_PANEL = "LandingPanel";
    public static final String LOGIN_PANEL = "LoginPanel";
    public static final String REGISTER_PANEL = "RegisterPanel";
    public static final String STUDENT_DASHBOARD = "StudentDashboard";
    public static final String INSTRUCTOR_DASHBOARD = "InstructorDashboard";

    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Keep references to dashboards for dynamic updates
    private StudentDashboard studentDashboard;
    private InstructorDashboard instructorDashboard;

    public Main() {
        setTitle("Online Quiz System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1000, 600);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add static panels
        mainPanel.add(new LandingPage(this), LANDING_PANEL);
        mainPanel.add(new LoginPanel(this), LOGIN_PANEL);
        mainPanel.add(new RegisterPanel(this), REGISTER_PANEL);

        add(mainPanel);
        showPanel(LANDING_PANEL); // Show landing first
    }

    // Show panel by name
    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }

    // Open student dashboard after successful login
    public void openStudentDashboard(int studentId, String studentUsername) {
        // If dashboard already exists, remove it first
        if (studentDashboard != null) {
            mainPanel.remove(studentDashboard);
        }
        studentDashboard = new StudentDashboard(this, studentId, studentUsername);
        mainPanel.add(studentDashboard, STUDENT_DASHBOARD);
        showPanel(STUDENT_DASHBOARD);
    }

    // Open instructor dashboard after successful login
    public void openInstructorDashboard(int instructorId, String instructorUsername) {
        // If dashboard already exists, remove it first
        if (instructorDashboard != null) {
            mainPanel.remove(instructorDashboard);
        }
        instructorDashboard = new InstructorDashboard(this, instructorId, instructorUsername);
        mainPanel.add(instructorDashboard, INSTRUCTOR_DASHBOARD);
        showPanel(INSTRUCTOR_DASHBOARD);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main frame = new Main();
            frame.setVisible(true);
        });
    }
}
