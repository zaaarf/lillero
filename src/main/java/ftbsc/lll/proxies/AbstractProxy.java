package ftbsc.lll.proxies;

import org.objectweb.asm.Type;

/**
 * Abstract proxy class, implementing common aspects
 * of {@link MethodProxy} and {@link FieldProxy}.
 * @since 0.3.0
 */
public abstract class AbstractProxy {

	/**
	 * The name of the corresponding element.
	 */
	public final String name;

	/**
	 * The descriptor for this element.
	 */
	public final String descriptor;

	/**
	 * The fully qualified name (i.e. java.lang.String) of
	 * the parent class.
	 */
	public final QualifiableProxy parent;

	/**
	 * The modifiers of the element, as a packed int.
	 * @see java.lang.reflect.Modifier
	 */
	public final int modifiers;

	/**
	 * The private constructor, should be called by all classes extending this in theirs.
	 * @param name the name of the element
	 * @param descriptor the descriptor for the element
	 * @param modifiers the modifiers, as a packed int
	 * @param parent the FQN of the parent class
	 */
	protected AbstractProxy(String name, String descriptor, int modifiers, QualifiableProxy parent) {
		this.name = name;
		this.descriptor = descriptor;
		this.modifiers = modifiers;
		this.parent = parent;
	}

	/**
	 * Indicates whether the given object is a proxy for the same element as this.
	 * @param obj the object to perform
	 * @return true if it's equal
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof AbstractProxy) {
			AbstractProxy p = (AbstractProxy) obj;
			return p.parent.equals(this.parent)
				&& p.name.equals(this.name)
				&& p.modifiers == this.modifiers
				&& p.descriptor.equals(this.descriptor);
		} else return false;
	}

	/**
	 * A Builder for the generic proxy.
	 * @param <T> the type of proxy
	 */
	public abstract static class Builder<T extends AbstractProxy> {

		/**
		 * The name of the element.
		 */
		protected String name;

		/**
		 * The modifiers of the element, as a packed int.
		 */
		protected int modifiers;

		/**
		 * The fully qualified name of the parent.
		 */
		protected QualifiableProxy parent;

		/**
		 * The descriptor of the element.
		 */
		protected String descriptor;

		/**
		 * The constructor.
		 * @param name the name of the element
		 */
		protected Builder(String name) {
			this.name = name;
			this.modifiers = 0;
		}

		/**
		 * @param newModifier the modifier to add
		 * @return the current state of the builder
		 */
		public Builder<T> addModifier(int newModifier) {
			this.modifiers |= newModifier;
			return this;
		}

		/**
		 * @param newModifier the new modifier value
		 * @return the current state of the builder
		 */
		public Builder<T> setModifiers(int newModifier) {
			this.modifiers = newModifier;
			return this;
		}

		/**
		 * @param parent the {@link QualifiableProxy} representing the parent
		 * @return the current state of the builder
		 */
		public Builder<T> setParent(QualifiableProxy parent) {
			this.parent = parent;
			return this;
		}

		/**
		 * Sets {@link Type} for this element from the descriptor, passed as a {@link String}.
		 * @param descriptor the descriptor passed as a {@link String}
		 * @return the builder's state after the change
		 */
		public Builder<T> setDescriptor(String descriptor) {
			this.descriptor = descriptor;
			return this;
		}

		/**
		 * @param type the {@link Type} corresponding to the element
		 * @return the current state of the builder
		 */
		public Builder<T> setType(Type type) {
			return this.setDescriptor(type.getDescriptor());
		}

		/**
		 * @return the built proxy object
		 */
		public abstract T build();
	}
}
