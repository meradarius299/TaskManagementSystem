package org.example.data_model;
import java.io.Serializable;

public abstract sealed class Task implements Serializable permits SimpleTask, ComplexTask{
    public int idTask;
    public String statusTask;

    public Task(int idTask) {
        this.idTask = idTask;
        this.statusTask = "UNCOMPLETED";
    }

    public abstract int estimateDuration();
}
