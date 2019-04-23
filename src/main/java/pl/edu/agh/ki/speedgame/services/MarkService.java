package pl.edu.agh.ki.speedgame.services;

import org.springframework.stereotype.Service;
import pl.edu.agh.ki.speedgame.model.dao.Mark;
import pl.edu.agh.ki.speedgame.repository.MarkRepository;

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
}
