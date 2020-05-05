package udemy.spring.todoapp.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import udemy.spring.todoapp.model.TaskGroup;
import udemy.spring.todoapp.model.TaskGroupRepository;
import udemy.spring.todoapp.model.TaskRepository;

import java.util.*;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TaskGroupServiceTest {

    @Test
    @DisplayName("should throw IllegalStateException when group has undone tasks")
    void toggleGroup_groupHasUndoneTasks_throwsIllegalStateException() {
        // given
        var mockTaskRepository = mock(TaskRepository.class);
        when(mockTaskRepository.existsByDoneIsFalseAndGroup_Id(anyInt()))
                .thenReturn(true);

        // system under test
        var toTest = new TaskGroupService(null, mockTaskRepository);

        // when
        var exception = catchThrowable(() -> toTest.toggleGroup(0));

        // then
        assertThat(exception)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("undone tasks");
    }

    @Test
    @DisplayName("should throw IllegalArgumentException when no groups for a given id")
    void toggleGroup_noGroups_throwsIllegalArgumentException() {
        // given
        var mockTaskRepository = mock(TaskRepository.class);
        when(mockTaskRepository.existsByDoneIsFalseAndGroup_Id(anyInt()))
                .thenReturn(false);
        // and
        var mockTaskGroupRepository = mock(TaskGroupRepository.class);
        when(mockTaskGroupRepository.findById(anyInt()))
                .thenReturn(Optional.empty());
        // system under test
        var toTest = new TaskGroupService(mockTaskGroupRepository, mockTaskRepository);

        // when
        var exception = catchThrowable(() -> toTest.toggleGroup(0));

        // then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("should toggle group")
    void toggleGroup_noUndoneTasks_existingGroup() {
        // given
        TaskGroup group = new TaskGroup();
        // and
        var mockTaskRepository = mock(TaskRepository.class);
        when(mockTaskRepository.existsByDoneIsFalseAndGroup_Id(anyInt()))
                .thenReturn(false);
        // and
        var mockTaskGroupRepository = mock(TaskGroupRepository.class);
        when(mockTaskGroupRepository.findById(anyInt()))
                .thenReturn(Optional.of(group));
        // and
        boolean isDoneBefore = group.isDone();

        // system under test
        var toTest = new TaskGroupService(mockTaskGroupRepository, mockTaskRepository);

        // when
        toTest.toggleGroup(0);

        // then
        boolean isDoneAfter = group.isDone();

//        assertThat(isDoneBefore == !isDoneAfter);
        assertThat(isDoneAfter).isEqualTo(!isDoneBefore);
    }
}