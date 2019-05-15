package pl.edu.agh.ki.speedgame.model.requests;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreateGameInputConfig {
    public String groupId = "123";
    public int taskNumber = 10;
    public List<String> tasksConfig = new ArrayList<>();
}
