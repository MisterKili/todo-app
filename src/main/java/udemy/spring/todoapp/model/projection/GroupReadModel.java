package udemy.spring.todoapp.model.projection;

import udemy.spring.todoapp.model.Task;
import udemy.spring.todoapp.model.TaskGroup;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class GroupReadModel {

    private int id;
    private String description;
    /**
     * Deadline of the latest task in group
     */
    private LocalDateTime deadline;
    private Set<GroupTaskReadModel> tasks;
    private boolean done;

    public GroupReadModel(TaskGroup source) {
        id = source.getId();
        description = source.getDescription();
        done = source.isDone();
        source.getTasks().stream()
                .map(Task::getDeadline)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .ifPresent(date -> deadline = date);
        tasks = source.getTasks().stream()
                .map(GroupTaskReadModel::new)
                .collect(Collectors.toSet());
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public boolean isDone() {
        return done;
    }

    public Set<GroupTaskReadModel> getTasks() {
        return tasks;
    }

    public void setTasks(Set<GroupTaskReadModel> tasks) {
        this.tasks = tasks;
    }
}
