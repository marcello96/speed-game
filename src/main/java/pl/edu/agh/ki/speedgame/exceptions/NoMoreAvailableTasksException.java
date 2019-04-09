package pl.edu.agh.ki.speedgame.exceptions;

public class NoMoreAvailableTasksException extends InFunException {
    public NoMoreAvailableTasksException() {
        super();
    }

    public NoMoreAvailableTasksException(String message) {
        super(message);
    }

    public NoMoreAvailableTasksException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoMoreAvailableTasksException(Throwable cause) {
        super(cause);
    }
}
