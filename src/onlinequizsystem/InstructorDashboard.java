package onlinequizsystem;

import javax.swing.JPanel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class InstructorDashboard extends JPanel {
    private static final long serialVersionUID = 1L;

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public InstructorDashboard(Main main, int instructorId, String instructorUsername) {
    	setBackground(new Color(245, 222, 179));
        setPreferredSize(new Dimension(904, 531)); 
        setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setFont(new Font("Arial", Font.PLAIN, 12));
        topBar.setBackground(new Color(210, 180, 140));
        topBar.setPreferredSize(new Dimension(904, 50)); 

        JLabel titleLabel = new JLabel("Online Quiz");
        titleLabel.setForeground(new Color(0, 0, 0));
        titleLabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 20));
        titleLabel.setBorder(new EmptyBorder(0, 15, 0, 0)); 

        JLabel userLabel = new JLabel("Logged in: " + instructorUsername);
        userLabel.setForeground(new Color(0, 0, 0));
        userLabel.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 15));

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 12));
        logoutButton.setBackground(Color.RED);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        logoutButton.addActionListener(e -> main.showPanel(Main.LANDING_PANEL));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rightPanel.setOpaque(false);
        rightPanel.add(userLabel);
        rightPanel.add(logoutButton);

        topBar.add(titleLabel, BorderLayout.WEST);
        topBar.add(rightPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);
        
        JLabel lblInstructor = new JLabel("                                           INSTRUCTOR");
        lblInstructor.setForeground(Color.BLACK);
        lblInstructor.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 20));
        lblInstructor.setBorder(new EmptyBorder(0, 15, 0, 0));
        topBar.add(lblInstructor, BorderLayout.CENTER);

        JPanel sideBar = new JPanel();
        sideBar.setLayout(new BoxLayout(sideBar, BoxLayout.Y_AXIS));
        sideBar.setBackground(new Color(245, 222, 179));
        sideBar.setPreferredSize(new Dimension(200, 481));
        sideBar.setBorder(new EmptyBorder(15, 10, 15, 10));

        JButton homeButton = new JButton("Dashboard");
        homeButton.setFont(new Font("Arial Black", Font.PLAIN, 12));
        homeButton.setForeground(new Color(0, 0, 0));
        homeButton.setBackground(new Color(255, 250, 240));
        JButton manageQuizzesButton = new JButton("Manage Quizzes");
        manageQuizzesButton.setBackground(new Color(255, 250, 240));
        manageQuizzesButton.setFont(new Font("Arial Black", Font.PLAIN, 12));
        JButton manageStudentsButton = new JButton("Manage Students");
        manageStudentsButton.setFont(new Font("Arial Black", Font.PLAIN, 12));
        manageStudentsButton.setBackground(new Color(255, 250, 240));
        JButton viewResultsButton = new JButton("View Results");
        viewResultsButton.setFont(new Font("Arial Black", Font.PLAIN, 12));
        viewResultsButton.setBackground(new Color(255, 250, 240));
        JButton quizLogsButton = new JButton("Quiz Activity Logs");
        quizLogsButton.setFont(new Font("Arial Black", Font.PLAIN, 12));
        quizLogsButton.setBackground(new Color(255, 250, 240));

        Dimension btnSize = new Dimension(Integer.MAX_VALUE, 40);
        homeButton.setMaximumSize(btnSize);
        manageQuizzesButton.setMaximumSize(btnSize);
        manageStudentsButton.setMaximumSize(btnSize);
        viewResultsButton.setMaximumSize(btnSize);
        quizLogsButton.setMaximumSize(btnSize);

        sideBar.add(homeButton);
        sideBar.add(Box.createRigidArea(new Dimension(0, 12)));
        sideBar.add(manageQuizzesButton);
        sideBar.add(Box.createRigidArea(new Dimension(0, 12)));
        sideBar.add(viewResultsButton);
        sideBar.add(Box.createRigidArea(new Dimension(0, 12)));
        sideBar.add(manageStudentsButton);
        sideBar.add(Box.createRigidArea(new Dimension(0, 12)));
        sideBar.add(quizLogsButton);

        add(sideBar, BorderLayout.WEST);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        JPanel homePanel = new JPanel(new BorderLayout());
        mainPanel.add(homePanel, "Dashboard");
        
        ManageQuizzesPanel quizzesPanel = new ManageQuizzesPanel(instructorId);
        mainPanel.add(quizzesPanel, "ManageQuizzes");

        ManageStudentsPanel studentsPanel = new ManageStudentsPanel(main, instructorId);
        mainPanel.add(studentsPanel, "ManageStudents");

        InstructorResultsPanel resultsPanel = new InstructorResultsPanel(instructorId);
        mainPanel.add(resultsPanel, "ViewResults");

        InstructorLogsPanel logsPanel = new InstructorLogsPanel(instructorId);
        mainPanel.add(logsPanel, "QuizLogs");

        add(mainPanel, BorderLayout.CENTER);

        homeButton.addActionListener(e -> cardLayout.show(mainPanel, "ManageQuizzes"));
        manageStudentsButton.addActionListener(e -> cardLayout.show(mainPanel, "ManageStudents"));
        manageQuizzesButton.addActionListener(e -> cardLayout.show(mainPanel, "ManageQuizzes"));
        viewResultsButton.addActionListener(e -> cardLayout.show(mainPanel, "ViewResults"));
        quizLogsButton.addActionListener(e -> cardLayout.show(mainPanel, "QuizLogs"));

        cardLayout.show(mainPanel, "ManageQuizzes");
    }
}  

