package ftbsc.lll.utils;

import ftbsc.lll.exceptions.PatternNotFoundException;
import ftbsc.lll.proxies.impl.FieldProxy;
import ftbsc.lll.proxies.impl.MethodProxy;
import ftbsc.lll.proxies.impl.TypeProxy;
import ftbsc.lll.utils.nodes.FieldProxyInsnNode;
import ftbsc.lll.utils.nodes.MethodProxyInsnNode;
import ftbsc.lll.utils.nodes.TypeProxyInsnNode;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * Utilities for writing patches.
 * Note that in ASM any node may only be owned by one list at a time.
 * This means that these utils may turn into footguns for unusual use cases.
 * Like always with bytecode, exercise caution.
 * @author zaaarf
 */
public class PatchUtils implements Opcodes {
	/**
	 * Builds and sets a {@link PatternMatcher} on the given method,
	 * looking for the given opcodes ignoring no-op instructions.
	 * @param method the method to match in
	 * @param opcodes the opcodes to look for
	 * @return the matched sequence
	 * @throws PatternNotFoundException if it does not find the pattern
	 */
	public static InsnList match(MethodNode method, int... opcodes) {
		return PatternMatcher.builder().opcodes(opcodes).ignoreNoOps().build().find(method);
	}

	/**
	 * Builds and sets a {@link PatternMatcher} on the given method,
	 * looking for a node identical to the given one
	 * @param method the method to match in
	 * @param opcode the opcode
	 * @param args the node arguments
	 * @return the matched sequence
	 * @see PatternMatcher.Builder#node(int, Object...) for usage details
	 * @throws PatternNotFoundException if it does not find the pattern
	 */
	public static InsnList matchNode(MethodNode method, int opcode, Object... args) {
		return PatternMatcher.builder().node(opcode, args).ignoreNoOps().build().find(method);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the preNodes produce a stack
	 * matching the given opcode.
	 * @param jumpOpcode the opcode to use (must be a jump operation)
	 * @param preNodes the nodes to invoke before the if check
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifOp(int jumpOpcode, InsnList preNodes, InsnList nodes) {
		InsnList lst = new InsnList();
		LabelNode skip = new LabelNode();
		lst.add(preNodes);
		lst.add(jump(jumpOpcode, skip));
		lst.add(nodes);
		lst.add(skip);
		return lst;
	}

	/**
	 * Builds a sequence that invokes the given nodes if the given {@link FieldProxy}
	 * is found to be true.
	 * @param fp the proxy to check
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifTrue(FieldProxy fp, InsnList nodes) {
		return ifTrue(list(getStatic(fp)), nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the given {@link FieldProxy}
	 * is found to be false.
	 * @param fp the proxy to check
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifFalse(FieldProxy fp, InsnList nodes) {
		return ifFalse(list(getStatic(fp)), nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the given {@link FieldProxy}
	 * is found to be null.
	 * @param fp the proxy to check
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifNull(FieldProxy fp, InsnList nodes) {
		return ifNull(list(getStatic(fp)), nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the given {@link FieldProxy}
	 * is found to be null.
	 * @param fp the proxy to check
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifNonNull(FieldProxy fp, InsnList nodes) {
		return ifNonNull(list(getStatic(fp)), nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the given {@link FieldProxy}
	 * is found to be equal to the given value.
	 * @param fp the proxy to check
	 * @param value the value to compare against
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifEqual(FieldProxy fp, int value, InsnList nodes) {
		return ifEqual(list(getStatic(fp)), value, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the given {@link FieldProxy}
	 * is found to be not equal to the given value.
	 * @param fp the proxy to check
	 * @param value the value to compare against
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifNotEqual(FieldProxy fp, int value, InsnList nodes) {
		return ifNotEqual(list(getStatic(fp)), value, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the given {@link FieldProxy}
	 * is found to be less or equal to the given value.
	 * @param fp the proxy to check
	 * @param value the value to compare against
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifLessOrEqual(FieldProxy fp, int value, InsnList nodes) {
		return ifLessOrEqual(list(getStatic(fp)), value, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the given {@link FieldProxy}
	 * is found to be less than the given value.
	 * @param fp the proxy to check
	 * @param value the value to compare against
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifLess(FieldProxy fp, int value, InsnList nodes) {
		return ifLess(list(getStatic(fp)), value, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the given {@link FieldProxy}
	 * is found to be greater or equal to the given value.
	 * @param fp the proxy to check
	 * @param value the value to compare against
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifGreaterOrEqual(FieldProxy fp, int value, InsnList nodes) {
		return ifGreaterOrEqual(list(getStatic(fp)), value, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the given {@link FieldProxy}
	 * is found to be greater than the given value.
	 * @param fp the proxy to check
	 * @param value the value to compare against
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifGreater(FieldProxy fp, int value, InsnList nodes) {
		return ifGreater(list(getStatic(fp)), value, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the given {@link FieldProxy}
	 * is found to be equal to the given value.
	 * @param fp the proxy to check
	 * @param value the value to compare against
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifEqual(FieldProxy fp, long value, InsnList nodes) {
		return ifEqual(list(getStatic(fp)), value, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the given {@link FieldProxy}
	 * is found to be not equal to the given value.
	 * @param fp the proxy to check
	 * @param value the value to compare against
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifNotEqual(FieldProxy fp, long value, InsnList nodes) {
		return ifNotEqual(list(getStatic(fp)), value, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the given {@link FieldProxy}
	 * is found to be less or equal to the given value.
	 * @param fp the proxy to check
	 * @param value the value to compare against
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifLessOrEqual(FieldProxy fp, long value, InsnList nodes) {
		return ifLessOrEqual(list(getStatic(fp)), value, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the given {@link FieldProxy}
	 * is found to be less than the given value.
	 * @param fp the proxy to check
	 * @param value the value to compare against
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifLess(FieldProxy fp, long value, InsnList nodes) {
		return ifLess(list(getStatic(fp)), value, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the given {@link FieldProxy}
	 * is found to be greater or equal to the given value.
	 * @param fp the proxy to check
	 * @param value the value to compare against
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifGreaterOrEqual(FieldProxy fp, long value, InsnList nodes) {
		return ifGreaterOrEqual(list(getStatic(fp)), value, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the given {@link FieldProxy}
	 * is found to be greater than the given value.
	 * @param fp the proxy to check
	 * @param value the value to compare against
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifGreater(FieldProxy fp, long value, InsnList nodes) {
		return ifGreater(list(getStatic(fp)), value, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the given {@link FieldProxy}
	 * is found to be equal to the given value.
	 * @param fp the proxy to check
	 * @param value the value to compare against
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifEqual(FieldProxy fp, float value, InsnList nodes) {
		return ifEqual(list(getStatic(fp)), value, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the given {@link FieldProxy}
	 * is found to be not equal to the given value.
	 * @param fp the proxy to check
	 * @param value the value to compare against
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifNotEqual(FieldProxy fp, float value, InsnList nodes) {
		return ifNotEqual(list(getStatic(fp)), value, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the given {@link FieldProxy}
	 * is found to be less or equal to the given value.
	 * @param fp the proxy to check
	 * @param value the value to compare against
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifLessOrEqual(FieldProxy fp, float value, InsnList nodes) {
		return ifLessOrEqual(list(getStatic(fp)), value, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the given {@link FieldProxy}
	 * is found to be less than the given value.
	 * @param fp the proxy to check
	 * @param value the value to compare against
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifLess(FieldProxy fp, float value, InsnList nodes) {
		return ifLess(list(getStatic(fp)), value, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the given {@link FieldProxy}
	 * is found to be greater or equal to the given value.
	 * @param fp the proxy to check
	 * @param value the value to compare against
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifGreaterOrEqual(FieldProxy fp, float value, InsnList nodes) {
		return ifGreaterOrEqual(list(getStatic(fp)), value, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the given {@link FieldProxy}
	 * is found to be greater than the given value.
	 * @param fp the proxy to check
	 * @param value the value to compare against
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifGreater(FieldProxy fp, float value, InsnList nodes) {
		return ifGreater(list(getStatic(fp)), value, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the given {@link FieldProxy}
	 * is found to be equal to the given value.
	 * @param fp the proxy to check
	 * @param value the value to compare against
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifEqual(FieldProxy fp, double value, InsnList nodes) {
		return ifEqual(list(getStatic(fp)), value, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the given {@link FieldProxy}
	 * is found to be not equal to the given value.
	 * @param fp the proxy to check
	 * @param value the value to compare against
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifNotEqual(FieldProxy fp, double value, InsnList nodes) {
		return ifNotEqual(list(getStatic(fp)), value, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the given {@link FieldProxy}
	 * is found to be less or equal to the given value.
	 * @param fp the proxy to check
	 * @param value the value to compare against
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifLessOrEqual(FieldProxy fp, double value, InsnList nodes) {
		return ifLessOrEqual(list(getStatic(fp)), value, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the given {@link FieldProxy}
	 * is found to be less than the given value.
	 * @param fp the proxy to check
	 * @param value the value to compare against
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifLess(FieldProxy fp, double value, InsnList nodes) {
		return ifLess(list(getStatic(fp)), value, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the given {@link FieldProxy}
	 * is found to be greater or equal to the given value.
	 * @param fp the proxy to check
	 * @param value the value to compare against
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifGreaterOrEqual(FieldProxy fp, double value, InsnList nodes) {
		return ifGreaterOrEqual(list(getStatic(fp)), value, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the given {@link FieldProxy}
	 * is found to be greater than the given value.
	 * @param fp the proxy to check
	 * @param value the value to compare against
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifGreater(FieldProxy fp, double value, InsnList nodes) {
		return ifGreater(list(getStatic(fp)), value, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be true.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifTrue(InsnList preNodes, InsnList nodes) {
		return ifOp(IFEQ, preNodes, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be false.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifFalse(InsnList preNodes, InsnList nodes) {
		return ifOp(IFNE, preNodes, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be null.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifNull(InsnList preNodes, InsnList nodes) {
		return ifOp(IFNONNULL, preNodes, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be not null.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifNonNull(InsnList preNodes, InsnList nodes) {
		return ifOp(IFNULL, preNodes, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be equal to the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param value the value to load
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifEqual(InsnList preNodes, int value, InsnList nodes) {
		preNodes.add(iconst(value));
		return ifOp(IF_ICMPNE, preNodes, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be not equal to the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param value the value to load
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifNotEqual(InsnList preNodes, int value, InsnList nodes) {
		preNodes.add(iconst(value));
		return ifOp(IF_ICMPEQ, preNodes, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be less or equal to the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param value the value to load
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifLessOrEqual(InsnList preNodes, int value, InsnList nodes) {
		preNodes.add(iconst(value));
		return ifOp(IF_ICMPGT, preNodes, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be less than the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param value the value to load
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifLess(InsnList preNodes, int value, InsnList nodes) {
		preNodes.add(iconst(value));
		return ifOp(IF_ICMPGE, preNodes, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be greater or equal to the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param value the value to load
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifGreaterOrEqual(InsnList preNodes, int value, InsnList nodes) {
		preNodes.add(iconst(value));
		return ifOp(IF_ICMPLT, preNodes, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be greater than the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param value the value to load
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifGreater(InsnList preNodes, int value, InsnList nodes) {
		preNodes.add(iconst(value));
		return ifOp(IF_ICMPLE, preNodes, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be equal to the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param value the value to load
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifEqual(InsnList preNodes, long value, InsnList nodes) {
		preNodes.add(lconst(value));
		preNodes.add(node(LCMP));
		return ifOp(IFNE, preNodes, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be not equal to the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param value the value to load
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifNotEqual(InsnList preNodes, long value, InsnList nodes) {
		preNodes.add(lconst(value));
		preNodes.add(node(LCMP));
		return ifOp(IFEQ, preNodes, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be less or equal to the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param value the value to load
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifLessOrEqual(InsnList preNodes, long value, InsnList nodes) {
		preNodes.add(lconst(value));
		preNodes.add(node(LCMP));
		return ifOp(IFGT, preNodes, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be less than the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param value the value to load
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifLess(InsnList preNodes, long value, InsnList nodes) {
		preNodes.add(lconst(value));
		preNodes.add(node(LCMP));
		return ifOp(IFGE, preNodes, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be greater or equal to the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param value the value to load
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifGreaterOrEqual(InsnList preNodes, long value, InsnList nodes) {
		preNodes.add(lconst(value));
		preNodes.add(node(LCMP));
		return ifOp(IFLT, preNodes, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be greater than the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param value the value to load
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifGreater(InsnList preNodes, long value, InsnList nodes) {
		preNodes.add(lconst(value));
		preNodes.add(node(LCMP));
		return ifOp(IFLE, preNodes, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be equal to the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param value the value to load
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifEqual(InsnList preNodes, float value, InsnList nodes) {
		preNodes.add(fconst(value));
		preNodes.add(node(FCMPL));
		return ifOp(IFNE, preNodes, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be not equal to the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param value the value to load
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifNotEqual(InsnList preNodes, float value, InsnList nodes) {
		preNodes.add(fconst(value));
		preNodes.add(node(FCMPL));
		return ifOp(IFEQ, preNodes, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be less or equal to the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param value the value to load
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifLessOrEqual(InsnList preNodes, float value, InsnList nodes) {
		preNodes.add(fconst(value));
		preNodes.add(node(FCMPG));
		return ifOp(IFGT, preNodes, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be less than the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param value the value to load
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifLess(InsnList preNodes, float value, InsnList nodes) {
		preNodes.add(fconst(value));
		preNodes.add(node(FCMPG));
		return ifOp(IFGE, preNodes, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be greater or equal to the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param value the value to load
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifGreaterOrEqual(InsnList preNodes, float value, InsnList nodes) {
		preNodes.add(fconst(value));
		preNodes.add(node(FCMPL));
		return ifOp(IFLT, preNodes, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be greater than the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param value the value to load
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifGreater(InsnList preNodes, float value, InsnList nodes) {
		preNodes.add(fconst(value));
		preNodes.add(node(FCMPL));
		return ifOp(IFLE, preNodes, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be equal to the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param value the value to load
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifEqual(InsnList preNodes, double value, InsnList nodes) {
		preNodes.add(dconst(value));
		preNodes.add(node(DCMPL));
		return ifOp(IFNE, preNodes, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be not equal to the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param value the value to load
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifNotEqual(InsnList preNodes, double value, InsnList nodes) {
		preNodes.add(dconst(value));
		preNodes.add(node(DCMPL));
		return ifOp(IFEQ, preNodes, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be less or equal to the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param value the value to load
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifLessOrEqual(InsnList preNodes, double value, InsnList nodes) {
		preNodes.add(dconst(value));
		preNodes.add(node(DCMPG));
		return ifOp(IFGT, preNodes, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be less than the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param value the value to load
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifLess(InsnList preNodes, double value, InsnList nodes) {
		preNodes.add(dconst(value));
		preNodes.add(node(DCMPG));
		return ifOp(IFGE, preNodes, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be greater or equal to the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param value the value to load
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifGreaterOrEqual(InsnList preNodes, double value, InsnList nodes) {
		preNodes.add(dconst(value));
		preNodes.add(node(DCMPL));
		return ifOp(IFLT, preNodes, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be greater than the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param value the value to load
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifGreater(InsnList preNodes, double value, InsnList nodes) {
		preNodes.add(dconst(value));
		preNodes.add(node(DCMPL));
		return ifOp(IFLE, preNodes, nodes);
	}

	/**
	 * Loads the given integer as a constant onto the stack.
	 * @param i the constant to load
	 * @return a node that can load the constant
	 */
	public static AbstractInsnNode iconst(int i) {
		switch(i) {
			case 0:
				return node(ICONST_0);
			case 1:
				return node(ICONST_1);
			case 2:
				return node(ICONST_2);
			case 3:
				return node(ICONST_3);
			case 4:
				return node(ICONST_4);
			case 5:
				return node(ICONST_5);
			case -1:
				return node(ICONST_M1);
			default:
				if(i >= Byte.MIN_VALUE && i <= Byte.MAX_VALUE) {
					return new IntInsnNode(BIPUSH, i);
				} else if(i >= Short.MIN_VALUE && i <= Short.MAX_VALUE) {
					return new IntInsnNode(SIPUSH, i);
				} else {
					return new LdcInsnNode(i);
				}
		}
	}

	/**
	 * Loads the given long as a constant onto the stack.
	 * @param l the constant to load
	 * @return a node that can load the constant
	 */
	public static AbstractInsnNode lconst(long l) {
		if(l == 0L) {
			return node(LCONST_0);
		} else if(l == 1L) {
			return node(LCONST_1);
		} else {
			return new LdcInsnNode(l);
		}
	}

	/**
	 * Loads the given float as a constant onto the stack.
	 * @param f the constant to load
	 * @return a node that can load the constant
	 */
	public static AbstractInsnNode fconst(float f) {
		if(f == 0F) {
			return node(FCONST_0);
		} else if(f == 1F) {
			return node(FCONST_1);
		} else if(f == 2F) {
			return node(FCONST_2);
		} else {
			return new LdcInsnNode(f);
		}
	}

	/**
	 * Loads the given double as a constant onto the stack.
	 * @param d the constant to load
	 * @return a node that can load the constant
	 */
	public static AbstractInsnNode dconst(double d) {
		if(d == 0D) {
			return node(DCONST_0);
		} else if(d == 1D) {
			return node(DCONST_1);
		} else {
			return new LdcInsnNode(d);
		}
	}

	/**
	 * Loads a null constant onto the stack.
	 * @return a node that can load a null constant
	 */
	public static AbstractInsnNode anull() {
		return new InsnNode(ACONST_NULL);
	}

	/**
	 * Inserts the given nodes at the start at the given method.
	 * @param method the method to add the nodes in
	 * @param nodes the nodes to insert
	 */
	public static void insertFirst(MethodNode method, AbstractInsnNode... nodes) {
		method.instructions.insert(list(nodes));
	}

	/**
	 * Inserts the given nodes at the start at the given method.
	 * @param method the method to add the nodes in
	 * @param nodes the nodes to insert
	 */
	public static void insertFirst(MethodNode method, InsnList nodes) {
		method.instructions.insert(nodes);
	}

	/**
	 * Inserts the given nodes after a certain node within the given method.
	 * @param method the method to add the nodes in
	 * @param afterNode the node to insert them after
	 * @param nodes the nodes to insert
	 */
	public static void insertAfter(MethodNode method, AbstractInsnNode afterNode, InsnList nodes) {
		method.instructions.insert(afterNode, nodes);
	}

	/**
	 * Inserts the given nodes after a certain node within the given method.
	 * @param method the method to add the nodes in
	 * @param matcher the built matcher to find the pattern after which to append
	 * @param nodes the nodes to insert
	 */
	public static void insertAfter(MethodNode method, PatternMatcher matcher, InsnList nodes) {
		method.instructions.insert(matcher.find(method).getLast(), nodes);
	}

	/**
	 * Inserts the given nodes after the given node within the given method.
	 * @param method the method to add the nodes in
	 * @param afterNode the node to insert them after
	 * @param nodes the nodes to insert
	 */
	public static void insertAfter(MethodNode method, AbstractInsnNode afterNode, AbstractInsnNode... nodes) {
		method.instructions.insert(afterNode, list(nodes));
	}

	/**
	 * Inserts the given nodes after the sequence found by the given matcher within the given method.
	 * @param method the method to add the nodes in
	 * @param matcher the built matcher to find the pattern after which to append
	 * @param nodes the nodes to insert
	 */
	public static void insertAfter(MethodNode method, PatternMatcher matcher, AbstractInsnNode... nodes) {
		method.instructions.insert(matcher.find(method).getLast(), list(nodes));
	}

	/**
	 * Inserts the given nodes before the given node within the given method.
	 * @param method the method to add the nodes in
	 * @param beforeNode the node to insert them before
	 * @param nodes the nodes to insert
	 */
	public static void insertBefore(MethodNode method, AbstractInsnNode beforeNode, InsnList nodes) {
		method.instructions.insertBefore(beforeNode, nodes);
	}

	/**
	 * Inserts the given nodes before the sequence found by the given matcher within the given method.
	 * @param method the method to add the nodes in
	 * @param matcher the built matcher to find the pattern before which to append
	 * @param nodes the nodes to insert
	 */
	public static void insertBefore(MethodNode method, PatternMatcher matcher, InsnList nodes) {
		method.instructions.insertBefore(matcher.find(method).getFirst(), nodes);
	}

	/**
	 * Inserts the given node before the given node within the given method.
	 * @param method the method to add the nodes in
	 * @param beforeNode the node to insert them before
	 * @param nodes the node to insert
	 */
	public static void insertBefore(MethodNode method, AbstractInsnNode beforeNode, AbstractInsnNode... nodes) {
		method.instructions.insertBefore(beforeNode, list(nodes));
	}

	/**
	 * Inserts the given nodes before the given node within the given method.
	 * @param method the method to add the nodes in
	 * @param matcher the built matcher to find the pattern before which to append
	 * @param nodes the nodes to insert
	 */
	public static void insertBefore(MethodNode method, PatternMatcher matcher, AbstractInsnNode... nodes) {
		method.instructions.insertBefore(matcher.find(method).getFirst(), list(nodes));
	}

	/**
	 * Inserts the given nodes before and after a certain node within the given method.
	 * @param method the method to add the nodes in
	 * @param node the node to insert them around
	 * @param before the nodes to insert before
	 * @param after the nodes to insert after
	 */
	public static void insertAround(MethodNode method, AbstractInsnNode node, InsnList before, InsnList after) {
		insertBefore(method, node, before);
		insertAfter(method, node, after);
	}

	/**
	 * Inserts the given nodes before and after a certain node within the given method.
	 * @param method the method to add the nodes in
	 * @param matcher the built matcher to find the pattern around which to append
	 * @param before the nodes to insert before
	 * @param after the nodes to insert after
	 */
	public static void insertAround(MethodNode method, PatternMatcher matcher, InsnList before, InsnList after) {
		insertBefore(method, matcher, before);
		insertAfter(method, matcher, after);
	}

	/**
	 * Skips the given node and executes the given list instead.
	 * This is a "safer" alternative to straight up deleting the node.
	 * @param method the method to add the nodes in
	 * @param node the node to skip
	 * @param instead the list of opcodes to use instead
	 */
	public static void skip(MethodNode method, AbstractInsnNode node, InsnList instead) {
		LabelNode ln = new LabelNode();
		insertBefore(method, node, jump(GOTO, ln));
		insertAfter(method, node, list(list(ln), instead));
	}

	/**
	 * Skips the given pattern and executes the given list instead.
	 * This is a "safer" alternative to straight up deleting the nodes.
	 * @param method the method to add the nodes in
	 * @param matcher the built matcher to find the pattern around which to append
	 * @param instead the list of opcodes to use instead
	 */
	public static void skip(MethodNode method, PatternMatcher matcher, InsnList instead) {
		LabelNode ln = new LabelNode();
		insertBefore(method, matcher, jump(GOTO, ln));
		insertAfter(method, matcher, list(list(ln), instead));
	}

	/**
	 * Skips the given node and executes the given list instead.
	 * This is a "safer" alternative to straight up deleting the node.
	 * @param method the method to add the nodes in
	 * @param node the node to skip
	 * @param instead the list of opcodes to use instead
	 */
	public static void skip(MethodNode method, AbstractInsnNode node, AbstractInsnNode... instead) {
		skip(method, node, list(instead));
	}

	/**
	 * Skips the given pattern and executes the given list instead.
	 * This is a "safer" alternative to straight up deleting the node.
	 * @param method the method to add the nodes in
	 * @param matcher the built matcher to find the pattern around which to append
	 * @param instead the list of opcodes to use instead
	 */
	public static void skip(MethodNode method, PatternMatcher matcher, AbstractInsnNode... instead) {
		skip(method, matcher, list(instead));
	}

	/**
	 * Creates an {@link InsnList} from the given opcodes.
	 * @param opcodes the opcodes to put in the list (must be valid candidates for {@link #node(int)})
	 * @return the built list
	 */
	public static InsnList list(int... opcodes) {
		InsnList seq = new InsnList();
		for(int n : opcodes) {
			seq.add(node(n));
		}

		return seq;
	}

	/**
	 * Creates an {@link InsnList} from the given nodes.
	 * @param nodes the nodes to put in there
	 * @return the built list
	 */
	public static InsnList list(AbstractInsnNode... nodes) {
		InsnList seq = new InsnList();
		for(AbstractInsnNode n : nodes) {
			seq.add(n);
		}

		return seq;
	}

	/**
	 * Creates an {@link InsnList} by chaining the given lists.
	 * Note that this will consume the input lists, so don't expect them to still be functional after this call.
	 * @param lists the lists of nodes to put in there
	 * @return the built list
	 */
	@SafeVarargs
	public static InsnList list(Iterable<AbstractInsnNode>... lists) {
		InsnList seq = new InsnList();
		for(Iterable<AbstractInsnNode> l : lists) {
			for(AbstractInsnNode n : l) {
				seq.add(n);
			}
		}

		return seq;
	}

	/**
	 * Appends the given nodes to the given list.
	 * @param lst the list to append to
	 * @param nodes the nodes to append
	 * @return the given list, with the extra nodes appended
	 */
	public static InsnList list(InsnList lst, AbstractInsnNode... nodes) {
		return list(lst, list(nodes));
	}

	/**
	 * Appends the given opcodes to the given list.
	 * @param lst the list to append to
	 *  @param opcodes the opcodes to put in the list (must be valid candidates for {@link #node(int)})
	 * @return the given list, with the extra nodes appended
	 */
	public static InsnList list(InsnList lst, int... opcodes) {
		return list(lst, list(opcodes));
	}

	/**
	 * Invokes an instanceof check on the last element on the stack.
	 * @param proxy the proxy for the type to check
	 * @return the created node
	 */
	public static TypeProxyInsnNode instanceOf(TypeProxy proxy) {
		return new TypeProxyInsnNode(INSTANCEOF, proxy);
	}

	/**
	 * Invokes an instanceof check on the last element on the stack.
	 * @param type the internal name (slash-separated fully qualified name) of the type
	 * @return the created node
	 */
	public static TypeInsnNode instanceOf(String type) {
		return new TypeInsnNode(INSTANCEOF, type);
	}

	/**
	 * Calls the given method proxy with INVOKESTATIC.
	 * @param mp the proxy
	 * @return the created node
	 */
	public static MethodProxyInsnNode invokeStatic(MethodProxy mp) {
		return new MethodProxyInsnNode(INVOKESTATIC, mp);
	}

	/**
	 * Calls the given method proxy with INVOKESTATIC and the interface flag set to true.
	 * @param mp the proxy
	 * @return the created node
	 */
	public static MethodProxyInsnNode invokeStaticInterface(MethodProxy mp) {
		return new MethodProxyInsnNode(INVOKESTATIC, mp, true);
	}

	/**
	 * Calls the given method proxy with INVOKEVIRTUAL.
	 * @param mp the proxy
	 * @return the created node
	 */
	public static MethodProxyInsnNode invokeVirtual(MethodProxy mp) {
		return new MethodProxyInsnNode(INVOKEVIRTUAL, mp);
	}

	/**
	 * Calls the given method proxy with INVOKEVIRTUAL and the interface flag set to true.
	 * @param mp the proxy
	 * @return the created node
	 */
	public static MethodProxyInsnNode invokeVirtualInterface(MethodProxy mp) {
		return new MethodProxyInsnNode(INVOKEVIRTUAL, mp, true);
	}

	/**
	 * Calls the given method proxy with INVOKESPECIAL.
	 * @param mp the proxy
	 * @return the created node
	 */
	public static MethodProxyInsnNode invokeSpecial(MethodProxy mp) {
		return new MethodProxyInsnNode(INVOKESPECIAL, mp);
	}

	/**
	 * Calls the given method proxy with INVOKESPECIAL and the interface flag set to true.
	 * @param mp the proxy
	 * @return the created node
	 */
	public static MethodProxyInsnNode invokeSpecialInterface(MethodProxy mp) {
		return new MethodProxyInsnNode(INVOKESPECIAL, mp, true);
	}

	/**
	 * Calls the given method proxy with INVOKEINTERFACE.
	 * @param mp the proxy
	 * @return the created node
	 */
	public static MethodProxyInsnNode invokeInterface(MethodProxy mp) {
		return new MethodProxyInsnNode(INVOKEINTERFACE, mp);
	}

	/**
	 * Gets the given field in the object referenced by the second-last item in
	 * the stack.
	 * @param fp the proxy
	 * @return the created node
	 */
	public static FieldProxyInsnNode getField(FieldProxy fp) {
		return new FieldProxyInsnNode(GETFIELD, fp);
	}

	/**
	 * Puts the value in the stack at the given field in the object referenced by
	 * the second-last item in the stack.
	 * @param fp the proxy
	 * @return the created node
	 */
	public static FieldProxyInsnNode putField(FieldProxy fp) {
		return new FieldProxyInsnNode(PUTFIELD, fp);
	}

	/**
	 * Puts the value in the stack at the given static field. 
	 * @param fp the proxy
	 * @return the created node
	 */
	public static FieldProxyInsnNode putStatic(FieldProxy fp) {
		return new FieldProxyInsnNode(PUTSTATIC, fp);
	}

	/**
	 * Gets the static field.
	 * @param fp the proxy
	 * @return the created node
	 */
	public static FieldProxyInsnNode getStatic(FieldProxy fp) {
		return new FieldProxyInsnNode(GETSTATIC, fp);
	}

	/**
	 * Loads the reference in the local variable with the given index.
	 * @param index the index of the variable
	 * @return the created node
	 */
	public static VarInsnNode aload(int index) {
		return new VarInsnNode(ALOAD, index);
	}

	/**
	 * Stores a reference value in the local variable with the given index.
	 * @param index the index of the variable
	 * @return the created node
	 */
	public static VarInsnNode astore(int index) {
		return new VarInsnNode(ASTORE, index);
	}

	/**
	 * Loads the double in the local variable with the given index.
	 * @param index the index of the variable
	 * @return the created node
	 */
	public static VarInsnNode dload(int index) {
		return new VarInsnNode(DLOAD, index);
	}

	/**
	 * Stores a double value in the local variable with the given index.
	 * @param index the index of the variable
	 * @return the created node
	 */
	public static VarInsnNode dstore(int index) {
		return new VarInsnNode(DSTORE, index);
	}

	/**
	 * Loads the float in the local variable with the given index.
	 * @param index the index of the variable
	 * @return the created node
	 */
	public static VarInsnNode fload(int index) {
		return new VarInsnNode(FLOAD, index);
	}

	/**
	 * Stores a float value in the local variable with the given index.
	 * @param index the index of the variable
	 * @return the created node
	 */
	public static VarInsnNode fstore(int index) {
		return new VarInsnNode(FSTORE, index);
	}

	/**
	 * Loads the integer in the local variable with the given index.
	 * @param index the index of the variable
	 * @return the created node
	 */
	public static VarInsnNode iload(int index) {
		return new VarInsnNode(ILOAD, index);
	}

	/**
	 * Loads the long in the local variable with the given index.
	 * @param index the index of the variable
	 * @return the created node
	 */
	public static VarInsnNode lload(int index) {
		return new VarInsnNode(LLOAD, index);
	}

	/**
	 * Stores a long value in the local variable with the given index.
	 * @param index the index of the variable
	 * @return the created node
	 */
	public static VarInsnNode lstore(int index) {
		return new VarInsnNode(LSTORE, index);
	}

	/**
	 * Stores an integer value in the local variable with the given index.
	 * @param index the index of the variable
	 * @return the created node
	 */
	public static VarInsnNode istore(int index) {
		return new VarInsnNode(ISTORE, index);
	}

	/**
	 * Jumps if the value on the stack is true.
	 * @param l the {@link LabelNode} to jump to
	 * @return the created node
	 */
	public static JumpInsnNode jumpIfTrue(LabelNode l) {
	return jump(IFNE, l);
	}

	/**
	 * Jumps if the value on the stack is false.
	 * @param l the {@link LabelNode} to jump to
	 * @return the created node
	 */
	public static JumpInsnNode jumpIfFalse(LabelNode l) {
		return jump(IFEQ, l);
	}

	/**
	 * Creates a jump node with the given opcode.
	 * @param opcode the opcode to use (assumed to be a valid jump opcode)
	 * @param l the label to jump to
	 * @return the created node
	 */
	public static JumpInsnNode jump(int opcode, LabelNode l) {
		return new JumpInsnNode(opcode, l);
	}

	/**
	 * Creates an {@link InsnNode} with the given opcode (which must not take any arguments).
	 * @param opcode the opcode
	 * @return the created node
	 */
	public static InsnNode node(int opcode) {
		return new InsnNode(opcode);
	}
}
