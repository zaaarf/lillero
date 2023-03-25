package ftbsc.lll.tools.nodes;

import ftbsc.lll.proxies.impl.FieldProxy;
import org.objectweb.asm.tree.FieldInsnNode;

/**
 * Overrides the {@link FieldInsnNode} to add a constructor
 * taking in a {@link FieldProxy}.
 * @since 0.3.0
 */
public class FieldProxyInsnNode extends FieldInsnNode {
	/**
	 * Constructs a new {@link FieldInsnNode} starting
	 * from a {@link FieldProxy}.
	 * @param opcode the opcode, must be one of GETSTATIC, PUTSTATIC,
	 *               GETFIELD or PUTFIELD
	 * @param f a {@link FieldProxy} representing the field to call
	 */
	public FieldProxyInsnNode(int opcode, FieldProxy f) {
		super(opcode, f.parent.internalName, f.name, f.descriptor);
	}
}
