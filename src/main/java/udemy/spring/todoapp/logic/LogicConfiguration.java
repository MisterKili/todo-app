package udemy.spring.todoapp.logic;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import udemy.spring.todoapp.TaskConfigurationProperties;
import udemy.spring.todoapp.model.ProjectRepository;
import udemy.spring.todoapp.model.TaskGroupRepository;
import udemy.spring.todoapp.model.TaskRepository;

@Configuration
public class LogicConfiguration {
    @Bean
    ProjectService projectService(
            ProjectRepository repository,
            TaskGroupRepository taskGroupRepository,
            TaskGroupService taskGroupService,
            TaskConfigurationProperties config) {
        return new ProjectService(repository, taskGroupRepository, taskGroupService, config);
    }

    @Bean
    TaskGroupService taskGroupService(TaskGroupRepository taskGroupRepository, TaskRepository taskRepository) {
        return new TaskGroupService(taskGroupRepository, taskRepository);
    }
}
