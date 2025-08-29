package bruh;

/**
 * Base class representing a generic task with a description and completion status.
 * <p>
 * Subclasses (e.g., {@code Todo}, {@code Deadline}, {@code Event}) extend this
 * to provide additional fields/behavior.
 */
public class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Creates a task with the given description; tasks start as not done.
     *
     * @param description human-readable description of the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the task description.
     *
     * @return description text
     */
    public String getDescription() { return description; }

    /**
     * Returns whether the task is marked as done.
     *
     * @return {@code true} if done, otherwise {@code false}
     */
    public boolean isDone() { return isDone; }

    /**
     * Returns a single-character status icon for display.
     * {@code "X"} for done, space for not done.
     *
     * @return status icon string
     */
    public String getStatusIcon() { return (isDone ? "X" : " "); }

    /**
     * Marks this task as done.
     */
    public void markAsDone() { this.isDone = true; }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() { this.isDone = false; }

    /**
     * Returns a compact string representation including status icon and description.
     *
     * @return formatted task string
     */
    @Override
    public String toString() { return "[" + getStatusIcon() + "] " + description; }
}

