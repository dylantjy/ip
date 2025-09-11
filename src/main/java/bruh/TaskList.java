package bruh;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

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

}

