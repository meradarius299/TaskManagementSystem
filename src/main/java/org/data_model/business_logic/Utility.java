package org.data_model.business_logic;

import org.data_model.data_access.Employee;
import org.data_model.data_access.Task;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Utility {
    public static void filterAndDisplayOverworkedEmployees(TasksManagement tm) {
        System.out.println("Employees with over 40 hours: ");

        for (Employee employee : tm.getMap().keySet()) {
            int dur = tm.calculateEmployeeWorkDuration(employee.getIdEmployee());
            if(dur > 40) {
                System.out.println("Employee " + employee.getIdEmployee() + " is over 40 hours with " + dur  + " hours");
            }
        }
    }
    public static Map<String, Map<String, Integer>> getTaskStatistics(TasksManagement tm) {
        Map<String, Map<String, Integer>> stats = new HashMap<>();
        for (Map.Entry<Employee, List<Task>> entry : tm.getMap().entrySet()){
            List<Task> tasks = entry.getValue();
            int completed = 0;
            if(tasks != null) {
                for (Task task : tasks) {
                    if(task != null && "Compelted".equalsIgnoreCase(task.getStatusTask()))
                    {
                        completed++;
                    }
                }
                Map<String, Integer> taskStats = new HashMap<>();
                taskStats.put("Completed", completed);
                taskStats.put("Uncompleted", tasks.size() - completed);
                stats.put(entry.getKey().getName(), taskStats);
            }
        }
        return stats;
    }
}