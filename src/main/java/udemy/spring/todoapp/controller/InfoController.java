package udemy.spring.todoapp.controller;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import udemy.spring.todoapp.TaskConfigurationProperties;

import javax.annotation.security.RolesAllowed;


@RestController
@RequestMapping("/info")
public class InfoController {
    private DataSourceProperties dataSource;
    private TaskConfigurationProperties myProp;

    public InfoController(final DataSourceProperties dataSource, final TaskConfigurationProperties myProp) {
        this.dataSource = dataSource;
        this.myProp = myProp;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/url")
    String url() {
        return dataSource.getUrl();
    }

    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    @GetMapping("/prop")
    boolean myProp() {
        return myProp.getTemplate().isAllowMultipleTasks();
    }
}
