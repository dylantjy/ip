package bruh;

import java.util.ArrayList;
import java.util.List;

/**
 * Mutable list wrapper for {@link Task} objects with 1-based indexing helpers.
 * <p>
 * Provides operations to add, delete, mark/unmark tasks, search by keyword,
 * and expose the underlying list for persistence.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() { this.tasks = new ArrayList<>(); }

    /**
     * Creates a task list initialized with the given tasks.
     *
     * @param initial tasks to copy into this list
     */
    public TaskList(List<Task> initial) { this.tasks = new ArrayList<>(initial); }

    /**
     * Returns the number of tasks.
     *
     * @return task count
     */
    public int size() { return tasks.size(); }

    /**
     * Returns the task at the given 1-based position.
     *
     * @param idx1Based 1-based index of the task to retrieve
     * @return the task at that position
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public Task get(int idx1Based) { return tasks.get(idx1Based - 1); }

    /**
     * Appends a task to the end of the list.
     *
     * @param t task to add
     */
    public void add(Task t) { tasks.add(t); }

    /**
     * Removes and returns the task at the given 1-based position.
     *
     * @param idx1Based 1-based index of the task to remove
     * @return the removed task
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public Task delete(int idx1Based) { return tasks.remove(idx1Based - 1); }

    /**
     * Marks the task at the given 1-based position as done.
     *
     * @param idx1Based 1-based index of the task to mark
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public void mark(int idx1Based) { get(idx1Based).markAsDone(); }

    /**
     * Marks the task at the given 1-based position as not done.
     *
     * @param idx1Based 1-based index of the task to unmark
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public void unmark(int idx1Based) { get(idx1Based).markAsNotDone(); }

    /**
     * Returns the underlying mutable list for persistence.
     *
     * @return the internal list of tasks
     */
    public List<Task> asList() { return tasks; }

    /**
     * Finds tasks whose descriptions contain the given keyword (case-sensitive).
     *
     * @param keyword substring to search for in task descriptions
     * @return a new list containing matching tasks (possibly empty)
     */
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

