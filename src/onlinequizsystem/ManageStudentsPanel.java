package onlinequizsystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ManageStudentsPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    // Modern color palette
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 73, 94);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color BACKGROUND_COLOR = new Color(247, 249, 252);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color MUTED_COLOR = new Color(127, 140, 141);
    private static final Color BORDER_COLOR = new Color(220, 221, 225);
    
    private int instructorId;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JLabel studentCountLabel;

    public ManageStudentsPanel(Main main, int instructorId) {
        this.instructorId = instructorId;
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main Content Panel
        JPanel contentPanel = createContentPanel(main);
        add(contentPanel, BorderLayout.CENTER);

        // Load students
        loadStudents();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

        // Left side - Title and subtitle
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("👥 Manage Students");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("View and manage your enrolled students");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(MUTED_COLOR);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subtitleLabel);

        // Right side - Student count
        studentCountLabel = new JLabel("0 Students");
        studentCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        studentCountLabel.setForeground(PRIMARY_COLOR);
        studentCountLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(studentCountLabel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createContentPanel(Main main) {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);

        // Create modern table
        createModernTable(main);

        // Table container with card styling
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(CARD_COLOR);
        tableContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(CARD_COLOR);
        scrollPane.setBackground(CARD_COLOR);

        tableContainer.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(tableContainer, BorderLayout.CENTER);

        return contentPanel;
    }

    private void createModernTable(Main main) {
        // Create table model with styled columns
        String[] columnNames = {"ID", "Full Name", "Username", "Email", "Actions"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only action column is editable
            }
        };

        studentTable = new JTable(tableModel);
        
        // Style the table
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        studentTable.setRowHeight(50);
        studentTable.setGridColor(BORDER_COLOR);
        studentTable.setSelectionBackground(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 30));
        studentTable.setSelectionForeground(TEXT_COLOR);
        studentTable.setBackground(CARD_COLOR);
        studentTable.setShowVerticalLines(true);
        studentTable.setShowHorizontalLines(true);
        studentTable.setIntercellSpacing(new Dimension(1, 1));

        // Style the header
        JTableHeader header = studentTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(SECONDARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 45));
        header.setBorder(BorderFactory.createEmptyBorder());

        // Set column widths
        studentTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // ID
        studentTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Full Name
        studentTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Username
        studentTable.getColumnModel().getColumn(3).setPreferredWidth(200); // Email
        studentTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Actions

        // Custom cell renderer for all columns except actions
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? CARD_COLOR : new Color(249, 250, 251));
                }
                
                setFont(new Font("Segoe UI", Font.PLAIN, 14));
                setForeground(TEXT_COLOR);
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                
                if (column == 0) { // ID column
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setFont(new Font("Segoe UI", Font.BOLD, 14));
                    setForeground(PRIMARY_COLOR);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }
                
                return c;
            }
        };

        // Apply renderer to non-action columns
        for (int i = 0; i < 4; i++) {
            studentTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Custom button renderer and editor for actions
        studentTable.getColumn("Actions").setCellRenderer(new ModernButtonRenderer());
        studentTable.getColumn("Actions").setCellEditor(new ModernButtonEditor(new JCheckBox(), main));
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

            int count = 0;
            while (rs.next()) {
                int studentId = rs.getInt("id");
                String fullName = rs.getString("full_name");
                String username = rs.getString("username");
                String email = rs.getString("email");

                tableModel.addRow(new Object[]{studentId, fullName, username, email, "View Details"});
                count++;
            }
            
            // Update student count
            studentCountLabel.setText(count + " Student" + (count != 1 ? "s" : ""));
            
        } catch (Exception e) {
            showStyledMessage("Error loading students: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showStyledMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}

// Modern button renderer
class ModernButtonRenderer extends JButton implements TableCellRenderer {
    private static final long serialVersionUID = 1L;
    private static final Color BUTTON_COLOR = new Color(41, 128, 185);

    public ModernButtonRenderer() {
        setOpaque(true);
        setFont(new Font("Segoe UI", Font.BOLD, 12));
        setBackground(BUTTON_COLOR);
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        setText("👁 View Details");
        setBackground(isSelected ? BUTTON_COLOR.darker() : BUTTON_COLOR);
        return this;
    }
}

// Modern button editor
class ModernButtonEditor extends DefaultCellEditor {
    private static final long serialVersionUID = 1L;
    private static final Color BUTTON_COLOR = new Color(41, 128, 185);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color CARD_COLOR = Color.WHITE;
    
    protected JButton button;
    private String label;
    private boolean clicked;
    private JTable table;
    private Main main;

    public ModernButtonEditor(JCheckBox checkBox, Main main) {
        super(checkBox);
        this.main = main;
        button = new JButton();
        button.setOpaque(true);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(BUTTON_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BUTTON_COLOR.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_COLOR);
            }
        });
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        this.table = table;
        label = (value == null) ? "" : value.toString();
        button.setText("👁 View Details");
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

            // Create modern student details dialog
            showStudentDetailsDialog(studentId, fullName, username, email);
        }
        clicked = false;
        return label;
    }

    private void showStudentDetailsDialog(int studentId, String fullName, String username, String email) {
        JDialog detailsDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(main), "Student Details", true);
        detailsDialog.setSize(450, 300);
        detailsDialog.setLocationRelativeTo(main);
        detailsDialog.getContentPane().setLayout(new BorderLayout());
        detailsDialog.getContentPane().setBackground(new Color(247, 249, 252));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BUTTON_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel titleLabel = new JLabel("👤 Student Information");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        detailsDialog.add(headerPanel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(CARD_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        contentPanel.add(createDetailRow("Student ID:", String.valueOf(studentId)));
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(createDetailRow("Full Name:", fullName));
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(createDetailRow("Username:", username));
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(createDetailRow("Email:", email));

        detailsDialog.add(contentPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(new Color(247, 249, 252));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 20, 25));

        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeButton.setBackground(new Color(52, 73, 94));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> detailsDialog.dispose());

        footerPanel.add(closeButton);
        detailsDialog.add(footerPanel, BorderLayout.SOUTH);

        detailsDialog.setVisible(true);
    }

    private JPanel createDetailRow(String label, String value) {
        JPanel rowPanel = new JPanel(new BorderLayout());
        rowPanel.setOpaque(false);

        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelComponent.setForeground(TEXT_COLOR);
        labelComponent.setPreferredSize(new Dimension(100, 20));

        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        valueComponent.setForeground(new Color(127, 140, 141));

        rowPanel.add(labelComponent, BorderLayout.WEST);
        rowPanel.add(valueComponent, BorderLayout.CENTER);

        return rowPanel;
    }

    public boolean stopCellEditing() {
        clicked = false;
        return super.stopCellEditing();
    }
}
