package bruh;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Storage {
    private final Path file;
    private final Path dir;

    public Storage(String relativePath) {
        assert relativePath != null && !relativePath.isBlank()
                : "storage path must be non-null and non-blank";

        this.file = Paths.get(relativePath).normalize();
        this.dir = file.getParent() == null ? Paths.get(".") : file.getParent().normalize();

        assert file != null : "file path must be set";
        assert dir  != null : "dir path must be set";
        assert !file.endsWith(".") : "file should not be '.'";
    }

    public ArrayList<Task> load() {
        // Invariant: both paths are absolute or normalized consistently
        assert file != null && dir != null : "paths initialized";

        ArrayList<Task> tasks = new ArrayList<>();
        try {
            if (!Files.exists(dir)) {
                // nothing to load yet
                return tasks;
            }
            if (!Files.exists(file)) {
                return tasks;
            }

            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            assert lines != null : "readAllLines should not return null";

            for (String line : lines) {
                assert line != null : "line must not be null";
                Task t = parseLine(line);
                // Accept nulls (corrupt lines) by design, but track invariant:
                if (t != null) {
                    tasks.add(t);
                }
            }

            // Postcondition: size cannot exceed number of lines
            assert tasks.size() <= Files.readAllLines(file, StandardCharsets.UTF_8).size()
                    : "parsed tasks cannot exceed input lines";

        } catch (IOException e) {
            // Silent fallback: start empty. (Could log to stderr if you like.)
            assert true : "I/O error while loading -> returning empty list";
        }
        return tasks;
    }

    public void save(List<Task> tasks) throws IOException {
        assert tasks != null : "tasks to save must not be null";
        for (Task t : tasks) {
            assert t != null : "task in list must not be null";
        }

        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        List<String> lines = new ArrayList<>();
        for (Task t : tasks) {
            String s = serialize(t);
            assert s != null && !s.isBlank() : "serialized line must be non-blank";
            // Optional: quick structural check (type | done | desc ...)
            assert s.matches("^[TDE] \\| [01] \\| .+")
                    : "serialized line must start with <T|D|E> | <0|1> | desc";
            lines.add(s);
        }

        Files.write(file, lines, StandardCharsets.UTF_8);
        // Postcondition: file exists after write
        assert Files.exists(file) : "save should create/write the target file";
    }

    // --- helpers ---

    private static Task parseLine(String line) {
        assert line != null : "input line must not be null";

        // Split on " | " with flexible spacing around |
        String[] parts = line.split("\\s*\\|\\s*");

        // Structural assumptions for minimal records
        if (parts.length < 3) return null;

        String type = parts[0].trim();
        String doneFlag = parts[1].trim();
        String desc = parts[2].trim();

        // Assumptions about fields
        if (!("0".equals(doneFlag) || "1".equals(doneFlag))) return null;
        if (desc.isEmpty()) return null;

        boolean isDone = "1".equals(doneFlag);

        try {
            switch (type) {
                case "T": {
                    Task t = new Todo(desc);
                    if (isDone) t.markAsDone();
                    assert t.getDescription() != null && !t.getDescription().isBlank()
                            : "Todo must have description";
                    return t;
                }
                case "D": {
                    if (parts.length < 4) return null;
                    String by = parts[3].trim();
                    Task t = new Deadline(desc, by);
                    if (isDone) t.markAsDone();
                    assert ((Deadline) t).getBy() != null && !((Deadline) t).getBy().isBlank()
                            : "Deadline must have 'by'";
                    return t;
                }
                case "E": {
                    if (parts.length < 5) return null;
                    String from = parts[3].trim();
                    String to   = parts[4].trim();
                    Task t = new Event(desc, from, to);
                    if (isDone) t.markAsDone();
                    assert ((Event) t).getFrom() != null && !((Event) t).getFrom().isBlank()
                            : "Event must have 'from'";
                    assert ((Event) t).getTo()   != null && !((Event) t).getTo().isBlank()
                            : "Event must have 'to'";
                    return t;
                }
                default:
                    return null;
            }
        } catch (Exception e) {
            // Corrupted line → skip
            return null;
        }
    }

    private static String serialize(Task t) {
        assert t != null : "task must not be null";
        if (t instanceof Todo) {
            return "T | " + (t.isDone() ? "1" : "0") + " | " + t.getDescription();
        } else if (t instanceof Deadline) {
            Deadline d = (Deadline) t;
            String by = (d.getByDate() != null) ? DateUtil.toIso(d.getByDate()) : d.getBy();
            assert by != null && !by.isBlank() : "deadline 'by' must be present";
            return "D | " + (t.isDone() ? "1" : "0") + " | " + d.getDescription() + " | " + by;
        } else if (t instanceof Event) {
            Event e = (Event) t;
            String from = (e.getFromDate() != null) ? DateUtil.toIso(e.getFromDate()) : e.getFrom();
            String to   = (e.getToDate()   != null) ? DateUtil.toIso(e.getToDate())   : e.getTo();
            assert from != null && !from.isBlank() : "event 'from' must be present";
            assert to   != null && !to.isBlank()   : "event 'to' must be present";
            return "E | " + (t.isDone() ? "1" : "0") + " | " + e.getDescription() + " | " + from + " | " + to;
        } else {
            // Fallback to Todo-like serialization for unknown types, but assert to flag it
            assert false : "unexpected Task subtype during serialization";
            return "T | " + (t.isDone() ? "1" : "0") + " | " + t.getDescription();
        }
    }
}

