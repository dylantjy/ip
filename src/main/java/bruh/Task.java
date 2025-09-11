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

    public void snoozeUntil(LocalDateTime until) {
        if (isDone) throw new IllegalStateException("Cannot snooze a completed task.");
        if (until == null || until.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Snooze time must be in the future.");
        }
        this.snoozeUntil = until;
    }

    public void snoozeFor(Duration d) {
        if (d == null || d.isNegative() || d.isZero()) {
            throw new IllegalArgumentException("Duration must be positive.");
        }
        snoozeUntil(LocalDateTime.now().plus(d));
    }

    public void unsnooze(){
    this.snoozeUntil = null;
    }

    public boolean isSnoozedNow() {
        return snoozeUntil != null && snoozeUntil.isAfter(LocalDateTime.now());
    }

    @Override
    public String toString() {
        String base = "[" + getStatusIcon() + "] " + description;
        if (isSnoozedNow()) {
            base += " (snoozed until " + snoozeUntil.toString() + ")";
        }
        return base;
    }
}
