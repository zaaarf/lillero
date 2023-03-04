package ftbsc.lll.exceptions;

/**
 * Thrown when failing to find a pattern.
 */
public class PatternNotFoundException extends RuntimeException {
	/**
	 * Constructs a new pattern not found exception with the specified detail message.
	 * @param message the detail message
	 */
	public PatternNotFoundException(String message) {
		super(message);
	}

	/**
	 * Constructs a new pattern not found exception with the specified detail message and cause.
	 * @param  message the detail message
	 * @param  cause the cause, may be null (indicating nonexistent or unknown cause)
	 */
	public PatternNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new pattern not found exception with the specified cause and a
	 * detail message of {@code (cause==null ? null : cause.toString())}
	 * @param  cause the cause, may be null (indicating nonexistent or unknown cause)
	 */
	public PatternNotFoundException(Throwable cause) {
		super(cause);
	}
}
