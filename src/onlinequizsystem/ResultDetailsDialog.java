package onlinequizsystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ResultDetailsDialog extends JDialog {
    private int studentId;
    private int quizId;

    public ResultDetailsDialog(int studentId, int quizId) {
        this.studentId = studentId;
        this.quizId = quizId;

        setTitle("Quiz Result Details");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setModal(true);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        add(scrollPane, BorderLayout.CENTER);

        loadResultDetails(contentPanel);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        add(closeButton, BorderLayout.SOUTH);
    }

    private void loadResultDetails(JPanel contentPanel) {
        try (Connection conn = DBConnection.getConnection()) {
            // Get questions in quiz
            String sqlQuestions = "SELECT question_id, question_text " +
                    "FROM questions WHERE quiz_id = ?";
            PreparedStatement stmtQ = conn.prepareStatement(sqlQuestions);
            stmtQ.setInt(1, quizId);
            ResultSet rsQ = stmtQ.executeQuery();

            while (rsQ.next()) {
                int questionId = rsQ.getInt("question_id");
                String questionText = rsQ.getString("question_text");

                // Create question label
                JLabel questionLabel = new JLabel("Q: " + questionText);
                questionLabel.setFont(new Font("Arial", Font.BOLD, 14));
                contentPanel.add(questionLabel);

                // Fetch all options
                String sqlOptions = "SELECT option_id, option_text, is_correct " +
                        "FROM options WHERE question_id = ?";
                PreparedStatement stmtOpt = conn.prepareStatement(sqlOptions);
                stmtOpt.setInt(1, questionId);
                ResultSet rsOpt = stmtOpt.executeQuery();

                // Fetch student's selected option
                String sqlStudent = "SELECT option_id FROM student_answers " +
                        "WHERE student_id = ? AND quiz_id = ? AND question_id = ?";
                PreparedStatement stmtStu = conn.prepareStatement(sqlStudent);
                stmtStu.setInt(1, studentId);
                stmtStu.setInt(2, quizId);
                stmtStu.setInt(3, questionId);
                ResultSet rsStu = stmtStu.executeQuery();
                Integer studentOptionId = null;
                if (rsStu.next()) {
                    studentOptionId = rsStu.getInt("option_id");
                }

                // Show all options with highlights
                while (rsOpt.next()) {
                    int optionId = rsOpt.getInt("option_id");
                    String optionText = rsOpt.getString("option_text");
                    boolean isCorrect = rsOpt.getBoolean("is_correct");

                    JLabel optionLabel = new JLabel(" - " + optionText);
                    optionLabel.setFont(new Font("Arial", Font.PLAIN, 13));

                    if (isCorrect) {
                        optionLabel.setForeground(new Color(0, 128, 0)); // green for correct
                        optionLabel.setText(optionLabel.getText() + " ✅");
                    }
                    if (studentOptionId != null && studentOptionId == optionId) {
                        optionLabel.setFont(new Font("Arial", Font.BOLD, 13));
                        optionLabel.setForeground(isCorrect ? new Color(0, 128, 0) : Color.RED);
                        optionLabel.setText(optionLabel.getText() + " (Your Answer)");
                    }

                    contentPanel.add(optionLabel);
                }

                contentPanel.add(Box.createVerticalStrut(10)); // spacing
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading details: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
