package udemy.spring.todoapp.model.event;

import udemy.spring.todoapp.model.Task;

import java.time.Clock;
import java.time.Instant;

public abstract class TaskEvent {

    public static TaskEvent changed(Task source) {
        return source.isDone() ? new TaskDone(source) : new TaskUndone(source);
    }

    private int taskId;
    private Instant occurrence;

    TaskEvent(int taskId, Clock clock) {
        this.taskId = taskId;
        this.occurrence = Instant.now(clock);
    }

    public int getTaskId() {
        return taskId;
    }

    public Instant getOccurrence() {
        return occurrence;
    }

    @Override
    public String toString() {
        return "TaskEvent{" +
                "taskId=" + taskId +
                ", occurrence=" + occurrence +
                '}';
    }
}
