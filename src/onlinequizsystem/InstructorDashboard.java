package onlinequizsystem;

import javax.swing.JPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class InstructorDashboard extends JPanel {
    private static final long serialVersionUID = 1L;
    private Main main;
    private int instructorId;
    private String instructorUsername;

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel sideBar;

    // Modern color palette
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 73, 94);
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);
    private static final Color WARNING_COLOR = new Color(231, 76, 60);
    private static final Color BACKGROUND_COLOR = new Color(247, 249, 252);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color SIDEBAR_COLOR = new Color(236, 240, 241);
    private static final Color BORDER_COLOR = new Color(220, 221, 225);

    public InstructorDashboard(Main main, int instructorId, String instructorUsername) {
        this.main = main;
        this.instructorId = instructorId;
        this.instructorUsername = instructorUsername;

        setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        // Create modern navigation and layout
        createTopNavigation();
        createSideNavigation();
        createMainContent();
    }

    private void createTopNavigation() {
        JPanel topNav = new JPanel(new BorderLayout());
        topNav.setBackground(PRIMARY_COLOR);
        topNav.setPreferredSize(new Dimension(0, 70));
        topNav.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        // Left side - Title and icon
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel("🏫");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        
        JLabel titleLabel = new JLabel("  Instructor Dashboard");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        
        leftPanel.add(iconLabel);
        leftPanel.add(titleLabel);

        // Right side - User info and logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);
        
        JLabel userLabel = new JLabel("👨‍🏫 " + instructorUsername);
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JButton logoutButton = createModernButton("Logout", WARNING_COLOR, 16);
        logoutButton.addActionListener(e -> main.showPanel(Main.LANDING_PANEL));
        
        rightPanel.add(userLabel);
        rightPanel.add(logoutButton);

        topNav.add(leftPanel, BorderLayout.WEST);
        topNav.add(rightPanel, BorderLayout.EAST);
        
        add(topNav, BorderLayout.NORTH);
    }

    private void createSideNavigation() {
        sideBar = new JPanel();
        sideBar.setLayout(new BoxLayout(sideBar, BoxLayout.Y_AXIS));
        sideBar.setBackground(SIDEBAR_COLOR);
        sideBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_COLOR),
            BorderFactory.createEmptyBorder(25, 20, 25, 20)
        ));
        sideBar.setPreferredSize(new Dimension(220, 0));

        // Navigation title
        JLabel navTitle = new JLabel("Navigation");
        navTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        navTitle.setForeground(TEXT_COLOR);
        navTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        sideBar.add(navTitle);
        sideBar.add(Box.createRigidArea(new Dimension(0, 20)));

        // Navigation buttons
        JButton dashboardBtn = createNavButton("🏠", "Dashboard", true);
        JButton studentsBtn = createNavButton("👥", "Manage Students", false);
        JButton quizzesBtn = createNavButton("📝", "Manage Quizzes", false);
        JButton resultsBtn = createNavButton("📊", "View Results", false);
        JButton logsBtn = createNavButton("📋", "Quiz Logs", false);

        sideBar.add(dashboardBtn);
        sideBar.add(Box.createRigidArea(new Dimension(0, 8)));
        sideBar.add(studentsBtn);
        sideBar.add(Box.createRigidArea(new Dimension(0, 8)));
        sideBar.add(quizzesBtn);
        sideBar.add(Box.createRigidArea(new Dimension(0, 8)));
        sideBar.add(resultsBtn);
        sideBar.add(Box.createRigidArea(new Dimension(0, 8)));
        sideBar.add(logsBtn);
        sideBar.add(Box.createVerticalGlue());

        // Button actions
        dashboardBtn.addActionListener(e -> {
            cardLayout.show(mainPanel, "Dashboard");
            updateNavButtonStates(dashboardBtn);
        });
        studentsBtn.addActionListener(e -> {
            cardLayout.show(mainPanel, "ManageStudents");
            updateNavButtonStates(studentsBtn);
        });
        quizzesBtn.addActionListener(e -> {
            cardLayout.show(mainPanel, "ManageQuizzes");
            updateNavButtonStates(quizzesBtn);
        });
        resultsBtn.addActionListener(e -> {
            cardLayout.show(mainPanel, "ViewResults");
            updateNavButtonStates(resultsBtn);
        });
        logsBtn.addActionListener(e -> {
            cardLayout.show(mainPanel, "QuizLogs");
            updateNavButtonStates(logsBtn);
        });

        add(sideBar, BorderLayout.WEST);
    }

    private void createMainContent() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setOpaque(false);

        // Dashboard home panel
        JPanel homePanel = createDashboardHome();
        mainPanel.add(homePanel, "Dashboard");

        // Other panels
        ManageStudentsPanel studentsPanel = new ManageStudentsPanel(main, instructorId);
        mainPanel.add(studentsPanel, "ManageStudents");

        ManageQuizzesPanel quizzesPanel = new ManageQuizzesPanel(instructorId);
        mainPanel.add(quizzesPanel, "ManageQuizzes");

        InstructorResultsPanel resultsPanel = new InstructorResultsPanel(instructorId);
        mainPanel.add(resultsPanel, "ViewResults");

        InstructorLogsPanel logsPanel = new InstructorLogsPanel(instructorId);
        mainPanel.add(logsPanel, "QuizLogs");

        add(mainPanel, BorderLayout.CENTER);

        // Show dashboard by default
        cardLayout.show(mainPanel, "Dashboard");
    }

    private JPanel createDashboardHome() {
        JPanel homePanel = new JPanel(new BorderLayout());
        homePanel.setOpaque(false);
        homePanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Welcome section
        JPanel welcomeCard = new JPanel(new BorderLayout());
        welcomeCard.setBackground(CARD_COLOR);
        welcomeCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        JLabel welcomeIcon = new JLabel("👋", SwingConstants.CENTER);
        welcomeIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));

        JLabel welcomeTitle = new JLabel("Welcome back, " + instructorUsername + "!", SwingConstants.CENTER);
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeTitle.setForeground(TEXT_COLOR);

        JLabel welcomeSubtitle = new JLabel("Ready to manage your quizzes and students", SwingConstants.CENTER);
        welcomeSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        welcomeSubtitle.setForeground(new Color(127, 140, 141));

        JPanel welcomeContent = new JPanel();
        welcomeContent.setLayout(new BoxLayout(welcomeContent, BoxLayout.Y_AXIS));
        welcomeContent.setOpaque(false);
        welcomeContent.add(welcomeIcon);
        welcomeContent.add(Box.createRigidArea(new Dimension(0, 15)));
        welcomeContent.add(welcomeTitle);
        welcomeContent.add(Box.createRigidArea(new Dimension(0, 8)));
        welcomeContent.add(welcomeSubtitle);

        welcomeCard.add(welcomeContent, BorderLayout.CENTER);

        homePanel.add(welcomeCard, BorderLayout.CENTER);
        return homePanel;
    }

    private JButton createNavButton(String icon, String text, boolean isActive) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Button styling based on active state
        if (isActive) {
            button.setBackground(PRIMARY_COLOR);
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(SIDEBAR_COLOR);
            button.setForeground(TEXT_COLOR);
        }

        button.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        // Icon and text
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        iconLabel.setForeground(button.getForeground());

        JLabel textLabel = new JLabel("  " + text);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textLabel.setForeground(button.getForeground());

        JPanel content = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        content.setOpaque(false);
        content.add(iconLabel);
        content.add(textLabel);

        button.add(content, BorderLayout.CENTER);

        // Hover effects
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isActive) {
                    button.setBackground(new Color(236, 240, 241));
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (!isActive) {
                    button.setBackground(SIDEBAR_COLOR);
                }
            }
        });

        return button;
    }

    private void updateNavButtonStates(JButton activeButton) {
        // Reset all buttons to inactive state
        for (Component comp : sideBar.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                btn.setBackground(SIDEBAR_COLOR);
                btn.setForeground(TEXT_COLOR);
                // Update icon and text colors
                for (Component subComp : ((JPanel) btn.getComponent(0)).getComponents()) {
                    if (subComp instanceof JLabel) {
                        ((JLabel) subComp).setForeground(TEXT_COLOR);
                    }
                }
            }
        }

        // Set active button
        activeButton.setBackground(PRIMARY_COLOR);
        activeButton.setForeground(Color.WHITE);
        // Update icon and text colors for active button
        for (Component subComp : ((JPanel) activeButton.getComponent(0)).getComponents()) {
            if (subComp instanceof JLabel) {
                ((JLabel) subComp).setForeground(Color.WHITE);
            }
        }
    }

    private JButton createModernButton(String text, Color bgColor, int fontSize) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

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
}
