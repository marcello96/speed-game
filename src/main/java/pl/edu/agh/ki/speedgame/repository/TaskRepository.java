package pl.edu.agh.ki.speedgame.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pl.edu.agh.ki.speedgame.model.orm.Task;

public interface TaskRepository extends CrudRepository<Task, Long> {
    @Query(value = "SELECT AVG(m.score) FROM Task t JOIN t.marks m WHERE t.name=?1", nativeQuery = true)
    double getAverageMark(String taskName);

    Task getTaskByName(String name);
}
