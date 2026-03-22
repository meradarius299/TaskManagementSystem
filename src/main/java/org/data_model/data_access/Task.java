package org.data_model.data_access;
import java.io.Serializable;

public abstract sealed class Task implements Serializable permits SimpleTask, ComplexTask{
    public int idTask;
    public String statusTask;

    public Task(int idTask) {
        this.idTask = idTask;
        this.statusTask = "Uncompleted";
    }

    public abstract int estimateDuration();

    public void setStatusTask(String newStatus) {
        this.statusTask = newStatus;
    }

    public int getIdTask() {
        return idTask;
    }

    public String getStatusTask() {
        return statusTask;
    }
}
