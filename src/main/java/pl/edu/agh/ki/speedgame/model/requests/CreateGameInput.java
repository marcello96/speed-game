package pl.edu.agh.ki.speedgame.model.requests;

import lombok.Data;
import pl.edu.agh.ki.speedgame.model.Game;

import java.util.List;

@Data
public class CreateGameInput {
    public String groupId = Game.GAME_ID;
    public List<TaskConfig> tasksConfig;

    public CreateGameInput(List<TaskConfig> tasks) {
        this.tasksConfig = tasks;
    }
}
