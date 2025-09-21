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
    private DefaultListModel<String> instructorListModel;
    private DefaultListModel<String> quizListModel;
    private JList<String> quizList;
    private DefaultListModel<String> resultListModel;
    private JList<String> resultList;

    public StudentDashboard(Main main, int studentId, String studentUsername) {
        setPreferredSize(new Dimension(904, 531));
        this.studentId = studentId;

        setLayout(new BorderLayout(0, 0));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(210, 180, 140));
        topBar.setPreferredSize(new Dimension(800, 50));

        JLabel titleLabel = new JLabel("Online Quiz");
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 20));
        titleLabel.setBorder(new EmptyBorder(0, 10, 0, 0));

        JLabel userLabel = new JLabel("Logged in: " + studentUsername);
        userLabel.setForeground(Color.BLACK);
        userLabel.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 15));

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

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 222, 179));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(mainPanel, BorderLayout.CENTER);

        JPanel joinPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        joinPanel.setBackground(new Color(222, 184, 135));
        JLabel codeLabel = new JLabel("Enter Instructor Code:");
        codeLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField codeField = new JTextField(10);
        codeField.setBackground(Color.WHITE);
        JButton joinButton = new JButton("Join Instructor");
        joinButton.setBackground(new Color(255, 235, 205));

        joinPanel.add(codeLabel);
        joinPanel.add(codeField);
        joinPanel.add(joinButton);
        mainPanel.add(joinPanel, BorderLayout.NORTH);

        JPanel contentArea = new JPanel(new GridLayout(1, 3, 15, 0));
        contentArea.setFont(new Font("Arial", Font.PLAIN, 10));
        contentArea.setBackground(new Color(245, 222, 179));
        contentArea.setBorder(new EmptyBorder(10, 0, 0, 0));
        mainPanel.add(contentArea, BorderLayout.CENTER);

        JPanel instructorPanel = new JPanel(new BorderLayout(10, 10));
        instructorPanel.setBackground(new Color(233, 150, 122));
        JLabel instructorHeader = new JLabel("My Instructors", SwingConstants.CENTER);
        instructorHeader.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 16));
        instructorPanel.add(instructorHeader, BorderLayout.NORTH);

        instructorListModel = new DefaultListModel<>();
        JList<String> instructorList = new JList<>(instructorListModel);
        instructorList.setBackground(new Color(255, 250, 240));
        instructorPanel.add(new JScrollPane(instructorList), BorderLayout.CENTER);

        JPanel quizPanel = new JPanel(new BorderLayout(10, 10));
        quizPanel.setBackground(new Color(233, 150, 122));
        JLabel quizHeader = new JLabel("Available Quizzes", SwingConstants.CENTER);
        quizHeader.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 16));
        quizPanel.add(quizHeader, BorderLayout.NORTH);

        quizListModel = new DefaultListModel<>();
        quizList = new JList<>(quizListModel);
        quizList.setBackground(new Color(255, 250, 240));
        quizPanel.add(new JScrollPane(quizList), BorderLayout.CENTER);

        JButton takeQuizButton = new JButton("Take Quiz");
        takeQuizButton.setFont(new Font("Arial", Font.PLAIN, 12));
        takeQuizButton.setBackground(new Color(255, 250, 240));
        quizPanel.add(takeQuizButton, BorderLayout.SOUTH);

        JPanel resultPanel = new JPanel(new BorderLayout(10, 10));
        resultPanel.setBackground(new Color(233, 150, 122));
        JLabel resultHeader = new JLabel("My Quiz Results", SwingConstants.CENTER);
        resultHeader.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 16));
        resultPanel.add(resultHeader, BorderLayout.NORTH);

        resultListModel = new DefaultListModel<>();
        resultList = new JList<>(resultListModel);
        resultList.setBackground(new Color(255, 250, 240));
        resultPanel.add(new JScrollPane(resultList), BorderLayout.CENTER);

        JButton viewResultButton = new JButton("View Result");
        viewResultButton.setFont(new Font("Arial", Font.PLAIN, 12));
        viewResultButton.setBackground(new Color(255, 250, 240));
        resultPanel.add(viewResultButton, BorderLayout.SOUTH);

        contentArea.add(instructorPanel);
        contentArea.add(quizPanel);
        contentArea.add(resultPanel);

        loadInstructors();
        loadQuizzes();
        loadResults();

        joinButton.addActionListener((ActionEvent e) -> {
            String code = codeField.getText().trim();
            if (code.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an instructor code.");
                return;
            }
            joinInstructor(code);
        });

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
            loadResults();
        });

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
