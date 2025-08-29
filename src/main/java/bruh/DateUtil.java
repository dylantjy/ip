package bruh;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utilities for parsing and formatting dates used by the Bruh application.
 * <p>
 * Supports ISO-8601 format ({@code yyyy-MM-dd}) and a human-friendly
 * display format ({@code MMM d yyyy}).
 */
public final class DateUtil {
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE; // yyyy-MM-dd
    private static final DateTimeFormatter PRETTY = DateTimeFormatter.ofPattern("MMM d yyyy");

    /** Prevent instantiation of this utility class. */
    private DateUtil() {}

    /**
     * Attempts to parse the given string as an ISO-8601 date ({@code yyyy-MM-dd}).
     *
     * @param s the string to parse
     * @return the parsed {@link LocalDate}, or {@code null} if parsing fails
     */
    public static LocalDate tryParseIso(String s) {
        try {
            return LocalDate.parse(s.trim(), ISO);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Formats the given date as an ISO-8601 string ({@code yyyy-MM-dd}).
     *
     * @param d the date to format
     * @return ISO string representation of the date
     */
    public static String toIso(LocalDate d) {
        return d.format(ISO);
    }

    /**
     * Formats the given date as a human-friendly string ({@code MMM d yyyy}).
     *
     * @param d the date to format
     * @return pretty string representation of the date
     */
    public static String toPretty(LocalDate d) {
        return d.format(PRETTY);
    }
}

