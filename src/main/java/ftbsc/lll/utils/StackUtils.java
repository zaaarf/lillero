package ftbsc.lll.utils;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Comparator;

/**
 * Various methods for manipulating the stack.
 * Includes anything from instantiation to variable manipulation - just about
 * anything that loads stuff on or from the stack.
 */
public class StackUtils implements Opcodes {
	/**
	 * Creates a new instance of an object, given its internal name, constructor descriptor and instructions to load
	 * the parameters.
	 * The created object is loaded on the stack.
	 * @param name the internal name of the object to initialize, where
	 *             the internal name of a class is its fully qualified name, where '.' are replaced by '/'
	 * @param desc the descriptor of the constructor to call
	 * @param args nodes containing instructions to load the constructor arguments, in the right order
	 * @return an instruction list containing the opcodes needed to create the new object and load it on the stack.
	 */
	public static InsnList instantiate(String name, String desc, AbstractInsnNode... args) {
		InsnSequence is = new InsnSequence();
		is.add(args);
		return instantiate(name, desc, is);
	}

	/**
	 * Creates a new instance of an object, given its internal name, constructor descriptor and instructions to load
	 * the parameters.
	 * The created object is loaded on the stack.
	 * @param name the internal name of the object to initialize, where
	 *             the internal name of a class is its fully qualified name, where '.' are replaced by '/'
	 * @param desc the descriptor of the constructor to call
	 * @param args a list of instructions loading the constructor arguments onto the stack in the correct order
	 * @return an instruction list containing the opcodes needed to create the new object and load it on the stack.
	 */
	public static InsnList instantiate(String name, String desc, InsnList args) {
		InsnSequence list = new InsnSequence();
		list.add(new TypeInsnNode(NEW, name), new InsnNode(DUP));
		if(args != null) list.add(args);
		list.add(new MethodInsnNode(INVOKESPECIAL, name, "<init>", desc, false));
		return list;
	}


	/**
	 * Creates a new local variable, lasting in scope from the first to the last label of the given method.
	 * @param method the method for which to declare the local variable
	 * @param name the variable's name
	 * @param desc the type descriptor for the new variable
	 * @return the index value of the new local variable
	 */
	public static int addLocalVariable(MethodNode method, String name, String desc) {
		return addLocalVariable(
			method, name, desc,
			(LabelNode) PatternMatcher.builder().label().build().find(method).getFirst(),
			(LabelNode) PatternMatcher.builder().label().reverse().build().find(method).getFirst()
		);
	}

	/**
	 * Creates a new local variable, lasting in scope between two given {@link LabelNode}s.
	 * @param method the method for which to declare the local variable
	 * @param name the variable's name
	 * @param desc the type descriptor for the new variable
	 * @param start the label at which the variable should enter scope
	 * @param end the label at which the variable should go out of scope
	 * @return the index value of the new local variable
	 */
	public static int addLocalVariable(MethodNode method, String name, String desc, LabelNode start, LabelNode end) {
		final int targetIndex =
			method.localVariables
				.stream()
				.max(Comparator.comparingInt(v -> v.index))
				.map(var -> var.desc.equals("J") || var.desc.equals("D")
					? var.index + 2 //skip two if long or double - major java moment
					: var.index + 1)
				.orElse(0);
		LocalVariableNode variable = new LocalVariableNode(name, desc, null, start, end, targetIndex);
		method.localVariables.add(variable);
		return targetIndex;
	}
}
