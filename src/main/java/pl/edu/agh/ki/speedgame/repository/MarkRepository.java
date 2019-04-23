package pl.edu.agh.ki.speedgame.repository;

import org.springframework.data.repository.CrudRepository;
import pl.edu.agh.ki.speedgame.model.dao.Mark;

public interface MarkRepository extends CrudRepository<Mark, Long> {

    Mark getMarkByName(String name);
}
