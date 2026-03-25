package org.data_model.business_logic;

import org.data_model.data_access.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;
import java.util.List;

public class TaskManagementGUI extends JFrame {
    private TasksManagement logic;
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField empIdField, empNameField;
    private JTextField taskIdField, startHourField, endHourField;
    private JComboBox<String> taskTypeCombo;
    private JComboBox<String> statusCombo;

    public TaskManagementGUI(TasksManagement logic) {
        this.logic = logic;
        prepareGUI();
        updateTable();
    }

    private void prepareGUI() {
        this.setTitle("Task Management System 2026");
        this.setSize(1100, 750);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        JPanel mainControlPanel = new JPanel(new BorderLayout());
        
        JPanel inputPanel = new JPanel(new GridLayout(0, 4, 15, 15));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        inputPanel.add(new JLabel("Employee ID:"));
        empIdField = new JTextField();
        inputPanel.add(empIdField);

        inputPanel.add(new JLabel("Employee Name:"));
        empNameField = new JTextField();
        inputPanel.add(empNameField);

        inputPanel.add(new JLabel("Parent Task ID:"));
        taskIdField = new JTextField();
        inputPanel.add(taskIdField);

        inputPanel.add(new JLabel("Task Type:"));
        taskTypeCombo = new JComboBox<>(new String[]{"Simple", "Complex"});
        inputPanel.add(taskTypeCombo);

        inputPanel.add(new JLabel("Hours (Start / End):"));
        JPanel hourPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        startHourField = new JTextField();
        endHourField = new JTextField();
        hourPanel.add(startHourField);
        hourPanel.add(endHourField);
        inputPanel.add(hourPanel);

        inputPanel.add(new JLabel("Status:"));
        statusCombo = new JComboBox<>(new String[]{"Uncompleted", "Completed"});
        inputPanel.add(statusCombo);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton btnAddEmp = new JButton("Add Employee");
        btnAddEmp.addActionListener(e -> addEmployeeAction());
        buttonPanel.add(btnAddEmp);

        JButton btnAddTask = new JButton("Assign Task");
        btnAddTask.addActionListener(e -> addTaskAction());
        buttonPanel.add(btnAddTask);

        JButton btnAddSub = new JButton("Add Subtask");
        btnAddSub.addActionListener(e -> addSubTaskAction());
        buttonPanel.add(btnAddSub);

        JButton btnModify = new JButton("Modify Status");
        btnModify.addActionListener(e -> modifyStatusAction());
        buttonPanel.add(btnModify);

        JButton btnStats = new JButton("Show Statistics");
        btnStats.addActionListener(e -> showStatsAction());
        buttonPanel.add(btnStats);

        JButton btnClear = new JButton("Clear All");
        btnClear.setBackground(new Color(255, 150, 150));
        btnClear.addActionListener(e -> clearAllDataAction());
        buttonPanel.add(btnClear);

        mainControlPanel.add(inputPanel, BorderLayout.CENTER);
        mainControlPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        this.add(mainControlPanel, BorderLayout.NORTH);

        String[] columns = {"Emp ID", "Name", "Task ID", "Type", "Status", "Duration (h)"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        
        JScrollPane scrollPane = new JScrollPane(table);
        this.add(scrollPane, BorderLayout.CENTER);

        this.setLocationRelativeTo(null);
    }

    private void addEmployeeAction() {
        try {
            int id = Integer.parseInt(empIdField.getText().trim());
            String name = empNameField.getText().trim();
            if(name.isEmpty()) throw new Exception("Name cannot be empty!");
            logic.assignTaskToEmployee(new Employee(id, name), null);
            updateTable();
        } catch (Exception ex) { showMessage("Error: " + ex.getMessage()); }
    }

    private void addTaskAction() {
        try {
            int empId = Integer.parseInt(empIdField.getText().trim());
            int tId = Integer.parseInt(taskIdField.getText().trim());
            String type = (String) taskTypeCombo.getSelectedItem();
            Task newTask = "Simple".equals(type) ? 
                new SimpleTask(tId, Integer.parseInt(startHourField.getText()), Integer.parseInt(endHourField.getText())) : 
                new ComplexTask(tId);

            boolean found = false;
            for (Employee e : logic.getMap().keySet()) {
                if (e.getIdEmployee() == empId) {
                    logic.assignTaskToEmployee(e, newTask);
                    found = true;
                    break;
                }
            }
            if(!found) showMessage("Employee not found!");
            updateTable();
        } catch (Exception ex) { showMessage("Invalid task data!"); }
    }

    private void addSubTaskAction() {
        try {
            int empId = Integer.parseInt(empIdField.getText().trim());
            int parentId = Integer.parseInt(taskIdField.getText().trim());
            String subIdStr = JOptionPane.showInputDialog(this, "New Subtask ID:");
            if (subIdStr == null) return;

            SimpleTask newSub = new SimpleTask(Integer.parseInt(subIdStr), 
                    Integer.parseInt(startHourField.getText()), Integer.parseInt(endHourField.getText()));
            logic.addSubTaskToComplex(empId, parentId, newSub);
            updateTable();
            showMessage("Subtask added successfully!");
        } catch (Exception ex) { showMessage("Error: " + ex.getMessage()); }
    }

    private void modifyStatusAction() {
        try {
            logic.modifyTaskStatus(Integer.parseInt(empIdField.getText().trim()), 
                    Integer.parseInt(taskIdField.getText().trim()), (String)statusCombo.getSelectedItem());
            updateTable();
        } catch (Exception ex) { showMessage("Error modifying status!"); }
    }

    private void clearAllDataAction() {
        int confirm = JOptionPane.showConfirmDialog(this, "Delete everything?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            logic.clearAllData();
            updateTable();
        }
    }

    private void showStatsAction() {
        Utility.filterAndDisplayOverworkedEmployees(logic);
        showMessage("Statistics generated in console.");
    }

    public void updateTable() {
        tableModel.setRowCount(0);
        for (Map.Entry<Employee, List<Task>> entry : logic.getMap().entrySet()) {
            Employee emp = entry.getKey();
            for (Task t : entry.getValue()) {
                if (t == null) continue;
                tableModel.addRow(new Object[]{ emp.getIdEmployee(), emp.getName(), t.getIdTask(), 
                    t.getClass().getSimpleName(), t.getStatusTask(), t.estimateDuration() });
            }
        }
    }

    private void showMessage(String msg) { JOptionPane.showMessageDialog(this, msg); }
}
