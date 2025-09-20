package onlinequizsystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class InstructorResultsPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTable table;
    private DefaultTableModel model;
    private int instructorId; // store instructor ID

    public InstructorResultsPanel(int instructorId) {
        this.instructorId = instructorId;
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Quiz Results", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Table
        model = new DefaultTableModel(new String[]{
                "Result ID", "Student Name", "Quiz Title", "Score", "Taken At"
        }, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("Refresh");
        JButton viewDetailsButton = new JButton("View Details");
        buttonPanel.add(refreshButton);
        buttonPanel.add(viewDetailsButton);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> loadResults());
        viewDetailsButton.addActionListener(e -> openResultDetails());

        // Load initial data
        loadResults();
    }

    private void loadResults() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT r.results_id, u.full_name, q.title, r.score, r.taken_at " +
                         "FROM results r " +
                         "JOIN users u ON r.student_id = u.id " +
                         "JOIN quizzes q ON r.quiz_id = q.quiz_id " +
                         "WHERE u.role = 'student' AND q.created_by = ? " +
                         "ORDER BY r.taken_at DESC";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, instructorId);
            ResultSet rs = stmt.executeQuery();

            model.setRowCount(0); // clear previous data
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("results_id"),
                        rs.getString("full_name"),
                        rs.getString("title"),
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
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a result first!");
            return;
        }

        int resultId = (int) model.getValueAt(row, 0);
        String studentName = (String) model.getValueAt(row, 1);
        String quizTitle = (String) model.getValueAt(row, 2);

        InstructorResultDetailsDialog dialog =
                new InstructorResultDetailsDialog(resultId, studentName, quizTitle, instructorId);
        dialog.setVisible(true);
    }
}
