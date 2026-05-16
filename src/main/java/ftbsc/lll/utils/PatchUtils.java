package ftbsc.lll.utils;

import ftbsc.lll.exceptions.PatternNotFoundException;
import ftbsc.lll.proxies.impl.FieldProxy;
import ftbsc.lll.proxies.impl.MethodProxy;
import ftbsc.lll.utils.nodes.FieldProxyInsnNode;
import ftbsc.lll.utils.nodes.MethodProxyInsnNode;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * Utilities for writing patches.
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
	public static InsnList fastMatch(MethodNode method, int... opcodes) {
		return PatternMatcher.builder().opcodes(opcodes).ignoreNoOps().build().find(method);
	}

	private static InsnList _if(InsnList preNodes, int jumpIfCheck, InsnList nodes) {
		InsnList lst = new InsnList();
		LabelNode skip = new LabelNode();
		lst.add(preNodes);
		lst.add(new JumpInsnNode(jumpIfCheck, skip));
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
	 * is found to be greater to the given value.
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
		return _if(preNodes, IFEQ, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be false.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifFalse(InsnList preNodes, InsnList nodes) {
		return _if(preNodes, IFNE, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be null.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifNull(InsnList preNodes, InsnList nodes) {
		return _if(preNodes, IFNONNULL, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be not null.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifNonNull(InsnList preNodes, InsnList nodes) {
		return _if(preNodes, IFNULL, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be equal to the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifEqual(InsnList preNodes, int value, InsnList nodes) {
		preNodes.add(iconst(value));
		return _if(preNodes, IF_ICMPNE, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be not equal to the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifNotEqual(InsnList preNodes, int value, InsnList nodes) {
		preNodes.add(iconst(value));
		return _if(preNodes, IF_ICMPEQ, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be less or equal to the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifLessOrEqual(InsnList preNodes, int value, InsnList nodes) {
		preNodes.add(iconst(value));
		return _if(preNodes, IF_ICMPGT, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be less than the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifLess(InsnList preNodes, int value, InsnList nodes) {
		preNodes.add(iconst(value));
		return _if(preNodes, IF_ICMPGE, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be greater or equal to the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifGreaterOrEqual(InsnList preNodes, int value, InsnList nodes) {
		preNodes.add(iconst(value));
		return _if(preNodes, IF_ICMPLT, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be greater than the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifGreater(InsnList preNodes, int value, InsnList nodes) {
		preNodes.add(iconst(value));
		return _if(preNodes, IF_ICMPLE, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be equal to the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifEqual(InsnList preNodes, long value, InsnList nodes) {
		preNodes.add(lconst(value));
		preNodes.add(node(LCMP));
		return _if(preNodes, IFNE, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be not equal to the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifNotEqual(InsnList preNodes, long value, InsnList nodes) {
		preNodes.add(lconst(value));
		preNodes.add(node(LCMP));
		return _if(preNodes, IFEQ, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be less or equal to the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifLessOrEqual(InsnList preNodes, long value, InsnList nodes) {
		preNodes.add(lconst(value));
		preNodes.add(node(LCMP));
		return _if(preNodes, IFGT, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be less than the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifLess(InsnList preNodes, long value, InsnList nodes) {
		preNodes.add(lconst(value));
		preNodes.add(node(LCMP));
		return _if(preNodes, IFGE, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be greater or equal to the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifGreaterOrEqual(InsnList preNodes, long value, InsnList nodes) {
		preNodes.add(lconst(value));
		preNodes.add(node(LCMP));
		return _if(preNodes, IFLT, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be greater than the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifGreater(InsnList preNodes, long value, InsnList nodes) {
		preNodes.add(lconst(value));
		preNodes.add(node(LCMP));
		return _if(preNodes, IFLE, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be equal to the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifEqual(InsnList preNodes, float value, InsnList nodes) {
		preNodes.add(fconst(value));
		preNodes.add(node(FCMPL));
		return _if(preNodes, IFNE, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be not equal to the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifNotEqual(InsnList preNodes, float value, InsnList nodes) {
		preNodes.add(fconst(value));
		preNodes.add(node(FCMPL));
		return _if(preNodes, IFEQ, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be less or equal to the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifLessOrEqual(InsnList preNodes, float value, InsnList nodes) {
		preNodes.add(fconst(value));
		preNodes.add(node(FCMPG));
		return _if(preNodes, IFGT, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be less than the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifLess(InsnList preNodes, float value, InsnList nodes) {
		preNodes.add(fconst(value));
		preNodes.add(node(FCMPG));
		return _if(preNodes, IFGE, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be greater or equal to the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifGreaterOrEqual(InsnList preNodes, float value, InsnList nodes) {
		preNodes.add(fconst(value));
		preNodes.add(node(FCMPL));
		return _if(preNodes, IFLT, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be greater than the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifGreater(InsnList preNodes, float value, InsnList nodes) {
		preNodes.add(fconst(value));
		preNodes.add(node(FCMPL));
		return _if(preNodes, IFLE, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be equal to the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifEqual(InsnList preNodes, double value, InsnList nodes) {
		preNodes.add(dconst(value));
		preNodes.add(node(DCMPL));
		return _if(preNodes, IFNE, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be not equal to the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifNotEqual(InsnList preNodes, double value, InsnList nodes) {
		preNodes.add(dconst(value));
		preNodes.add(node(DCMPL));
		return _if(preNodes, IFEQ, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be less or equal to the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifLessOrEqual(InsnList preNodes, double value, InsnList nodes) {
		preNodes.add(dconst(value));
		preNodes.add(node(DCMPG));
		return _if(preNodes, IFGT, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be less than the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifLess(InsnList preNodes, double value, InsnList nodes) {
		preNodes.add(dconst(value));
		preNodes.add(node(DCMPG));
		return _if(preNodes, IFGE, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be greater or equal to the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifGreaterOrEqual(InsnList preNodes, double value, InsnList nodes) {
		preNodes.add(dconst(value));
		preNodes.add(node(DCMPL));
		return _if(preNodes, IFLT, nodes);
	}

	/**
	 * Builds a sequence that invokes the given nodes if the element on the stack is
	 * found to be greater than the given value.
	 * @param preNodes the nodes to load the item on the stack (consumes the value)
	 * @param nodes the nodes to invoke if the check passes
	 * @return the built sequence
	 */
	public static InsnList ifGreater(InsnList preNodes, double value, InsnList nodes) {
		preNodes.add(dconst(value));
		preNodes.add(node(DCMPL));
		return _if(preNodes, IFLE, nodes);
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
				return new LdcInsnNode(i);
		}
	}

	/**
	 * Loads the given long as a constant onto the stack.
	 * @param l the constant to load
	 * @return a node that can load the constant
	 */
	public static AbstractInsnNode lconst(long l) {
		if(l == 0F) {
			return node(LCONST_0);
		} else if(l == 1F) {
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
	 * Inserts the given node at the start at the given method.
	 * @param method the method to add the nodes in
	 * @param node the node to insert
	 */
	public static void insertFirst(MethodNode method, AbstractInsnNode node) {
		method.instructions.insert(node);
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
	 * Inserts the given node after a certain node within the given method.
	 * @param method the method to add the nodes in
	 * @param afterNode the node to insert them after
	 * @param node the node to insert
	 */
	public static void insertAfter(MethodNode method, AbstractInsnNode afterNode, AbstractInsnNode node) {
		method.instructions.insert(afterNode, node);
	}

	/**
	 * Inserts the given node after the given node within the given method.
	 * @param method the method to add the nodes in
	 * @param matcher the built matcher to find the pattern after which to append
	 * @param node the node to insert
	 */
	public static void insertAfter(MethodNode method, PatternMatcher matcher, AbstractInsnNode node) {
		method.instructions.insert(matcher.find(method).getLast(), node);
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
	 * @param node the node to insert
	 */
	public static void insertBefore(MethodNode method, AbstractInsnNode beforeNode, AbstractInsnNode node) {
		method.instructions.insertBefore(beforeNode, node);
	}

	/**
	 * Inserts the given node after the sequence found by the given matcher within the given method.
	 * @param method the method to add the nodes in
	 * @param matcher the built matcher to find the pattern after which to append
	 * @param node the nodes to insert
	 */
	public static void insertBefore(MethodNode method, PatternMatcher matcher, AbstractInsnNode node) {
		method.instructions.insertBefore(matcher.find(method).getFirst(), node);
	}

	/**
	 * Inserts the given node after the given node within the given method.
	 * @param method the method to add the nodes in
	 * @param beforeNode the node to insert them after
	 * @param nodes the node to insert
	 */
	public static void insertBefore(MethodNode method, AbstractInsnNode beforeNode, AbstractInsnNode... nodes) {
		method.instructions.insertBefore(beforeNode, list(nodes));
	}

	/**
	 * Inserts the given nodes after the given node within the given method.
	 * @param method the method to add the nodes in
	 * @param matcher the built matcher to find the pattern after which to append
	 * @param nodes the nodes to insert
	 */
	public static void insertBefore(MethodNode method, PatternMatcher matcher, AbstractInsnNode... nodes) {
		method.instructions.insertBefore(matcher.find(method).getFirst(), list(nodes));
	}

	/**
	 * Creates an {@link InsnList} from the given opcodes.
	 * @param opodes the nodes to put in there
	 * @return the built list
	 */
	public static InsnList list(int... opodes) {
		InsnList seq = new InsnList();
		for(int n : opodes) {
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
	 * Invokes statically the given method proxy.
	 * @param mp the proxy
	 * @return the created node
	 */
	public static MethodProxyInsnNode invokeStatic(MethodProxy mp) {
		return new MethodProxyInsnNode(INVOKESTATIC, mp);
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
		return new JumpInsnNode(IFNE, l);
	}

	/**
	 * Jumps if the value on the stack is false.
	 * @param l the {@link LabelNode} to jump to
	 * @return the created node
	 */
	public static JumpInsnNode jumpIfFalse(LabelNode l) {
		return new JumpInsnNode(IFEQ, l);
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
