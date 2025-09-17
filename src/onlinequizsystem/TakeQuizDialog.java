package onlinequizsystem;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

public class TakeQuizDialog extends JDialog {
    private final int quizId;
    private final int studentId;
    private final JPanel questionPanel;
    private final HashMap<Integer, ButtonGroup> answerGroups = new HashMap<>();

    public TakeQuizDialog(int quizId, int studentId) {
        super((Frame) null, "Take Quiz", true);
        this.quizId = quizId;
        this.studentId = studentId;

        setSize(700, 600);
        setLocationRelativeTo(null);

        questionPanel = new JPanel();
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
        questionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(questionPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JButton submitButton = new JButton("Submit Quiz");
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitButton.setBackground(new Color(46, 139, 87));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.addActionListener(e -> submitQuiz());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(submitButton);

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // ✅ Check if student already took this quiz
        if (alreadyAttempted()) {
            JOptionPane.showMessageDialog(this,
                    "⚠️ You have already taken this quiz.\nYou cannot attempt it again.",
                    "Quiz Attempted", JOptionPane.WARNING_MESSAGE);
            dispose();
            return;
        }

        // ✅ Log when quiz is opened
        logQuizAction("started");

        loadQuestions();
    }

    /** Check if student already has a result entry for this quiz */
    private boolean alreadyAttempted() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT 1 FROM results WHERE student_id = ? AND quiz_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            stmt.setInt(2, quizId);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // true if result exists
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Log actions in quiz_logs table */
    private void logQuizAction(String action) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO quiz_logs (user_id, quiz_id, action, timestamp) VALUES (?, ?, ?, NOW())";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            stmt.setInt(2, quizId);
            stmt.setString(3, action);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadQuestions() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT question_id, question_text FROM questions WHERE quiz_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, quizId);
            ResultSet rs = stmt.executeQuery();

            int qNum = 1;
            while (rs.next()) {
                int questionId = rs.getInt("question_id");
                String questionText = rs.getString("question_text");

                JLabel qLabel = new JLabel("Q" + qNum + ": " + questionText);
                qLabel.setFont(new Font("Arial", Font.BOLD, 14));
                qLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

                JPanel qPanel = new JPanel();
                qPanel.setLayout(new BoxLayout(qPanel, BoxLayout.Y_AXIS));
                qPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                qPanel.add(qLabel);

                ButtonGroup group = new ButtonGroup();

                PreparedStatement optStmt = conn.prepareStatement(
                        "SELECT option_id, option_text FROM options WHERE question_id = ?"
                );
                optStmt.setInt(1, questionId);
                ResultSet optRs = optStmt.executeQuery();

                JPanel optionPanel = new JPanel();
                optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.Y_AXIS));
                optionPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 0));

                while (optRs.next()) {
                    int optionId = optRs.getInt("option_id");
                    String optionText = optRs.getString("option_text");

                    JRadioButton optionBtn = new JRadioButton(optionText);
                    optionBtn.setFont(new Font("Arial", Font.PLAIN, 13));
                    optionBtn.setActionCommand(String.valueOf(optionId));
                    group.add(optionBtn);
                    optionPanel.add(optionBtn);
                }

                qPanel.add(optionPanel);
                answerGroups.put(questionId, group);
                questionPanel.add(qPanel);

                qNum++;
            }

            questionPanel.revalidate();
            questionPanel.repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading questions: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void submitQuiz() {
        int score = 0;
        int total = answerGroups.size();

        try (Connection conn = DBConnection.getConnection()) {
            for (Integer questionId : answerGroups.keySet()) {
                ButtonGroup group = answerGroups.get(questionId);
                if (group.getSelection() == null) continue; // no answer selected

                int selectedOptionId = Integer.parseInt(group.getSelection().getActionCommand());

                // get option text for record
                String optSql = "SELECT option_text, is_correct FROM options WHERE option_id = ?";
                PreparedStatement optStmt = conn.prepareStatement(optSql);
                optStmt.setInt(1, selectedOptionId);
                ResultSet optRs = optStmt.executeQuery();
                String optionText = "";
                boolean isCorrect = false;
                if (optRs.next()) {
                    optionText = optRs.getString("option_text");
                    isCorrect = optRs.getBoolean("is_correct");
                }

                // save student answer
                String insertAns = "INSERT INTO student_answers (student_id, quiz_id, question_id, option_id, answer_text, submitted_at) " +
                                   "VALUES (?, ?, ?, ?, ?, NOW())";
                PreparedStatement insertStmt = conn.prepareStatement(insertAns);
                insertStmt.setInt(1, studentId);
                insertStmt.setInt(2, quizId);
                insertStmt.setInt(3, questionId);
                insertStmt.setInt(4, selectedOptionId);
                insertStmt.setString(5, optionText);
                insertStmt.executeUpdate();

                if (isCorrect) score++;
            }

            // save overall result
            String insertResult = "INSERT INTO results (student_id, quiz_id, score, taken_at) VALUES (?, ?, ?, NOW())";
            PreparedStatement resultStmt = conn.prepareStatement(insertResult);
            resultStmt.setInt(1, studentId);
            resultStmt.setInt(2, quizId);
            resultStmt.setInt(3, score);
            resultStmt.executeUpdate();

            // ✅ Log quiz submission
            logQuizAction("submitted");

            JOptionPane.showMessageDialog(this, "✅ Quiz Submitted!\nYour Score: " + score + " / " + total);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error submitting quiz: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
