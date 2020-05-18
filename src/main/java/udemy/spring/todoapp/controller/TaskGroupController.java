package udemy.spring.todoapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import udemy.spring.todoapp.logic.TaskGroupService;
import udemy.spring.todoapp.model.Task;
import udemy.spring.todoapp.model.TaskGroup;
import udemy.spring.todoapp.model.TaskGroupRepository;
import udemy.spring.todoapp.model.TaskRepository;
import udemy.spring.todoapp.model.projection.GroupReadModel;
import udemy.spring.todoapp.model.projection.GroupWriteModel;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/groups")
public class TaskGroupController {
    private static final Logger logger = LoggerFactory.getLogger(TaskGroupController.class);
    private TaskGroupService taskGroupService;
    private TaskGroupRepository taskGroupRepository;
    private TaskRepository taskRepository;


    TaskGroupController(TaskGroupService taskGroupService, TaskGroupRepository taskGroupRepository, TaskRepository taskRepository) {
        this.taskGroupService = taskGroupService;
        this.taskGroupRepository = taskGroupRepository;
        this.taskRepository = taskRepository;
    }

    @PostMapping
    ResponseEntity<GroupReadModel> createGroup(@RequestBody @Valid GroupWriteModel toCreate) {
        GroupReadModel result = taskGroupService.createGroup(toCreate);
        return ResponseEntity.created(URI.create("/" + result.getId())).body(result);
    }

    @GetMapping
    ResponseEntity<List<GroupReadModel>> readAllGroups() {
        return ResponseEntity.ok(taskGroupService.readAll());
    }


    @Transactional
    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> toggleGroup(@PathVariable int id) {
        taskGroupService.toggleGroup(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(IllegalStateException.class)
    ResponseEntity<String> handleIllegalState(IllegalStateException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
