package ftbsc.lll.utils;

import ftbsc.lll.exceptions.InstructionMismatchException;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * An immutable view over a part of an {@link InsnList}.
 * Attempts to modify it will throw {@link UnsupportedOperationException}.
 */
public class InsnSubList extends InsnList {
	private final List<AbstractInsnNode> sublist = new ArrayList<>();

	/**
	 * Returns an immutable view of the {@link InsnList} between the given extremes.
	 * @param startNode the starting node of the pattern, must be non-null
	 * @param endNode the first node of the pattern, must be non-null
	 * @return the resulting {@link InsnList}
	 * @throws InstructionMismatchException if the given nodes are not linked or in the wrong order
	 */
	public static InsnSubList of(AbstractInsnNode startNode, AbstractInsnNode endNode) {
		InsnSubList list = new InsnSubList();
		for(; startNode != null; startNode = startNode.getNext()) {
			list.sublist.add(startNode);
			if(startNode == endNode) {
				return list;
			}
		}

		throw new InstructionMismatchException(list.getFirst(), list.getLast());
	}

	@Override
	public int size() {
		return this.sublist.size();
	}

	@Override
	public AbstractInsnNode getFirst() {
		return this.sublist.get(0);
	}

	@Override
	public AbstractInsnNode getLast() {
		return this.sublist.get(this.sublist.size() - 1);
	}

	@Override
	public AbstractInsnNode get(int index) {
		return this.sublist.get(index);
	}

	@Override
	public boolean contains(AbstractInsnNode insnNode) {
		return this.sublist.contains(insnNode);
	}

	@Override
	public int indexOf(AbstractInsnNode insnNode) {
		return this.sublist.indexOf(insnNode);
	}

	@Override
	public void accept(MethodVisitor methodVisitor) {
		AbstractInsnNode currentInsn = this.getFirst();
		AbstractInsnNode last = this.getLast();
		while(currentInsn != null) {
			currentInsn.accept(methodVisitor);
			currentInsn = currentInsn.getNext();
			if(last == currentInsn) {
				break;
			}
		}
	}

	@Override
	public ListIterator<AbstractInsnNode> iterator(int index) {
		return this.sublist.listIterator(index);
	}

	@Override
	public AbstractInsnNode[] toArray() {
		return this.sublist.toArray(new AbstractInsnNode[0]);
	}

	@Override
	public void set(AbstractInsnNode oldInsnNode, AbstractInsnNode newInsnNode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(AbstractInsnNode insnNode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(InsnList insnList) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void insert(InsnList insnList) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void insert(AbstractInsnNode insnNode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void insert(AbstractInsnNode previousInsn, InsnList insnList) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void insert(AbstractInsnNode previousInsn, AbstractInsnNode insnNode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void insertBefore(AbstractInsnNode nextInsn, InsnList insnList) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void insertBefore(AbstractInsnNode nextInsn, AbstractInsnNode insnNode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void remove(AbstractInsnNode insnNode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void resetLabels() {
		throw new UnsupportedOperationException();
	}
}
