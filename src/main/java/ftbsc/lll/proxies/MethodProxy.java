package ftbsc.lll.proxies;

import ftbsc.lll.tools.DescriptorBuilder;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * A container for information about class methods to be used
 * in ASM patching.
 * @since 0.3.0
 */
public class MethodProxy extends AbstractProxy {

	/**
	 * The parameters of the method.
	 * It holds fully qualified names for objects, and {@link Class}
	 * objects for primitives.
	 */
	private final Object[] parameters;

	/**
	 * The return type of the method.
	 * It contains if it's an object, or a {@link Class}
	 * object for primitives.
	 */
	private final Object returnType;

	/**
	 * Caches the the descriptor after generating it once for
	 * performance.
	 */
	private String descriptorCache;

	/**
	 * A public constructor, builds a proxy from a {@link Method}
	 * obtained from reflection.
	 * @param m the {@link Method} object corresponding to this.
	 */
	public MethodProxy(Method m) {
		super(m.getName(), m.getModifiers(), Type.getInternalName(m.getDeclaringClass()));
		List<Object> parameters = new ArrayList<>();
		for(Class<?> p : m.getParameterTypes())
			parameters.add(p.isPrimitive() ? p	: new TypeContainer(p));
		this.parameters = parameters.toArray();
		Class<?> returnType = m.getReturnType();
		this.returnType = returnType.isPrimitive() ? returnType	: new TypeContainer(returnType);
	}

	/**
	 * A protected constructor, called only from the builder.
	 * @param name the name of the method
	 * @param modifiers the modifiers of the method
	 * @param parent the FQN of the parent class of the method
	 * @param parameters the parameters of the method
	 * @param returnType the return type of the method
	 */
	protected MethodProxy(String name, int modifiers, String parent, Object[] parameters, Object returnType) {
		super(name, modifiers, parent);
		this.parameters = parameters;
		this.returnType = returnType;
		this.descriptorCache = null;
	}

	/**
	 * Builds (or returns from cache if present)
	 * the method's descriptor.
	 * @return the method's descriptor
	 */
	@Override
	public String getDescriptor() {
		if(this.descriptorCache != null)
			return this.descriptorCache;
		DescriptorBuilder b = new DescriptorBuilder();
		for(Object p : this.parameters)
			addTypeToDescriptorBuilder(b, p, false);
		addTypeToDescriptorBuilder(b, this.returnType, true);
		this.descriptorCache = b.build();
		return this.descriptorCache;
	}

	/**
	 * A static method used internally to detect and correctly insert a
	 * {@link TypeContainer} into a {@link DescriptorBuilder}.
	 * @param b the {@link DescriptorBuilder}
	 * @param p the {@link TypeContainer}
	 * @param isReturnType whether it should be inserted as a return type
	 */
	private static void addTypeToDescriptorBuilder(DescriptorBuilder b, Object p, boolean isReturnType) {
		if(p instanceof TypeContainer) {
			TypeContainer param = (TypeContainer) p;
			if(isReturnType)
				b.setReturnType(param.fqn, param.arrayLevel);
			else b.addParameter(param.fqn, param.arrayLevel);
		} else {
			if(isReturnType)
				b.setReturnType((Class<?>) p);
			else b.addParameter((Class<?>) p);
		}
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
		private final List<Object> parameters;

		/**
		 * The return type of the method. Defaults to void.
		 */
		private Object returnType;

		/**
		 * The constructor of the builder, used only internally.
		 * @param name the name of the method
		 */
		Builder(String name) {
			super(name);
			this.parameters = new ArrayList<>();
			this.returnType = void.class;
		}

		/**
		 * Adds a parameter of a given type.
		 * @param fqn the fully qualified name of the parameter type
		 * @param arrayLevel the array level of the parameter type
		 * @return the builder's state after the change
		 */
		public Builder addParameter(String fqn, int arrayLevel) {
			this.parameters.add(new TypeContainer(fqn, arrayLevel));
			return this;
		}

		/**
		 * Adds a parameter of a given type.
		 * @param paramType the {@link Class} object corresponding to
		 *                  the parameter type.
		 * @return the builder's state after the change
		 */
		public Builder addParameter(Class<?> paramType) {
			this.parameters.add(paramType);
			return this;
		}

		/**
		 * Sets the return type to the given type.
		 * @param fqn the fully qualified name of the return type
		 * @param arrayLevel the array level of the return type
		 * @return the builder's state after the change
		 */
		public Builder setReturnType(String fqn, int arrayLevel) {
			this.returnType = new TypeContainer(fqn, arrayLevel);
			return this;
		}

		/**
		 * Sets the return type to the given type.
		 * @param returnType the {@link Class} object corresponding to
		 *                   the return type
		 * @return the builder's state after the change
		 */
		public Builder setReturnType(Class<?> returnType) {
			this.returnType = returnType;
			return this;
		}

		/**
		 * Builds a {@link MethodProxy} of the given kind.
		 * @return the built {@link MethodProxy}
		 */
		@Override
		public MethodProxy build() {
			return new MethodProxy(name, modifiers, parent, parameters.toArray(), returnType);
		}
	}

	/**
	 * A container class, holding information about a given type.
	 */
	protected static class TypeContainer {
		/**
		 * The fully qualified name of the type.
		 */
		public final String fqn;

		/**
		 * The array level of the type.
		 */
		public final int arrayLevel;

		/**
		 * Public constructor for the class.
		 * @param fqn the fully qualified name of the type
		 * @param arrayLevel the array level of the type
		 */
		public TypeContainer(String fqn, int arrayLevel) {
			this.fqn = fqn;
			this.arrayLevel = arrayLevel;
		}

		/**
		 * Public constructor for the class, extracting the
		 * necessary information from a {@link Class} object.
		 * @param clazz the class object
		 */
		public TypeContainer(Class<?> clazz) {
			int arrayLevel = 0;
			while(clazz.isArray()) {
				arrayLevel++;
				clazz = clazz.getComponentType();
			}
			this.arrayLevel = arrayLevel;
			this.fqn = clazz.getCanonicalName();
		}
	}
}
