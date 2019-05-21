package pl.edu.agh.ki.speedgame.model.requests;

import lombok.Data;

@Data
public class JoinGameInput {
    public String groupId = "123";
    public String nick;
    public int age = 20;
}
