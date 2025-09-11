package bruh;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class DateParsing {
    private DateParsing() {}

    public static LocalDate tryParseToDate(String s) {
        if (s == null || s.isBlank()) return null;
        String t = s.trim();
        try { return LocalDate.parse(t); } catch (Exception ignored) {}
        try { return LocalDateTime.parse(t).toLocalDate(); } catch (Exception ignored) {}
        try { return LocalDateTime.parse(t.replace(' ', 'T')).toLocalDate(); } catch (Exception ignored) {}
        // new: parse "Oct 10 2025"
        try { 
            return LocalDate.parse(t, DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH));
        } catch (Exception ignored) {}
        try { 
            return LocalDate.parse(t, DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH));
        } catch (Exception ignored) {}
        return null;
    }
}

