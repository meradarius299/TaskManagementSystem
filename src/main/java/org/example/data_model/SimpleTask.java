package org.example.data_model;

public final class SimpleTask extends Task {
    public int startHour;
    public int endHour;

    public SimpleTask(int idTask, int startHour, int endHour) {
        super(idTask);
        this.startHour = startHour;
        this.endHour = endHour;
    }

    @Override
    public int estimateDuration() {

            if(this.startHour > this.endHour)
                return 24 - (this.startHour - this.endHour);
            else
                return this.endHour - this.startHour;
    }
}
