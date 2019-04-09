package pl.edu.agh.ki.speedgame.exceptions;

public class InFunException extends Exception {
    public InFunException() {
        super();
    }

    public InFunException(String message) {
        super(message);
    }

    public InFunException(String message, Throwable cause) {
        super(message, cause);
    }

    public InFunException(Throwable cause) {
        super(cause);
    }
}
