package org.data_model.data_access;

public final class SimpleTask extends Task {
    public int startHour;
    public int endHour;

    public SimpleTask(int id, int start, int end) {
        super(id);
        this.idTask = id;
        this.startHour = start;
        this.endHour = end;
        this.statusTask = "Uncompleted";
    }

    @Override
    public int estimateDuration() {

            if(this.startHour > this.endHour)
                return 24 - (this.startHour - this.endHour);
            else
                return this.endHour - this.startHour;
    }
}
