package onlinequizsystem;

import java.awt.BorderLayout;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class StudentResultsDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    //private final JPanel contentPanel = new JPanel();
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private int studentId;

    /**
     * Launch the application for testing
     */
    public static void main(String[] args) {
        try {
            StudentResultsDialog dialog = new StudentResultsDialog(1); 
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
        setSize(700, 450);
        setLocationRelativeTo(null);
        setModal(true);
        getContentPane().setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(210, 180, 140));
        topBar.setPreferredSize(new Dimension(700, 50));

        JLabel titleLabel = new JLabel("📊 My Quiz Results", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLACK);

        topBar.add(titleLabel, BorderLayout.CENTER);
        getContentPane().add(topBar, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(255, 250, 240));
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        tableModel = new DefaultTableModel(new String[]{"Quiz ID", "Quiz Title", "Score", "Date Taken"}, 0) {
            
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        resultsTable = new JTable(tableModel);
        resultsTable.setRowHeight(28);
        resultsTable.getTableHeader().setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 13));
        resultsTable.getTableHeader().setBackground(new Color(222, 184, 135));
        resultsTable.getTableHeader().setForeground(Color.BLACK);
        resultsTable.setFont(new Font("Arial", Font.PLAIN, 13));
        resultsTable.setSelectionBackground(new Color(210, 180, 140));
        resultsTable.setSelectionForeground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setBackground(new Color(255, 250, 240));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(210, 180, 140)));
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(new Color(245, 222, 179));

        JButton viewDetailsButton = new JButton("View Details");
        viewDetailsButton.setFont(new Font("Arial", Font.PLAIN, 12));
        viewDetailsButton.setBackground(new Color(255, 250, 240));
        viewDetailsButton.addActionListener(e -> openResultDetails());
        footerPanel.add(viewDetailsButton);

        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.PLAIN, 12));
        closeButton.setBackground(new Color(255, 250, 240));
        closeButton.addActionListener(e -> dispose());
        footerPanel.add(closeButton);

        getContentPane().add(footerPanel, BorderLayout.SOUTH);
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
        ResultDetailsDialog dialog = new ResultDetailsDialog(studentId, quizId);
        dialog.setVisible(true);
    }
}
