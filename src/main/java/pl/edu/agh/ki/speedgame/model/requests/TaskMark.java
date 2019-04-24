package pl.edu.agh.ki.speedgame.model.requests;

import lombok.Data;

@Data
public class TaskMark {
    private String taskName;
    private int mark;

    public TaskMark(String taskName) {
        this.taskName = taskName;
    }
}
