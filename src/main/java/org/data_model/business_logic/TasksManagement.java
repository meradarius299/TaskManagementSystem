package org.data_model.business_logic;

import org.data_model.data_access.ComplexTask;
import org.data_model.data_access.Employee;
import org.data_model.data_access.Task;
import java.io.Serializable;
import java.util.*;

public class TasksManagement implements Serializable {

    private Map<Employee, List<Task>> map = new HashMap<>();

    public Map<Employee, List<Task>> getMap() {
        return map;
    }

    public void assignTaskToEmployee(Employee e, Task t) {
        if (!map.containsKey(e)) {
            map.put(e, new ArrayList<>());
        }
        
        if (t != null) {
            map.get(e).add(t);
        }
    }

    public int calculateEmployeeWorkDuration(int idEmployee) {
        int sum = 0;
        for (Map.Entry<Employee, List<Task>> entry : map.entrySet()) {
            if (entry.getKey().getIdEmployee() == idEmployee) {
                for (Task t : entry.getValue()) {
                    if (t != null && "Completed".equalsIgnoreCase(t.getStatusTask())) {
                        sum += t.estimateDuration();
                    }
                }
            }
        }
        return sum;
    }

    public void modifyTaskStatus(int idEmployee, int idTask, String newStatus) {
        for (Map.Entry<Employee, List<Task>> entry : map.entrySet()) {
            if (entry.getKey().getIdEmployee() == idEmployee) {
                List<Task> tasks = entry.getValue();

                if (tasks != null) {
                    for (Task t : tasks) {
                        if (t != null && t.getIdTask() == idTask) {
                            t.setStatusTask(newStatus);
                            return;
                        }
                    }
                }
            }
        }
        System.out.println("Angajatul sau Task-ul nu a fost gasit!");
    }
    public void addSubTaskToComplex(int employeeId, int parentTaskId, Task newSub) {
        for (Map.Entry<Employee, List<Task>> entry : map.entrySet()) {
            if (entry.getKey().getIdEmployee() == employeeId) {
                List<Task> tasks = entry.getValue();
                if (tasks != null) {
                    for (Task t : tasks) {
                        if (t != null && t.getIdTask() == parentTaskId) {
                            if (t instanceof ComplexTask) {
                                ((ComplexTask) t).addSubTask(newSub);
                                return;
                            }
                        }
                    }
                }
            }
        }
        throw new RuntimeException("ComplexTask-ul cu ID " + parentTaskId + " nu a fost gasit!");
    }

    public void clearAllData() {
        map.clear();
        System.out.println("Toate datele au fost sterse din memorie.");
    }

}
