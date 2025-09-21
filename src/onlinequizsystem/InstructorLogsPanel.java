package onlinequizsystem;

import javax.swing.JPanel;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class InstructorLogsPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTable logsTable;
    private DefaultTableModel tableModel;
    private int instructorId;

    public InstructorLogsPanel(int instructorId) {
    	setPreferredSize(new Dimension(904, 531));
        this.instructorId = instructorId;
        setBackground(new Color(245, 222, 179));
        setLayout(new BorderLayout(0, 0));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(205, 133, 63));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JLabel title = new JLabel("Quiz Activity Logs", JLabel.LEFT);
        title.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 18));
        title.setForeground(Color.BLACK);

        titlePanel.add(title, BorderLayout.WEST);
        add(titlePanel);

        tableModel = new DefaultTableModel(
                new String[]{"Student Name", "Email", "Quiz Title", "Action", "Timestamp"}, 0
        );
        logsTable = new JTable(tableModel);
        logsTable.setFillsViewportHeight(true);
        logsTable.setRowHeight(28);
        logsTable.setShowGrid(true);
        logsTable.setGridColor(new Color(210, 180, 140));
        logsTable.setBackground(new Color(255, 250, 240));
        logsTable.setFont(new Font("Arial", Font.PLAIN, 13));
        logsTable.getTableHeader().setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 14));
        logsTable.getTableHeader().setBackground(new Color(255, 250, 240));
        logsTable.getTableHeader().setForeground(Color.BLACK);

        ((DefaultTableCellRenderer) logsTable.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(SwingConstants.CENTER);

        JScrollPane scrollPane = new JScrollPane(logsTable);
        scrollPane.setBackground(new Color(233, 150, 122));
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane);

        loadLogs();
    }

    private void loadLogs() {
        tableModel.setRowCount(0);

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT u.full_name, u.email, q.title, l.action, l.timestamp " +
                         "FROM quiz_logs l " +
                         "JOIN users u ON l.user_id = u.id " +
                         "JOIN quizzes q ON l.quiz_id = q.quiz_id " +
                         "WHERE q.created_by = ? " +
                         "ORDER BY l.timestamp DESC";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, instructorId);
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
