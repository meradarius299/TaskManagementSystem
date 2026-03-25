package org.data_model.business_logic;

import org.data_model.data_access.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;


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
        } catch (Exception e) {}; //back to default
        this.logic = logic;
        prepareGUI();
        updateTable();
    }

    private void prepareGUI() {
        this.setTitle("Task Management System");
        this.setSize(900,600);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new  BorderLayout());
        JPanel inputPanel =  new JPanel(new GridLayout(0,4,15, 15 ));

        inputPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        inputPanel.add(new JLabel("Employee ID:"));
        employeeIdField = new JTextField();
        inputPanel.add(employeeIdField);

        inputPanel.add(new JLabel("Employee Name:"));
        employeeNameField = new JTextField();
        inputPanel.add(employeeNameField);

        inputPanel.add(new JLabel("Task ID:"));
        taskIdField = new JTextField();
        inputPanel.add(taskIdField);

        inputPanel.add(new JLabel("Task Type:"));
        taskTypeCombo = new JComboBox<>(new String[]{"Simple", "Complex"});
        inputPanel.add(taskTypeCombo);

        inputPanel.add(new JLabel("Hours (Start/End):"));
        JPanel hourPanel =  new JPanel(new GridLayout(1,2,5,0));
        startHoursField = new JTextField();
        endHoursField = new JTextField();
        hourPanel.add(startHoursField);
        hourPanel.add(endHoursField);
        inputPanel.add(hourPanel);

        inputPanel.add(new JLabel("Status:"));
        statusCombo = new JComboBox<>(new String[]{"Uncompleted", "Completed"});
        inputPanel.add(statusCombo);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,10,10));

        JButton addEmployeeBtn = new JButton("Add Employee");
        addEmployeeBtn.setFocusPainted(false);
        addEmployeeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addEmployeeAction();
            }
        });
        buttonPanel.add(addEmployeeBtn);

        JButton addTaskBtn = new JButton("Add Task");
        addTaskBtn.setFocusPainted(false);
        addTaskBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTaskAction();
            }
        });
        buttonPanel.add(addTaskBtn);

        JButton addSubTaskBtn = new JButton("Add Subtask");
        addSubTaskBtn.setFocusPainted(false);
        addSubTaskBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addSubTaskAction();
            }
        });
        buttonPanel.add(addSubTaskBtn);

        JButton modifyStatusBtn = new JButton("Modify Status");
        modifyStatusBtn.setFocusPainted(false);
        modifyStatusBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modifyStatusAction();
            }
        });
        buttonPanel.add(modifyStatusBtn);

        JButton showStatsBtn = new JButton("Show Statistics");
        showStatsBtn.setFocusPainted(false);
        showStatsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showStatusAction();
            }
        });
        buttonPanel.add(showStatsBtn);

        JButton clearBtn =  new JButton("Clear");
        clearBtn.setFocusPainted(false);
        clearBtn.setBackground(new Color(204, 90, 90));
        clearBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAllDataAction();
            }
        });
        buttonPanel.add(clearBtn);

        mainPanel.add(inputPanel,BorderLayout.CENTER);
        mainPanel.add(buttonPanel,BorderLayout.SOUTH);

        this.add(mainPanel, BorderLayout.NORTH);

        String[] columns = {"ID Empolyee", "Employee Name", "ID Task", "Type", "Status","Duration(hours)"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);
        this.add(new JScrollPane(table), BorderLayout.CENTER);

        this.setLocationRelativeTo(null);
    }

    private void addEmployeeAction() {
        try{
            int idEmployee = Integer.parseInt(employeeIdField.getText().trim());
            String name = employeeNameField.getText();
            if(name.isEmpty()) throw new Exception("Name cannot be empty!");
            logic.assignTaskToEmployee(new Employee(idEmployee, name), null);
            updateTable();
        } catch (Exception ex) { showMessage("Error: " + ex.getMessage());}
    }

    public void addTaskAction(){
        try{
            int employeeId = Integer.parseInt(employeeIdField.getText().trim());
            int taskId = Integer.parseInt(taskIdField.getText().trim());
            String type = (String) taskTypeCombo.getSelectedItem();
            Task newTask = "Simple".equals(type) ?
                    new SimpleTask(taskId,Integer.parseInt(startHoursField.getText()),
                            Integer.parseInt(endHoursField.getText())) :
                    new ComplexTask(taskId);
            boolean ok = false;
            for (Employee e : logic.getMap().keySet()){
                if(e.getIdEmployee() == employeeId){
                    logic.assignTaskToEmployee(e, newTask);
                    ok = true;
                    break;
                }
            }
            if(!ok) showMessage("Error: No such employee exists!");
            updateTable();
        }catch(Exception ex){showMessage("Error: " + ex.getMessage());}
    }

    public void addSubTaskAction() {
        try{
            int employeeId = Integer.parseInt(employeeIdField.getText().trim());
            int taskId = Integer.parseInt(taskIdField.getText().trim());
            String subIdStr = JOptionPane.showInputDialog(this, "Enter SubTask ID:");
            if(subIdStr == null) return;
            SimpleTask newSub = new SimpleTask(Integer.parseInt(subIdStr),
                    Integer.parseInt(startHoursField.getText()), Integer.parseInt(endHoursField.getText()));
            logic.addSubTaskToComplex(employeeId, taskId, newSub);
            updateTable();
            showMessage("Subtask added successfully!");
        }catch(Exception ex) { showMessage("Error: " + ex.getMessage());}
    }

    private void modifyStatusAction() {
        try{
            int idEmployee = Integer.parseInt(employeeIdField.getText().trim());
            int idTask = Integer.parseInt(taskIdField.getText().trim());
            String status = (String) statusCombo.getSelectedItem();
            logic.modifyTaskStatus(idEmployee, idTask, status);
            updateTable();
        }catch (Exception ex) { showMessage("Error to modify status!");}
    }

    private void clearAllDataAction() {
        int confirm = JOptionPane.showConfirmDialog(this, "Delete everything?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if(confirm == JOptionPane.YES_OPTION){
            logic.clearAllData();
            updateTable();
        }
    }

    private void showStatusAction() {
        Utility.filterAndDisplayOverworkedEmployees(logic);
        Map<String, Map<String,Integer>> stats = Utility.getTaskStatistics(logic);
        showMessage("Statistics generated in console for " + stats.size() + " employees");
    }

    private void updateTable(){
        tableModel.setRowCount(0);
        for (Map.Entry<Employee, List<Task>> entry : logic.getMap().entrySet()){
            Employee emp = entry.getKey();
            for(Task task : entry.getValue()){
                if(task == null) continue;
                tableModel.addRow(new Object[]{
                        emp.getIdEmployee(),
                        emp.getName(),
                        task.getIdTask(),
                        task.getClass().getSimpleName(),
                        task.getStatusTask(),
                        task.estimateDuration()
                });
            }
        }
    }

    private void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }
}