package onlinequizsystem;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Component;
import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class TakeQuizDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	// Modern color palette
	private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
	private static final Color SECONDARY_COLOR = new Color(52, 73, 94);
	private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
	private static final Color DANGER_COLOR = new Color(231, 76, 60);
	private static final Color BACKGROUND_COLOR = new Color(247, 249, 252);
	private static final Color CARD_COLOR = Color.WHITE;
	private static final Color TEXT_COLOR = new Color(44, 62, 80);
	private static final Color MUTED_COLOR = new Color(127, 140, 141);

	private int quizId;
	private int studentId;
	private JPanel questionPanel;
	private HashMap<Integer, ButtonGroup> answerGroups = new HashMap<>();
	private JLabel progressLabel;
	private int totalQuestions = 0;

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

		setTitle("📝 Take Quiz");
		setBounds(100, 100, 800, 700);
		setBackground(BACKGROUND_COLOR);
		getContentPane().setLayout(new BorderLayout());

		// Header Panel
		JPanel headerPanel = createHeaderPanel();
		getContentPane().add(headerPanel, BorderLayout.NORTH);

		// Content Panel
		contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
		contentPanel.setLayout(new BorderLayout());
		contentPanel.setBackground(BACKGROUND_COLOR);
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		// Question panel with modern scroll
		questionPanel = new JPanel();
		questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
		questionPanel.setBackground(BACKGROUND_COLOR);
		
		JScrollPane scrollPane = new JScrollPane(questionPanel);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		contentPanel.add(scrollPane, BorderLayout.CENTER);

		// Button panel
		JPanel buttonPane = createButtonPanel();
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		// Check if already attempted
		if (alreadyAttempted()) {
			showStyledMessage("You have already completed this quiz!", "Quiz Completed", JOptionPane.INFORMATION_MESSAGE);
			dispose();
			return;
		}

		// Log quiz start
		logQuizAction("started");

		// Load questions
		loadQuestions();
	}

	private JPanel createHeaderPanel() {
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setBackground(PRIMARY_COLOR);
		headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

		// Title section
		JPanel titlePanel = new JPanel(new BorderLayout());
		titlePanel.setOpaque(false);

		JLabel titleLabel = new JLabel("📝 Quiz Assessment");
		titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
		titleLabel.setForeground(Color.WHITE);

		JLabel subtitleLabel = new JLabel("Answer all questions carefully");
		subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		subtitleLabel.setForeground(new Color(255, 255, 255, 180));

		titlePanel.add(titleLabel, BorderLayout.NORTH);
		titlePanel.add(subtitleLabel, BorderLayout.SOUTH);

		// Progress section
		progressLabel = new JLabel("Progress: 0/0 questions");
		progressLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
		progressLabel.setForeground(Color.WHITE);
		progressLabel.setHorizontalAlignment(SwingConstants.RIGHT);

		headerPanel.add(titlePanel, BorderLayout.WEST);
		headerPanel.add(progressLabel, BorderLayout.EAST);

		return headerPanel;
	}

	private JPanel createButtonPanel() {
		JPanel buttonPane = new JPanel(new BorderLayout());
		buttonPane.setBackground(BACKGROUND_COLOR);
		buttonPane.setBorder(BorderFactory.createEmptyBorder(20, 25, 25, 25));

		// Left side - info
		JLabel infoLabel = new JLabel("💡 Select one answer per question");
		infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
		infoLabel.setForeground(MUTED_COLOR);

		// Right side - buttons
		JPanel buttonGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		buttonGroup.setOpaque(false);

		JButton cancelButton = createStyledButton("Cancel", SECONDARY_COLOR);
		cancelButton.addActionListener(e -> {
			int choice = JOptionPane.showConfirmDialog(this, 
				"Are you sure you want to cancel? Your progress will be lost.", 
				"Cancel Quiz", JOptionPane.YES_NO_OPTION);
			if (choice == JOptionPane.YES_OPTION) {
				logQuizAction("cancelled");
				dispose();
			}
		});

		JButton submitButton = createStyledButton("Submit Quiz", SUCCESS_COLOR);
		submitButton.addActionListener(e -> confirmAndSubmit());

		buttonGroup.add(cancelButton);
		buttonGroup.add(submitButton);

		buttonPane.add(infoLabel, BorderLayout.WEST);
		buttonPane.add(buttonGroup, BorderLayout.EAST);

		return buttonPane;
	}

	private JButton createStyledButton(String text, Color bgColor) {
		JButton button = new JButton(text);
		button.setFont(new Font("Segoe UI", Font.BOLD, 14));
		button.setBackground(bgColor);
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		button.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.setPreferredSize(new Dimension(140, 45));

		// Hover effect
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

				// Question card
				JPanel questionCard = new JPanel();
				questionCard.setLayout(new BoxLayout(questionCard, BoxLayout.Y_AXIS));
				questionCard.setBackground(CARD_COLOR);
				questionCard.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createLineBorder(new Color(220, 221, 225), 1),
					BorderFactory.createEmptyBorder(20, 20, 20, 20)
				));
				questionCard.setAlignmentX(Component.LEFT_ALIGNMENT);

				// Question header
				JLabel questionLabel = new JLabel("Question " + qNum);
				questionLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
				questionLabel.setForeground(PRIMARY_COLOR);

				// Question text
				JLabel questionTextLabel = new JLabel("<html><div style='width: 650px;'>" + questionText + "</div></html>");
				questionTextLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
				questionTextLabel.setForeground(TEXT_COLOR);
				questionTextLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 15, 0));

				questionCard.add(questionLabel);
				questionCard.add(questionTextLabel);

				// Options panel
				JPanel optionsPanel = new JPanel();
				optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
				optionsPanel.setOpaque(false);
				optionsPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

				ButtonGroup group = new ButtonGroup();

				PreparedStatement optStmt = conn.prepareStatement(
					"SELECT option_id, option_text FROM options WHERE question_id = ?"
				);
				optStmt.setInt(1, questionId);
				ResultSet optRs = optStmt.executeQuery();

				char optionLetter = 'A';
				while (optRs.next()) {
					int optionId = optRs.getInt("option_id");
					String optionText = optRs.getString("option_text");

					JRadioButton optionBtn = new JRadioButton(optionLetter + ". " + optionText);
					optionBtn.setActionCommand(String.valueOf(optionId));
					optionBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
					optionBtn.setForeground(TEXT_COLOR);
					optionBtn.setOpaque(false);
					optionBtn.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
					optionBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
					
					// Style the radio button
					optionBtn.setFocusPainted(false);

					group.add(optionBtn);
					optionsPanel.add(optionBtn);
					optionLetter++;
				}

				questionCard.add(optionsPanel);
				answerGroups.put(questionId, group);
				questionPanel.add(questionCard);
				
				// Add spacing between questions
				questionPanel.add(javax.swing.Box.createRigidArea(new Dimension(0, 15)));

				qNum++;
			}

			totalQuestions = qNum - 1;
			updateProgress();
			questionPanel.revalidate();
			questionPanel.repaint();
		} catch (Exception e) {
			showStyledMessage("Error loading questions: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private void updateProgress() {
		progressLabel.setText("Progress: 0/" + totalQuestions + " questions");
	}

	private void confirmAndSubmit() {
		// Check if all questions are answered
		int answered = 0;
		for (ButtonGroup group : answerGroups.values()) {
			if (group.getSelection() != null) answered++;
		}

		if (answered < totalQuestions) {
			int choice = JOptionPane.showConfirmDialog(this,
				"You have only answered " + answered + " out of " + totalQuestions + " questions.\n" +
				"Unanswered questions will be marked as incorrect. Continue?",
				"Incomplete Quiz", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (choice != JOptionPane.YES_OPTION) return;
		}

		int finalChoice = JOptionPane.showConfirmDialog(this,
			"Are you sure you want to submit your quiz?\nThis action cannot be undone.",
			"Submit Quiz", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		
		if (finalChoice == JOptionPane.YES_OPTION) {
			submitQuiz();
		}
	}

	// Submit quiz with enhanced feedback
	private void submitQuiz() {
		int score = 0;
		int total = answerGroups.size();

		try (Connection conn = DBConnection.getConnection()) {
			for (Integer questionId : answerGroups.keySet()) {
				ButtonGroup group = answerGroups.get(questionId);
				
				if (group.getSelection() != null) {
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
			}

			// Save result
			String insertResult = "INSERT INTO results (student_id, quiz_id, score, taken_at) VALUES (?, ?, ?, NOW())";
			PreparedStatement resultStmt = conn.prepareStatement(insertResult);
			resultStmt.setInt(1, studentId);
			resultStmt.setInt(2, quizId);
			resultStmt.setInt(3, score);
			resultStmt.executeUpdate();

			logQuizAction("submitted");

			// Enhanced result message
			double percentage = (double) score / total * 100;
			String grade;
			if (percentage >= 90) grade = "Excellent! 🌟";
			else if (percentage >= 80) grade = "Good Job! 👍";
			else if (percentage >= 70) grade = "Well Done! ✅";
			else if (percentage >= 60) grade = "Keep Trying! 💪";
			else grade = "Need Improvement 📚";

			String message = String.format(
				"Quiz Completed Successfully!\n\n" +
				"Your Score: %d / %d (%.1f%%)\n" +
				"Grade: %s\n\n" +
				"Great effort! Keep learning!",
				score, total, percentage, grade
			);

			showStyledMessage(message, "Quiz Results", JOptionPane.INFORMATION_MESSAGE);
			dispose();
		} catch (Exception e) {
			showStyledMessage("Error submitting quiz: " + e.getMessage(), "Submission Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private void showStyledMessage(String message, String title, int messageType) {
		JOptionPane.showMessageDialog(this, message, title, messageType);
	}
}
