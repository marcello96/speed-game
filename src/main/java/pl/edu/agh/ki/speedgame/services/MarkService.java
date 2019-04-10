package pl.edu.agh.ki.speedgame.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.ki.speedgame.model.orm.Mark;
import pl.edu.agh.ki.speedgame.model.orm.Task;
import pl.edu.agh.ki.speedgame.repository.TaskRepository;

@Service
public class MarkService {
    @Autowired
    private TaskRepository taskRepository;

    public void addMark(String taskName, int mark) {
        Task prevTask = taskRepository.getTaskByName(taskName);
        prevTask.getMarks().add(new Mark(mark));
        taskRepository.save(prevTask);
    }
}
