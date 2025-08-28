public class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public String getDescription() { return description; }
    public boolean isDone() { return isDone; }

    public String getStatusIcon() { return (isDone ? "X" : " "); }

    public void markAsDone() { this.isDone = true; }
    public void markAsNotDone() { this.isDone = false; }

    @Override
    public String toString() { return "[" + getStatusIcon() + "] " + description; }
}

