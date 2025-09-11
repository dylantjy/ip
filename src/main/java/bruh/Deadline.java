package bruh;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


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

    // Getter that always returns ISO (yyyy-MM-dd)
    public String getBy() {
       return (byDate != null) ? byDate.toString() : by;
    }

    public LocalDate getByDate() { return byDate; }

    public String getByText() { return by; } // keep if other code uses it

    public void setByText(String newBy) {
        this.by = newBy;
        // keep byDate in sync if parseable
        LocalDate parsed = DateParsing.tryParseToDate(newBy);
        if (parsed != null) this.byDate = parsed;
    }

    public void setByDate(LocalDate newByDate) {
       this.byDate = newByDate;
       this.by = (newByDate == null) ? this.by : newByDate.toString(); // ISO text
    }

    @Override
    public String toString() {
       String when = (byDate != null)
           ? byDate.format(java.time.format.DateTimeFormatter.ofPattern("MMM d yyyy", java.util.Locale.ENGLISH))
           : by;
       return "[D][" + getStatusIcon() + "] " + description + " (by: " + when + ")";
    }
}

