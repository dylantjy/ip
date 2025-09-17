package bruh;

import java.time.*;

public class Task {
    protected String description;
    protected boolean isDone;
    protected LocalDateTime snoozeUntil;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
        this.snoozeUntil = null;
    }

    public String getDescription() { return description; }
    public boolean isDone() { return isDone; }
    public LocalDateTime getSnoozeUntil() { return snoozeUntil; }

    public String getStatusIcon() { return (isDone ? "X" : " "); }

    public void markAsDone() { this.isDone = true; }
    public void markAsNotDone() { this.isDone = false; }

    @Override
    public String toString() {
        String base = "[" + getStatusIcon() + "] " + description;
        return base;
    }
}
