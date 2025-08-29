package bruh;
import java.time.LocalDate;

/**
 * Represents an event task spanning a start and end date/time.
 * <p>
 * Keeps both the original textual inputs and parsed {@link LocalDate}
 * values (when parseable) for display and persistence compatibility.
 */
public class Event extends Task {
    protected String from;
    protected String to;
    protected LocalDate fromDate;   // if parsed
    protected LocalDate toDate;     // if parsed

    /**
     * Creates an {@code Event} from textual start/end values.
     *
     * @param description description of the event
     * @param from        start date/time as text (e.g., {@code 2025-09-01})
     * @param to          end date/time as text
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
        this.fromDate = DateUtil.tryParseIso(from);
        this.toDate = DateUtil.tryParseIso(to);
    }

    /**
     * Returns the start value as ISO date text if parsed, otherwise the original text.
     *
     * @return start value in ISO string or original text
     */
    public String getFrom() { return (fromDate != null) ? DateUtil.toIso(fromDate) : from; }

    /**
     * Returns the end value as ISO date text if parsed, otherwise the original text.
     *
     * @return end value in ISO string or original text
     */
    public String getTo()   { return (toDate != null)   ? DateUtil.toIso(toDate)   : to; }

    /**
     * Returns the parsed start date, if available.
     *
     * @return {@link LocalDate} for start, or {@code null} if not parsed
     */
    public LocalDate getFromDate() { return fromDate; }

    /**
     * Returns the parsed end date, if available.
     *
     * @return {@link LocalDate} for end, or {@code null} if not parsed
     */
    public LocalDate getToDate()   { return toDate; }

    /**
     * Returns a user-friendly string containing the event description and range.
     *
     * @return formatted string for display
     */
    @Override
    public String toString() {
        String fromStr = (fromDate != null) ? DateUtil.toPretty(fromDate) : from;
        String toStr   = (toDate   != null) ? DateUtil.toPretty(toDate)   : to;
        return "[E]" + super.toString() + " (from: " + fromStr + " to: " + toStr + ")";
    }
}

