package org.data_model.business_logic;

import org.data_model.data_access.ComplexTask;
import org.data_model.data_access.Employee;
import org.data_model.data_access.Task;
import org.data_model.data_access.SerializationOperation;
import java.io.Serializable;
import java.util.*;


public class TasksManagement implements Serializable {

    private Map<Employee, List<Task>> map = new HashMap<>();
    public Map<Employee, List<Task>> getMap() {
        return map;
    }
    public void assignTaskToEmployee(Employee e, Task t) {
        if(t == null) {
            if(!map.containsKey(e)) {
                map.put(e, new ArrayList<>());
            }
            return;
        }
        for (List<Task> allTasks : map.values()) {
            for (Task task : allTasks) {
                if (task != null && task.getIdTask() == t.getIdTask()) {
                    throw new RuntimeException("Task ID " + t.getIdTask() +
                            " is already assigned to another employee!");
                }
            }
        }
        if (map.containsKey(e)) {
            map.get(e).add(t);
        }else  {
            List<Task> newList = new ArrayList<>();
            newList.add(t);
            map.put(e, newList);
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
                        if (searchAndModify(t, idTask, newStatus)) {
                            return;
                        }
                    }
                }
            }
        }
    }

    private boolean searchAndModify(Task t, int idTask, String newStatus) {
        if(t == null) return false;
        if(t.getIdTask() == idTask) {
            t.setStatusTask(newStatus);
            return true;
        }
        if(t instanceof ComplexTask) {
            ComplexTask complexTask = (ComplexTask) t;
            for (Task sub :  complexTask.getSubtasks()) {
                if (searchAndModify(sub, idTask, newStatus)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addSubTaskToComplex(int employeeId, int parentTaskId, Task newSubTask) {
        for (Map.Entry<Employee, List<Task>> entry : map.entrySet()) {
            if (entry.getKey().getIdEmployee() == employeeId) {
                List<Task> mainTasks = entry.getValue();
                if (mainTasks != null) {
                    for (Task t : mainTasks) {
                        if (findTaskAndAdd(t, parentTaskId, newSubTask)) {
                            return;
                        }
                    }
                }
            }
        }
        throw new RuntimeException("Parent Task ID " + parentTaskId + " not found!");
    }

    private boolean findTaskAndAdd(Task current, int targetId, Task toAdd) {
        if (current.getIdTask() == targetId) {
            if (current instanceof ComplexTask) {
                ((ComplexTask) current).addSubTask(toAdd);
                return true;
            } else {
                throw new RuntimeException("Task " + targetId + " is a SimpleTask and cannot have subtasks!");
            }
        }
        if (current instanceof ComplexTask) {
            for (Task sub : ((ComplexTask) current).getSubtasks()) {
                if (findTaskAndAdd(sub, targetId, toAdd)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void clearAllData() {
        map.clear();
        System.out.println("All data has been cleared!");
    }
}
