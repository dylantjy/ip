import java.time.LocalDate;

public class Deadline extends Task {
    protected String by;            // keep original text for back-compat
    protected LocalDate byDate;     // preferred when available

    public Deadline(String description, String by) {
        super(description);
        this.by = by;
        this.byDate = DateUtil.tryParseIso(by);
    }

    // new ctor for direct LocalDate usage (optional)
    public Deadline(String description, LocalDate byDate) {
        super(description);
        this.byDate = byDate;
        this.by = (byDate == null) ? "" : DateUtil.toIso(byDate);
    }

    public String getBy() { return (byDate != null) ? DateUtil.toIso(byDate) : by; }
    public LocalDate getByDate() { return byDate; }

    @Override
    public String toString() {
        String when = (byDate != null) ? DateUtil.toPretty(byDate) : by;
        return "[D]" + super.toString() + " (by: " + when + ")";
    }
}
