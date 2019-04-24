package pl.edu.agh.ki.speedgame.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.edu.agh.ki.speedgame.exceptions.InFunException;

@Slf4j
@ControllerAdvice
public class ExceptionInfunHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InFunException.class)
    public final String handleInFunException(InFunException ex, Model model) {
        log.error("Handle error", ex);
        model.addAttribute("error", ex.getMessage());
        return "errorView";
    }

    @ExceptionHandler(Exception.class)
    public final String handleException(Exception ex, Model model) {
        log.error("Handle error", ex);
        model.addAttribute("error", "Błąd ogólny");
        return "errorView";
    }

}
