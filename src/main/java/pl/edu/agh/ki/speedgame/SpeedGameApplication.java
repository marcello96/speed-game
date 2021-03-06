package pl.edu.agh.ki.speedgame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("pl.edu.agh.ki.speedgame.repository")
@EntityScan("pl.edu.agh.ki.speedgame.model.dao")
public class SpeedGameApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpeedGameApplication.class, args);
    }
}
