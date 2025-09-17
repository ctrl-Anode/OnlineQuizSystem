package onlinequizsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StudentResultsDialog extends JDialog {
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private int studentId;

    public StudentResultsDialog(int studentId) {
        this.studentId = studentId;

        setTitle("My Quiz Results");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setModal(true);

        tableModel = new DefaultTableModel(new String[]{"Quiz ID", "Quiz Title", "Score", "Date Taken"}, 0);
        resultsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(resultsTable);

        JButton viewDetailsButton = new JButton("View Details");
        viewDetailsButton.addActionListener(e -> openResultDetails());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(viewDetailsButton);

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

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

            tableModel.setRowCount(0);
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
        new ResultDetailsDialog(studentId, quizId).setVisible(true);
    }
}
