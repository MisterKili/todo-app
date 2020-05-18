package udemy.spring.todoapp.adapter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import udemy.spring.todoapp.model.Task;
import udemy.spring.todoapp.model.TaskRepository;

import java.util.List;

@Repository
interface SqlTaskRepository extends TaskRepository, JpaRepository<Task, Integer> {

    @Override
    @Query(nativeQuery = true, value = "select count(*) > 0 from tasks where id=:id")
    boolean existsById(@Param("id") Integer id);

    @Override
    boolean existsByDoneIsFalseAndGroup_Id(Integer groupId);

    @Override
    List<Task> findAllByGroup_Id(Integer id);

    @Override
    @Query(nativeQuery = true, value = "select * from tasks where DONE IS FALSE AND DEADLINE IS NULL OR CAST(DEADLINE as DATE) <= TODAY()")
    List<Task> findAllForToday();
}
