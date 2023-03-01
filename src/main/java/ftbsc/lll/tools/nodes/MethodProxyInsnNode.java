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
	 */
	public MethodProxyInsnNode(MethodProxy m, int opcode) {
		super(
			opcode,
			m.getParent().replace('.', '/'),
			m.getSrgName(),
			m.getDescriptor()
		);
	}
}