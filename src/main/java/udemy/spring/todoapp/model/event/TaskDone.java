package udemy.spring.todoapp.model.event;

import udemy.spring.todoapp.model.Task;

import java.time.Clock;

public class TaskDone extends TaskEvent {

    TaskDone(final Task source) {
        super(source.getId(), Clock.systemDefaultZone());
    }
}
