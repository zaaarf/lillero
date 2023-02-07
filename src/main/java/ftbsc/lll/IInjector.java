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
	default String reason() { return ""; }

	/**
	 * This is used by the Launch Plugin to identify which classes should be
	 * altered, and on which classes should this injector operate.
	 * Class name should be dot-separated, for example "net.minecraft.client.Minecraft".
	 * @return class to transform
	 */
	String targetClass();

	/**
	 * This is used by the Launch Plugin to identify the method to transform within
	 * the class. It should return the Searge name of target.
	 * Example: "func_71407_l", which is "tick()" on "Minecraft" class in 1.16.5
	 *
	 * @return method to transform
	 */
	String methodName();

	/**
	 * This should return the target method's descriptor.
	 * Methods in Java may have the same name but different parameters: a descriptor
	 * compiles that information, as well as the return type, in as little space as
	 * possible.
	 * Examples:
	 * (IF)V - returns void, takes in int and float
	 * (Ljava/lang/Object;)I - returns int, takes in a java.lang.Object
	 * (ILjava/lang/String;)[I - returns int[], takes in an int and a String
	 * See <a>https://asm.ow2.io/asm4-guide.pdf</a> for a more detailed explanation.
	 * @return descriptor of method to target.
	 */
	String methodDesc();

	/**
	 * This method will be called once the Launch Plugin has identified the right class and
	 * method to patch. Override this for the actual patching.
	 * @param clazz  class node currently being patched
	 * @param method node of method currently being patched
	 */
	void inject(ClassNode clazz, MethodNode method);
}

