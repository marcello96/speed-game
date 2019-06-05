package pl.edu.agh.ki.speedgame.model.requests;

import lombok.Data;
import pl.edu.agh.ki.speedgame.model.Game;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreateGameInputConfig {
    public String groupId = Game.GAME_ID;
    public List<String> tasksConfig = new ArrayList<>();
}
