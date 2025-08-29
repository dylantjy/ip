package bruh;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles persistence of tasks to and from a flat file.
 * <p>
 * Uses a simple line-based format:
 * <ul>
 *   <li>{@code T | 0/1 | description}</li>
 *   <li>{@code D | 0/1 | description | yyyy-MM-dd or raw}</li>
 *   <li>{@code E | 0/1 | description | from | to}</li>
 * </ul>
 * Lines that are malformed or cannot be parsed are skipped gracefully.
 */
public class Storage {
    private final Path file;
    private final Path dir;

    /**
     * Creates a {@code Storage} that reads/writes to the given relative path.
     *
     * @param relativePath path to the data file (e.g., {@code data/bruh.txt})
     */
    public Storage(String relativePath) {
        this.file = Paths.get(relativePath).normalize();
        this.dir = file.getParent() == null ? Paths.get(".") : file.getParent().normalize();
    }

    /**
     * Loads tasks from disk.
     * <p>
     * If the directory/file does not exist, returns an empty list.
     * Malformed lines are ignored.
     *
     * @return a mutable list of tasks loaded from the file (possibly empty)
     */
    public ArrayList<Task> load() {
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
            for (String line : lines) {
                Task t = parseLine(line);
                if (t != null) {
                    tasks.add(t);
                }
            }
        } catch (IOException e) {
            // Silent fallback: start empty. (Could log to stderr if you like.)
        }
        return tasks;
    }

    /**
     * Saves the given tasks to disk, creating parent directories if needed.
     *
     * @param tasks the tasks to persist
     * @throws IOException if writing to the file fails
     */
    public void save(List<Task> tasks) throws IOException {
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        List<String> lines = new ArrayList<>();
        for (Task t : tasks) {
            lines.add(serialize(t));
        }
        Files.write(file, lines, StandardCharsets.UTF_8);
    }

    // --- helpers ---

    /**
     * Parses a single storage line into a {@link Task}.
     * Lines that cannot be parsed return {@code null}.
     *
     * @param line one line from the storage file
     * @return the parsed task, or {@code null} if the line is invalid
     */
    private static Task parseLine(String line) {
        // Split on " | " with flexible spacing around |
        String[] parts = line.split("\\s*\\|\\s*");
        if (parts.length < 3) return null;

        String type = parts[0].trim();
        String doneFlag = parts[1].trim();
        String desc = parts[2].trim();
        boolean isDone = "1".equals(doneFlag);

        try {
                switch (type) {
                case "T": {
                    Task t = new Todo(desc);
                    if (isDone) t.markAsDone();
                    return t;
                }
                case "D": {
                    if (parts.length < 4) return null;
                    String by = parts[3].trim();
                    Task t = new Deadline(desc, by);
                    if (isDone) t.markAsDone();
                    return t;
                }
                case "E": {
                    if (parts.length < 5) return null;
                    String from = parts[3].trim();
                    String to = parts[4].trim();
                    Task t = new Event(desc, from, to);
                    if (isDone) t.markAsDone();
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

    /**
     * Serializes a {@link Task} to a single storage line using the format
     * documented in the class header.
     *
     * @param t the task to serialize
     * @return the line to write to storage
     */
    private static String serialize(Task t) {
        if (t instanceof Todo) {
            return "T | " + (t.isDone() ? "1" : "0") + " | " + t.getDescription();
        } else if (t instanceof Deadline) {
            Deadline d = (Deadline) t;
            String by = (d.getByDate() != null) ? DateUtil.toIso(d.getByDate()) : d.getBy();
            return "D | " + (t.isDone() ? "1" : "0") + " | " + d.getDescription() + " | " + by;
        } else if (t instanceof Event) {
            Event e = (Event) t;
            String from = (e.getFromDate() != null) ? DateUtil.toIso(e.getFromDate()) : e.getFrom();
            String to   = (e.getToDate()   != null) ? DateUtil.toIso(e.getToDate())   : e.getTo();
            return "E | " + (t.isDone() ? "1" : "0") + " | " + e.getDescription() + " | " + from + " | " + to;
        } else {
            return "T | " + (t.isDone() ? "1" : "0") + " | " + t.getDescription();
        }
    }
}

