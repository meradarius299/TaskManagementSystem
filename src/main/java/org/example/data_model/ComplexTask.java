package org.example.data_model;

import java.util.ArrayList;

public final class ComplexTask extends Task {
    public ArrayList<Task> subtasks;

    public ComplexTask(int idTask) {
        super(idTask);
        this.subtasks = new ArrayList<>();
    }

    public void addSubtask(Task task) {
        this.subtasks.add(task);
    }

    @Override
    public int estimateDuration() {
        int sumDuration = 0;
        for (Task task : this.subtasks) {
            sumDuration += task.estimateDuration();
        }
        return sumDuration;
    }

    public ArrayList<Task> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(ArrayList<Task> subtasks) {
        this.subtasks = subtasks;
    }
}
