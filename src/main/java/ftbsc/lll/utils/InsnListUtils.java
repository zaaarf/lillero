package ftbsc.lll.utils;

import ftbsc.lll.exceptions.InstructionMismatchException;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

/**
 * A collection of utilities for manipulating {@link InsnList}s.
 */
public class InsnListUtils {

	/**
	 * Factory method that builds an {@link InsnList} with the given nodes.
	 * @param nodes the nodes in question
	 * @return the resulting {@link InsnList}
	 */
	public static InsnList of(AbstractInsnNode... nodes) {
		InsnList list = new InsnList();
		for(AbstractInsnNode node : nodes) {
		list.add(node);
		}
		return list;
	}

	/**
	 * Creates a sublist with all the nodes between the given extremes.
	 * @param startNode the starting node of the pattern, must be non-null
	 * @param endNode the first node of the pattern, must be non-null
	 * @return the resulting {@link InsnList}
	 */
	public static InsnList between(AbstractInsnNode startNode, AbstractInsnNode endNode) {
		InsnList list = new InsnList();
		for(; startNode != null; startNode = startNode.getNext()) {
			list.add(startNode);
			if(startNode == endNode) {
				return list;
			}
		}

		throw new InstructionMismatchException(list.getFirst(), list.getLast());
	}

	/**
	 * Replaces a node with another one. Mostly used internally.
	 * @param list the list to perform the operation on
	 * @param oldNode node to replace
	 * @param newNode new node
	 */
	public static void replaceNode(InsnList list, AbstractInsnNode oldNode, AbstractInsnNode newNode) {
		list.insert(oldNode, newNode);
		list.remove(oldNode);
	}

	/**
	 * Replaces n occurrences of said opcode with the given node.
	 * @param list the list to perform the operation on
	 * @param opcode the opcode to replace
	 * @param newNode the replacement node
	 * @param reverse whether the search should be done from the end
	 * @param amount how many occurrences to replace, set to 0 to replace all
	 * @return true if anything was changed, false otherwise
	 */
	public static boolean replace(InsnList list, int opcode, AbstractInsnNode newNode, int amount, boolean reverse) {
		boolean changed = false;
		for(
			AbstractInsnNode cur = list.getFirst();
			cur != null && cur.getPrevious() != list.getLast() && cur.getNext() != list.getFirst();
			cur = reverse ? cur.getPrevious() : cur.getNext()
		) {
			if(cur.getOpcode() == opcode) {
				replaceNode(list, cur, newNode);
				changed = true;
				amount--; // will go to negative if it was already 0, causing it to go on until for loop finishes
				if(amount == 0) {
					return changed;
				}
			}
		}
		return changed;
	}

	/**
	 * Cut a number of nodes from the list.
	 * @param list the list to perform the operation on
	 * @param amount how many nodes to cut
	 * @param reverse true if it should cut from the end, false otherwise
	 */
	public static void cut(InsnList list, int amount, boolean reverse) {
		for(int i = 0; i < amount; i++) {
			list.remove(reverse ? list.getLast() : list.getFirst());
		}
	}
}
