package ftbsc.lll.utils;

import ftbsc.lll.exceptions.PatternNotFoundException;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Describes a pattern to match on a list of ASM instructions.
 */
public class PatternMatcher {
	/**
	 * The list of predicates to match.
	 */
	private final List<Predicate<AbstractInsnNode>> predicates;

	/**
	 * Whether pattern search should be done from the end.
	 */
	private final boolean reverse;

	/**
	 * Patterns flagged with this ignore labels.
	 */
	private final boolean ignoreLabels;

	/**
	 * Patterns flagged with this ignore FRAME instructions.
	 */
	private final boolean ignoreFrames;

	/**
	 * Patterns flagged with this ignore LINENUMBER instructions.
	 */
	private final boolean ignoreLineNumbers;

	/**
	 * Private constructor because a PatternMatcher should only ever be initialized
	 * through the builder.
	 * @param predicates the list of predicates to match
	 * @param reverse search direction
	 * @param ignoreLabels whether LABEL instructions should be ignored
	 * @param ignoreFrames whether FRAME instructions should be ignored
	 * @param ignoreLineNumbers whether LINENUMBER instructions should be ignored
	 */
	private PatternMatcher(List<Predicate<AbstractInsnNode>> predicates, boolean reverse,
	                       boolean ignoreLabels, boolean ignoreFrames, boolean ignoreLineNumbers) {
		this.predicates = predicates;
		this.reverse = reverse;
		this.ignoreLabels = ignoreLabels;
		this.ignoreFrames = ignoreFrames;
		this.ignoreLineNumbers = ignoreLineNumbers;
	}

	/**
	 * @return the Builder object for this {@link PatternMatcher}
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Tries to match the given pattern on a given {@link MethodNode}.
	 * @param node the {@link MethodNode} to search
	 * @return the InsnSequence object representing the matched pattern
	 */
	public InsnSequence find(MethodNode node) {
		return find(reverse ? node.instructions.getLast() : node.instructions.getFirst());
	}

	/**
	 * Tries to match the given pattern starting from a given node.
	 * @param node the node to start the search on
	 * @return the {@link InsnSequence} object representing the matched pattern
	 */
	public InsnSequence find(AbstractInsnNode node) {
		if(node != null) {
			AbstractInsnNode first, last;
			for(AbstractInsnNode cur = node; cur != null; cur = reverse ? cur.getPrevious() : cur.getNext()) {
				if(predicates.size() == 0) return new InsnSequence(cur); //match whatever
				first = cur;
				last = cur;
				for(int match = 0; last != null && match < predicates.size(); last = reverse ? last.getPrevious() : last.getNext()) {
					if(match != 0) {
						if(ignoreLabels && last.getType() == AbstractInsnNode.LABEL) continue;
						if(ignoreFrames && last.getType() == AbstractInsnNode.FRAME) continue;
						if(ignoreLineNumbers && last.getType() == AbstractInsnNode.LINE) continue;
					}
					if(!predicates.get(match).test(last)) break;
					if(match == predicates.size() - 1) {
						if(reverse) return new InsnSequence(last, first); //we are matching backwards
						else return new InsnSequence(first, last);
					} else match++;
				}
			}
		}
		throw new PatternNotFoundException("Failed to find pattern!");
	}

	/**
	 * The Builder object for {@link PatternMatcher}.
	 */
	public static class Builder {

		/**
		 * List of predicates the pattern has to match.
		 */
		private final List<Predicate<AbstractInsnNode>> predicates = new ArrayList<>();

		/**
		 * Whether the pattern matching should proceed in reversed order.
		 */
		private boolean reverse = false;

		/**
		 * Patterns flagged with this ignore labels.
		 */
		private boolean ignoreLabels = false;

		/**
		 * Patterns flagged with this ignore FRAME instructions.
		 */
		private boolean ignoreFrames = false;

		/**
		 * Patterns flagged with this ignore LINENUMBER instructions.
		 */
		private boolean ignoreLineNumbers = false;

		/**
		 * Builds the pattern defined so far.
		 * @return the built {@link PatternMatcher}
		 */
		public PatternMatcher build() {
			return new PatternMatcher(predicates, reverse, ignoreLabels, ignoreFrames, ignoreLineNumbers);
		}

		/**
		 * Sets the pattern to match starting from the end.
		 * @return the builder's state after the operation
		 */
		public Builder reverse() {
			this.reverse = true;
			return this;
		}

		/**
		 * Adds a custom predicate to the list. Also used internally.
		 * @param predicate the predicate to add
		 * @return the builder's state after the operation
		 */
		public Builder check(Predicate<AbstractInsnNode> predicate) {
			predicates.add(predicate);
			return this;
		}

		/**
		 * Wildcard, matches any kind of node.
		 * @return the builder's state after the operation
		 */
		public Builder any() {
			return check(i -> true);
		}

		/**
		 * Matches a specific opcode.
		 * @param opcode opcode to match
		 * @return the builder's state after the operation
		 */
		public Builder opcode(int opcode) {
			return check(i -> i.getOpcode() == opcode);
		}

		/**
		 * Matches a list of opcodes.
		 * @param opcodes list of opcodes to match
		 * @return the builder's state after the operation
		 */
		public Builder opcodes(int... opcodes) {
			Builder res = this;
			for(int o : opcodes)
				res = opcode(o);
			return res;
		}

		/**
		 * Matches a method invokation of any kind: one of INVOKEVIRTUAL,
		 * INVOKESPECIAL, INVOKESTATIC or INVOKEINTERFACE.
		 * @return the builder's state after the operation
		 */
		public Builder method() {
			return check(i -> i.getType() == AbstractInsnNode.METHOD_INSN);
		}

		/**
		 * Matches a field invokation of any kind: one of GETSTATIC, PUTSTATIC,
		 * GETFIELD or PUTFIELD.
		 * @return the builder's state after the operation
		 */
		public Builder field() {
			return check(i -> i.getType() == AbstractInsnNode.FIELD_INSN);
		}

		/**
		 * Matches any kind of jump instruction.
		 * @return the builder's state after the operation
		 */
		public Builder jump() {
			return check(i -> i.getType() == AbstractInsnNode.JUMP_INSN);
		}

		/**
		 * Matches any kind of label.
		 * @return the builder's state after the operation
		 */
		public Builder label() {
			return check(i -> i.getType() == AbstractInsnNode.LABEL);
		}

		/**
		 * Tells the pattern matcher to ignore LABEL instructions.
		 * @return the builder's state after the operation
		 */
		public Builder ignoreLabels() {
			this.ignoreLabels = true;
			return this;
		}

		/**
		 * Tells the pattern matcher to ignore FRAME instructions.
		 * @return the builder's state after the operation
		 */
		public Builder ignoreFrames() {
			this.ignoreFrames = true;
			return this;
		}

		/**
		 * Tells the pattern matcher to ignore LINENUMBER instructions.
		 * @return the builder's state after the operation
		 */
		public Builder ignoreLineNumbers() {
			this.ignoreLineNumbers = true;
			return this;
		}
	}
}
