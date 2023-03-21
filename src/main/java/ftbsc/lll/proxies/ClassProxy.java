package ftbsc.lll.proxies;

import org.objectweb.asm.Type;

import java.lang.reflect.Modifier;

import static ftbsc.lll.tools.DescriptorBuilder.nameToDescriptor;

/**
 * A container for information about classes to be used
 * in ASM patching.
 * @since 0.4.0
 */
public class ClassProxy extends QualifiableProxy {
	/**
	 * Protected constructor, called only from the builder.
	 * @param name the name of the class
	 * @param type the {@link Type} of the class
	 * @param modifiers the modifiers of the class
	 * @param parent the package containing this class
	 */
	protected ClassProxy(String name, Type type, int modifiers, String parent) {
		super(type, modifiers, PackageProxy.from(parent), String.format("%s.%s", name, parent));
	}

	/**
	 * Protected constructor, called only from the builder.
	 * @param name the name of the class
	 * @param type the {@link Type} of the class
	 * @param modifiers the modifiers of the class
	 * @param containerClass the FQN of the parent class of the class
	 */
	protected ClassProxy(String name, Type type, int modifiers, QualifiableProxy containerClass) {
		super(type, modifiers, containerClass, String.format("%s$%s", name, containerClass.fullyQualifiedName));
	}

	/**
	 * Builds a {@link ClassProxy} from a {@link Type} and modifiers.
	 * @param type the {@link Type} representing this Class
	 * @param modifiers the modifiers of the class
	 */
	public static ClassProxy from(Type type, int modifiers) {
		String fqn = type.getInternalName().replace('/', '.');
		String simpleName = extractSimpleNameFromFQN(fqn);
		String parent = extractParentFromFQN(fqn);
		if(fqn.contains("$"))
			return new ClassProxy(simpleName, type, modifiers, from(parent, 0, Modifier.PUBLIC));
		else return new ClassProxy(simpleName, type, modifiers, parent);
	}

	/**
	 * Builds a {@link ClassProxy} given only the fully-qualified name and modifiers.
	 * @param fqn the fully qualified name of the desired class
	 * @param arrayLevel the array level for this type
	 * @param modifiers the access modifiers of the desired class
	 * @implNote If present, parent classes will be assumed to have {@code public} as
	 * 					 their only modifier.
	 * @return the built {@link ClassProxy}
	 */
	protected static ClassProxy from(String fqn, int arrayLevel, int modifiers) {
		return from(Type.getObjectType(nameToDescriptor(fqn, arrayLevel)), modifiers);
	}

	/**
	 * Builds a {@link ClassProxy} from a {@link Class} object.
	 * @param clazz the {@link Class} object representing the target class
	 * @return the built {@link ClassProxy}
	 */
	public static ClassProxy from(Class<?> clazz) {
		Class<?> parentClass = clazz.getEnclosingClass();
		if(parentClass == null)
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
				from(parentClass)
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
	 * Indicates whether the given object is a proxy for the same element as this.
	 * @param obj the object to perform
	 * @return true if it's equal
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof ClassProxy && super.equals(obj);
	}

	/**
	 * A builder object for {@link ClassProxy}.
	 */
	public static class Builder extends AbstractProxy.Builder<ClassProxy> {

		/**
		 * The constructor of the builder, used only internally.
		 * @param name the "simple name" of the class
		 */
		Builder(String name) {
			super(name);
		}

		/**
		 * Sets this class as an inner class and sets the containing
		 * class to the given class object.
		 * @param containerClass the {@link Class} representing the
		 *                       container class
		 * @return the builder's state after the change
		 */
		public Builder setParent(Class<?> containerClass) {
			super.setParent(ClassProxy.from(containerClass));
			return this;
		}

		/**
		 * Sets this class as an inner class and builds a {@link ClassProxy}
		 * from the given parent and modifiers.
		 * @param parentFQN the fully qualified name of the parent
		 * @param modifiers the modifiers of the parent (if it's a class)
		 * @param isParentPackage whether this parent should be interpreted as a package or class
		 * @return the builder's state after the change
		 */
		public Builder setParent(String parentFQN, int modifiers, boolean isParentPackage) {
			super.setParent(isParentPackage ? PackageProxy.from(parentFQN) : ClassProxy.from(parentFQN, 0, modifiers));
			return this;
		}

		/**
		 * Sets this class as an inner class and builds a {@link ClassProxy}
		 * from the given parent.
		 * @param parentFQN the fully qualified name of the parent
		 * @param isParentPackage whether this parent should be interpreted as a package or class
		 * @return the builder's state after the change
		 */
		public Builder setParent(String parentFQN, boolean isParentPackage) {
			return this.setParent(parentFQN, 0, isParentPackage);
		}

		/**
		 * Builds a {@link ClassProxy} of the given kind.
		 * @return the built {@link ClassProxy}
		 */
		@Override
		public ClassProxy build() {
			return new ClassProxy(this.name, this.type, this.modifiers, this.parent);
		}
	}
}