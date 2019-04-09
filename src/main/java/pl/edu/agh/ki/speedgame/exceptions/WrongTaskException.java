package pl.edu.agh.ki.speedgame.exceptions;

public class WrongTaskException extends InFunException {
    public WrongTaskException() {
        super();
    }

    public WrongTaskException(String message) {
        super(message);
    }

    public WrongTaskException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongTaskException(Throwable cause) {
        super(cause);
    }
}
