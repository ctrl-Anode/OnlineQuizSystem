package onlinequizsystem;

import java.awt.EventQueue;
import java.awt.*;
import javax.swing.*;

//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;

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
    	setPreferredSize(new Dimension(904, 531));
    	setBackground(Color.LIGHT_GRAY);
        setTitle("Online Quiz System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 904, 531);

        contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createLandingPage(), LANDING_PANEL);
        mainPanel.add(new LoginPanel(this), LOGIN_PANEL);
        mainPanel.add(new RegisterPanel(this), REGISTER_PANEL);

        contentPane.add(mainPanel, BorderLayout.CENTER);

        showPanel(LANDING_PANEL); 
    }

    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }

    public void openStudentDashboard(int studentId, String studentUsername) {
        if (studentDashboard != null) {
            mainPanel.remove(studentDashboard);
        }
        studentDashboard = new StudentDashboard(this, studentId, studentUsername);
        mainPanel.add(studentDashboard, STUDENT_DASHBOARD);
        showPanel(STUDENT_DASHBOARD);
    }

    public void openInstructorDashboard(int instructorId, String instructorUsername) {
        if (instructorDashboard != null) {
            mainPanel.remove(instructorDashboard);
        }
        instructorDashboard = new InstructorDashboard(this, instructorId, instructorUsername);
        mainPanel.add(instructorDashboard, INSTRUCTOR_DASHBOARD);
        showPanel(INSTRUCTOR_DASHBOARD);
    }

    private JPanel createLandingPage() {
        JPanel landingPanel = new JPanel();
        landingPanel.setLayout(null); 
        landingPanel.setBackground(new Color(245, 222, 179));

        
        JLabel title = new JLabel("Welcome to Online Quiz System", JLabel.CENTER);
        title.setFont(new Font("Baskerville Old Face", Font.BOLD, 30));
        title.setBounds(139, 23, 600, 40); 
        landingPanel.add(title);

        JLabel subtitle = new JLabel("Test your knowledge and track your progress.", JLabel.CENTER);
        subtitle.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 20));
        subtitle.setBounds(139, 113, 600, 30);
        landingPanel.add(subtitle);
        
        JButton loginBtn = new JButton("Login");
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setBackground(new Color(46, 139, 87));
        loginBtn.setBounds(308, 336, 120, 40); 
        landingPanel.add(loginBtn);

        JButton registerBtn = new JButton("Register");
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setBackground(new Color(65, 105, 225));
        registerBtn.setBounds(470, 336, 120, 40);
        landingPanel.add(registerBtn);

        JLabel footer = new JLabel("BCRV 2025 Online Quiz System", JLabel.CENTER);
        footer.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 12));
        footer.setBounds(247, 444, 400, 30);
        landingPanel.add(footer);

        loginBtn.addActionListener(e -> showPanel(LOGIN_PANEL));
        registerBtn.addActionListener(e -> showPanel(REGISTER_PANEL));

        return landingPanel;
    }

}
