package org.example;
import java.io.Serializable;

public abstract sealed class Task implements Serializable permits data_model.SimpleTask, data_model.ComplexTask {
    public int idTask;
    public String statusTask;

    public Task(int idTask) {
        this.idTask = idTask;
        this.statusTask = "UNCOMPLETED";
    }

    public abstract int estimateDuration();
}
