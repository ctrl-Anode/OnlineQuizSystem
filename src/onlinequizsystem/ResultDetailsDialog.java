package onlinequizsystem;

import java.awt.BorderLayout;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ResultDetailsDialog extends JDialog {
    private static final long serialVersionUID = 1L;
   // private final JPanel contentPanel = new JPanel();
    private int studentId;
    private int quizId;
    /**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ResultDetailsDialog dialog = new ResultDetailsDialog(0, 0);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
    public ResultDetailsDialog(int studentId, int quizId) {
        this.studentId = studentId;
        this.quizId = quizId;

        setTitle("Quiz Result Details");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setModal(true);

        getContentPane().setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(210, 180, 140));
        topBar.setPreferredSize(new Dimension(700, 50));

        JLabel titleLabel = new JLabel("Quiz Result Details", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLACK);

        topBar.add(titleLabel, BorderLayout.CENTER);
        getContentPane().add(topBar, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(new Color(255, 250, 240)); 
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(new Color(245, 222, 179));
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.PLAIN, 12));
        closeButton.setBackground(new Color(255, 250, 240));
        closeButton.addActionListener(e -> dispose());
        footerPanel.add(closeButton);
        getContentPane().add(footerPanel, BorderLayout.SOUTH);

        loadResultDetails(contentPanel);
    }

    private void loadResultDetails(JPanel contentPanel) {
        try (Connection conn = DBConnection.getConnection()) {
            
            String sqlQuestions = "SELECT question_id, question_text FROM questions WHERE quiz_id = ?";
            PreparedStatement stmtQ = conn.prepareStatement(sqlQuestions);
            stmtQ.setInt(1, quizId);
            ResultSet rsQ = stmtQ.executeQuery();

            int qNum = 1;
            while (rsQ.next()) {
                int questionId = rsQ.getInt("question_id");
                String questionText = rsQ.getString("question_text");

                JLabel questionLabel = new JLabel("Q" + qNum + ": " + questionText);
                questionLabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 14));
                questionLabel.setForeground(new Color(70, 70, 70));
                questionLabel.setBorder(new EmptyBorder(8, 0, 5, 0));
                contentPanel.add(questionLabel);

                String sqlOptions = "SELECT option_id, option_text, is_correct FROM options WHERE question_id = ?";
                PreparedStatement stmtOpt = conn.prepareStatement(sqlOptions);
                stmtOpt.setInt(1, questionId);
                ResultSet rsOpt = stmtOpt.executeQuery();

                String sqlStudent = "SELECT option_id FROM student_answers " +
                        "WHERE student_id = ? AND quiz_id = ? AND question_id = ?";
                PreparedStatement stmtStu = conn.prepareStatement(sqlStudent);
                stmtStu.setInt(1, studentId);
                stmtStu.setInt(2, quizId);
                stmtStu.setInt(3, questionId);
                ResultSet rsStu = stmtStu.executeQuery();
                Integer studentOptionId = rsStu.next() ? rsStu.getInt("option_id") : null;

                while (rsOpt.next()) {
                    int optionId = rsOpt.getInt("option_id");
                    String optionText = rsOpt.getString("option_text");
                    boolean isCorrect = rsOpt.getBoolean("is_correct");

                    JLabel optionLabel = new JLabel(" • " + optionText);
                    optionLabel.setFont(new Font("Arial", Font.PLAIN, 13));

                    if (isCorrect) {
                        optionLabel.setForeground(new Color(0, 128, 0)); 
                        optionLabel.setText(optionLabel.getText() + " ✅");
                    }
                    if (studentOptionId != null && studentOptionId == optionId) {
                        optionLabel.setFont(new Font("Arial", Font.BOLD, 13));
                        optionLabel.setForeground(isCorrect ? new Color(0, 128, 0) : Color.RED);
                        optionLabel.setText(optionLabel.getText() + " (Your Answer)");
                    }

                    contentPanel.add(optionLabel);
                }

                contentPanel.add(Box.createVerticalStrut(15));
                qNum++;
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading details: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
