package onlinequizsystem;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.BoxLayout;
import javax.swing.border.EmptyBorder;

public class TakeQuizDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	private int quizId;
	private int studentId;
	private JPanel questionPanel;
	private HashMap<Integer, ButtonGroup> answerGroups = new HashMap<>();

	/**
	 * Launch the dialog for testing
	 */
	public static void main(String[] args) {
		try {
			TakeQuizDialog dialog = new TakeQuizDialog(1, 1); // test values
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
		setBounds(100, 100, 700, 600);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setLayout(new BorderLayout());
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		// Question panel with scroll
		questionPanel = new JPanel();
		questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
		JScrollPane scrollPane = new JScrollPane(questionPanel);
		contentPanel.add(scrollPane, BorderLayout.CENTER);

		// Button pane
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton submitButton = new JButton("Submit Quiz");
		submitButton.addActionListener(e -> submitQuiz());
		buttonPane.add(submitButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> dispose());
		buttonPane.add(cancelButton);

		// Check if already attempted
		if (alreadyAttempted()) {
			JOptionPane.showMessageDialog(this, "You already took this quiz!", "Warning", JOptionPane.WARNING_MESSAGE);
			dispose();
			return;
		}

		// Log quiz start
		logQuizAction("started");

		// Load questions
		loadQuestions();
	}

	// Check if quiz already taken
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

	// Log actions
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

	// Load quiz questions
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

				while (optRs.next()) {
					int optionId = optRs.getInt("option_id");
					String optionText = optRs.getString("option_text");

					JRadioButton optionBtn = new JRadioButton(optionText);
					optionBtn.setActionCommand(String.valueOf(optionId));
					group.add(optionBtn);
					qPanel.add(optionBtn);
				}

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

	// Submit quiz
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

				// Save student answer
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

			// Save result
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
