package udemy.spring.todoapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import udemy.spring.todoapp.logic.TaskGroupService;
import udemy.spring.todoapp.model.Task;
import udemy.spring.todoapp.model.TaskGroupRepository;
import udemy.spring.todoapp.model.TaskRepository;
import udemy.spring.todoapp.model.projection.GroupReadModel;
import udemy.spring.todoapp.model.projection.GroupTaskWriteModel;
import udemy.spring.todoapp.model.projection.GroupWriteModel;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@Controller
@IllegalExceptionProcessng
@RequestMapping("/groups")
public class TaskGroupController {

    private static final Logger logger = LoggerFactory.getLogger(TaskGroupController.class);
    private TaskGroupService taskGroupService;
    private TaskRepository taskRepository;


    TaskGroupController(TaskGroupService taskGroupService,TaskRepository taskRepository) {
        this.taskGroupService = taskGroupService;
        this.taskRepository = taskRepository;
    }

    @ResponseBody
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<GroupReadModel> createGroup(@RequestBody @Valid GroupWriteModel toCreate) {
        GroupReadModel result = taskGroupService.createGroup(toCreate);
        return ResponseEntity.created(URI.create("/" + result.getId())).body(result);
    }

    @ResponseBody
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<GroupReadModel>> readAllGroups() {
        return ResponseEntity.ok(taskGroupService.readAll());
    }

    @ResponseBody
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<Task>> readAllTasksFromGroup(@PathVariable int id) {
        return ResponseEntity.ok(taskRepository.findAllByGroup_Id(id));
    }

    @ResponseBody
    @Transactional
    @PatchMapping(path = "/{id}")
    ResponseEntity<?> toggleGroup(@PathVariable int id) {
        taskGroupService.toggleGroup(id);
        return ResponseEntity.noContent().build();
    }

    // ============ FOR FRONTEND ============

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    String showGroups(Model model) {
        model.addAttribute("group", new GroupWriteModel());
        return "groups";
    }

    @PostMapping(produces = MediaType.TEXT_HTML_VALUE,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String addGroup(
            @ModelAttribute("group") @Valid GroupWriteModel toCreate,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "groups";
        }
        taskGroupService.createGroup(toCreate);
        model.addAttribute("group", new GroupWriteModel());
        model.addAttribute("groups", getTaskGroups());
        model.addAttribute("message", "Dodano grupę!");
        return "groups";
    }

    @PostMapping(params = "addTask",
            produces = MediaType.TEXT_HTML_VALUE)
    String addGroupTask(@ModelAttribute("group") GroupWriteModel current) {
        current.getTasks().add(new GroupTaskWriteModel());
        return "groups";
    }

    @ModelAttribute("groups")
    List<GroupReadModel> getTaskGroups() {
        return taskGroupService.readAll();
    }
}
