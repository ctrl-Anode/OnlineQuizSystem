package onlinequizsystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ResultDetailsDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    
    // Modern color palette
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color WARNING_COLOR = new Color(243, 156, 18);
    private static final Color BACKGROUND_COLOR = new Color(247, 249, 252);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color MUTED_COLOR = new Color(127, 140, 141);
    
    private int studentId;
    private int quizId;
    private int totalQuestions = 0;
    private int correctAnswers = 0;

    public ResultDetailsDialog(int studentId, int quizId) {
        this.studentId = studentId;
        this.quizId = quizId;

        setTitle("📊 Quiz Result Details");
        setSize(850, 650);
        setLocationRelativeTo(null);
        setModal(true);
        setBackground(BACKGROUND_COLOR);
        getContentPane().setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        getContentPane().add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(BACKGROUND_COLOR);
        mainContentPanel.setBorder(new EmptyBorder(20, 20, 10, 20));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BACKGROUND_COLOR);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainContentPanel.add(scrollPane, BorderLayout.CENTER);

        getContentPane().add(mainContentPanel, BorderLayout.CENTER);

        // Footer Panel
        JPanel footerPanel = createFooterPanel();
        getContentPane().add(footerPanel, BorderLayout.SOUTH);

        // Load content
        loadResultDetails(contentPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        // Left side - title
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("📊 Quiz Result Analysis");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Detailed breakdown of your performance");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(255, 255, 255, 180));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subtitleLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        return headerPanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(BACKGROUND_COLOR);
        footerPanel.setBorder(new EmptyBorder(15, 25, 20, 25));

        // Left side - legend
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        legendPanel.setOpaque(false);

        JLabel legendTitle = new JLabel("Legend: ");
        legendTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        legendTitle.setForeground(MUTED_COLOR);

        JLabel correctLegend = new JLabel("✅ Correct Answer");
        correctLegend.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        correctLegend.setForeground(SUCCESS_COLOR);

        JLabel wrongLegend = new JLabel("❌ Your Wrong Answer");
        wrongLegend.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        wrongLegend.setForeground(DANGER_COLOR);

        JLabel skippedLegend = new JLabel("⚪ Not Answered");
        skippedLegend.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        skippedLegend.setForeground(MUTED_COLOR);

        legendPanel.add(legendTitle);
        legendPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        legendPanel.add(correctLegend);
        legendPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        legendPanel.add(wrongLegend);
        legendPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        legendPanel.add(skippedLegend);

        // Right side - close button
        JButton closeButton = createStyledButton("Close", PRIMARY_COLOR);
        closeButton.addActionListener(e -> dispose());

        footerPanel.add(legendPanel, BorderLayout.WEST);
        footerPanel.add(closeButton, BorderLayout.EAST);

        return footerPanel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
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

    private void loadResultDetails(JPanel contentPanel) {
        try (Connection conn = DBConnection.getConnection()) {
            // First, get overall score
            String scoreQuery = "SELECT score FROM results WHERE student_id = ? AND quiz_id = ?";
            PreparedStatement scoreStmt = conn.prepareStatement(scoreQuery);
            scoreStmt.setInt(1, studentId);
            scoreStmt.setInt(2, quizId);
            ResultSet scoreRs = scoreStmt.executeQuery();
            
            int totalScore = 0;
            if (scoreRs.next()) {
                totalScore = scoreRs.getInt("score");
            }

            // Get questions in quiz
            String sqlQuestions = "SELECT question_id, question_text FROM questions WHERE quiz_id = ? ORDER BY question_id";
            PreparedStatement stmtQ = conn.prepareStatement(sqlQuestions);
            stmtQ.setInt(1, quizId);
            ResultSet rsQ = stmtQ.executeQuery();

            int questionNumber = 1;
            while (rsQ.next()) {
                int questionId = rsQ.getInt("question_id");
                String questionText = rsQ.getString("question_text");
                totalQuestions++;

                // Create question card
                JPanel questionCard = createQuestionCard(conn, questionId, questionText, questionNumber);
                contentPanel.add(questionCard);
                contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

                questionNumber++;
            }

            // Add summary card at the top
            JPanel summaryCard = createSummaryCard(totalScore);
            contentPanel.add(summaryCard, 0);
            contentPanel.add(Box.createRigidArea(new Dimension(0, 20)), 1);

            contentPanel.revalidate();
            contentPanel.repaint();

        } catch (Exception e) {
            showStyledMessage("Error loading details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private JPanel createSummaryCard(int score) {
        JPanel summaryCard = new JPanel(new BorderLayout());
        summaryCard.setBackground(CARD_COLOR);
        summaryCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        // Score section
        JPanel scorePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        scorePanel.setOpaque(false);

        JLabel scoreLabel = new JLabel("Final Score: " + score + "/" + totalQuestions);
        scoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        scoreLabel.setForeground(TEXT_COLOR);

        double percentage = totalQuestions > 0 ? (double) score / totalQuestions * 100 : 0;
        JLabel percentageLabel = new JLabel(String.format("(%.1f%%)", percentage));
        percentageLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        Color percentageColor;
        if (percentage >= 90) percentageColor = SUCCESS_COLOR;
        else if (percentage >= 70) percentageColor = WARNING_COLOR;
        else percentageColor = DANGER_COLOR;
        
        percentageLabel.setForeground(percentageColor);

        scorePanel.add(scoreLabel);
        scorePanel.add(Box.createRigidArea(new Dimension(10, 0)));
        scorePanel.add(percentageLabel);

        summaryCard.add(scorePanel, BorderLayout.CENTER);

        return summaryCard;
    }

    private JPanel createQuestionCard(Connection conn, int questionId, String questionText, int questionNumber) 
            throws Exception {
        JPanel questionCard = new JPanel();
        questionCard.setLayout(new BoxLayout(questionCard, BoxLayout.Y_AXIS));
        questionCard.setBackground(CARD_COLOR);
        questionCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 221, 225), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Question header
        JLabel questionHeader = new JLabel("Question " + questionNumber);
        questionHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        questionHeader.setForeground(PRIMARY_COLOR);
        questionHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Question text
        JLabel questionLabel = new JLabel("<html><div style='width: 700px;'>" + questionText + "</div></html>");
        questionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        questionLabel.setForeground(TEXT_COLOR);
        questionLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 15, 0));
        questionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        questionCard.add(questionHeader);
        questionCard.add(questionLabel);

        // Get student's answer
        String sqlStudent = "SELECT option_id FROM student_answers WHERE student_id = ? AND quiz_id = ? AND question_id = ?";
        PreparedStatement stmtStu = conn.prepareStatement(sqlStudent);
        stmtStu.setInt(1, studentId);
        stmtStu.setInt(2, quizId);
        stmtStu.setInt(3, questionId);
        ResultSet rsStu = stmtStu.executeQuery();
        Integer studentOptionId = null;
        if (rsStu.next()) {
            studentOptionId = rsStu.getInt("option_id");
        }

        // Get all options
        String sqlOptions = "SELECT option_id, option_text, is_correct FROM options WHERE question_id = ? ORDER BY option_id";
        PreparedStatement stmtOpt = conn.prepareStatement(sqlOptions);
        stmtOpt.setInt(1, questionId);
        ResultSet rsOpt = stmtOpt.executeQuery();

        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setOpaque(false);
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

        char optionLetter = 'A';
        boolean studentCorrect = false;
        
        while (rsOpt.next()) {
            int optionId = rsOpt.getInt("option_id");
            String optionText = rsOpt.getString("option_text");
            boolean isCorrect = rsOpt.getBoolean("is_correct");

            JPanel optionPanel = new JPanel(new BorderLayout());
            optionPanel.setOpaque(false);
            optionPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

            JLabel optionLabel = new JLabel(optionLetter + ". " + optionText);
            optionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            String statusIcon = "";
            Color optionColor = TEXT_COLOR;

            if (isCorrect) {
                statusIcon = " ✅";
                optionColor = SUCCESS_COLOR;
                optionLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            }

            if (studentOptionId != null && studentOptionId == optionId) {
                if (isCorrect) {
                    statusIcon += " (Your Answer)";
                    studentCorrect = true;
                } else {
                    statusIcon = " ❌ (Your Answer)";
                    optionColor = DANGER_COLOR;
                    optionLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                }
            }

            optionLabel.setText(optionLabel.getText() + statusIcon);
            optionLabel.setForeground(optionColor);

            optionPanel.add(optionLabel, BorderLayout.WEST);
            optionsPanel.add(optionPanel);
            optionLetter++;
        }

        // Add result indicator
        if (studentCorrect) {
            correctAnswers++;
        }

        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setOpaque(false);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel statusLabel;
        if (studentOptionId == null) {
            statusLabel = new JLabel("⚪ Not Answered");
            statusLabel.setForeground(MUTED_COLOR);
        } else if (studentCorrect) {
            statusLabel = new JLabel("✅ Correct");
            statusLabel.setForeground(SUCCESS_COLOR);
        } else {
            statusLabel = new JLabel("❌ Incorrect");
            statusLabel.setForeground(DANGER_COLOR);
        }
        
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusPanel.add(statusLabel);

        questionCard.add(optionsPanel);
        questionCard.add(statusPanel);

        return questionCard;
    }

    private void showStyledMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}
