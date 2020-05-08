package udemy.spring.todoapp.logic;

import org.springframework.stereotype.Service;
import udemy.spring.todoapp.TaskConfigurationProperties;
import udemy.spring.todoapp.model.*;
import udemy.spring.todoapp.model.projection.GroupReadModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

//@Service
public class ProjectService {

    private ProjectRepository projectRepository;
    private TaskGroupRepository taskGroupRepository;
    private TaskConfigurationProperties config;

    public ProjectService(ProjectRepository projectRepository, TaskGroupRepository taskGroupRepository,
                          TaskConfigurationProperties config) {
        this.projectRepository = projectRepository;
        this.taskGroupRepository = taskGroupRepository;
        this.config = config;
    }

    public List<Project> readAll() {
        var result = projectRepository.findAll();
        return result;
    }

    public Project create(Project entity) {
        var result = projectRepository.save(entity);
        return result;
    }

    public GroupReadModel createGroup(LocalDateTime deadline, int projectId) {
        if (!config.getTemplate().isAllowMultipleTasks() && taskGroupRepository.existsByDoneIsFalseAndProject_Id(projectId)) {
            throw new IllegalStateException("Only one undone group from project is allowed");
        }
        TaskGroup result = projectRepository.findById(projectId)
                .map(project -> {
                    var targetGroup = new TaskGroup();
                    targetGroup.setDescription(project.getDescription());
                    targetGroup.setTasks(project.getSteps().stream()
                            .map(projectStep -> new Task(
                                    projectStep.getDescription(),
                                    deadline.plusDays(projectStep.getDaysToDeadline()))
                            )
                            .collect(Collectors.toSet())
                            );
                    targetGroup.setProject(project);
                    return taskGroupRepository.save(targetGroup);
                }).orElseThrow(() -> new IllegalArgumentException("Project with given id not found"));
        return new GroupReadModel(result);
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
