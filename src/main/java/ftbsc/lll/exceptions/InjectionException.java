package ftbsc.lll.exceptions;

/**
 * Thrown when the injection of a patch fails.
 */
public class InjectionException extends RuntimeException {

	/**
	 * Constructs a new injection exception with the specified detail message.
	 * @param message the detail message
	 */
	public InjectionException(String message) {
		super(message);
	}

	/**
	 * Constructs a new injection exception with the specified detail message and cause.
	 * @param  message the detail message
	 * @param  cause the cause, may be null (indicating nonexistent or unknown cause)
	 */
	public InjectionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new injection exception with the specified cause and a
	 * detail message of {@code (cause==null ? null : cause.toString())}
	 * @param  cause the cause, may be null (indicating nonexistent or unknown cause)
	 */
	public InjectionException(Throwable cause) {
		super(cause);
	}
}
