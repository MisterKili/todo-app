package udemy.spring.todoapp.model.projection;

import udemy.spring.todoapp.model.Task;

import java.time.LocalDateTime;

public class GroupTaskReadModel {

    private String description;
    private boolean done;
    private LocalDateTime deadline;

    public GroupTaskReadModel(Task source) {
        description = source.getDescription();
        done = source.isDone();
        deadline = source.getDeadline();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }
}
