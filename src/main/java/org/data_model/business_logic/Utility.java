package org.data_model.business_logic;

import org.data_model.data_access.Employee;
import org.data_model.data_access.Task;
import java.util.*;


public class Utility {

    public static void filterAndDisplayOverworkedEmployees(TasksManagement tm) {
        List<Employee> overworked = new ArrayList<>();

        Map<Integer, Integer> empDurations = new HashMap<>();


        for (Employee emp : tm.getMap().keySet()) {
            int duration = tm.calculateEmployeeWorkDuration(emp.getIdEmployee());
            if (duration > 40) {
                overworked.add(emp);
                empDurations.put(emp.getIdEmployee(), duration);
            }
        }


        overworked.sort((e1, e2) ->
                Integer.compare(empDurations.get(e1.getIdEmployee()), empDurations.get(e2.getIdEmployee()))
        );


        System.out.println("Angajati cu peste 40 de ore (sortati crescator):");
        for (Employee emp : overworked) {
            System.out.println(emp.getName() + " - " + empDurations.get(emp.getIdEmployee()) + " ore");
        }
    }

    public static Map<String, Map<String, Integer>> getTaskStatistics(TasksManagement tm) {
        Map<String, Map<String, Integer>> stats = new HashMap<>();

        for (Map.Entry<Employee, List<Task>> entry : tm.getMap().entrySet()) {
            String employeeName = entry.getKey().getName();
            List<Task> tasks = entry.getValue();

            int completed = 0;
            int uncompleted = 0;

            if (tasks != null) {
                for (Task t : tasks) {
                    if (t != null) {
                        if ("Completed".equalsIgnoreCase(t.getStatusTask())) {
                            completed++;
                        } else {
                            uncompleted++;
                        }
                    }
                }
            }

            Map<String, Integer> counts = new HashMap<>();
            counts.put("Completed", completed);
            counts.put("Uncompleted", uncompleted);

            stats.put(employeeName, counts);
        }

        return stats;
    }
}
