package pl.edu.agh.ki.speedgame.services;

import org.springframework.stereotype.Service;
import pl.edu.agh.ki.speedgame.model.User;

@Service
public class UserService {

    private final MarkService markService;

    public UserService(MarkService markService) {
        this.markService = markService;
    }

    public String getTaskNameIfAvailable(User user) {
        String currentTask = markService.getRandomGame(user.getGame().getTasks());
        user.setCurrentTask(currentTask);
        return currentTask;
    }
}
