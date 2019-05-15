package pl.edu.agh.ki.speedgame.model.requests;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
public class TaskMark {
    private String taskName;
    @Min(value = 1, message = "Oceń grę wybierając od 1 do 5 gwiazek")
    @Max(value = 5, message = "Oceń grę wybierając od 1 do 5 gwiazek")
    private int mark;

    public TaskMark(String taskName) {
        this.taskName = taskName;
    }
}
