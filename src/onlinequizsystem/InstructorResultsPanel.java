package onlinequizsystem;

import javax.swing.JPanel;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class InstructorResultsPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTable table;
    private DefaultTableModel model;
    private int instructorId; 

    public InstructorResultsPanel(int instructorId) {
        this.instructorId = instructorId;

        setPreferredSize(new Dimension(904, 531));
        setBackground(new Color(245, 222, 179));
        setLayout(new BorderLayout(0, 0));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(205, 133, 63));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JLabel titleLabel = new JLabel("Quiz Results", JLabel.LEFT);
        titleLabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 18));
        titleLabel.setForeground(Color.BLACK);

        titlePanel.add(titleLabel, BorderLayout.WEST);
        add(titlePanel, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{
                "Result ID", "Student Name", "Quiz Title", "Score", "Taken At"
        }, 0);

        table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setRowHeight(28);
        table.setShowGrid(true);
        table.setGridColor(new Color(210, 180, 140));
        table.setBackground(new Color(255, 250, 240));
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(255, 250, 240));
        table.getTableHeader().setForeground(Color.BLACK);

        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(SwingConstants.CENTER);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(new Color(233, 150, 122));
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        buttonPanel.setBackground(new Color(205, 133, 63));

        JButton refreshButton = new JButton("Refresh");
        JButton viewDetailsButton = new JButton("View Details");
        viewDetailsButton.setBackground(new Color(255, 250, 240));

        styleButton(refreshButton);
        styleButton(viewDetailsButton);

        refreshButton.addActionListener(e -> loadResults());
        viewDetailsButton.addActionListener(e -> openResultDetails());

        buttonPanel.add(refreshButton);
        buttonPanel.add(viewDetailsButton);

        add(buttonPanel, BorderLayout.SOUTH);

        loadResults();
    }

    private void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(new Color(255, 250, 240));
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Arial", Font.PLAIN, 13));
        button.setPreferredSize(new Dimension(120, 30));
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

            model.setRowCount(0); 
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
