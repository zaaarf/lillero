package ftbsc.lll.proxies;

import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ftbsc.lll.tools.DescriptorBuilder.nameToDescriptor;

/**
 * A container for information about class methods to be used
 * in ASM patching.
 * @since 0.3.0
 */
public class MethodProxy extends AbstractProxy {

	/**
	 * An array of {@link TypeProxy} each representing the parameters of the method.
	 */
	public final TypeProxy[] parameters;

	/**
	 * The {@link TypeProxy} for the return type of the method.
	 */
	public final TypeProxy returnType;

	/**
	 * A protected constructor, called only from the builder.
	 * @param name the name of the method
	 * @param modifiers the modifiers of the method
	 * @param parent the {@link QualifiableProxy} for the parent
	 * @param parameters the parameters of the method
	 * @param returnType the return type of the method
	 */
	protected MethodProxy(String name, int modifiers, QualifiableProxy parent, Type[] parameters, Type returnType) {
		super(name, Type.getMethodDescriptor(returnType, parameters), modifiers, parent);
		this.parameters = Arrays.stream(parameters)
			.map(t -> TypeProxy.from(t, 0))
			.toArray(TypeProxy[]::new);
		this.returnType = TypeProxy.from(returnType, 0);
	}

	/**
	 * A public constructor, builds a proxy from a {@link Method}
	 * obtained from reflection.
	 * @param m the {@link Method} object corresponding to this.
	 */
	public MethodProxy(Method m) {
		this(m.getName(),
			m.getModifiers(),
			TypeProxy.from(m.getDeclaringClass()),
			Type.getArgumentTypes(m),
			Type.getReturnType(m)
		);
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
	 * Indicates whether the given object is a proxy for the same element as this.
	 * @param obj the object to perform
	 * @return true if it's equal
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof MethodProxy) {
			MethodProxy m = (MethodProxy) obj;
			return super.equals(obj) && m.returnType.equals(this.returnType) && Arrays.equals(m.parameters, this.parameters);
		} else return false;
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
		 * Sets the parent class of this method to the one described by the
		 * fully qualified name and with the given modifiers.
		 * @param parentFQN the fully qualified name of the parent
		 * @param modifiers the modifiers of the parent
		 * @return the builder's state after the change
		 */
		public Builder setParent(String parentFQN, int modifiers) {
			super.setParent(TypeProxy.from(parentFQN, 0, modifiers));
			return this;
		}

		/**
		 * Sets the parent class of this method to the one described by the
		 * fully qualified name.
		 * @param parentFQN the fully qualified name of the parent
		 * @return the builder's state after the change
		 */
		public Builder setParent(String parentFQN) {
			return this.setParent(parentFQN, 0);
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
			return new MethodProxy(
				this.name,
				this.modifiers,
				this.parent,
				this.parameters.toArray(new Type[0]),
				this.returnType);
		}
	}
}
