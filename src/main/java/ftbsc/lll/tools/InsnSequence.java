package ftbsc.lll.tools;

import ftbsc.lll.exceptions.InstructionMismatchException;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

import java.util.Objects;

/**
 * Represents a sequence of instructions contained within two given nodes.
 * Extends InsnList, but provides additional flexibility and features.
 */
public class InsnSequence extends InsnList {
	/**
	 * Public constructor.
	 * This creates an empty sequence.
	 */
	public InsnSequence() {
		super();
	}

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

	/**
	 * Extends the existing get function from InsnList to allow for negative indexes.
	 * @param index the index of the instruction that must be returned
	 * @return the instruction whose index is given
	 */
	@Override
	public AbstractInsnNode get(int index) {
		if(index >= 0)
			return super.get(index);
		index = Math.abs(index);
		if(index > size())
			throw new IndexOutOfBoundsException();
		return this.toArray()[size() - index];
	}

	/**
	 * Adds an array of nodes to the list.
	 * @param nodes the nodes to add
	 */
	public void add(AbstractInsnNode... nodes) {
		for(AbstractInsnNode node : nodes)
			this.add(node);
	}

	/**
	 * Wraps InsnList's add() to ignore null values.
	 * @param node to add
	 */
	@Override
	public void add(AbstractInsnNode node) {
		if(node != null)
			super.add(node);
	}

	/**
	 * Replaces a node with another one. Mostly used internally.
	 * @param oldNode node to replace
	 * @param newNode new node
	 */
	public void replaceNode(AbstractInsnNode oldNode, AbstractInsnNode newNode) {
		super.insert(oldNode, newNode);
		super.remove(oldNode);
	}

	/**
	 * Replaces n occurrences of said opcode with the given node.
	 * @param opcode the opcode to replace
	 * @param newNode the replacement node
	 * @param amount how many occurrences to replace, set to 0 to replace all
	 * @return true if anything was changed, false otherwise
	 */
	public boolean replace(int opcode, AbstractInsnNode newNode, int amount) {
		return replace(opcode, newNode, amount, false);
	}

	/**
	 * Replaces n occurrences of said opcode with the given node.
	 * @param opcode the opcode to replace
	 * @param newNode the replacement node
	 * @param reverse whether the search should be done from the end
	 * @param amount how many occurrences to replace, set to 0 to replace all
	 * @return true if anything was changed, false otherwise
	 */
	public boolean replace(int opcode, AbstractInsnNode newNode, int amount, boolean reverse) {
		boolean changed = false;
		for(AbstractInsnNode cur = this.getFirst();
		    cur != null && cur.getPrevious() != this.getLast() && cur.getNext() != this.getFirst();
			cur = reverse ? cur.getPrevious() : cur.getNext()) {
			if(cur.getOpcode() == opcode) {
				this.replaceNode(cur, newNode);
				changed = true;
				amount--; // will go to negative if it was already 0, causing it to go on until for loop finishes
				if(amount == 0)
					return changed;
			}
		}
		return changed;
	}

	/**
	 * Cut a number of nodes from the list.
	 * @param amount how many nodes to cut
	 * @param reverse true if should cut from the end, false otherwise
	 */
	public void cut(int amount, boolean reverse) {
		for(int i = 0; i < amount; i++)
			this.remove(reverse ? getLast() : getFirst());
	}
}
