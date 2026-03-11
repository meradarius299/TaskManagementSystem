package org.example;
import java.io.Serializable;

public abstract class Task implements Serializable{
    public int idTask;
    public String statusTask;

    public Task(int idTask) {
        this.idTask = idTask;
        this.statusTask = "UNCOMPLETED";
    }

    public abstract int estimateDuration();
}
