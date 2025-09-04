package bruh;

import java.util.ArrayList;
import java.util.List;

public class TaskList {
    private final ArrayList<Task> tasks;

    public TaskList() { this.tasks = new ArrayList<>(); }
    public TaskList(List<Task> initial) { this.tasks = new ArrayList<>(initial); }

    public int size() { return tasks.size(); }
    public Task get(int idx1Based) { return tasks.get(idx1Based - 1); }

    public void add(Task t) { tasks.add(t); }
    public Task delete(int idx1Based) { return tasks.remove(idx1Based - 1); }

    public void mark(int idx1Based) { get(idx1Based).markAsDone(); }
    public void unmark(int idx1Based) { get(idx1Based).markAsNotDone(); }

    public List<Task> asList() { return tasks; }

    public List<Task> find(String keyword) {
        List<Task> matches = new ArrayList<>();
        for (Task task : tasks) {          // assuming you have a List<Task> tasks;
            if (task.getDescription().contains(keyword)) {
                matches.add(task);
            }
        }
        return matches;
    }

}
