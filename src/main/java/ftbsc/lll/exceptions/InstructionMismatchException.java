package ftbsc.lll.exceptions;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

/**
 * Thrown when attempting to build an {@link InsnList} between two unconnected nodes.
 */
public class InstructionMismatchException extends RuntimeException {
	/**
	 * Constructs a new instruction mismatch exception with the specified detail message.
	 * @param start the first node
	 * @param end the second node
	 */
	public InstructionMismatchException(AbstractInsnNode start, AbstractInsnNode end) {
		super(String.format("Nodes %s and %s are not connected.", start, end));
	}
}
