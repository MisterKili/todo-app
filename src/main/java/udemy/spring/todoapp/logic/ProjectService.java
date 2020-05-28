package udemy.spring.todoapp.logic;

import udemy.spring.todoapp.TaskConfigurationProperties;
import udemy.spring.todoapp.model.*;
import udemy.spring.todoapp.model.projection.GroupReadModel;
import udemy.spring.todoapp.model.projection.GroupTaskWriteModel;
import udemy.spring.todoapp.model.projection.GroupWriteModel;
import udemy.spring.todoapp.model.projection.ProjectWriteModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

//@Service
public class ProjectService {

    private ProjectRepository projectRepository;
    private TaskGroupRepository taskGroupRepository;
    private TaskGroupService taskGroupService;
    private TaskConfigurationProperties config;

    public ProjectService(ProjectRepository projectRepository, TaskGroupRepository taskGroupRepository,
                          TaskGroupService taskGroupService, TaskConfigurationProperties config) {
        this.projectRepository = projectRepository;
        this.taskGroupRepository = taskGroupRepository;
        this.taskGroupService = taskGroupService;
        this.config = config;
    }

    public List<Project> readAll() {
        var result = projectRepository.findAll();
        return result;
    }

    public Project save(final ProjectWriteModel toSave) {
        var result = projectRepository.save(toSave.toProject());
        return result;
    }

    public GroupReadModel createGroup(LocalDateTime deadline, int projectId) {
        if (!config.getTemplate().isAllowMultipleTasks() && taskGroupRepository.existsByDoneIsFalseAndProject_Id(projectId)) {
            throw new IllegalStateException("Only one undone group from project is allowed");
        }
        return projectRepository.findById(projectId)
                .map(project -> {
                    var targetGroup = new GroupWriteModel();
                    targetGroup.setDescription(project.getDescription());
                    targetGroup.setTasks(
                            project.getSteps().stream()
                            .map(projectStep -> {
                                var task = new GroupTaskWriteModel();
                                task.setDescription(projectStep.getDescription());
                                task.setDeadline(deadline.plusDays(projectStep.getDaysToDeadline()));
                                return task;
                            }
                            )
                            .collect(Collectors.toList())
                    );
                    return taskGroupService.createGroup(targetGroup, project);
                }).orElseThrow(() -> new IllegalArgumentException("Project with given id not found"));
    }

//    public GroupReadModel createGroup(int projectId, LocalDateTime deadline) {
//        // pobrac project
//        Project project = projectRepository.findById(projectId)
//                .orElseThrow(() -> new IllegalArgumentException("Project with given id not found"));
//        // sprawdzic czy istnieja niezamkniete grupy
//        boolean existsUnclosedGroups = taskGroupRepository.existsByDoneIsFalseAndProject_Id(projectId);
//        // sprawidzic czy allowMultipleTask=true to luz, jesli nie to jesli istnieja niezrobione grupy wyjatek
//        boolean allowMultipleTasks = properties.getTemplate().isAllowMultipleTasks();
//        if (existsUnclosedGroups && !allowMultipleTasks) {
//            throw new IllegalStateException("Group has undone tasks and multiple tasks are not allowed. Do all the tasks first");
//        }
//        // przepisac opis z projektu do grupy, done=false, project to project
//        TaskGroup taskGroup = new TaskGroup();
//        taskGroup.setDescription(project.getDescription());
//        taskGroup.setProject(project);
//        // project steps -> tasks
//        for (var step: project.getSteps()) {
//            Task task = new Task();
//            // daystodeadline -> deadline
//            task.setDeadline(deadline.minusDays(step.getDaysToDeadline()));
//            taskGroup.addTask(task);
//        }
//        return new GroupReadModel(taskGroup);
//    }
}
