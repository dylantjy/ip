package bruh;

import java.time.*;

public final class TimeParsing {
    private TimeParsing() {}

    /** "+3d2h30m", "+15m", "+2h", "+1d" */
    public static Duration parseRelative(String s) {
        if (s == null || !s.startsWith("+"))
            throw new IllegalArgumentException("Relative duration must start with '+', e.g., +3d2h30m");
        long days = 0, hours = 0, minutes = 0;
        String num = "";
        String m = s.substring(1).toLowerCase();

        for (int i = 0; i < m.length(); i++) {
            char c = m.charAt(i);
            if (Character.isDigit(c)) { num += c; continue; }
            if (num.isEmpty()) throw new IllegalArgumentException("Missing number before unit '" + c + "'");
            long val = Long.parseLong(num); num = "";
            switch (c) {
                case 'd' -> days += val;
                case 'h' -> hours += val;
                case 'm' -> minutes += val;
                default -> throw new IllegalArgumentException("Unknown unit: " + c + " (use d/h/m)");
            }
        }
        if (!num.isEmpty()) throw new IllegalArgumentException("Dangling number at end of duration.");
        Duration d = Duration.ofDays(days).plusHours(hours).plusMinutes(minutes);
        if (d.isZero() || d.isNegative()) throw new IllegalArgumentException("Duration must be > 0");
        return d;
    }

    /** Accepts:
     *  - "YYYY-MM-DDTHH:MM"
     *  - "YYYY-MM-DD HH:MM"
     *  - "YYYY-MM-DD" (interpreted as 00:00)
     */
    public static LocalDateTime parseDateTime(String s) {
        if (s == null || s.isBlank()) throw new IllegalArgumentException("Empty date/time");
        String t = s.trim();
        if (t.matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}$")) return LocalDateTime.parse(t);
        if (t.matches("^\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}$"))
            return LocalDateTime.parse(t.replace(' ', 'T'));
        if (t.matches("^\\d{4}-\\d{2}-\\d{2}$")) return LocalDate.parse(t).atStartOfDay();
        throw new IllegalArgumentException("Unsupported date/time format: " + s);
    }
}

