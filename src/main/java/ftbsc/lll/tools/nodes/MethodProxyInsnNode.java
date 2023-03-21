package ftbsc.lll.tools.nodes;

import ftbsc.lll.proxies.MethodProxy;
import org.objectweb.asm.tree.MethodInsnNode;

/**
 * Overrides the {@link MethodInsnNode} to add a constructor
 * taking in a {@link MethodProxy}.
 * @since 0.3.0
 */
public class MethodProxyInsnNode extends MethodInsnNode {

	/**
	 * Constructs a new {@link MethodInsnNode} starting
	 * from a {@link MethodProxy}.
	 * @param opcode the opcode, must be one of INVOKEVIRTUAL,
	 *               INVOKESPECIAL, INVOKESTATIC or INVOKEINTERFACE
	 * @param m a {@link MethodProxy} representing the method to call
	 */
	public MethodProxyInsnNode(int opcode, MethodProxy m) {
		super(opcode,	m.parent.internalName, m.name, m.descriptor);
	}
}
