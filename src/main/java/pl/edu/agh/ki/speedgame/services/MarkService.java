package pl.edu.agh.ki.speedgame.services;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;
import pl.edu.agh.ki.speedgame.model.dao.Mark;
import pl.edu.agh.ki.speedgame.repository.MarkRepository;

import java.util.Comparator;
import java.util.List;

@Service
public class MarkService {

    private final MarkRepository markRepository;

    public MarkService(MarkRepository markRepository) {
        this.markRepository = markRepository;
    }

    public void addMark(String taskName, int mark) {
        Mark taskMark = markRepository.getMarkByName(taskName);
        int amountOfMarks = taskMark.getAmountOfMarks();
        taskMark.setAverageMark((taskMark.getAverageMark() * amountOfMarks +  mark) / (amountOfMarks + 1));
        taskMark.setAmountOfMarks(amountOfMarks + 1);
        markRepository.save(taskMark);
    }

    public String getRandomGame(List<String> games) {
        return markRepository.getMarksByNameIn(games)
                .stream()
                .map(mark -> new ImmutablePair<>(mark.getName(), calculateProbability(mark)))
                .max(Comparator.comparing(ImmutablePair::getValue))
                .get()
                .getKey();
    }

    private static double calculateProbability(Mark mark) {
        return (3*5 + mark.getAmountOfMarks() * mark.getAverageMark()) / (3 + mark.getAmountOfMarks());
    }
}
