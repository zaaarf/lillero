package ftbsc.lll.proxies;

import org.objectweb.asm.Type;

import java.lang.reflect.Field;

/**
 * A container for information about class fields to be used
 * in ASM patching.
 * @since 0.3.0
 */
public class FieldProxy extends AbstractProxy {
	/**
	 * A public constructor, builds a proxy from a {@link Field}
	 * obtained from reflection.
	 * @param f the {@link Field} object corresponding to this.
	 */
	public FieldProxy(Field f) {
		super(f.getName(), Type.getType(f.getType()), f.getModifiers(), ClassProxy.from(f.getDeclaringClass()));
	}

	/**
	 * Protected constructor, called only from the builder.
	 * @param name the name of the field
	 * @param type the {@link Type} of the field
	 * @param modifiers the modifiers of the field
	 * @param parent the {@link QualifiableProxy} for the parent
	 */
	protected FieldProxy(String name, Type type, int modifiers, QualifiableProxy parent) {
		super(name, type, modifiers, parent);
	}

	/**
	 * Returns a new instance of {@link FieldProxy.Builder}.
	 * @param name the name of the field
	 * @return the builder object for field proxies
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
		return obj instanceof FieldProxy && super.equals(obj);
	}

	/**
	 * A builder object for {@link FieldProxy}.
	 */
	public static class Builder extends AbstractProxy.Builder<FieldProxy> {
		/**
		 * The constructor of the builder, used only internally.
		 * @param name the name of the field
		 */
		Builder(String name) {
			super(name);
		}

		/**
		 * Sets the parent class of this field to the one described by the
		 * fully qualified name and with the given modifiers.
		 * @param parentFQN the fully qualified name of the parent
		 * @return the builder's state after the change
		 */
		public Builder setParent(String parentFQN, int modifiers) {
			super.setParent(ClassProxy.from(parentFQN, 0, modifiers));
			return this;
		}

		/**
		 * Sets the parent class of this field to the one described by the
		 * fully qualified name.
		 * @param parentFQN the fully qualified name of the parent
		 * @return the builder's state after the change
		 */
		public Builder setParent(String parentFQN) {
			return this.setParent(parentFQN, 0);
		}

		/**
		 * Builds a {@link FieldProxy} of the given kind.
		 * @return the built {@link FieldProxy}
		 */
		@Override
		public FieldProxy build() {
			return new FieldProxy(this.name, this.type, this.modifiers, this.parent);
		}
	}
}
