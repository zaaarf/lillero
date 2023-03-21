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
	 * The {@link Type} corresponding to this element.
	 */
	public final Type type;

	/**
	 * The fully qualified name (i.e. java.lang.String) of
	 * the parent class.
	 */
	public final String parent;

	/**
	 * The modifiers of the element, as a packed int.
	 * @see java.lang.reflect.Modifier
	 */
	public final int modifiers;

	/**
	 * The private constructor, should be called by all classes extending this in theirs.
	 * @param name the name of the element
	 * @param type the {@link Type} for the element
	 * @param modifiers the modifiers, as a packed int
	 * @param parent the FQN of the parent class
	 */
	protected AbstractProxy(String name, Type type, int modifiers, String parent) {
		this.name = name;
		this.type = type;
		this.modifiers = modifiers;
		this.parent = parent;
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
		protected String parent;

		/**
		 * The {@link Type} corresponding to the element.
		 */
		protected Type type;

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
		 * @param parentFQN the fully qualified name of the parent
		 * @return the current state of the builder
		 */
		public Builder<T> setParent(String parentFQN) {
			this.parent = parentFQN;
			return this;
		}

		/**
		 * @param type the {@link Type} corresponding to the element
		 * @return the current state of the builder
		 */
		public Builder<T> setType(Type type) {
			this.type = type;
			return this;
		}


		/**
		 * Sets {@link Type} for this element from the descriptor, passed as a {@link String}.
		 * @param descriptor the descriptor passed as a {@link String}
		 * @return the builder's state after the change
		 */
		public Builder<T> setDescriptor(String descriptor) {
			return this.setType(Type.getType(descriptor));
		}



		/**
		 * @return the built proxy object
		 */
		public abstract T build();
	}
}
