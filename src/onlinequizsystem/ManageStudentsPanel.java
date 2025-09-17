package onlinequizsystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ManageStudentsPanel extends JPanel {
    private Main main;
    private int instructorId;
    private JTable studentTable;
    private DefaultTableModel tableModel;

    public ManageStudentsPanel(Main main, int instructorId) {
        this.main = main;
        this.instructorId = instructorId;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Manage Students");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        add(titleLabel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Student ID");
        tableModel.addColumn("Full Name");
        tableModel.addColumn("Username");
        tableModel.addColumn("Email");
        tableModel.addColumn("Action");

        studentTable = new JTable(tableModel) {
            // Make Action column contain buttons
            public Class<?> getColumnClass(int column) {
                return column == 4 ? JButton.class : Object.class;
            }

            public boolean isCellEditable(int row, int column) {
                return column == 4; // only action button editable
            }
        };

        JScrollPane scrollPane = new JScrollPane(studentTable);
        add(scrollPane, BorderLayout.CENTER);

        // Load students
        loadStudents();

        // Add button click handling for "View Details"
        studentTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = studentTable.rowAtPoint(evt.getPoint());
                int col = studentTable.columnAtPoint(evt.getPoint());
                if (col == 4) { // Action column
                    int studentId = (int) studentTable.getValueAt(row, 0);
                    String fullName = (String) studentTable.getValueAt(row, 1);
                    String username = (String) studentTable.getValueAt(row, 2);
                    String email = (String) studentTable.getValueAt(row, 3);

                    // Show details dialog
                    JOptionPane.showMessageDialog(
                            main,
                            "Student Details:\n\n" +
                                    "ID: " + studentId + "\n" +
                                    "Full Name: " + fullName + "\n" +
                                    "Username: " + username + "\n" +
                                    "Email: " + email,
                            "Student Details",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }
        });
    }

    private void loadStudents() {
        tableModel.setRowCount(0); // Clear existing data

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT s.id, s.full_name, s.username, s.email " +
                         "FROM student_instructors si " +
                         "JOIN users s ON si.student_id = s.id " +
                         "WHERE si.instructor_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, instructorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int studentId = rs.getInt("id");
                String fullName = rs.getString("full_name");
                String username = rs.getString("username");
                String email = rs.getString("email");

                // Add row with a "View Details" button
                JButton viewButton = new JButton("View Details");
                tableModel.addRow(new Object[]{studentId, fullName, username, email, viewButton});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading students: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
