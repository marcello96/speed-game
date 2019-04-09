package pl.edu.agh.ki.speedgame.model.requests;

import lombok.Data;

@Data
public class Task {
    public TaskConfig taskConfig;

    public boolean checked;

    public Task(TaskConfig taskConfig) {
        this.taskConfig = taskConfig;
    }

    public Task(TaskConfig taskConfig, boolean checked) {
        this.taskConfig = taskConfig;
        this.checked = checked;
    }
}