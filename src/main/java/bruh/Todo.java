package bruh;

/**
 * A simple task with only a description (no dates/times).
 */
public class Todo extends Task {

    /**
     * Creates a {@code Todo} with the given description.
     *
     * @param description the task description
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns a string representation prefixed with {@code [T]}.
     *
     * @return formatted string for display
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}

