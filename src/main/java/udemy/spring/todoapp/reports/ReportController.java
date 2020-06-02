package udemy.spring.todoapp.reports;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import udemy.spring.todoapp.model.Task;
import udemy.spring.todoapp.model.TaskRepository;

import java.util.List;

@RestController
@RequestMapping("/reports")
class ReportController {

    private final TaskRepository taskRepository;
    private final PersistedTaskEventRepository eventRepository;

    ReportController(TaskRepository taskRepository, PersistedTaskEventRepository eventRepository) {
        this.taskRepository = taskRepository;
        this.eventRepository = eventRepository;
    }

    @GetMapping("/count/{id}")
    ResponseEntity<TaskWithChangesCount> readTaskWithCount(@PathVariable int id) {
        return taskRepository.findById(id)
                .map(task -> new TaskWithChangesCount(task, eventRepository.findByTaskId(id)))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/donebeforedeadline/{id}")
    ResponseEntity<TaskDoneBeforeDeadline> readTaskDoneBeforeDeadline(@PathVariable int id) {
        return taskRepository.findById(id)
                .map(task -> new TaskDoneBeforeDeadline(task, eventRepository.findByTaskId(id)))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private class TaskWithChangesCount {

        public String description;
        public boolean done;
        public int changesCount;

        TaskWithChangesCount(final Task task, final List<PersistedTaskEvent> events) {
            description = task.getDescription();
            done = task.isDone();
            changesCount = events.size();
        }
    }

    private class TaskDoneBeforeDeadline {

        public String description;
        public boolean done;
        public boolean doneBeforeDeadline;

        TaskDoneBeforeDeadline(final Task task, final List<PersistedTaskEvent> events) {
            description = task.getDescription();
            done = task.isDone();
            doneBeforeDeadline = checkIfTaskDoneBeforeDeadline(task, events);
        }

        private boolean checkIfTaskDoneBeforeDeadline(final Task task, final List<PersistedTaskEvent> events) {
            var lastEventDate = events.get(events.size() - 1).occurrence;

            if (task.getDeadline() == null || task.getDeadline().isBefore(lastEventDate)) {
                return true;
            }
            return false;
        }

    }
}
