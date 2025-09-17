package onlinequizsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class InstructorLogsPanel extends JPanel {
    private JTable logsTable;
    private DefaultTableModel tableModel;

    public InstructorLogsPanel() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("📊 Quiz Activity Logs", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        // Table setup
        tableModel = new DefaultTableModel(
                new String[]{"Student Name", "Email", "Quiz Title", "Action", "Timestamp"}, 0
        );
        logsTable = new JTable(tableModel);
        logsTable.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(logsTable);
        add(scrollPane, BorderLayout.CENTER);

        loadLogs();
    }

    /** Load quiz activity logs */
    private void loadLogs() {
        tableModel.setRowCount(0); // clear previous

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT u.full_name, u.email, q.title, l.action, l.timestamp " +
                         "FROM quiz_logs l " +
                         "JOIN users u ON l.user_id = u.id " +
                         "JOIN quizzes q ON l.quiz_id = q.quiz_id " +
                         "ORDER BY l.timestamp DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("title"),
                        rs.getString("action"),
                        rs.getTimestamp("timestamp")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading quiz logs: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
