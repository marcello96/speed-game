package pl.edu.agh.ki.speedgame.model.requests;

import lombok.Data;

@Data
public class RequestTask {
    public boolean checked;
    private TaskConfig taskConfig;

    public RequestTask(TaskConfig taskConfig) {
        this.taskConfig = taskConfig;
    }

    public RequestTask(TaskConfig taskConfig, boolean checked) {
        this.taskConfig = taskConfig;
        this.checked = checked;
    }
}
