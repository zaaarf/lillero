package ftbsc.lll.proxies;

/**
 * Abstract proxy class, implementing common aspects
 * of {@link MethodProxy} and {@link FieldProxy}.
 * @since 0.3.0
 */
public abstract class AbstractProxy {

	/**
	 * The name of the corresponding class member.
	 */
	private final String name;

	/**
	 * The fully qualified name (i.e. java.lang.String) of
	 * the parent class.
	 */
	private final String parent;

	/**
	 * The modifiers of the member, as a packed int.
	 * @see java.lang.reflect.Modifier
	 */
	private final int modifiers;

	/**
	 * @return the name of the item
	 */
	public String getname() {
		return this.name;
	}

	/**
	 * @return the modifiers of the member, as a packed int
	 * @see java.lang.reflect.Modifier
	 */
	public int getModifiers() {
		return this.modifiers;
	}

	/**
	 * @return the fully qualified name of the parent class
	 */
	public String getParent() {
		return this.parent;
	}

	/**
	 * @return the descriptor of the member
	 */
	public abstract String getDescriptor();

	/**
	 * The private constructor, should be called by all classes extending this in theirs.
	 * @param name the name of the member
	 * @param modifiers the modifiers, as a packed int
	 * @param parent the FQN of the parent class
	 */
	protected AbstractProxy(String name, int modifiers, String parent) {
		this.name = name;
		this.modifiers = modifiers;
		this.parent = parent;
	}

	/**
	 * A Builder for the generic proxy.
	 * @param <T> the type of proxy
	 */
	public abstract static class Builder<T extends AbstractProxy> {

		/**
		 * The name of the member.
		 */
		protected final String name;

		/**
		 * The modifiers of the member, as a packed int.
		 */
		protected int modifiers;

		/**
		 * The fully qualified name of the parent.
		 */
		protected String parent;

		/**
		 * The constructor.
		 * @param name the name of the member
		 */
		protected Builder(String name) {
			this.name = name;
			this.modifiers = 0;
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
		public Builder<T> setModifier(int newModifier) {
			this.modifiers = newModifier;
			return this;
		}

		/**
		 * @return the built proxy object
		 */
		public abstract T build();
	}
}
