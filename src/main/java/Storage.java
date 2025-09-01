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
        this.file = Paths.get(relativePath).normalize();
        this.dir = file.getParent() == null ? Paths.get(".") : file.getParent().normalize();
    }

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
