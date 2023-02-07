package ftbsc.lll.exception;

/**
 * Thrown when the injection of a patch fails.
 */
public class InjectionException extends RuntimeException {
	public InjectionException(String message) {
		super(message);
	}

	public InjectionException(String message, Throwable cause) {
		super(message, cause);
	}

	public InjectionException(Throwable cause) {
		super(cause);
	}
}