package bruh;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.lang.reflect.Field;
import java.time.format.DateTimeFormatter;

public class TaskList {
    private final ArrayList<Task> tasks;

    public TaskList() { 
        this.tasks = new ArrayList<>(); 
        assert tasks.isEmpty() : "new TaskList should start empty"; 
    }

    public TaskList(List<Task> initial) { 
        assert initial != null : "initial list must not be null";
        this.tasks = new ArrayList<>(initial); 
        assert this.tasks.size() == initial.size() : "copied all initial tasks";
    }

    public int size() { 
        assert tasks != null : "tasks list must not be null";
        return tasks.size(); 
    }

    public Task get(int idx1Based) { 
        assert idx1Based > 0 && idx1Based <= tasks.size() 
            : "1-based index must be within bounds";
        return tasks.get(idx1Based - 1); 
    }

    public void add(Task... ts) {
        assert ts != null : "tasks to add must not be null";
        for (Task t : ts) {
            assert t != null : "task must not be null";
            tasks.add(t);
        }
        assert tasks.size() >= ts.length : "tasks list grew after add";
    }

    public Task delete(int idx1Based) { 
        assert idx1Based > 0 && idx1Based <= tasks.size() 
            : "delete index must be in range";
        Task removed = tasks.remove(idx1Based - 1); 
        assert removed != null : "removed task should not be null";
        return removed;
    }

    public void mark(int idx1Based) { 
        assert idx1Based > 0 && idx1Based <= tasks.size() : "mark index valid";
        get(idx1Based).markAsDone(); 
    }

    public void unmark(int idx1Based) { 
        assert idx1Based > 0 && idx1Based <= tasks.size() : "unmark index valid";
        get(idx1Based).markAsNotDone(); 
    }

    public List<Task> asList() { 
        assert tasks != null : "tasks list must not be null";
        return Collections.unmodifiableList(tasks); 
    }

    public List<Task> find(String keyword) {
        assert keyword != null && !keyword.isBlank() : "keyword must be non-empty";
        List<Task> matches = new ArrayList<>();
        for (Task task : tasks) {
            assert task != null : "task in list must not be null";
            if (task.getDescription().contains(keyword)) {
                matches.add(task);
            }
        }
        return matches;
    }
    
    public List<Task> findAny(String... keywords) {
        assert keywords != null && keywords.length > 0 : "at least one keyword";
        List<Task> matches = new ArrayList<>();
        for (Task task : tasks) {
            assert task != null : "task must not be null";
            if (matchesAny(task, keywords)) {
                matches.add(task);
            }
        }  
        return matches;
     } 

