package pl.edu.agh.ki.speedgame.exceptions;

public class GameWithSuchIdExistException extends InFunException {
    public GameWithSuchIdExistException() {
        super();
    }

    public GameWithSuchIdExistException(String message) {
        super(message);
    }

    public GameWithSuchIdExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public GameWithSuchIdExistException(Throwable cause) {
        super(cause);
    }
}
