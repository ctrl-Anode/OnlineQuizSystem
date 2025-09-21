package onlinequizsystem;

import javax.swing.JPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setOpaque(false);
        mainContent.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Join instructor section
        JPanel joinSection = createJoinInstructorCard();
        
        // Main dashboard grid
        JPanel dashboardGrid = new JPanel(new GridLayout(1, 3, 20, 0));
        dashboardGrid.setOpaque(false);
        
        // Instructor card
        JPanel instructorCard = createInstructorCard();
        
        // Quiz card
        JPanel quizCard = createQuizCard();
        
        // Results card
        JPanel resultCard = createResultCard();
        
        dashboardGrid.add(instructorCard);
        dashboardGrid.add(quizCard);
        dashboardGrid.add(resultCard);

        mainContent.add(joinSection, BorderLayout.NORTH);
        mainContent.add(dashboardGrid, BorderLayout.CENTER);
        
        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createJoinInstructorCard() {
        JPanel card = createCard("👨‍🏫 Join Instructor", "Connect with your instructor using their code");
        card.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        
        JPanel content = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        content.setOpaque(false);
        
        JLabel codeLabel = new JLabel("Instructor Code:");
        codeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        codeLabel.setForeground(TEXT_COLOR);
        
        JTextField codeField = new JTextField(12);
        codeField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        codeField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        JButton joinButton = createModernButton("Join", ACCENT_COLOR, 14);
        
        joinButton.addActionListener(e -> {
            String code = codeField.getText().trim();
            if (!code.isEmpty()) {
                joinInstructor(code);
                codeField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Please enter an instructor code!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        content.add(codeLabel);
        content.add(codeField);
        content.add(joinButton);
        
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel createInstructorCard() {
        JPanel card = createCard("👥 My Instructors", "Instructors you are connected with");
        
        instructorListModel = new DefaultListModel<>();
        JList<String> instructorList = new JList<>(instructorListModel);
        instructorList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        instructorList.setBackground(Color.WHITE);
        instructorList.setSelectionBackground(PRIMARY_COLOR.brighter());
        instructorList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(instructorList);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.setPreferredSize(new Dimension(0, 200));
        
        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    private JPanel createQuizCard() {
        JPanel card = createCard("📝 Available Quizzes", "Quizzes you can take");
        
        quizListModel = new DefaultListModel<>();
        quizList = new JList<>(quizListModel);
        quizList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        quizList.setBackground(Color.WHITE);
        quizList.setSelectionBackground(PRIMARY_COLOR.brighter());
        quizList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(quizList);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.setPreferredSize(new Dimension(0, 150));
        
        JButton takeQuizButton = createModernButton("Take Quiz", PRIMARY_COLOR, 14);
        takeQuizButton.addActionListener(e -> {
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
                        JOptionPane.showMessageDialog(this,
                                "⚠️ You have already taken this quiz. You cannot attempt it again.",
                                "Quiz Attempted", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error checking quiz attempt: " + ex.getMessage());
                    ex.printStackTrace();
                    return;
                }
                
                new TakeQuizDialog(quizId, studentId).setVisible(true);
                loadResults(); // Refresh results after taking quiz
            } else {
                JOptionPane.showMessageDialog(this, "Please select a quiz to take!", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        buttonPanel.add(takeQuizButton, BorderLayout.CENTER);
        
        card.add(scrollPane, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createResultCard() {
        JPanel card = createCard("� My Results", "Your quiz scores and history");
        
        resultListModel = new DefaultListModel<>();
        resultList = new JList<>(resultListModel);
        resultList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resultList.setBackground(Color.WHITE);
        resultList.setSelectionBackground(PRIMARY_COLOR.brighter());
        resultList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(resultList);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.setPreferredSize(new Dimension(0, 150));
        
        JButton viewResultButton = createModernButton("View Details", SECONDARY_COLOR, 14);
        viewResultButton.addActionListener(e -> {
            int selectedIndex = resultList.getSelectedIndex();
            if (selectedIndex != -1) {
                String selected = resultListModel.getElementAt(selectedIndex);
                int quizId = Integer.parseInt(selected.split(":")[0]);
                new ResultDetailsDialog(studentId, quizId).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a result to view!", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        buttonPanel.add(viewResultButton, BorderLayout.CENTER);
        
        card.add(scrollPane, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createCard(String title, String subtitle) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_COLOR);
        
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(127, 140, 141));
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        card.add(headerPanel, BorderLayout.NORTH);
        return card;
    }

    private JButton createModernButton(String text, Color bgColor, int fontSize) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
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

    // Load results
    private void loadResults() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT r.quiz_id, q.title, r.score, r.taken_at " +
                    "FROM results r " +
                    "JOIN quizzes q ON r.quiz_id = q.quiz_id " +
                    "WHERE r.student_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            resultListModel.clear();
            while (rs.next()) {
                resultListModel.addElement(
                        rs.getInt("quiz_id") + ": " + rs.getString("title") +
                                " | Score: " + rs.getInt("score") +
                                " | Taken: " + rs.getTimestamp("taken_at")
                );
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading results: " + e.getMessage());
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
