package pl.edu.agh.ki.speedgame.model.requests;

import lombok.Data;

import java.util.List;

@Data
public class CreateGameInput {
    public String groupId = "123";
    public int taskNumber = 10;
    public List<TaskConfig> tasksConfig;

    public CreateGameInput(List<TaskConfig> tasks) {
        this.tasksConfig = tasks;
    }
}
