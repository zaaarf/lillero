package ftbsc.lll.exceptions;

/**
 * Thrown when attempting to build an {@link ftbsc.lll.tools.InsnSequence} between two
 * unconnected nodes.
 */
public class InstructionMismatchException extends RuntimeException {
	public InstructionMismatchException(String message) {
		super(message);
	}

	public InstructionMismatchException(String message, Throwable cause) {
		super(message, cause);
	}

	public InstructionMismatchException(Throwable cause) {
		super(cause);
	}
}
