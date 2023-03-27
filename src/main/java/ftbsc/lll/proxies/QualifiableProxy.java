package ftbsc.lll.proxies;

/**
 * A proxy for elements who have a fully-qualified name.
 * @since 0.4.0
 */
public abstract class QualifiableProxy extends AbstractProxy {
	/**
	 * The fully-qualified name of the element represented by this proxy.
	 */
	public final String fullyQualifiedName;

	/**
	 * The "internal name" (fully-qualified with slashes) of the element
	 * represented by this proxy.
	 */
	public final String internalName;

	/**
	 * The protected constructor, should be called by all classes extending this in theirs.
	 * @param descriptor the descriptor for the element
	 * @param modifiers the modifiers, as a packed int
	 * @param parent the {@link QualifiableProxy} representing the parent of this element
	 * @param fullyQualifiedName the FQN of the element
	 * @param proxyType the {@link ProxyType} being represented here
	 */
	protected QualifiableProxy(String descriptor, int modifiers, QualifiableProxy parent, String fullyQualifiedName, ProxyType proxyType) {
		super(extractSimpleNameFromFQN(fullyQualifiedName), descriptor, modifiers, parent, proxyType);
		this.fullyQualifiedName = fullyQualifiedName;
		this.internalName = this.fullyQualifiedName.replace('.', '/');
	}

	/**
	 * Returns a {@link String} containing the FQN of the parent element
	 * to this, which may represent a package or class.
	 * @param fqn the fully qualified name of the element
	 * @return the parent, or null if the parent was the root element
	 */
	protected static String extractParentFromFQN(String fqn) {
		String lastSeparator = fqn.contains("$") ? "$" : ".";
		int pos = fqn.lastIndexOf(lastSeparator);
		if(pos == -1) return null;
		return fqn.substring(0, pos);
	}

	/**
	 * Returns a {@link String} containing the simple name of the element.
	 * @param fqn the fully qualified name of the element
	 * @return the simple name
	 */
	protected static String extractSimpleNameFromFQN(String fqn) {
		String lastSeparator = fqn.contains("$") ? "\\$" : "\\.";
		String[] split = fqn.split(lastSeparator);
		if(split.length == 1) return fqn;
		else return split[split.length - 1];
	}

	/**
	 * Indicates whether the given object is a proxy for the same element as this.
	 * @param obj the object to perform
	 * @return true if it's equal
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof QualifiableProxy && super.equals(obj) && ((QualifiableProxy) obj).fullyQualifiedName.equals(fullyQualifiedName);
	}
}
