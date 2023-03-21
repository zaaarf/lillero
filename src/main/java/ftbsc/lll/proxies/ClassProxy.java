package ftbsc.lll.proxies;

import org.objectweb.asm.Type;

/**
 * A container for information about classes to be used
 * in ASM patching.
 * @since 0.4.0
 */
public class ClassProxy extends AbstractProxy {

	/**
	 * The fully-qualified name of the class represented by this proxy.
	 */
	public final String fqn;

	/**
	 * The {@link ClassProxy} representing the class which contains the
	 * class represented by this proxy. May be null if the class represented
	 * by this proxy is not an inner class.
	 */
	public final ClassProxy containerClass;

	/**
	 * Protected constructor, called only from the builder.
	 * @param name the name of the class
	 * @param type the {@link Type} of the class
	 * @param modifiers the modifiers of the class
	 * @param parent the FQN of the parent class of the class
	 */
	protected ClassProxy(String name, Type type, int modifiers, String parent) {
		super(name, type, modifiers, parent);
		this.fqn = String.format("%s.%s", name, parent);
		this.containerClass = null;
	}

	/**
	 * Protected constructor, called only from the builder.
	 * @param name the name of the class
	 * @param type the {@link Type} of the class
	 * @param modifiers the modifiers of the class
	 * @param containerClass the FQN of the parent class of the class
	 */
	protected ClassProxy(String name, Type type, int modifiers, ClassProxy containerClass) {
		super(name, type, modifiers, containerClass.fqn);
		this.fqn = String.format("%s$%s", name, parent);
		this.containerClass = containerClass;
	}

	/**
	 * Builds a {@link ClassProxy} given only the fully-qualified name and modifiers.
	 * @param fqn the fully qualified name of the desired class
	 * @param modifiers the access modifiers of the desired class
	 * @return the built {@link ClassProxy}
	 */
	protected static ClassProxy from(String fqn, int modifiers) {
		Type type = Type.getObjectType(fqn.replace('.', '/'));
		if(fqn.contains("$")) {
			String[] split = fqn.split("\\$");
			String simpleName = split[split.length - 1];
			ClassProxy parentClass = from(fqn.replace("$" + simpleName, ""), 0);
			return new ClassProxy(simpleName, type, modifiers, parentClass);
		} else {
			String[] split = fqn.split("\\.");
			String simpleName = split[split.length - 1];
			String parent = fqn.replace("." + simpleName, "");
			return new ClassProxy(simpleName, type, modifiers, parent);
		}
	}

	/**
	 * Builds a {@link ClassProxy} from a {@link Class} object.
	 * @param clazz the {@link Class} object representing the target class
	 * @return the built {@link ClassProxy}
	 */
	protected static ClassProxy from(Class<?> clazz) {
		if(clazz.getEnclosingClass() == null)
			return new ClassProxy(
				clazz.getSimpleName(),
				Type.getType(clazz),
				clazz.getModifiers(),
				clazz.getPackage().getName()
			);
		else
			return new ClassProxy(
				clazz.getSimpleName(),
				Type.getType(clazz),
				clazz.getModifiers(),
				from(clazz.getEnclosingClass())
			);
	}

	/**
	 * Returns a new instance of {@link ClassProxy.Builder}.
	 * @param name the name of the class
	 * @return the builder object for class proxies
	 */
	public static Builder builder(String name) {
		return new Builder(name);
	}

	/**
	 * A builder object for {@link ClassProxy}.
	 */
	public static class Builder extends AbstractProxy.Builder<ClassProxy> {
		
		private ClassProxy containerClass;
		
		/**
		 * The constructor of the builder, used only internally.
		 * @param name the "simple name" of the class
		 */
		Builder(String name) {
			super(name);
			this.containerClass = null;
		}

		/**
		 * Sets this class as an inner class and sets the containing
		 * class to the given class object.
		 * @param containerClass the {@link Class} representing the
		 *                       container class
		 * @return the builder's state after the change
		 */
		public Builder setContainerClass(Class<?> containerClass) {
			this.containerClass = ClassProxy.from(containerClass);
			return this;
		}

		/**
		 * Sets this class as an inner class and sets the containing
		 * class to the given proxy.
		 * @param containerClass the {@link ClassProxy} representing
		 *                         the container class
		 * @return the builder's state after the change
		 */
		public Builder setContainerClass(ClassProxy containerClass) {
			this.containerClass = containerClass;
			return this;
		}

		/**
		 * Sets this class as an inner class and builds a {@link ClassProxy}
		 * from the given parent and modifiers.
		 * @param parentFQN  the fully qualified name of the parent
		 * @return the builder's state after the change
		 */
		public Builder setParent(String parentFQN, int modifiers) {
			return this.setContainerClass(ClassProxy.from(parentFQN, modifiers));
		}

		/**
		 * Sets this class as an inner class and builds a {@link ClassProxy}
		 * from the given parent.
		 * @param parentFQN  the fully qualified name of the parent
		 * @return the builder's state after the change
		 */
		@Override
		public Builder setParent(String parentFQN) {
			return this.setParent(parentFQN, 0);
		}

		/**
		 * Builds a {@link ClassProxy} of the given kind.
		 * @return the built {@link ClassProxy}
		 */
		@Override
		public ClassProxy build() {
			if(this.containerClass == null)
				return new ClassProxy(this.name, this.type, this.modifiers, this.parent);
			else return new ClassProxy(this.name, this.type, this.modifiers, this.containerClass);
		}
	}
}