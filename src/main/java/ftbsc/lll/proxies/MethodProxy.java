package ftbsc.lll.proxies;

import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static ftbsc.lll.tools.DescriptorBuilder.nameToDescriptor;

/**
 * A container for information about class methods to be used
 * in ASM patching.
 * @since 0.3.0
 */
public class MethodProxy extends AbstractProxy {

	/**
	 * The parameters of the method.
	 */
	public final Type[] parameters;

	/**
	 * The return type of the method.
	 */
	public final Type returnType;

	/**
	 * A public constructor, builds a proxy from a {@link Method}
	 * obtained from reflection.
	 * @param m the {@link Method} object corresponding to this.
	 */
	public MethodProxy(Method m) {
		super(m.getName(), Type.getType(m), m.getModifiers(), Type.getInternalName(m.getDeclaringClass()));
		Type mt = Type.getType(m);
		this.parameters = mt.getArgumentTypes();
		this.returnType = mt.getReturnType();
	}

	/**
	 * A protected constructor, called only from the builder.
	 * @param name the name of the method
	 * @param modifiers the modifiers of the method
	 * @param parent the FQN of the parent class of the method
	 * @param parameters the parameters of the method
	 * @param returnType the return type of the method
	 */
	protected MethodProxy(String name, int modifiers, String parent, Type[] parameters, Type returnType) {
		super(name, Type.getMethodType(returnType, parameters), modifiers, parent);
		this.parameters = parameters;
		this.returnType = returnType;
	}

	/**
	 * Returns a new instance of {@link MethodProxy.Builder}.
	 * @param name the name of the method
	 * @return the builder object for method proxies
	 */
	public static Builder builder(String name) {
		return new Builder(name);
	}

	/**
	 * A builder object for {@link MethodProxy}.
	 */
	public static class Builder extends AbstractProxy.Builder<MethodProxy> {
		/**
		 * The parameters of the method.
		 */
		private final List<Type> parameters;

		/**
		 * The return type of the method. Defaults to void.
		 */
		private Type returnType;

		/**
		 * The constructor of the builder, used only internally.
		 * @param name the name of the method
		 */
		Builder(String name) {
			super(name);
			this.parameters = new ArrayList<>();
			this.returnType = Type.getType(void.class);
		}

		/**
		 * Adds a parameter of a given type.
		 * @param fqn the fully qualified name of the parameter type
		 * @param arrayLevel the array level of the parameter type
		 * @return the builder's state after the change
		 */
		public Builder addParameter(String fqn, int arrayLevel) {
			this.parameters.add(Type.getType(nameToDescriptor(fqn, arrayLevel)));
			return this;
		}

		/**
		 * Adds a parameter of a given type.
		 * @param paramType the {@link Class} object corresponding to
		 *                  the parameter type.
		 * @return the builder's state after the change
		 */
		public Builder addParameter(Class<?> paramType) {
			this.parameters.add(Type.getType(paramType));
			return this;
		}

		/**
		 * Sets the return type to the given type.
		 * @param fqn the fully qualified name of the return type
		 * @param arrayLevel the array level of the return type
		 * @return the builder's state after the change
		 */
		public Builder setReturnType(String fqn, int arrayLevel) {
			this.returnType = Type.getType(nameToDescriptor(fqn, arrayLevel));
			return this;
		}

		/**
		 * Sets the return type to the given type.
		 * @param returnType the {@link Class} object corresponding to
		 *                   the return type
		 * @return the builder's state after the change
		 */
		public Builder setReturnType(Class<?> returnType) {
			this.returnType = Type.getType(returnType);
			return this;
		}

		/**
		 * Builds a {@link MethodProxy} of the given kind.
		 * @return the built {@link MethodProxy}
		 */
		@Override
		public MethodProxy build() {
			return new MethodProxy(name, modifiers, parent, parameters.toArray(new Type[0]), returnType);
		}
	}
}
