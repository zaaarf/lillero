package ftbsc.lll.exceptions;

import ftbsc.lll.utils.InsnSequence;

/**
 * Thrown when attempting to build an {@link InsnSequence} between two
 * unconnected nodes.
 */
public class InstructionMismatchException extends RuntimeException {
	/**
	 * Constructs a new instruction mismatch exception with the specified detail message.
	 * @param message the detail message
	 */
	public InstructionMismatchException(String message) {
		super(message);
	}

	/**
	 * Constructs a new instruction mismatch exception with the specified detail message and cause.
	 * @param  message the detail message
	 * @param  cause the cause, may be null (indicating nonexistent or unknown cause)
	 */
	public InstructionMismatchException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new instruction mismatch exception with the specified cause and a
	 * detail message of {@code (cause==null ? null : cause.toString())}
	 * @param  cause the cause, may be null (indicating nonexistent or unknown cause)
	 */
	public InstructionMismatchException(Throwable cause) {
		super(cause);
	}
}
