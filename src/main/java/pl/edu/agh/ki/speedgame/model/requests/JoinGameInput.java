package pl.edu.agh.ki.speedgame.model.requests;

import lombok.Data;
import pl.edu.agh.ki.speedgame.model.Game;

@Data
public class JoinGameInput {
    public String groupId = Game.GAME_ID;
    public String nick;
    public int age = 20;
}
