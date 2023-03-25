package ftbsc.lll.proxies.impl;

import ftbsc.lll.proxies.ProxyType;
import ftbsc.lll.proxies.QualifiableProxy;

/**
 * A container for information about a package.
 * @since 0.4.0
 */
public class PackageProxy extends QualifiableProxy {

	/**
	 * The {@link PackageProxy} representing the root package.
	 */
	public static final PackageProxy ROOT = new PackageProxy(null, "");

	/**
	 * The protected constructor, called only from {@link PackageProxy#from(String)}.
	 * @param parent the {@link PackageProxy} representing the parent
	 * @param fqn the fully-qualified name of this package
	 */
	protected PackageProxy(PackageProxy parent, String fqn) {
		super(null, 0, parent, fqn, ProxyType.PACKAGE);
	}

	/**
	 * Builds a {@link PackageProxy} from its fully-qualified name.
	 * @param fqn the fully-qualified name of the package
	 * @return the built {@link PackageProxy}
	 */
	protected static PackageProxy from(String fqn) {
		if(fqn == null || fqn.equals("")) return ROOT;
		return new PackageProxy(from(extractParentFromFQN(fqn)), fqn);
	}

	/**
	 * Builds a {@link PackageProxy} from a reflective {@link Package} object.
	 * @param p the {@link Package} object
	 * @return the built {@link PackageProxy}
	 */
	protected static PackageProxy from(Package p) {
		return from(extractParentFromFQN(p.getName()));
	}

	/**
	 * Indicates whether the given object is a proxy for the same element as this.
	 * @param obj the object to perform
	 * @return true if it's equal
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof PackageProxy && super.equals(obj);
	}
}
