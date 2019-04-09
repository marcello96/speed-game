package pl.edu.agh.ki.speedgame.exceptions;

public class CannotRemoveGameException extends InFunException {
    public CannotRemoveGameException() {
        super();
    }

    public CannotRemoveGameException(String message) {
        super(message);
    }

    public CannotRemoveGameException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotRemoveGameException(Throwable cause) {
        super(cause);
    }
}

