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
        assert s != null : "Date string must not be null";   // <--- added
        try {
            LocalDate parsed = LocalDate.parse(s.trim(), ISO);
            assert parsed != null : "Parsed date must not be null";   // <--- added
            return parsed;
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static String toIso(LocalDate d) {
        assert d != null : "LocalDate must not be null";   // <--- added
        String formatted = d.format(ISO);
        assert formatted.matches("\\d{4}-\\d{2}-\\d{2}") : "toIso must return yyyy-MM-dd"; // optional check
        return formatted;
    }

    public static String toPretty(LocalDate d) {
        assert d != null : "LocalDate must not be null";   // <--- added
        String formatted = d.format(PRETTY);
        assert formatted != null && !formatted.isBlank() : "toPretty must not return blank";
        return formatted;
    }
}

