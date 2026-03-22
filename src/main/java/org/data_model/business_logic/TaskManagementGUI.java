package org.data_model.business_logic;

import org.data_model.data_access.*;
import org.data_model.data_access.SerializationOperation;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
        this.setSize(1000, 700);


        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setLayout(new BorderLayout());

        String[] columns = {"ID Emp", "Nume", "ID Task", "Tip", "Status", "Durata (h)"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        this.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new GridLayout(0, 4, 10, 10));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        controlPanel.add(new JLabel("ID Angajat:"));
        empIdField = new JTextField();
        controlPanel.add(empIdField);
        controlPanel.add(new JLabel("Nume Angajat:"));
        empNameField = new JTextField();
        controlPanel.add(empNameField);

        // Inputuri Task
        controlPanel.add(new JLabel("ID Task (Parinte):"));
        taskIdField = new JTextField();
        controlPanel.add(taskIdField);
        controlPanel.add(new JLabel("Tip Task:"));
        taskTypeCombo = new JComboBox<>(new String[]{"Simple", "Complex"});
        controlPanel.add(taskTypeCombo);

        controlPanel.add(new JLabel("Ore (Start / End):"));
        JPanel hourPanel = new JPanel(new GridLayout(1, 2));
        startHourField = new JTextField();
        endHourField = new JTextField();
        hourPanel.add(startHourField);
        hourPanel.add(endHourField);
        controlPanel.add(hourPanel);

        controlPanel.add(new JLabel("Status:"));
        statusCombo = new JComboBox<>(new String[]{"Uncompleted", "Completed"});
        controlPanel.add(statusCombo);

        JButton btnAddEmp = new JButton("Adauga Angajat");
        btnAddEmp.addActionListener(e -> addEmployeeAction());
        controlPanel.add(btnAddEmp);

        JButton btnAddTask = new JButton("Atribuie Task");
        btnAddTask.addActionListener(e -> addTaskAction());
        controlPanel.add(btnAddTask);

        JButton btnAddSub = new JButton("Adauga Subtask la Complex");
        btnAddSub.addActionListener(e -> addSubTaskAction());
        controlPanel.add(btnAddSub);

        JButton btnModify = new JButton("Modifica Status");
        btnModify.addActionListener(e -> modifyStatusAction());
        controlPanel.add(btnModify);

        JButton btnStats = new JButton("Afișeaza Statistici");
        btnStats.addActionListener(e -> showStatsAction());
        controlPanel.add(btnStats);

        this.add(controlPanel, BorderLayout.SOUTH);
        try {
            int empId = Integer.parseInt(empIdField.getText().trim());
            int parentId = Integer.parseInt(taskIdField.getText().trim());

            String subIdStr = JOptionPane.showInputDialog("ID pentru Subtask-ul nou:");
            if (subIdStr == null) return;
            int subId = Integer.parseInt(subIdStr);

            int start = Integer.parseInt(startHourField.getText().trim());
            int end = Integer.parseInt(endHourField.getText().trim());

            SimpleTask sub = new SimpleTask(subId, start, end);
            logic.addSubTaskToComplex(empId, parentId, sub);

            updateTable();
        } catch (NumberFormatException ex) {

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "A aparut o eroare: " + ex.getMessage());
        }
        JButton btnClear = new JButton("Șterge Tot");
        btnClear.setBackground(new Color(255, 100, 100));
        btnClear.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Sigur vrei să ștergi TOȚI angajatii și task-urile?",
                    "Confirmare Stergere", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                logic.clearAllData();
                updateTable();

                JOptionPane.showMessageDialog(this, "Tabelul a fost curatat!");
            }
        });
        controlPanel.add(btnClear);

    }

    private void addEmployeeAction() {
        try {
            int id = Integer.parseInt(empIdField.getText().trim());
            String name = empNameField.getText().trim();
            if(name.isEmpty()) throw new Exception("Numele nu poate fi gol!");

            logic.assignTaskToEmployee(new Employee(id, name), null);
            updateTable();
        } catch (Exception ex) { showMessage("Eroare: " + ex.getMessage()); }
    }

    private void addTaskAction() {
        try {
            int empId = Integer.parseInt(empIdField.getText().trim());
            int tId = Integer.parseInt(taskIdField.getText().trim());
            String type = (String) taskTypeCombo.getSelectedItem();

            Task newTask;
            if ("Simple".equals(type)) {
                int s = Integer.parseInt(startHourField.getText());
                int e = Integer.parseInt(endHourField.getText());
                newTask = new SimpleTask(tId, s, e);
            } else {
                newTask = new ComplexTask(tId);
            }

            boolean found = false;
            for (Employee e : logic.getMap().keySet()) {
                if (e.getIdEmployee() == empId) {
                    logic.assignTaskToEmployee(e, newTask);
                    found = true;
                    break;
                }
            }
            if(!found) showMessage("Angajatul nu a fost găsit!");
            updateTable();
        } catch (Exception ex) { showMessage("Date task invalide!"); }
    }

    private void addSubTaskAction() {
        try {
            int empId = Integer.parseInt(empIdField.getText().trim());
            int parentId = Integer.parseInt(taskIdField.getText().trim());

            String subIdStr = JOptionPane.showInputDialog("Introduceti ID-ul noului Subtask:");
            if (subIdStr == null) return;

            int subId = Integer.parseInt(subIdStr);
            int start = Integer.parseInt(startHourField.getText());
            int end = Integer.parseInt(endHourField.getText());

            SimpleTask newSub = new SimpleTask(subId, start, end);
            logic.addSubTaskToComplex(empId, parentId, newSub);

            updateTable();
            showMessage("Subtask adaugat cu succes!");
        } catch (Exception ex) {
            showMessage("Eroare la adăugare subtask: " + ex.getMessage());
        }
    }

    private void modifyStatusAction() {
        try {
            int empId = Integer.parseInt(empIdField.getText().trim());
            int tId = Integer.parseInt(taskIdField.getText().trim());
            String status = (String) statusCombo.getSelectedItem();

            logic.modifyTaskStatus(empId, tId, status);
            updateTable();
        } catch (Exception ex) { showMessage("Eroare la modificare status!"); }
    }

    private void showStatsAction() {
        Utility.filterAndDisplayOverworkedEmployees(logic);
        Map<String, Map<String, Integer>> stats = Utility.getTaskStatistics(logic);
        showMessage("Statistici rulate in consola pentru " + stats.size() + " angajati.");
    }

    public void updateTable() {
        tableModel.setRowCount(0);
        for (Map.Entry<Employee, List<Task>> entry : logic.getMap().entrySet()) {
            Employee emp = entry.getKey();
            List<Task> tasks = entry.getValue();

            if (tasks != null && !tasks.isEmpty()) {
                for (Task t : tasks) {
                    if (t == null) continue;
                    tableModel.addRow(new Object[]{
                            emp.getIdEmployee(), emp.getName(), t.getIdTask(),
                            t.getClass().getSimpleName(), t.getStatusTask(), t.estimateDuration()
                    });
                }
            } else {
                tableModel.addRow(new Object[]{emp.getIdEmployee(), emp.getName(), "-", "-", "-", 0});
            }
        }
    }

    private void showMessage(String msg) { JOptionPane.showMessageDialog(this, msg); }
}