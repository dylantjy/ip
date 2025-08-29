package bruh;
import java.time.LocalDate;

/**
 * A task with a deadline.
 * <p>
 * Stores both the original user-supplied deadline text (for backward compatibility)
 * and a parsed {@link LocalDate} when available.
 */
public class Deadline extends Task {
    /** Original deadline text as entered by the user (kept for back-compat). */
    protected String by;
    /** Parsed deadline date, preferred when available. */
    protected LocalDate byDate;

    /**
     * Creates a {@code Deadline} from a description and a textual deadline.
     *
     * @param description the task description
     * @param by          the deadline string (e.g., {@code 2025-09-01})
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
        this.byDate = DateUtil.tryParseIso(by);
    }

    /**
     * Creates a {@code Deadline} from a description and a {@link LocalDate}.
     *
     * @param description the task description
     * @param byDate      the deadline date (may be {@code null})
     */
    public Deadline(String description, LocalDate byDate) {
        super(description);
        this.byDate = byDate;
        this.by = (byDate == null) ? "" : DateUtil.toIso(byDate);
    }

    /**
     * Returns the deadline as an ISO string if a parsed date exists,
     * otherwise returns the original user-supplied text.
     *
     * @return the deadline string
     */
    public String getBy() { return (byDate != null) ? DateUtil.toIso(byDate) : by; }

    /**
     * Returns the parsed deadline date, if available.
     *
     * @return the deadline as a {@link LocalDate}, or {@code null} if not parsed
     */
    public LocalDate getByDate() { return byDate; }

    /**
     * Returns a user-friendly representation of this deadline task.
     *
     * @return string representation including the pretty-formatted deadline
     */
    @Override
    public String toString() {
        String when = (byDate != null) ? DateUtil.toPretty(byDate) : by;
        return "[D]" + super.toString() + " (by: " + when + ")";
    }
}

