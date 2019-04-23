package pl.edu.agh.ki.speedgame.model.requests;

import org.springframework.stereotype.Service;
import pl.edu.agh.ki.speedgame.exceptions.NoGameSelectedException;
import pl.edu.agh.ki.speedgame.services.GameService;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
public class ManagementService {
    private final GameService gameService;

    public ManagementService(GameService gameService) {
        this.gameService = gameService;
    }

    public List<String> extractUserChoice(CreateGameInputConfig createGameInputConfig) throws NoGameSelectedException {
        if (createGameInputConfig.getTasksConfig() == null || createGameInputConfig.getTasksConfig().isEmpty()) {
            throw new NoGameSelectedException("Nie wybrano żadnej gry");
        }
            return createGameInputConfig.getTasksConfig();
    }

    public String extractGroupId(CreateGameInputConfig createGameInputConfig) {
        String groupId = createGameInputConfig.getGroupId();
        if (isBlank(groupId)) {
            return gameService.generateRandomGroupId();
        }
        return groupId;
    }
}
