package bruh;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class DateUtil {
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE; // yyyy-MM-dd
    private static final DateTimeFormatter PRETTY = DateTimeFormatter.ofPattern("MMM d yyyy");

    private DateUtil() {}

    // Try parse yyyy-MM-dd; return null if not parseable
    public static LocalDate tryParseIso(String s) {
        try {
            return LocalDate.parse(s.trim(), ISO);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static String toIso(LocalDate d) {
        return d.format(ISO);
    }

    public static String toPretty(LocalDate d) {
        return d.format(PRETTY);
    }
}