     private static boolean matchesAny(Task task, String... keywords) {
         String d = task.getDescription();
         for (String kw : keywords) {
             if (kw != null && !kw.isBlank() && d.contains(kw)) return true;
         }
         return false;
     }

// === RESCHED support (paste inside TaskList class) ==========================

/** 1-based index guard (GUI passes 1,2,3...). */
private Task at(int idx1Based) {
    assert idx1Based > 0 && idx1Based <= tasks.size() : "index out of range";
    return tasks.get(idx1Based - 1);
}

/** Try multiple date formats; return null if unparsable. */
private static LocalDateTime parseFlexibleToLdt(String s) {
    if (s == null) return null;
    String n = s.trim().replace(' ', 'T'); // allow "YYYY-MM-DD HH:MM"
    try { return LocalDateTime.parse(n); } catch (DateTimeParseException ignored) {}
    try { return LocalDate.parse(n).atStartOfDay(); } catch (DateTimeParseException ignored) {}

    // common non-ISO date patterns (IP/Duke often uses d/M/yyyy)
    String[] pats = {"d/M/yyyy", "dd/M/yyyy", "d/MM/yyyy", "dd/MM/yyyy", "d-M-yyyy", "dd-MM-yyyy"};
    for (String p : pats) {
        try {
            return LocalDate.parse(s.trim(), DateTimeFormatter.ofPattern(p)).atStartOfDay();
        } catch (DateTimeParseException ignored) {}
    }
    return null;
}

/** Format as date-only if midnight; otherwise full ISO datetime. */
private static String formatPreferDate(LocalDateTime dt) {
    boolean isMidnight = dt.getHour() == 0 && dt.getMinute() == 0
            && dt.getSecond() == 0 && dt.getNano() == 0;
    return isMidnight ? dt.toLocalDate().toString() : dt.toString();
}

/** Locate likely deadline field name. */
private static Field findDeadlineField(Class<?> cls) {
    String[] names = {"by", "deadline", "due"};
    while (cls != null) {
        for (String n : names) {
            try {
                Field f = cls.getDeclaredField(n);
                f.setAccessible(true);
                return f;
            } catch (NoSuchFieldException ignored) { }
        }
        cls = cls.getSuperclass();
    }
    return null;
}

/** Read current deadline as LocalDateTime from a Deadline-like field. */
private static LocalDateTime readDeadlineAsLdt(Object obj, Field f) {
    try {
        Object v = f.get(obj);
        if (v == null) return null;
        if (v instanceof LocalDateTime) return (LocalDateTime) v;
        if (v instanceof LocalDate) return ((LocalDate) v).atStartOfDay();
        if (v instanceof String) return parseFlexibleToLdt((String) v);
    } catch (IllegalAccessException ignored) { }
    return null;
}

/** Attempt to write the deadline field; return true on success. */
private static boolean writeDeadline(Object obj, Field f, LocalDateTime newBy, String originalText) {
    try {
        Class<?> type = f.getType();
        if (type.equals(LocalDateTime.class)) {
            f.set(obj, newBy);
            return true;
        } else if (type.equals(LocalDate.class)) {
            f.set(obj, newBy.toLocalDate());
            return true;
        } else if (type.equals(String.class)) {
            // If caller passed an exact text (from user), prefer that; else format neatly.
            String s = (originalText != null && !originalText.isBlank())
                    ? originalText.trim()
                    : formatPreferDate(newBy);
            f.set(obj, s);
            return true;
        }
    } catch (IllegalAccessException ignored) { }
    return false;
}

/** Shift a Deadline task’s date by N days (can be negative). */
public void shiftDeadlineByDays(int idx1Based, int days) {
    Task t = at(idx1Based);
    if (!(t instanceof Deadline)) {
        throw new IllegalArgumentException("Selected task has no deadline to shift.");
    }
    Field f = findDeadlineField(t.getClass());
    if (f == null) throw new IllegalStateException("Can't find deadline field (by/deadline/due).");

    LocalDateTime cur = readDeadlineAsLdt(t, f);
    if (cur == null) throw new IllegalStateException("Existing deadline is not a recognizable date.");

    LocalDateTime newer = cur.plusDays(days);
    if (!writeDeadline(t, f, newer, null)) {
        // Fallback: replace with a new Deadline(desc, dateText)
        String desc = t.getDescription();
        tasks.set(idx1Based - 1, new Deadline(desc, formatPreferDate(newer)));
    }
}

/** Set a Deadline task’s date to an absolute date/time (text comes from GUI). */
public void rescheduleDeadlineAbsolute(int idx1Based, String when) {
    Task t = at(idx1Based);
    if (!(t instanceof Deadline)) {
        throw new IllegalArgumentException("Selected task has no deadline to reschedule.");
    }
    LocalDateTime newBy = parseFlexibleToLdt(when);
    if (newBy == null) throw new IllegalArgumentException("Unrecognized date format: " + when);

    Field f = findDeadlineField(t.getClass());
    if (f == null || !writeDeadline(t, f, newBy, when)) {
        // Fallback: replace with a new Deadline(desc, dateText)
        String desc = t.getDescription();
        tasks.set(idx1Based - 1, new Deadline(desc, formatPreferDate(newBy)));
    }
}
// === end RESCHED support =====================================================

	
}

