package ftbsc.lll.exceptions;

/**
 * Thrown when failing to find a pattern.
 */
public class PatternNotFoundException extends RuntimeException {
	/**
	 * Constructs a new pattern not found exception with the given message.
	 * @param message the given message
	 */
	public PatternNotFoundException(String message) {
		super(message);
	}
}
