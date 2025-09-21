package onlinequizsystem;

import javax.swing.JPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

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
        homePanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Top section - Welcome and Stats Cards
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);

        // Welcome section
        JPanel welcomeSection = createWelcomeSection();
        topSection.add(welcomeSection, BorderLayout.NORTH);

        // Stats cards
        JPanel statsSection = createStatsCardsSection();
        topSection.add(statsSection, BorderLayout.CENTER);

        // Bottom section - Recent Activity and Quick Actions
        JPanel bottomSection = new JPanel(new BorderLayout(20, 0));
        bottomSection.setOpaque(false);
        bottomSection.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Recent Activity (Left)
        JPanel recentActivityPanel = createRecentActivityPanel();
        bottomSection.add(recentActivityPanel, BorderLayout.WEST);

        // Quick Actions (Right)
        JPanel quickActionsPanel = createQuickActionsPanel();
        bottomSection.add(quickActionsPanel, BorderLayout.CENTER);

        homePanel.add(topSection, BorderLayout.NORTH);
        homePanel.add(bottomSection, BorderLayout.CENTER);

        return homePanel;
    }

    private JPanel createWelcomeSection() {
        JPanel welcomeCard = new JPanel(new BorderLayout());
        welcomeCard.setBackground(CARD_COLOR);
        welcomeCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        JLabel welcomeIcon = new JLabel("👋", SwingConstants.LEFT);
        welcomeIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel welcomeTitle = new JLabel("Welcome back, " + instructorUsername + "!");
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcomeTitle.setForeground(TEXT_COLOR);
        welcomeTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel welcomeSubtitle = new JLabel("Here's what's happening with your quizzes today");
        welcomeSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        welcomeSubtitle.setForeground(new Color(127, 140, 141));
        welcomeSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(welcomeTitle);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(welcomeSubtitle);

        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        contentPanel.setOpaque(false);
        contentPanel.add(welcomeIcon);
        contentPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        contentPanel.add(textPanel);

        welcomeCard.add(contentPanel, BorderLayout.CENTER);
        return welcomeCard;
    }

    private JPanel createStatsCardsSection() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Load statistics from database
        int totalQuizzes = getTotalQuizzes();
        int totalStudents = getTotalStudents();
        int recentActivities = getRecentActivities();
        double avgPerformance = getAveragePerformance();

        // Create stat cards
        JPanel quizzesCard = createStatCard("📝", "Total Quizzes", String.valueOf(totalQuizzes), PRIMARY_COLOR);
        JPanel studentsCard = createStatCard("👥", "Students", String.valueOf(totalStudents), ACCENT_COLOR);
        JPanel activitiesCard = createStatCard("📊", "Recent Activities", String.valueOf(recentActivities), new Color(52, 152, 219));
        JPanel performanceCard = createStatCard("🎯", "Avg Performance", String.format("%.1f%%", avgPerformance), new Color(155, 89, 182));

        statsPanel.add(quizzesCard);
        statsPanel.add(studentsCard);
        statsPanel.add(activitiesCard);
        statsPanel.add(performanceCard);

        return statsPanel;
    }

    private JPanel createStatCard(String icon, String title, String value, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setPreferredSize(new Dimension(200, 100));

        // Icon
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(accentColor);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(new Color(127, 140, 141));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(valueLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        contentPanel.add(titleLabel);

        card.add(iconLabel, BorderLayout.WEST);
        card.add(contentPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createRecentActivityPanel() {
        JPanel activityPanel = new JPanel(new BorderLayout());
        activityPanel.setBackground(CARD_COLOR);
        activityPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        activityPanel.setPreferredSize(new Dimension(400, 300));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(SECONDARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel headerLabel = new JLabel("📋 Recent Activity");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerLabel.setForeground(Color.WHITE);

        headerPanel.add(headerLabel, BorderLayout.WEST);
        activityPanel.add(headerPanel, BorderLayout.NORTH);

        // Activity list
        JPanel activityListPanel = new JPanel();
        activityListPanel.setLayout(new BoxLayout(activityListPanel, BoxLayout.Y_AXIS));
        activityListPanel.setBackground(CARD_COLOR);
        activityListPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Load recent activities
        loadRecentActivities(activityListPanel);

        JScrollPane scrollPane = new JScrollPane(activityListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(CARD_COLOR);
        scrollPane.getViewport().setBackground(CARD_COLOR);
        activityPanel.add(scrollPane, BorderLayout.CENTER);

        return activityPanel;
    }

    private JPanel createQuickActionsPanel() {
        JPanel actionsPanel = new JPanel(new BorderLayout());
        actionsPanel.setBackground(CARD_COLOR);
        actionsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(SECONDARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel headerLabel = new JLabel("⚡ Quick Actions");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerLabel.setForeground(Color.WHITE);

        headerPanel.add(headerLabel, BorderLayout.WEST);
        actionsPanel.add(headerPanel, BorderLayout.NORTH);

        // Actions grid
        JPanel actionsGrid = new JPanel(new GridLayout(2, 2, 15, 15));
        actionsGrid.setBackground(CARD_COLOR);
        actionsGrid.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Action buttons
        JButton createQuizBtn = createActionButton("📝", "Create Quiz", "Add new quiz");
        createQuizBtn.addActionListener(e -> {
            cardLayout.show(mainPanel, "ManageQuizzes");
            updateNavButtonStates((JButton) sideBar.getComponent(4)); // Quizzes button
        });

        JButton viewStudentsBtn = createActionButton("👥", "View Students", "Manage students");
        viewStudentsBtn.addActionListener(e -> {
            cardLayout.show(mainPanel, "ManageStudents");
            updateNavButtonStates((JButton) sideBar.getComponent(2)); // Students button
        });

        JButton viewResultsBtn = createActionButton("📊", "View Results", "Check performance");
        viewResultsBtn.addActionListener(e -> {
            cardLayout.show(mainPanel, "ViewResults");
            updateNavButtonStates((JButton) sideBar.getComponent(6)); // Results button
        });

        JButton viewLogsBtn = createActionButton("📋", "Activity Logs", "Monitor activities");
        viewLogsBtn.addActionListener(e -> {
            cardLayout.show(mainPanel, "QuizLogs");
            updateNavButtonStates((JButton) sideBar.getComponent(8)); // Logs button
        });

        actionsGrid.add(createQuizBtn);
        actionsGrid.add(viewStudentsBtn);
        actionsGrid.add(viewResultsBtn);
        actionsGrid.add(viewLogsBtn);

        actionsPanel.add(actionsGrid, BorderLayout.CENTER);
        return actionsPanel;
    }

    private JButton createActionButton(String icon, String title, String subtitle) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setBackground(new Color(249, 250, 251));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitleLabel.setForeground(new Color(127, 140, 141));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        textPanel.add(subtitleLabel);

        button.add(iconLabel, BorderLayout.NORTH);
        button.add(textPanel, BorderLayout.CENTER);

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(241, 243, 244));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(249, 250, 251));
            }
        });

        return button;
    }

    // Database methods for statistics
    private int getTotalQuizzes() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM quizzes WHERE created_by = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, instructorId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getTotalStudents() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT COUNT(DISTINCT si.student_id) FROM student_instructors si WHERE si.instructor_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, instructorId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getRecentActivities() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM quiz_logs l " +
                        "JOIN quizzes q ON l.quiz_id = q.quiz_id " +
                        "WHERE q.created_by = ? AND l.timestamp >= NOW() - INTERVAL 24 HOUR";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, instructorId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private double getAveragePerformance() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT AVG(r.score * 100.0 / (SELECT COUNT(*) FROM questions WHERE quiz_id = r.quiz_id)) as avg_percentage " +
                        "FROM results r " +
                        "JOIN quizzes q ON r.quiz_id = q.quiz_id " +
                        "WHERE q.created_by = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, instructorId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("avg_percentage");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private void loadRecentActivities(JPanel activityListPanel) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT u.full_name, q.title, l.action, l.timestamp " +
                        "FROM quiz_logs l " +
                        "JOIN users u ON l.user_id = u.id " +
                        "JOIN quizzes q ON l.quiz_id = q.quiz_id " +
                        "WHERE q.created_by = ? " +
                        "ORDER BY l.timestamp DESC LIMIT 8";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, instructorId);
            ResultSet rs = stmt.executeQuery();

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            
            while (rs.next()) {
                JPanel activityItem = createActivityItem(
                    rs.getString("full_name"),
                    rs.getString("title"),
                    rs.getString("action"),
                    timeFormat.format(rs.getTimestamp("timestamp"))
                );
                activityListPanel.add(activityItem);
                activityListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }

            if (activityListPanel.getComponentCount() == 0) {
                JLabel noActivity = new JLabel("No recent activities");
                noActivity.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                noActivity.setForeground(new Color(127, 140, 141));
                noActivity.setAlignmentX(Component.CENTER_ALIGNMENT);
                activityListPanel.add(noActivity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPanel createActivityItem(String studentName, String quizTitle, String action, String time) {
        JPanel item = new JPanel(new BorderLayout());
        item.setOpaque(false);
        item.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        // Action icon
        String icon = "📝";
        Color actionColor = new Color(127, 140, 141);
        if (action.toLowerCase().contains("started")) {
            icon = "▶️";
            actionColor = new Color(52, 152, 219);
        } else if (action.toLowerCase().contains("submitted")) {
            icon = "✅";
            actionColor = ACCENT_COLOR;
        } else if (action.toLowerCase().contains("cancelled")) {
            icon = "❌";
            actionColor = WARNING_COLOR;
        }

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

        // Activity text
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel mainText = new JLabel(studentName + " " + action.toLowerCase());
        mainText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        mainText.setForeground(TEXT_COLOR);
        mainText.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subText = new JLabel(quizTitle);
        subText.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        subText.setForeground(actionColor);
        subText.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(mainText);
        textPanel.add(subText);

        // Time
        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        timeLabel.setForeground(new Color(127, 140, 141));

        item.add(iconLabel, BorderLayout.WEST);
        item.add(textPanel, BorderLayout.CENTER);
        item.add(timeLabel, BorderLayout.EAST);

        return item;
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
