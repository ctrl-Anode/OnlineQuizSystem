package onlinequizsystem;

import java.awt.BorderLayout;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class TakeQuizDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    //private final JPanel contentPanel = new JPanel();
    private JPanel questionPanel;
    private int quizId;
    private int studentId;
    private HashMap<Integer, ButtonGroup> answerGroups = new HashMap<>();

    /**
     * Launch the dialog for testing
     */
    public static void main(String[] args) {
        try {
            TakeQuizDialog dialog = new TakeQuizDialog(1, 1); 
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the dialog.
     */
    public TakeQuizDialog(int quizId, int studentId) {
        this.quizId = quizId;
        this.studentId = studentId;

        setTitle("Take Quiz");
        setSize(750, 600);
        setLocationRelativeTo(null);
        setModal(true);
        getContentPane().setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(210, 180, 140)); 
        topBar.setPreferredSize(new Dimension(700, 50));

        JLabel titleLabel = new JLabel("Take Quiz", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLACK);

        topBar.add(titleLabel, BorderLayout.CENTER);
        getContentPane().add(topBar, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(255, 250, 240)); 
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        questionPanel = new JPanel();
        questionPanel.setBackground(new Color(255, 250, 240));
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(questionPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(210, 180, 140)));
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(new Color(245, 222, 179));

        JButton submitButton = new JButton("Submit Quiz");
        submitButton.setFont(new Font("Arial", Font.PLAIN, 13));
        submitButton.setBackground(new Color(255, 235, 205));
        submitButton.addActionListener(e -> submitQuiz());
        footerPanel.add(submitButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 13));
        cancelButton.setBackground(new Color(255, 235, 205));
        cancelButton.addActionListener(e -> dispose());
        footerPanel.add(cancelButton);

        getContentPane().add(footerPanel, BorderLayout.SOUTH);

        if (alreadyAttempted()) {
            JOptionPane.showMessageDialog(this, "You already took this quiz!", "Warning", JOptionPane.WARNING_MESSAGE);
            dispose();
            return;
        }

        logQuizAction("started");

        loadQuestions();
    }

    private boolean alreadyAttempted() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT 1 FROM results WHERE student_id = ? AND quiz_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            stmt.setInt(2, quizId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

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
                qLabel.setBorder(new EmptyBorder(8, 0, 4, 0));

                JPanel qPanel = new JPanel();
                qPanel.setLayout(new BoxLayout(qPanel, BoxLayout.Y_AXIS));
                qPanel.setBackground(new Color(255, 248, 220));
                qPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(210, 180, 140), 1),
                        new EmptyBorder(8, 8, 8, 8)
                ));
                qPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                qPanel.add(qLabel);

                ButtonGroup group = new ButtonGroup();
                PreparedStatement optStmt = conn.prepareStatement(
                        "SELECT option_id, option_text FROM options WHERE question_id = ?"
                );
                optStmt.setInt(1, questionId);
                ResultSet optRs = optStmt.executeQuery();

                while (optRs.next()) {
                    int optionId = optRs.getInt("option_id");
                    String optionText = optRs.getString("option_text");

                    JRadioButton optionBtn = new JRadioButton(optionText);
                    optionBtn.setFont(new Font("Arial", Font.PLAIN, 13));
                    optionBtn.setBackground(new Color(255, 248, 220));
                    optionBtn.setActionCommand(String.valueOf(optionId));

                    group.add(optionBtn);
                    qPanel.add(optionBtn);
                }

                answerGroups.put(questionId, group);
                questionPanel.add(qPanel);
                questionPanel.add(Box.createVerticalStrut(10)); 

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
                if (group.getSelection() == null) continue;

                int selectedOptionId = Integer.parseInt(group.getSelection().getActionCommand());

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

                String insertAns = "INSERT INTO student_answers (student_id, quiz_id, question_id, option_id, answer_text, submitted_at) VALUES (?, ?, ?, ?, ?, NOW())";
                PreparedStatement insertStmt = conn.prepareStatement(insertAns);
                insertStmt.setInt(1, studentId);
                insertStmt.setInt(2, quizId);
                insertStmt.setInt(3, questionId);
                insertStmt.setInt(4, selectedOptionId);
                insertStmt.setString(5, optionText);
                insertStmt.executeUpdate();

                if (isCorrect) score++;
            }

            String insertResult = "INSERT INTO results (student_id, quiz_id, score, taken_at) VALUES (?, ?, ?, NOW())";
            PreparedStatement resultStmt = conn.prepareStatement(insertResult);
            resultStmt.setInt(1, studentId);
            resultStmt.setInt(2, quizId);
            resultStmt.setInt(3, score);
            resultStmt.executeUpdate();

            logQuizAction("submitted");

            JOptionPane.showMessageDialog(this, "Quiz Submitted! Score: " + score + " / " + total);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error submitting quiz: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
