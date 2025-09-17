package onlinequizsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class InstructorResultsPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private InstructorDashboard dashboard;

    public InstructorResultsPanel(InstructorDashboard dashboard) {
        this.dashboard = dashboard;

        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{"Result ID", "Student", "Quiz", "Score", "Taken At"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JButton viewDetailsBtn = new JButton("View Details");
        viewDetailsBtn.addActionListener(e -> openResultDetails());
        add(viewDetailsBtn, BorderLayout.SOUTH);

        loadResults();
    }

    private void loadResults() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT r.results_id, u.full_name AS student_name, q.title AS quiz_title, " +
                         "r.score, r.taken_at " +
                         "FROM results r " +
                         "JOIN users u ON r.student_id = u.id " +
                         "JOIN quizzes q ON r.quiz_id = q.quiz_id " +
                         "ORDER BY r.taken_at DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            model.setRowCount(0); // clear table
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("results_id"),
                        rs.getString("student_name"),
                        rs.getString("quiz_title"),
                        rs.getInt("score"),
                        rs.getTimestamp("taken_at")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading results: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void openResultDetails() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a result first.");
            return;
        }

        int resultId = (int) model.getValueAt(selectedRow, 0);
        String studentName = (String) model.getValueAt(selectedRow, 1);
        String quizTitle = (String) model.getValueAt(selectedRow, 2);

        new InstructorResultDetailsDialog(resultId, studentName, quizTitle).setVisible(true);
    }
}
