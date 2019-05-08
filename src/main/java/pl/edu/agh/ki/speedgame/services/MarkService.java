package pl.edu.agh.ki.speedgame.services;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import pl.edu.agh.ki.speedgame.model.dao.Mark;
import pl.edu.agh.ki.speedgame.repository.MarkRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

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

    public List<Mark> getAllMarks() {
        return StreamSupport.stream(markRepository.findAll().spliterator(), false)
                .filter(mark -> mark.getAmountOfMarks() > 0)
                .collect(toList());
    }

    public String getRandomGame(List<String> games) {
        AtomicReference<Double> accumulatedWeight = new AtomicReference<>(0.0);

        List<Pair<String, Double>> gameProbabilityPairs = markRepository.getMarksByNameIn(games)
                .stream()
                .map(mark -> new ImmutablePair<>(mark.getName(), accumulatedWeight.accumulateAndGet(calculateProbability(mark), Double::sum)))
                .collect(toList());

        double randomWeight = new Random().nextDouble() * accumulatedWeight.get();

        return gameProbabilityPairs.stream()
                .filter(pair -> pair.getValue() >= randomWeight)
                .findFirst()
                .map(Pair::getKey)
                .get();
    }

    private static double calculateProbability(Mark mark) {
        return (3*5 + mark.getAmountOfMarks() * mark.getAverageMark()) / (3 + mark.getAmountOfMarks());
    }
}
