package pl.edu.agh.ki.speedgame.services;

import org.springframework.stereotype.Service;
import pl.edu.agh.ki.speedgame.model.dao.MarkDao;
import pl.edu.agh.ki.speedgame.model.dao.TaskDao;
import pl.edu.agh.ki.speedgame.repository.TaskRepository;

@Service
public class MarkService {

    private final TaskRepository taskRepository;

    public MarkService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void addMark(String taskName, int mark) {
        TaskDao prevTask = taskRepository.getTaskByName("tetris");
        MarkDao newMark = new MarkDao(prevTask, mark);
        prevTask.getMarks().add(newMark);
        taskRepository.save(prevTask);
    }
}
