package pl.edu.agh.ki.speedgame.model.requests;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TaskConfig {

    public String name;
    public List<ParameterConfig> config;

    @Data
    @AllArgsConstructor
    public class ParameterConfig {
        private String value;
        private String name;
        private boolean required;
    }
}
