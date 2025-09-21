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

public class StudentDashboard extends JPanel {
    private static final long serialVersionUID = 1L;
    private int studentId;
    private String studentUsername;
    private DefaultListModel<String> instructorListModel;
    private DefaultListModel<String> quizListModel;
    private JList<String> quizList;
    private DefaultListModel<String> resultListModel;
    private JList<String> resultList;
    private Main main;

    // Modern color palette
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 73, 94);
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);
    private static final Color WARNING_COLOR = new Color(231, 76, 60);
    private static final Color BACKGROUND_COLOR = new Color(247, 249, 252);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color BORDER_COLOR = new Color(220, 221, 225);

    public StudentDashboard(Main main, int studentId, String studentUsername) {
        this.main = main;
        this.studentId = studentId;
        this.studentUsername = studentUsername;

        setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        // Create modern dashboard layout
        createTopNavigation();
        createMainContent();
        
        // Load data
        loadInstructors();
        loadQuizzes();
        loadResults();
    }

    private void createTopNavigation() {
        JPanel topNav = new JPanel(new BorderLayout());
        topNav.setBackground(PRIMARY_COLOR);
        topNav.setPreferredSize(new Dimension(0, 70));
        topNav.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        // Left side - Title and icon
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel("🎓");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        
        JLabel titleLabel = new JLabel("  Student Dashboard");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        
        leftPanel.add(iconLabel);
        leftPanel.add(titleLabel);

        // Right side - User info and logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);
        
        JLabel userLabel = new JLabel("👤 " + studentUsername);
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

    private void createMainContent() {
        // Create main content panel that will be scrollable
        JPanel scrollableContent = new JPanel();
        scrollableContent.setLayout(new BoxLayout(scrollableContent, BoxLayout.Y_AXIS));
        scrollableContent.setBackground(BACKGROUND_COLOR);
        scrollableContent.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

        // Welcome section with statistics
        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setOpaque(false);
        topSection.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel welcomeSection = createWelcomeSection();
        JPanel statsSection = createStatsSection();
        
        welcomeSection.setAlignmentX(Component.CENTER_ALIGNMENT);
        statsSection.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        topSection.add(welcomeSection);
        topSection.add(Box.createRigidArea(new Dimension(0, 15)));
        topSection.add(statsSection);

        // Join instructor section (enhanced)
        JPanel joinSection = createEnhancedJoinSection();
        joinSection.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Main dashboard grid (enhanced)
        JPanel dashboardGrid = new JPanel(new GridLayout(1, 3, 15, 0));
        dashboardGrid.setOpaque(false);
        dashboardGrid.setAlignmentX(Component.CENTER_ALIGNMENT);
        dashboardGrid.setMaximumSize(new Dimension(1000, 350));
        
        JPanel instructorCard = createEnhancedInstructorCard();
        JPanel quizCard = createEnhancedQuizCard();
        JPanel resultCard = createEnhancedResultCard();
        
        dashboardGrid.add(instructorCard);
        dashboardGrid.add(quizCard);
        dashboardGrid.add(resultCard);

        // Add all sections to scrollable content
        scrollableContent.add(topSection);
        scrollableContent.add(Box.createRigidArea(new Dimension(0, 20)));
        scrollableContent.add(joinSection);
        scrollableContent.add(Box.createRigidArea(new Dimension(0, 20)));
        scrollableContent.add(dashboardGrid);
        scrollableContent.add(Box.createVerticalGlue());

        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(scrollableContent);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createWelcomeSection() {
        JPanel welcomeCard = new JPanel(new BorderLayout());
        welcomeCard.setBackground(CARD_COLOR);
        welcomeCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        welcomeCard.setMaximumSize(new Dimension(1000, 90));
        welcomeCard.setPreferredSize(new Dimension(1000, 90));

        JLabel welcomeIcon = new JLabel("👋");
        welcomeIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel welcomeTitle = new JLabel("Welcome back, " + studentUsername + "!");
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcomeTitle.setForeground(TEXT_COLOR);
        welcomeTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel welcomeSubtitle = new JLabel("Ready to learn? Check out your available quizzes and track your progress!");
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

    private JPanel createStatsSection() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(1000, 80));
        statsPanel.setPreferredSize(new Dimension(1000, 80));

        // Get statistics
        int totalInstructors = getTotalInstructors();
        int totalQuizzes = getTotalAvailableQuizzes();
        int totalResults = getTotalResults();

        JPanel instructorsCard = createMiniStatCard("👥", "Instructors", String.valueOf(totalInstructors), PRIMARY_COLOR);
        JPanel quizzesCard = createMiniStatCard("📝", "Available Quizzes", String.valueOf(totalQuizzes), ACCENT_COLOR);
        JPanel resultsCard = createMiniStatCard("🏆", "Completed", String.valueOf(totalResults), new Color(155, 89, 182));

        statsPanel.add(instructorsCard);
        statsPanel.add(quizzesCard);
        statsPanel.add(resultsCard);

        return statsPanel;
    }

    private JPanel createMiniStatCard(String icon, String title, String value, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setPreferredSize(new Dimension(0, 80));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setPreferredSize(new Dimension(40, 80));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(accentColor);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(new Color(127, 140, 141));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(Box.createVerticalGlue());
        contentPanel.add(valueLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalGlue());

        card.add(iconLabel, BorderLayout.WEST);
        card.add(contentPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createEnhancedJoinSection() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        card.setMaximumSize(new Dimension(1000, 130));
        card.setPreferredSize(new Dimension(1000, 130));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(SECONDARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel headerLabel = new JLabel("🔗 Join Instructor");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerLabel.setForeground(Color.WHITE);

        JLabel subHeaderLabel = new JLabel("Enter your instructor's code to access their quizzes");
        subHeaderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subHeaderLabel.setForeground(new Color(255, 255, 255, 180));

        JPanel titleContainer = new JPanel();
        titleContainer.setLayout(new BoxLayout(titleContainer, BoxLayout.Y_AXIS));
        titleContainer.setOpaque(false);
        titleContainer.add(headerLabel);
        titleContainer.add(subHeaderLabel);

        headerPanel.add(titleContainer, BorderLayout.WEST);
        card.add(headerPanel, BorderLayout.NORTH);

        // Content
        JPanel content = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        content.setBackground(CARD_COLOR);
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel codeLabel = new JLabel("Instructor Code:");
        codeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        codeLabel.setForeground(TEXT_COLOR);
        
        JTextField codeField = new JTextField(15);
        codeField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        codeField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        codeField.setBackground(Color.WHITE);
        
        JButton joinButton = createEnhancedButton("🚀 Join", ACCENT_COLOR);
        
        joinButton.addActionListener(e -> {
            String code = codeField.getText().trim();
            if (!code.isEmpty()) {
                joinInstructor(code);
                codeField.setText("");
                // Refresh statistics after joining
                SwingUtilities.invokeLater(() -> {
                    createMainContent(); // Refresh the entire content to update stats
                    revalidate();
                    repaint();
                });
            } else {
                showStyledMessage("Please enter an instructor code!", "Input Required", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        content.add(codeLabel);
        content.add(codeField);
        content.add(joinButton);
        
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel createEnhancedInstructorCard() {
        JPanel card = createEnhancedCard("👥", "My Instructors", "Instructors you are connected with", SECONDARY_COLOR);
        
        instructorListModel = new DefaultListModel<>();
        JList<String> instructorList = new JList<>(instructorListModel);
        styleEnhancedList(instructorList);
        
        JScrollPane scrollPane = new JScrollPane(instructorList);
        styleScrollPane(scrollPane);
        
        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    private JPanel createEnhancedQuizCard() {
        JPanel card = createEnhancedCard("📝", "Available Quizzes", "Quizzes you can take", PRIMARY_COLOR);
        
        quizListModel = new DefaultListModel<>();
        quizList = new JList<>(quizListModel);
        styleEnhancedList(quizList);
        
        JScrollPane scrollPane = new JScrollPane(quizList);
        styleScrollPane(scrollPane);
        
        JButton takeQuizButton = createEnhancedButton("🎯 Take Quiz", PRIMARY_COLOR);
        takeQuizButton.addActionListener(e -> handleTakeQuiz());
        
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        buttonPanel.add(takeQuizButton, BorderLayout.CENTER);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        card.add(contentPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createEnhancedResultCard() {
        JPanel card = createEnhancedCard("🏆", "My Results", "Your quiz scores and performance history", new Color(155, 89, 182));
        
        resultListModel = new DefaultListModel<>();
        resultList = new JList<>(resultListModel);
        styleEnhancedList(resultList);
        
        JScrollPane scrollPane = new JScrollPane(resultList);
        styleScrollPane(scrollPane);
        
        JButton viewResultButton = createEnhancedButton("📊 View Details", SECONDARY_COLOR);
        viewResultButton.addActionListener(e -> handleViewResult());
        
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        buttonPanel.add(viewResultButton, BorderLayout.CENTER);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        card.add(contentPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createEnhancedCard(String icon, String title, String subtitle, Color headerColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        card.setPreferredSize(new Dimension(320, 320));
        
        // Header with colored background
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(headerColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        iconLabel.setForeground(Color.WHITE);
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitleLabel.setForeground(new Color(255, 255, 255, 180));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);
        
        JPanel headerContent = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerContent.setOpaque(false);
        headerContent.add(iconLabel);
        headerContent.add(Box.createRigidArea(new Dimension(10, 0)));
        headerContent.add(textPanel);
        
        headerPanel.add(headerContent, BorderLayout.WEST);
        card.add(headerPanel, BorderLayout.NORTH);
        
        // Content area
        JPanel contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(CARD_COLOR);
        contentArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.add(contentArea, BorderLayout.CENTER);
        
        return card;
    }

    private void styleEnhancedList(JList<String> list) {
        list.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        list.setBackground(Color.WHITE);
        list.setSelectionBackground(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 30));
        list.setSelectionForeground(TEXT_COLOR);
        list.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        list.setFixedCellHeight(32);
    }

    private void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    }

    private JButton createEnhancedButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
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

    private void handleTakeQuiz() {
        int selectedIndex = quizList.getSelectedIndex();
        if (selectedIndex != -1) {
            String selected = quizListModel.getElementAt(selectedIndex);
            int quizId = Integer.parseInt(selected.split(":")[0]);
            
            // Check if already taken
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "SELECT 1 FROM results WHERE student_id = ? AND quiz_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, studentId);
                stmt.setInt(2, quizId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    showStyledMessage("You have already completed this quiz!\nYou cannot take it again.", 
                            "Quiz Already Completed", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            } catch (Exception ex) {
                showStyledMessage("Error checking quiz status: " + ex.getMessage(), 
                        "Database Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            new TakeQuizDialog(quizId, studentId).setVisible(true);
            loadResults(); // Refresh results after taking quiz
        } else {
            showStyledMessage("Please select a quiz from the list to take.", 
                    "No Quiz Selected", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleViewResult() {
        int selectedIndex = resultList.getSelectedIndex();
        if (selectedIndex != -1) {
            String selected = resultListModel.getElementAt(selectedIndex);
            int quizId = Integer.parseInt(selected.split(":")[0]);
            new ResultDetailsDialog(studentId, quizId).setVisible(true);
        } else {
            showStyledMessage("Please select a result from the list to view details.", 
                    "No Result Selected", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Enhanced load results with better formatting
    private void loadResults() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT r.quiz_id, q.title, r.score, r.taken_at, " +
                        "(SELECT COUNT(*) FROM questions WHERE quiz_id = r.quiz_id) as total_questions " +
                        "FROM results r " +
                        "JOIN quizzes q ON r.quiz_id = q.quiz_id " +
                        "WHERE r.student_id = ? " +
                        "ORDER BY r.taken_at DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            resultListModel.clear();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, HH:mm");
            
            while (rs.next()) {
                int score = rs.getInt("score");
                int totalQuestions = rs.getInt("total_questions");
                double percentage = totalQuestions > 0 ? (double) score / totalQuestions * 100 : 0;
                String formattedDate = dateFormat.format(rs.getTimestamp("taken_at"));
                
                String resultText = String.format("%d: %s | %d/%d (%.1f%%) | %s",
                    rs.getInt("quiz_id"),
                    rs.getString("title"),
                    score,
                    totalQuestions,
                    percentage,
                    formattedDate
                );
                
                resultListModel.addElement(resultText);
            }
        } catch (Exception e) {
            showStyledMessage("Error loading results: " + e.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Statistics methods
    private int getTotalInstructors() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM student_instructors WHERE student_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getTotalAvailableQuizzes() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT COUNT(DISTINCT q.quiz_id) FROM quizzes q " +
                        "JOIN student_instructors si ON q.created_by = si.instructor_id " +
                        "WHERE si.student_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getTotalResults() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM results WHERE student_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void showStyledMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    // Load instructors
    private void loadInstructors() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT u.full_name, u.instructor_code " +
                    "FROM student_instructors si " +
                    "JOIN users u ON si.instructor_id = u.id " +
                    "WHERE si.student_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            instructorListModel.clear();
            while (rs.next()) {
                instructorListModel.addElement(
                        rs.getString("full_name") + " (Code: " + rs.getString("instructor_code") + ")"
                );
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading instructors: " + e.getMessage());
        }
    }

    // Load quizzes
    private void loadQuizzes() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT q.quiz_id, q.title, u.full_name " +
                    "FROM quizzes q " +
                    "JOIN users u ON q.created_by = u.id " +
                    "JOIN student_instructors si ON si.instructor_id = u.id " +
                    "WHERE si.student_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            quizListModel.clear();
            while (rs.next()) {
                quizListModel.addElement(
                        rs.getInt("quiz_id") + ": " + rs.getString("title") +
                                " (by " + rs.getString("full_name") + ")"
                );
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading quizzes: " + e.getMessage());
        }
    }

    // Join instructor
    private void joinInstructor(String code) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT id, full_name FROM users WHERE instructor_code = ? AND role = 'instructor'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int instructorId = rs.getInt("id");
                String instructorName = rs.getString("full_name");

                String insertSql = "INSERT IGNORE INTO student_instructors (student_id, instructor_id) VALUES (?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setInt(1, studentId);
                insertStmt.setInt(2, instructorId);
                insertStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "✅ Successfully joined instructor: " + instructorName, 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadInstructors();
                loadQuizzes();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid instructor code. Please check and try again.", 
                        "Invalid Code", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error joining instructor: " + e.getMessage(), 
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}