package onlinequizsystem;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class StudentResultsDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTable resultsTable;
	private DefaultTableModel tableModel;
	private int studentId;

	/**
	 * Launch the application for testing
	 */
	public static void main(String[] args) {
		try {
			StudentResultsDialog dialog = new StudentResultsDialog(1); // test with dummy studentId
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public StudentResultsDialog(int studentId) {
		this.studentId = studentId;

		setTitle("My Quiz Results");
		setBounds(100, 100, 600, 400);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setLayout(new BorderLayout());
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		// Table
		tableModel = new DefaultTableModel(new String[]{"Quiz ID", "Quiz Title", "Score", "Date Taken"}, 0);
		resultsTable = new JTable(tableModel);
		JScrollPane scrollPane = new JScrollPane(resultsTable);
		contentPanel.add(scrollPane, BorderLayout.CENTER);

		// Buttons
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton viewDetailsButton = new JButton("View Details");
		viewDetailsButton.addActionListener(e -> openResultDetails());
		buttonPane.add(viewDetailsButton);

		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(e -> dispose());
		buttonPane.add(closeButton);

		// Load results
		loadResults();
	}

	private void loadResults() {
		try (Connection conn = DBConnection.getConnection()) {
			String sql = "SELECT r.quiz_id, q.title, r.score, r.attempt_date " +
					"FROM results r " +
					"JOIN quizzes q ON r.quiz_id = q.quiz_id " +
					"WHERE r.student_id = ? ORDER BY r.attempt_date DESC";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, studentId);
			ResultSet rs = stmt.executeQuery();

			tableModel.setRowCount(0); // clear
			while (rs.next()) {
				tableModel.addRow(new Object[]{
					rs.getInt("quiz_id"),
					rs.getString("title"),
					rs.getInt("score"),
					rs.getTimestamp("attempt_date")
				});
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Error loading results: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void openResultDetails() {
		int selectedRow = resultsTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Please select a quiz result to view.");
			return;
		}

		int quizId = (int) tableModel.getValueAt(selectedRow, 0);
		ResultDetailsDialog dialog = new ResultDetailsDialog(studentId, quizId);
		dialog.setVisible(true);
	}
}
