package ftbsc.lll.tools.nodes;

import ftbsc.lll.proxies.TypeProxy;
import org.objectweb.asm.tree.TypeInsnNode;

/**
 * Overrides the {@link TypeInsnNode} to add a constructor
 * taking in a {@link TypeProxy}.
 * @since 0.4.0
 */
public class TypeProxyInsnNode extends TypeInsnNode {
	/**
	 * Constructs a new {@link TypeInsnNode} starting from a
	 * {@link TypeProxy}. The user should ensure that the TypeInsnNode
	 * represents a declared type before calling this.
	 * @param opcode the opcode, must be one of NEW, ANEWARRAY,
	 *               CHECKCAST or INSTANCEOF
	 * @param t a {@link TypeProxy} representing the type to call
	 */
	public TypeProxyInsnNode(int opcode, TypeProxy t) {
		super(opcode, t.internalName);
	}
}
