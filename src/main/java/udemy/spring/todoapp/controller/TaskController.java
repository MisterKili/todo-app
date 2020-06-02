package udemy.spring.todoapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import udemy.spring.todoapp.logic.TaskService;
import udemy.spring.todoapp.model.Task;
import udemy.spring.todoapp.model.TaskRepository;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/tasks")
class TaskController {
    public static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private final ApplicationEventPublisher eventPublisher;
    private final TaskRepository repository;
    private final TaskService service;

    TaskController(ApplicationEventPublisher publisher, final TaskRepository repository, TaskService service){
        this.eventPublisher = publisher;
        this.repository = repository;
        this.service = service;
    }

    @GetMapping(params = {"!sort", "!page", "!size"})
    CompletableFuture<ResponseEntity<List<Task>>> readAllTasks(){
        logger.warn("Exposing all the tasks");
        return service.findAllAsync().thenApply(ResponseEntity::ok);
//        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping(path = "/{id}")
    ResponseEntity<Task> readTask(@PathVariable int id) {
        return repository.findById(id)
                .map(task -> ResponseEntity.ok(task))
                .orElse(ResponseEntity.notFound().build());
    }

//    @GetMapping("/test")
//    void oldFashionedWay(HttpServletRequest req, HttpServletResponse res) throws IOException {
//        req.getParameter("foo");
//        res.getWriter().println("test old-fashioned way");
//    }

    @GetMapping
    ResponseEntity<List<Task>> readAllTasks(Pageable page){
        logger.info("Custom pageable");
        return ResponseEntity.ok(repository.findAll(page).getContent());
    }
//
//    @GetMapping(path = "/search/done", produces = MediaType.APPLICATION_JSON_VALUE)
//    String foo(){return "";}
//
//    @GetMapping(path = "/search/done", produces = MediaType.TEXT_XML_VALUE)
//    String bar(){return "";}

    @GetMapping("/search/done")
    ResponseEntity<List<Task>> readDoneTasks(@RequestParam(defaultValue = "true") boolean state) {
        return ResponseEntity.ok(
                repository.findByDone(state)
        );
    }

    @GetMapping("/today")
    ResponseEntity<List<Task>> readTasksForToday() {
        return ResponseEntity.ok(repository.findAllForToday());
    }


    @PutMapping(path = "/{id}")
    ResponseEntity<?> updateTask(@PathVariable int id, @RequestBody @Valid Task toUpdate) {
        if(!repository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        repository.findById(id)
                .ifPresent(task -> {
                    task.updateFrom(toUpdate);
                    repository.save(task);
                });
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    ResponseEntity<Task> createTask(@RequestBody @Valid Task toCreate) {
        Task result = repository.save(toCreate);
//        URI location = new URI("localhost:8080/tasks/" + result.getId());
        return ResponseEntity.created(URI.create("/" + result.getId())).body(result);
    }

    @Transactional
    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> toggleTask(@PathVariable int id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.findById(id)
                .map(Task::toggle)
                .ifPresent(eventPublisher::publishEvent);
        return ResponseEntity.noContent().build();
    }

//    public void foobar() {
//        this.toggleTask(1);     // nie zadziala bo normalnie to Spring woła funkcje przez proxy - dlatego trzeba by dac np @Transactional
//    }
}
