package org.data_model.business_logic;

import org.data_model.data_access.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class TaskManagementGUI extends JFrame {

    private TasksManagement logic;
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField employeeIdField, employeeNameField;
    private JTextField taskIdField, startHoursField, endHoursField;
    private JComboBox<String> taskTypeCombo, statusCombo;

    public TaskManagementGUI(TasksManagement logic) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}

        TasksManagement savedData = (TasksManagement) SerializationOperation.deserialize();
        if (savedData != null) {
            this.logic = savedData;
        } else {
            this.logic = logic;
        }

        prepareGUI();
        updateTable();
    }

    private void prepareGUI() {
        this.setTitle("Task Management System");
        this.setSize(1000, 700);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(0, 4, 15, 15));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        inputPanel.add(new JLabel("Employee ID:"));
        employeeIdField = new JTextField();
        inputPanel.add(employeeIdField);

        inputPanel.add(new JLabel("Employee Name:"));
        employeeNameField = new JTextField();
        inputPanel.add(employeeNameField);

        inputPanel.add(new JLabel("Target Task ID:"));
        taskIdField = new JTextField();
        inputPanel.add(taskIdField);

        inputPanel.add(new JLabel("Task Type:"));
        taskTypeCombo = new JComboBox<>(new String[]{"Simple", "Complex"});
        inputPanel.add(taskTypeCombo);

        inputPanel.add(new JLabel("Hours (Start/End):"));
        JPanel hourPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        startHoursField = new JTextField();
        endHoursField = new JTextField();
        hourPanel.add(startHoursField);
        hourPanel.add(endHoursField);
        inputPanel.add(hourPanel);

        inputPanel.add(new JLabel("Status:"));
        statusCombo = new JComboBox<>(new String[]{"Uncompleted", "Completed"});
        inputPanel.add(statusCombo);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton addEmployeeBtn = new JButton("Add Employee");
        addEmployeeBtn.addActionListener(e -> addEmployeeAction());
        buttonPanel.add(addEmployeeBtn);

        JButton addTaskBtn = new JButton("Add Task");
        addTaskBtn.addActionListener(e -> addTaskAction());
        buttonPanel.add(addTaskBtn);

        JButton addSubTaskBtn = new JButton("Add Subtask");
        addSubTaskBtn.addActionListener(e -> addSubTaskAction());
        buttonPanel.add(addSubTaskBtn);

        JButton modifyStatusBtn = new JButton("Modify Status");
        modifyStatusBtn.addActionListener(e -> modifyStatusAction());
        buttonPanel.add(modifyStatusBtn);

        JButton showStatsBtn = new JButton("Show Statistics");
        showStatsBtn.addActionListener(e -> showStatusAction());
        buttonPanel.add(showStatsBtn);

        JButton clearBtn = new JButton("Clear All");
        clearBtn.setBackground(new Color(204, 90, 90));
        clearBtn.addActionListener(e -> clearAllDataAction());
        buttonPanel.add(clearBtn);

        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        this.add(mainPanel, BorderLayout.NORTH);

        String[] columns = {"Emp ID", "Emp Name", "Task ID", "Type", "Status", "Duration"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);

        this.add(new JScrollPane(table), BorderLayout.CENTER);
        this.setLocationRelativeTo(null);
    }

    private void addEmployeeAction() {
        try {
            int id = Integer.parseInt(employeeIdField.getText().trim());
            String name = employeeNameField.getText().trim();
            if (name.isEmpty()) throw new Exception("Name is empty!");
            logic.assignTaskToEmployee(new Employee(id, name), null);
            saveAndRefresh();
        } catch (Exception ex) { showMessage("Error: " + ex.getMessage()); }
    }

    private void addTaskAction() {
        try {
            int empId = Integer.parseInt(employeeIdField.getText().trim());
            int tId = Integer.parseInt(taskIdField.getText().trim());
            String type = (String) taskTypeCombo.getSelectedItem();

            Task t = "Simple".equals(type) ?
                    new SimpleTask(tId, Integer.parseInt(startHoursField.getText()), Integer.parseInt(endHoursField.getText())) :
                    new ComplexTask(tId);

            boolean found = false;
            for (Employee e : logic.getMap().keySet()) {
                if (e.getIdEmployee() == empId) {
                    logic.assignTaskToEmployee(e, t);
                    found = true;
                    break;
                }
            }
            if (!found) throw new Exception("Employee not found!");
            saveAndRefresh();
        } catch (Exception ex) { showMessage("Error: " + ex.getMessage()); }
    }

    public void addSubTaskAction() {
        JDialog dialog = new JDialog(this, "Add New Subtask", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);

        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField subEmpIdField = new JTextField();
        JTextField subParentIdField = new JTextField();
        JTextField newSubIdField = new JTextField();
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Simple", "Complex"});
        JTextField startField = new JTextField("");
        JTextField endField = new JTextField("");
        JLabel startLabel = new JLabel("Start Hour:");
        JLabel endLabel = new JLabel("End Hour:");

        inputPanel.add(new JLabel("Employee ID:"));
        inputPanel.add(subEmpIdField);
        inputPanel.add(new JLabel("Parent Task ID:"));
        inputPanel.add(subParentIdField);
        inputPanel.add(new JLabel("New Subtask ID:"));
        inputPanel.add(newSubIdField);
        inputPanel.add(new JLabel("Subtask Type:"));
        inputPanel.add(typeCombo);
        inputPanel.add(startLabel);
        inputPanel.add(startField);
        inputPanel.add(endLabel);
        inputPanel.add(endField);

        typeCombo.addActionListener(e -> {
            boolean isSimple = "Simple".equals(typeCombo.getSelectedItem());
            startLabel.setVisible(isSimple);
            startField.setVisible(isSimple);
            endLabel.setVisible(isSimple);
            endField.setVisible(isSimple);
            dialog.revalidate();
            dialog.repaint();
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Save Subtask");
        JButton cancelBtn = new JButton("Cancel");
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        saveBtn.addActionListener(e -> {
            try {
                int empId = Integer.parseInt(subEmpIdField.getText().trim());
                int parentId = Integer.parseInt(subParentIdField.getText().trim());
                int subId = Integer.parseInt(newSubIdField.getText().trim());
                String type = (String) typeCombo.getSelectedItem();

                Task newSub;
                if ("Simple".equals(type)) {
                    newSub = new SimpleTask(subId,
                            Integer.parseInt(startField.getText().trim()),
                            Integer.parseInt(endField.getText().trim()));
                } else {
                    newSub = new ComplexTask(subId);
                }

                logic.addSubTaskToComplex(empId, parentId, newSub);
                SerializationOperation.serialize(this.logic);
                updateTable();

                dialog.dispose();
                showMessage("Subtask " + subId + " added to parent " + parentId);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void modifyStatusAction() {
        try {
            int empId = Integer.parseInt(employeeIdField.getText().trim());
            int tId = Integer.parseInt(taskIdField.getText().trim());
            String status = (String) statusCombo.getSelectedItem();
            logic.modifyTaskStatus(empId, tId, status);
            saveAndRefresh();
        } catch (Exception ex) { showMessage("Error: " + ex.getMessage()); }
    }

    private void clearAllDataAction() {
        if (JOptionPane.showConfirmDialog(this, "Delete all data?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            logic.clearAllData();
            saveAndRefresh();
        }
    }

    private void showStatusAction() {
        Utility.filterAndDisplayOverworkedEmployees(logic);
        showMessage("Stats generated in console.");
    }

    private void saveAndRefresh() {
        SerializationOperation.serialize(this.logic);
        updateTable();
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        for (Map.Entry<Employee, List<Task>> entry : logic.getMap().entrySet()) {
            Employee emp = entry.getKey();
            if (entry.getValue() != null) {
                for (Task t : entry.getValue()) {
                    addRecursiveToTable(emp, t, 0);
                }
            }
        }
    }

    private void addRecursiveToTable(Employee emp, Task t, int level) {
        if (t == null) return;
        String indent = "    ".repeat(level);
        String subIdLabel = (level == 0) ? "-" : String.valueOf(t.getIdTask());

        tableModel.addRow(new Object[]{
                emp.getIdEmployee(),
                emp.getName(),
                indent + t.getIdTask(),
                t.getClass().getSimpleName(),
                t.getStatusTask(),
                t.estimateDuration(),
                subIdLabel
        });
        if (t instanceof ComplexTask) {
            for (Task child : ((ComplexTask) t).getSubtasks()) {
                addRecursiveToTable(emp, child, level + 1);
            }
        }
    }

    private void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }
}