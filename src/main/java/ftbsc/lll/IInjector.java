package ftbsc.lll;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Patch classes should implement this interface and be declared as services in
 * the META-INF/services folder (or through modules in Java 9+, but only Java 8
 * is officially supported).
 */

public interface IInjector {

	/**
	 * @return name of injector, for logging
	 */
	String name();

	/**
	 * @return reason for this patch, for logging
	 */
	default String reason() { return "No reason specified"; }

	/**
	 * This is used to identify which classes should be altered, and on which class
	 * should this injector operate.
	 * Class name should be dot-separated, for example "net.minecraft.client.Minecraft".
	 * @return class to transform
	 */
	String targetClass();

	/**
	 * This is used to identify the method to transform within the class.
	 * It should return the name of target.
	 * @return method to transform
	 */
	String methodName();

	/**
	 * This should return the target method's descriptor.
	 * Methods in Java may have the same name but different parameters: a descriptor
	 * compiles that information, as well as the return type, in as little space as
	 * possible.
	 * Examples:
	 * <ul>
	 *  <li>(IF)V - returns void, takes in int and float</li>
	 *  <li>(Ljava/lang/Object;)I - returns int, takes in a java.lang.Object</li>
	 *  <li>(ILjava/lang/String;)[I - returns int[], takes in an int and a String</li>
	 * </ul>
	 * See ASM's <a href="https://asm.ow2.io/asm4-guide.pdf">documentation</a> for a more detailed explanation.
	 * @return descriptor of method to target.
	 */
	String methodDesc();

	/**
	 * This method is to be called by the launcher after identifying the right class and
	 * method to patch. The overriding method should contain the logic for actually
	 * pathing.
	 * @param clazz  the {@link ClassNode} currently being patched
	 * @param method the {@link MethodNode} of method currently being patched
	 */
	void inject(ClassNode clazz, MethodNode method);
}

