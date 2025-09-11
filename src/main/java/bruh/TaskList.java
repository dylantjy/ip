package bruh;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    // already had this variant — kept with minor tweaks
    public void add(Task t) {
        assert t != null : "task must not be null";
        tasks.add(t);
        assert tasks.get(tasks.size() - 1) == t : "task appended at end";
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
        return tasks;
    }

    // --- Snooze APIs ---

    /** Snooze task at 1-based index until an absolute time. */
    public void snoozeUntil(int idx1Based, LocalDateTime until) {
        assert idx1Based > 0 && idx1Based <= tasks.size() : "snooze index valid";
        assert until != null : "snooze-until must not be null";
        Task t = get(idx1Based);
        assert !t.isDone() : "cannot snooze a completed task";
        assert until.isAfter(LocalDateTime.now()) : "snooze target must be in the future";
        t.snoozeUntil(until); // uses Task’s guardrails
    }

    /** Snooze task at 1-based index for a relative duration. */
    public void snoozeFor(int idx1Based, Duration d) {
        assert idx1Based > 0 && idx1Based <= tasks.size() : "snooze index valid";
        assert d != null && !d.isNegative() && !d.isZero() : "duration must be positive";
        Task t = get(idx1Based);
        assert !t.isDone() : "cannot snooze a completed task";
        t.snoozeFor(d);
    }

    /** Clear snooze on a task (wake immediately). */
    public void unsnooze(int idx1Based) {
        assert idx1Based > 0 && idx1Based <= tasks.size() : "unsnooze index valid";
        get(idx1Based).unsnooze();
    }

    /** Number of tasks currently snoozed (as of now). */
    public long snoozedCount() {
        return tasks.stream().filter(Task::isSnoozedNow).count();
    }

    /** Tasks that are not done and not currently snoozed. */
    public List<Task> listActive() {
        return tasks.stream()
            .filter(t -> !t.isDone() && !t.isSnoozedNow())
            .collect(Collectors.toList());
    }

    /** Tasks that are currently snoozed (hidden from active view). */
    public List<Task> listSnoozed() {
        return tasks.stream()
            .filter(Task::isSnoozedNow)
            .collect(Collectors.toList());
    }

    // --- Search (existing behavior: case-sensitive contains over ALL tasks) ---

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

        outer:
        for (Task task : tasks) {
            assert task != null : "task must not be null";
            String d = task.getDescription();
            for (String kw : keywords) {
                assert kw != null && !kw.isBlank() : "keyword must be non-empty";
                if (d.contains(kw)) {
                    matches.add(task);
                    continue outer;
                }
            }
        }
        return matches;
    }

    // --- Active-only search helpers (don’t surface snoozed tasks) ---

    public List<Task> findActive(String keyword) {
        assert keyword != null && !keyword.isBlank() : "keyword must be non-empty";
        return listActive().stream()
            .filter(t -> t.getDescription().contains(keyword))
            .collect(Collectors.toList());
    }

    public List<Task> findAnyActive(String... keywords) {
        assert keywords != null && keywords.length > 0 : "at least one keyword";
        List<Task> matches = new ArrayList<>();
        outer:
        for (Task task : listActive()) {
            String d = task.getDescription();
            for (String kw : keywords) {
                assert kw != null && !kw.isBlank() : "keyword must be non-empty";
                if (d.contains(kw)) {
                    matches.add(task);
                    continue outer;
                }
            }
        }
        return matches;
    }

    // Set to an absolute date (string). Prefer LocalDate if parseable.
    public void rescheduleDeadlineAbsolute(int idx1Based, String newByText) {
       assert idx1Based > 0 && idx1Based <= tasks.size();
       Task t = get(idx1Based);
       if (!(t instanceof Deadline dl)) {
           throw new IllegalStateException("Task #" + idx1Based + " is not a deadline.");
       }
       java.time.LocalDate d = DateParsing.tryParseToDate(newByText);
       if (d != null) dl.setByDate(d); else dl.setByText(newByText);
    }

    // Shift by N days relative to current byDate/by text.
    public void shiftDeadlineByDays(int idx1Based, int days) {
       assert idx1Based > 0 && idx1Based <= tasks.size();
       Task t = get(idx1Based);
       if (!(t instanceof Deadline dl)) {
          throw new IllegalStateException("Task #" + idx1Based + " is not a deadline.");
       }
       java.time.LocalDate base = dl.getByDate();
       if (base == null) {
          java.time.LocalDate parsed = DateParsing.tryParseToDate(dl.getByText());
          if (parsed == null) {
              throw new IllegalArgumentException("Cannot parse current deadline date: " + dl.getByText());
          }
          dl.setByDate(parsed);
          base = parsed;
       }
       dl.setByDate(base.plusDays(days));
    }


}


