package ftbsc.lll.exceptions;

/**
 * Thrown when failing to find a pattern.
 */
public class PatternNotFoundException extends RuntimeException {
	public PatternNotFoundException(String message) {
		super(message);
	}

	public PatternNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public PatternNotFoundException(Throwable cause) {
		super(cause);
	}
}
