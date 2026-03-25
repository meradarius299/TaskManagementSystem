package org.data_model.data_access;

public final class SimpleTask extends Task {
    public int startHour;
    public int endHour;

    public SimpleTask(int idTask, int startHour, int endHour) {
        super(idTask);
        this.idTask = idTask;
        this.startHour = startHour;
        this.endHour = endHour;
        this.statusTask = statusTask;
    }

    @Override
    public int estimateDuration() {

        if (this.startHour > this.endHour)
            return 24 - (this.startHour - this.endHour);
        else
            return this.endHour - this.startHour;
    }
}
