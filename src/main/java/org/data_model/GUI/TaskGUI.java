package org.data_model.GUI;

import org.data_model.business_logic.TaskManagementGUI;
import org.data_model.business_logic.TasksManagement;
import org.data_model.data_access.SerializationOperation;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TaskGUI {
    public static void main(String[] args) {
        TasksManagement savedLogic = (TasksManagement) SerializationOperation.deserialize();

        final TasksManagement tasksManagement = (savedLogic != null) ? savedLogic : new TasksManagement();

        SwingUtilities.invokeLater(() -> {
            TaskManagementGUI gui = new TaskManagementGUI(tasksManagement);

            gui.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            gui.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {

                    SerializationOperation.serialize(tasksManagement);
                    System.out.println("Date salvate cu succes. Inchidere aplicație...");
                    System.exit(0);
                }
            });

            gui.setVisible(true);
        });
    }
}