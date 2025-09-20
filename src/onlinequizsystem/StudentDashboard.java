package onlinequizsystem;

import javax.swing.JPanel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StudentDashboard extends JPanel {
	private static final long serialVersionUID = 1L;
    private int studentId;
    //private String studentUsername;
    private DefaultListModel<String> instructorListModel;
    private DefaultListModel<String> quizListModel;
    private JList<String> quizList;
    private DefaultListModel<String> resultListModel;
    private JList<String> resultList;
    //private Main main;

    public StudentDashboard(Main main, int studentId, String studentUsername) {
        //this.main = main;
        this.studentId = studentId;
        //this.studentUsername = studentUsername;

        setLayout(new BorderLayout());

        // 🔹 Top Bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(70, 130, 180));
        topBar.setPreferredSize(new Dimension(800, 50));

        JLabel titleLabel = new JLabel("Student Dashboard");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(new EmptyBorder(0, 10, 0, 0));

        JLabel userLabel = new JLabel("Logged in: " + studentUsername);
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

        // 🔹 Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Join Instructor Section
        JPanel joinPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel codeLabel = new JLabel("Enter Instructor Code:");
        JTextField codeField = new JTextField(10);
        JButton joinButton = new JButton("Join Instructor");

        joinPanel.add(codeLabel);
        joinPanel.add(codeField);
        joinPanel.add(joinButton);

        // Instructor List
        instructorListModel = new DefaultListModel<>();
        JList<String> instructorList = new JList<>(instructorListModel);
        JScrollPane instructorScroll = new JScrollPane(instructorList);
        instructorScroll.setBorder(BorderFactory.createTitledBorder("My Instructors"));

        // Quiz List
        quizListModel = new DefaultListModel<>();
        quizList = new JList<>(quizListModel);
        JScrollPane quizScroll = new JScrollPane(quizList);
        quizScroll.setBorder(BorderFactory.createTitledBorder("Available Quizzes"));

        JButton takeQuizButton = new JButton("Take Selected Quiz");

        JPanel quizPanel = new JPanel(new BorderLayout());
        quizPanel.add(quizScroll, BorderLayout.CENTER);
        quizPanel.add(takeQuizButton, BorderLayout.SOUTH);

        // Results List
        resultListModel = new DefaultListModel<>();
        resultList = new JList<>(resultListModel);
        JScrollPane resultScroll = new JScrollPane(resultList);
        resultScroll.setBorder(BorderFactory.createTitledBorder("My Quiz Results"));

        JButton viewResultButton = new JButton("View Selected Result");

        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.add(resultScroll, BorderLayout.CENTER);
        resultPanel.add(viewResultButton, BorderLayout.SOUTH);

        // Split: Quizzes + Results
        JSplitPane centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, quizPanel, resultPanel);
        centerSplit.setDividerLocation(400);

        // Split: Instructors + (Quizzes + Results)
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, instructorScroll, centerSplit);
        mainSplit.setDividerLocation(250);

        mainPanel.add(joinPanel, BorderLayout.NORTH);
        mainPanel.add(mainSplit, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Load data
        loadInstructors();
        loadQuizzes();
        loadResults();

        // 🔹 Button Action: Join Instructor
        joinButton.addActionListener((ActionEvent e) -> {
            String code = codeField.getText().trim();
            if (code.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an instructor code.");
                return;
            }
            joinInstructor(code);
        });

        // 🔹 Button Action: Take Quiz
        takeQuizButton.addActionListener(e -> {
            int selectedIndex = quizList.getSelectedIndex();
            if (selectedIndex == -1) {
                JOptionPane.showMessageDialog(this, "Please select a quiz.");
                return;
            }

            String selected = quizListModel.getElementAt(selectedIndex);
            int quizId = Integer.parseInt(selected.split(":")[0]);

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
            loadResults(); // refresh after taking quiz
        });

        // 🔹 Button Action: View Result
        viewResultButton.addActionListener(e -> {
            int selectedIndex = resultList.getSelectedIndex();
            if (selectedIndex == -1) {
                JOptionPane.showMessageDialog(this, "Please select a result to view.");
                return;
            }

            String selected = resultListModel.getElementAt(selectedIndex);
            int quizId = Integer.parseInt(selected.split(":")[0]);

            new ResultDetailsDialog(studentId, quizId).setVisible(true);
        });
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

                JOptionPane.showMessageDialog(this, "Joined instructor: " + instructorName);
                loadInstructors();
                loadQuizzes();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid instructor code.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error joining instructor: " + e.getMessage());
        }
    }
}
