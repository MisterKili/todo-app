package udemy.spring.todoapp.model.event;

import udemy.spring.todoapp.model.Task;

import java.time.Clock;

public class TaskUndone extends TaskEvent {

    TaskUndone(final Task source) {
        super(source.getId(), Clock.systemDefaultZone());
    }
}
