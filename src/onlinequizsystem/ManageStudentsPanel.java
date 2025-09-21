package onlinequizsystem;

import javax.swing.JPanel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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

        setBackground(new Color(245, 222, 179)); 
        setPreferredSize(new Dimension(904, 531));
        setLayout(new BorderLayout(0, 0));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(205, 133, 63)); 
        titlePanel.setBorder(new EmptyBorder(8, 10, 8, 10));

        JLabel titleLabel = new JLabel("Manage Students", JLabel.LEFT);
        titleLabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 18));
        titleLabel.setForeground(Color.BLACK);
        titlePanel.add(titleLabel, BorderLayout.WEST);

        add(titlePanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new Object[]{"Student ID", "Full Name", "Username", "Email", "Action"}, 0
        );
        studentTable = new JTable(tableModel);
        studentTable.setRowHeight(28);
        studentTable.setShowGrid(true);
        studentTable.setGridColor(new Color(210, 180, 140));
        studentTable.setBackground(new Color(255, 250, 240));
        studentTable.setFont(new Font("Arial", Font.PLAIN, 13));
        studentTable.getTableHeader().setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 14));
        studentTable.getTableHeader().setBackground(new Color(255, 250, 240));
        studentTable.getTableHeader().setForeground(Color.BLACK);

        ((DefaultTableCellRenderer) studentTable.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(SwingConstants.CENTER);

        studentTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        studentTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), main));

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBackground(new Color(233, 150, 122));
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        loadStudents();
    }

    private void loadStudents() {
        tableModel.setRowCount(0); 

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

                tableModel.addRow(new Object[]{studentId, fullName, username, email, "View Details"});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading students: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
    private static final long serialVersionUID = 1L;

    public ButtonRenderer() {
        setOpaque(true);
        setBackground(new Color(255, 250, 240)); 
        setForeground(Color.BLACK);
        setFont(new Font("Arial", Font.PLAIN, 12));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        setText((value == null) ? "" : value.toString());
        return this;
    }
}

class ButtonEditor extends DefaultCellEditor {
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
        button.setBackground(new Color(205, 133, 63));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.PLAIN, 12));

        button.addActionListener(e -> fireEditingStopped());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        this.table = table;
        label = (value == null) ? "" : value.toString();
        button.setText(label);
        clicked = true;
        return button;
    }

    @Override
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

    @Override
    public boolean stopCellEditing() {
        clicked = false;
        return super.stopCellEditing();
    }
}
