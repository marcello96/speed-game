package pl.edu.agh.ki.speedgame.services;

import org.springframework.stereotype.Service;
import pl.edu.agh.ki.speedgame.exceptions.NoMoreAvailableTasksException;
import pl.edu.agh.ki.speedgame.model.User;

@Service
public class UserService {

    private final MarkService markService;

    public UserService(MarkService markService) {
        this.markService = markService;
    }

    public String getTaskNameIfAvailable(User user) throws NoMoreAvailableTasksException {
        if (user.getLeftTasks() > 0) {
            user.decrementLeftTasks();
            String currentTask = markService.getRandomGame(user.getAvailableTasks());
            user.setCurrentTask(currentTask);
            return currentTask;
        } else {
            throw new NoMoreAvailableTasksException("Nie ma już więcej dostępnych zadań");
        }
    }
}
