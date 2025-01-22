package ftbsc.lll.utils;

import ftbsc.lll.exceptions.PatternNotFoundException;
import ftbsc.lll.proxies.impl.FieldProxy;
import ftbsc.lll.proxies.impl.MethodProxy;
import ftbsc.lll.proxies.impl.TypeProxy;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
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
	 * @return the {@link InsnList} object representing the matched pattern
	 */
	public InsnList find(MethodNode node) {
		return this.find(this.reverse ? node.instructions.getLast() : node.instructions.getFirst());
	}

	/**
	 * Tries to match the given pattern starting from a given node.
	 * @param node the node to start the search on
	 * @return the {@link InsnList} object representing the matched pattern
	 */
	public InsnList find(AbstractInsnNode node) {
		if(node != null) {
			AbstractInsnNode first, last;
			for(AbstractInsnNode cur = node; cur != null; cur = this.reverse ? cur.getPrevious() : cur.getNext()) {
				if(this.predicates.isEmpty()) return InsnListUtils.of(cur); //match whatever
				first = cur;
				last = cur;
				for(int match = 0; last != null && match < this.predicates.size(); last = this.reverse ? last.getPrevious() : last.getNext()) {
					if(match != 0) {
						if(this.ignoreLabels && last.getType() == AbstractInsnNode.LABEL) continue;
						if(this.ignoreFrames && last.getType() == AbstractInsnNode.FRAME) continue;
						if(this.ignoreLineNumbers && last.getType() == AbstractInsnNode.LINE) continue;
					}
					if(!this.predicates.get(match).test(last)) break;
					if(match == this.predicates.size() - 1) {
						if(this.reverse) return InsnListUtils.between(last, first); //we are matching backwards
						else return InsnListUtils.between(first, last);
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
			this.predicates.add(predicate);
			return this;
		}

		/**
		 * Wildcard, matches any kind of node.
		 * @return the builder's state after the operation
		 */
		public Builder any() {
			return this.check(i -> true);
		}

		/**
		 * Matches a specific opcode.
		 * @param opcode opcode to match
		 * @return the builder's state after the operation
		 */
		public Builder opcode(int opcode) {
			return this.check(i -> i.getOpcode() == opcode);
		}

		/**
		 * Matches a list of opcodes.
		 * @param opcodes list of opcodes to match
		 * @return the builder's state after the operation
		 */
		public Builder opcodes(int... opcodes) {
			Builder res = this;
			for(int o : opcodes) {
				res = this.opcode(o);
			}

			return res;
		}

		/**
		 * Matches a method invocation of any kind: one of INVOKEVIRTUAL,
		 * INVOKESPECIAL, INVOKESTATIC or INVOKEINTERFACE.
		 * @return the builder's state after the operation
		 */
		public Builder method() {
			return this.check(i -> i.getType() == AbstractInsnNode.METHOD_INSN);
		}

		/**
		 * Matches a field invocation of any kind: one of GETSTATIC, PUTSTATIC,
		 * GETFIELD or PUTFIELD.
		 * @return the builder's state after the operation
		 */
		public Builder field() {
			return this.check(i -> i.getType() == AbstractInsnNode.FIELD_INSN);
		}

		/**
		 * Matches any kind of jump instruction.
		 * @return the builder's state after the operation
		 */
		public Builder jump() {
			return this.check(i -> i.getType() == AbstractInsnNode.JUMP_INSN);
		}

		/**
		 * Matches any kind of label.
		 * @return the builder's state after the operation
		 */
		public Builder label() {
			return this.check(i -> i.getType() == AbstractInsnNode.LABEL);
		}

		/**
		 * Matches the given opcode and the exact given arguments.
		 * Partial argument matches are not supported: all arguments must be provided for
		 * the check to succeed.
		 * The expected order of arguments is the one used in the relevant node constructor;
		 * where possible, a proxy can substitute the parent/name/descriptor arguments.
		 * Lists may be used in place of arrays; varargs will also be supported where the
		 * relevant node constructor accepted them. Raw labels may be used in place of LabelNodes.
		 * Matches made using method are the safest, but other tests are generally faster,
		 * although the difference will likely be negligible in nearly all use cases.
		 * @param opcode the opcode
		 * @param args the arguments (you may use proxies in place of name/descriptors)
		 * @return the builder's state after the operation
		 */
		public Builder node(int opcode, Object... args) {
			return this.check(i -> matchNode(i, opcode, args));
		}

		/**
		 * Tests whether the arguments at the given index of the given array match the ones
		 * at the expected array. It will first check if the item at the given index is an
		 * array or {@link List}. If it is, it will check that against the expected array;
		 * if it isn't, and varargs is true, it will attempt to compare the expected array
		 * against all the elements of the given array starting from the given index.
		 * @param startIdx inclusive start index
		 * @param given the array the check is being performed on
		 * @param expected the expected array
		 * @param varargs whether to check for varargs
		 * @param predicate the comparison predicate between the given and expected argument
		 * @return true if it was a match
		 */
		private static boolean matchList(
			int startIdx,
			Object[] given,
			Object[] expected,
			boolean varargs,
			BiPredicate<Object, Object> predicate
		) {
			if(given.length <= startIdx) return false;
			if(given[startIdx] instanceof Object[]) {
				given = (Object[]) given[startIdx];
				startIdx = 0;
			} else if(given[startIdx] instanceof List<?>) {
				given = ((List<?>) given[startIdx]).toArray();
				startIdx = 0;
			} else if(!varargs) {
				return false;
			}

			if(given.length - startIdx != expected.length) return false;
			for(; startIdx < expected.length; startIdx++) {
				if(!predicate.test(given[startIdx], expected[startIdx])) {
					return false;
				}
			}

			return true;
		}

		private static final BiPredicate<Object, Object> COMPARE_LABELS = (p, ex) -> {
			LabelNode expected = (LabelNode) ex;
			return expected.equals(p) || expected.getLabel().equals(p);
		};

		/**
		 * Tests whether a given {@link AbstractInsnNode} matches the given opcode and arguments.
		 * @param i the node to test
		 * @param opcode the opcode to look for
		 * @param args the arguments to look for
		 * @return true if it was a match
		 */
		private static boolean matchNode(AbstractInsnNode i, int opcode, Object... args) {
			if(i.getOpcode() != opcode) return false;
			switch(i.getType()) {
				case AbstractInsnNode.INSN:
					return args.length == 0;
				case AbstractInsnNode.JUMP_INSN:
					JumpInsnNode jmp = (JumpInsnNode) i;
					return args.length == 1 && (
						jmp.label.getLabel().equals(args[0])
							|| jmp.label.equals(args[0])
					);
				case AbstractInsnNode.INVOKE_DYNAMIC_INSN: // why would you do this?
					if(args.length < 4) return false;
					InvokeDynamicInsnNode indy = (InvokeDynamicInsnNode) i;
					return indy.name.equals(args[0])
						&& indy.desc.equals(args[1])
						&& indy.bsm.equals(args[2])
						&& matchList(3, args, indy.bsmArgs, true, Object::equals);
				case AbstractInsnNode.INT_INSN:
					return args.length == 1
						&& args[0] instanceof Integer
						&& ((IntInsnNode) i).operand == (Integer) args[0];
				case AbstractInsnNode.IINC_INSN:
					IincInsnNode iinc = (IincInsnNode) i;
					return args.length == 2
						&& args[0] instanceof Integer
						&& args[1] instanceof Integer
						&& iinc.var == (Integer) args[0]
						&& iinc.incr == (Integer) args[1];
				case AbstractInsnNode.LDC_INSN:
					return args.length == 1
						&& Objects.equals(((LdcInsnNode) i).cst, args[0]);
				case AbstractInsnNode.LOOKUPSWITCH_INSN:
					if(args.length < 3) return false;
					LookupSwitchInsnNode lookup = (LookupSwitchInsnNode) i;
					return (lookup.dflt.equals(args[0]) || lookup.dflt.getLabel().equals(args[0]))
						&& matchList(1, args, lookup.keys.toArray(), false, Object::equals)
						&& matchList(2, args, lookup.labels.toArray(), false, Object::equals);
				case AbstractInsnNode.MULTIANEWARRAY_INSN:
					MultiANewArrayInsnNode mana = (MultiANewArrayInsnNode) i;
					return args.length == 2
						&& args[1] instanceof Integer
						&& mana.dims == (Integer) args[1]
						&& mana.desc.equals(
							args[0] instanceof TypeProxy
								? ((TypeProxy) args[0]).descriptor
								: args[0]
						);
				case AbstractInsnNode.METHOD_INSN:
					MethodInsnNode method = (MethodInsnNode) i;
					boolean methodMatch = true;
					switch(args.length) {
						case 2:
							methodMatch = args[1] instanceof Boolean
								&& method.itf == (Boolean) args[1];
						case 1:
							methodMatch &= args[0] instanceof MethodProxy;
							if(methodMatch) {
								MethodProxy proxy = (MethodProxy) args[0];
								return proxy.parent.internalName.equals(method.owner)
									&& proxy.name.equals(method.name)
									&& proxy.descriptor.equals(method.desc);
							} else break;
						case 4:
							methodMatch = args[3] instanceof Boolean
								&& method.itf == (Boolean) args[3];
						case 3:
							return methodMatch
								&& args[0].equals(method.owner)
								&& args[1].equals(method.desc)
								&& args[2].equals(method.name);
					}
					return false;
				case AbstractInsnNode.FIELD_INSN:
					FieldInsnNode field = (FieldInsnNode) i;
					if(args.length == 1 && args[0] instanceof FieldProxy) {
						FieldProxy proxy = (FieldProxy) args[0];
						return proxy.parent.internalName.equals(field.owner)
							&& proxy.name.equals(field.name)
							&& proxy.descriptor.equals(field.desc);
					} else if(args.length == 3) {
						return args[0].equals(field.owner)
							&& args[1].equals(field.name)
							&& args[2].equals(field.desc);
					} else return false;
				case AbstractInsnNode.TYPE_INSN:
					TypeInsnNode type = (TypeInsnNode) i;
					if(args.length != 1) return false;
					if(args[0] instanceof TypeProxy) {
						return ((TypeProxy) args[0]).internalName.equals(type.desc);
					} else return args[0].equals(type.desc);
				case AbstractInsnNode.TABLESWITCH_INSN:
					if(args.length < 4) return false;
					TableSwitchInsnNode tab = (TableSwitchInsnNode) i;
					return args[0] instanceof Integer
						&& tab.min == (Integer) args[0]
						&& args[1] instanceof Integer
						&& tab.min == (Integer) args[1]
						&& COMPARE_LABELS.test(args[2], tab.dflt)
						&& matchList(3, args, tab.labels.toArray(), true, COMPARE_LABELS);
				case AbstractInsnNode.VAR_INSN:
					return args.length == 1
						&& ((VarInsnNode) i).var == (Integer) args[0];
				default:
					return false;
			}
		}

		/**
		 * Tells the pattern matcher to ignore LABEL nodes.
		 * @return the builder's state after the operation
		 */
		public Builder ignoreLabels() {
			this.ignoreLabels = true;
			return this;
		}

		/**
		 * Tells the pattern matcher to ignore FRAME nodes.
		 * @return the builder's state after the operation
		 */
		public Builder ignoreFrames() {
			this.ignoreFrames = true;
			return this;
		}

		/**
		 * Tells the pattern matcher to ignore LINENUMBER nodes.
		 * @return the builder's state after the operation
		 */
		public Builder ignoreLineNumbers() {
			this.ignoreLineNumbers = true;
			return this;
		}

		/**
		 * Tells the pattern matcher to ignore all no-ops.
		 * @return the builder's state after the operation
		 */
		public Builder ignoreNoOps() {
			return this.ignoreLabels()
				.ignoreFrames()
				.ignoreLineNumbers();
		}
	}
}
