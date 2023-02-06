package ftbsc.lll;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public interface IInjector {

	/**
	 * @return name of injector, for logging
	 */
	String name();

	/**
	 * @return reason for patching for this injector, for loggin
	 */
	default String reason() { return ""; }

	/**
	 * This is used by the Launch Plugin to identify which classes should be
	 *  altered, and on which classes this injector should operate.
	 *
	 *  Class name should be dot-separated, for example "net.minecraft.client.Minecraft"
	 *
	 * @return target class to operate onto
	 */
	String targetClass();

	/**
	 * This is used by the Launch Plugin to identify which methods to provide
	 *  to this injector for patching. It should return the Searge name of wanted function.
	 *  example: "func_71407_l", which is "tick()" on "Minecraft" class in 1.16.5
	 *
	 * @return target method name to operate onto
	 */
	String methodName();

	/**
	 * This is used by the Launch Plugin to identify which methods to provide
	 *  to this injector for patching. It should return the method descriptor, with
	 *  parameters and return types. example: "()V" for void parameters and return.
	 *
	 *  TODO better example...
	 *
	 * @return target method name to operate onto
	 */
	String methodDesc();

	/**
	 * Once the Launch Plugin has identified classes and methods for injectors,
	 *  this method will be called providing the correct class and method nodes for patching.
	 *
	 * @param clazz  class node which is being patched
	 * @param method main method node of requested function for patching
	 */
	void inject(ClassNode clazz, MethodNode method);
}

