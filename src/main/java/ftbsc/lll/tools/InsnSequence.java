package ftbsc.lll.tools;

import ftbsc.lll.exception.InstructionMismatchException;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

import java.util.Objects;

/**
 * Represents a sequence of instructions contained within two given nodes.
 * Extends InsnList, but provides additional flexibility.
 */
public class InsnSequence extends InsnList implements Opcodes {
	/**
	 * Public constructor.
	 * Must be given two non-null, connected nodes.
	 * @param startNode the starting node of the pattern
	 * @param endNode the first node of the pattern
	 */
	public InsnSequence(AbstractInsnNode startNode, AbstractInsnNode endNode) {
		Objects.requireNonNull(startNode);
		Objects.requireNonNull(endNode);
		for(; startNode != endNode && startNode != null; startNode = startNode.getNext())
			super.add(startNode);
		if (startNode == null)
			throw new InstructionMismatchException("Nodes" + getFirst() + " and " + getLast() + " are not connected.");
	}

	//TODO replace
}
