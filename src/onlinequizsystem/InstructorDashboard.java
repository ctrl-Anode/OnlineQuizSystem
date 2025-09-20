package onlinequizsystem;

import javax.swing.JPanel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class InstructorDashboard extends JPanel {
    private static final long serialVersionUID = 1L;
//    private Main main;
//    private int instructorId;
//    private String instructorUsername;

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public InstructorDashboard(Main main, int instructorId, String instructorUsername) {
//        this.main = main;
//        this.instructorId = instructorId;
//        this.instructorUsername = instructorUsername;

        setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(70, 130, 180));
        topBar.setPreferredSize(new Dimension(800, 50));

        JLabel titleLabel = new JLabel("Instructor Dashboard");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(new EmptyBorder(0, 10, 0, 0));

        JLabel userLabel = new JLabel("Logged in: " + instructorUsername);
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 12));
        logoutButton.setBackground(Color.RED);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        logoutButton.addActionListener(e -> main.showPanel(Main.LANDING_PANEL));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rightPanel.setOpaque(false);
        rightPanel.add(userLabel);
        rightPanel.add(logoutButton);

        topBar.add(titleLabel, BorderLayout.WEST);
        topBar.add(rightPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        JPanel homePanel = new JPanel(new BorderLayout());
        homePanel.add(new JLabel("Welcome, " + instructorUsername, SwingConstants.CENTER), BorderLayout.CENTER);
        mainPanel.add(homePanel, "Dashboard");

        ManageStudentsPanel studentsPanel = new ManageStudentsPanel(main, instructorId);
        mainPanel.add(studentsPanel, "ManageStudents");

        ManageQuizzesPanel quizzesPanel = new ManageQuizzesPanel(instructorId);
        mainPanel.add(quizzesPanel, "ManageQuizzes");

        InstructorResultsPanel resultsPanel = new InstructorResultsPanel(instructorId);
        mainPanel.add(resultsPanel, "ViewResults");

        InstructorLogsPanel logsPanel = new InstructorLogsPanel(instructorId);
        mainPanel.add(logsPanel, "QuizLogs");

        add(mainPanel, BorderLayout.CENTER);

        JPanel sideBar = new JPanel();
        sideBar.setLayout(new BoxLayout(sideBar, BoxLayout.Y_AXIS));
        sideBar.setBackground(new Color(230, 230, 230));
        sideBar.setBorder(new EmptyBorder(10, 10, 10, 10));
        sideBar.setPreferredSize(new Dimension(200, 600));

        JButton homeButton = new JButton("Dashboard");
        JButton manageStudentsButton = new JButton("Manage Students");
        JButton manageQuizzesButton = new JButton("Manage Quizzes");
        JButton viewResultsButton = new JButton("View Results");
        JButton quizLogsButton = new JButton("Quiz Activity Logs");

        homeButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        manageStudentsButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        manageQuizzesButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        viewResultsButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        quizLogsButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        sideBar.add(homeButton);
        sideBar.add(Box.createRigidArea(new Dimension(0, 10)));
        sideBar.add(manageStudentsButton);
        sideBar.add(Box.createRigidArea(new Dimension(0, 10)));
        sideBar.add(manageQuizzesButton);
        sideBar.add(Box.createRigidArea(new Dimension(0, 10)));
        sideBar.add(viewResultsButton);
        sideBar.add(Box.createRigidArea(new Dimension(0, 10)));
        sideBar.add(quizLogsButton);

        add(sideBar, BorderLayout.WEST);

        //homeButton.addAncestorListener(e -> cardLayout.show(mainPanel, "ManageStudents"));
        manageStudentsButton.addActionListener(e -> cardLayout.show(mainPanel, "ManageStudents"));
        manageQuizzesButton.addActionListener(e -> cardLayout.show(mainPanel, "ManageQuizzes"));
        viewResultsButton.addActionListener(e -> cardLayout.show(mainPanel, "ViewResults"));
        quizLogsButton.addActionListener(e -> cardLayout.show(mainPanel, "QuizLogs"));

        //Unang lalabas
        cardLayout.show(mainPanel, "ManageStudents");
    }
}
