package onlinequizsystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
//import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ManageStudentsPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private int instructorId;
    private JTable studentTable;
    private DefaultTableModel tableModel;

    public ManageStudentsPanel(Main main, int instructorId) {
        this.instructorId = instructorId;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Manage Students");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        add(titleLabel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new Object[]{"Student ID", "Full Name", "Username", "Email", "Action"}, 0);
        studentTable = new JTable(tableModel);

        // Use custom renderer + editor for button column
        studentTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        studentTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), main));

        JScrollPane scrollPane = new JScrollPane(studentTable);
        add(scrollPane, BorderLayout.CENTER);

        // Load students
        loadStudents();
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

                // Add row with placeholder button text
                tableModel.addRow(new Object[]{studentId, fullName, username, email, "View Details"});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading students: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

// Custom button renderer
class ButtonRenderer extends JButton implements TableCellRenderer {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ButtonRenderer() {
        setOpaque(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        setText((value == null) ? "" : value.toString());
        return this;
    }
}

// Custom button editor
class ButtonEditor extends DefaultCellEditor {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected JButton button;
    private String label;
    private boolean clicked;
    private JTable table;
    private Main main;

    public ButtonEditor(JCheckBox checkBox, Main main) {
        super(checkBox);
        this.main = main;
        button = new JButton();
        button.setOpaque(true);

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        this.table = table;
        label = (value == null) ? "" : value.toString();
        button.setText(label);
        clicked = true;
        return button;
    }

    public Object getCellEditorValue() {
        if (clicked) {
            int row = table.getSelectedRow();
            int studentId = (int) table.getValueAt(row, 0);
            String fullName = (String) table.getValueAt(row, 1);
            String username = (String) table.getValueAt(row, 2);
            String email = (String) table.getValueAt(row, 3);

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
        clicked = false;
        return label;
    }

    public boolean stopCellEditing() {
        clicked = false;
        return super.stopCellEditing();
    }
}
