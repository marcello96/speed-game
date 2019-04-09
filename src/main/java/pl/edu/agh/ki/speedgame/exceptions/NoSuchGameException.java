package pl.edu.agh.ki.speedgame.exceptions;


public class NoSuchGameException extends InFunException {
    public NoSuchGameException() {
        super();
    }

    public NoSuchGameException(String message) {
        super(message);
    }

    public NoSuchGameException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchGameException(Throwable cause) {
        super(cause);
    }
}
