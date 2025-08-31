package bruh;
import java.time.LocalDate;

public class Event extends Task {
    protected String from;
    protected String to;
    protected LocalDate fromDate;   // if parsed
    protected LocalDate toDate;     // if parsed

    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
        this.fromDate = DateUtil.tryParseIso(from);
        this.toDate = DateUtil.tryParseIso(to);
    }

    public String getFrom() { return (fromDate != null) ? DateUtil.toIso(fromDate) : from; }
    public String getTo()   { return (toDate != null)   ? DateUtil.toIso(toDate)   : to; }
    public LocalDate getFromDate() { return fromDate; }
    public LocalDate getToDate()   { return toDate; }

    @Override
    public String toString() {
        String fromStr = (fromDate != null) ? DateUtil.toPretty(fromDate) : from;
        String toStr   = (toDate   != null) ? DateUtil.toPretty(toDate)   : to;
        return "[E]" + super.toString() + " (from: " + fromStr + " to: " + toStr + ")";
    }
}