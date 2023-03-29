package ftbsc.lll.proxies.impl;

import ftbsc.lll.proxies.AbstractProxy;
import ftbsc.lll.proxies.ProxyType;
import ftbsc.lll.proxies.QualifiableProxy;
import org.objectweb.asm.Type;

import java.lang.reflect.Modifier;

import static ftbsc.lll.tools.DescriptorBuilder.nameToDescriptor;

/**
 * A container for information about classes to be used
 * in ASM patching.
 * @since 0.4.0
 */
public class TypeProxy extends QualifiableProxy {
	/**
	 * Whether this proxy represents a primitive.
	 */
	public final boolean primitive;

	/**
	 * Protected constructor, called only from the builders.
	 * @param name the name of the class
	 * @param descriptor the descriptor of the class
	 * @param modifiers the modifiers of the class
	 * @param parent the package containing this class
	 * @param primitive whether the proxy is a primitive
	 */
	protected TypeProxy(String name, String descriptor, int modifiers, String parent, boolean primitive) {
		super(
			descriptor,
			modifiers,
			PackageProxy.from(parent),
			parent == null ? name : String.format("%s.%s", parent, name),
			ProxyType.TYPE
		);
		this.primitive = primitive;
	}

	/**
	 * Protected constructor, called only from the builders.
	 * @param name the name of the class
	 * @param descriptor the descriptor of the element
	 * @param modifiers the modifiers of the class
	 * @param primitive whether the proxy is a primitive
	 * @param containerClass the FQN of the parent class of the class
	 */
	protected TypeProxy(String name, String descriptor, int modifiers, TypeProxy containerClass, boolean primitive) {
		super(
			descriptor,
			modifiers,
			containerClass,
			String.format("%s$%s", containerClass.fullyQualifiedName, name),
			ProxyType.TYPE
		);
		this.primitive = primitive;
	}

	/**
	 * Builds a {@link TypeProxy} from a {@link Type} and modifiers.
	 * @param type the {@link Type} representing this Class
	 * @param modifiers the modifiers of the class
	 * @return the built {@link TypeProxy}
	 */
	public static TypeProxy from(Type type, int modifiers) {
		while(type.getSort() == Type.ARRAY)
			type = type.getElementType();
		boolean primitive = type.getSort() < Type.ARRAY;
		String fqn = primitive ? type.getClassName() : type.getInternalName().replace('/', '.');
		String simpleName = extractSimpleNameFromFQN(fqn);
		String parent = extractParentFromFQN(fqn);
		if(fqn.contains("$") && parent != null)
			return new TypeProxy(simpleName, type.getDescriptor(), modifiers, from(parent, 0, Modifier.PUBLIC), primitive);
		else return new TypeProxy(simpleName, type.getDescriptor(), modifiers, parent, primitive);
	}

	/**
	 * Builds a {@link TypeProxy} given only the fully-qualified name and modifiers.
	 * If present, parent classes will be assumed to have {@code public} as their
	 * only modifier.
	 * @param fqn the fully qualified name of the desired class
	 * @param arrayLevel the array level for this type
	 * @param modifiers the access modifiers of the desired class
	 * @return the built {@link TypeProxy}
	 */
	public static TypeProxy from(String fqn, int arrayLevel, int modifiers) {
		return from(Type.getType(nameToDescriptor(fqn, arrayLevel)), modifiers);
	}

	/**
	 * Builds a {@link TypeProxy} from a {@link Class} object.
	 * @param clazz the {@link Class} object representing the target class
	 * @return the built {@link TypeProxy}
	 */
	public static TypeProxy from(Class<?> clazz) {
		Class<?> parentClass = clazz.getEnclosingClass();
		if(parentClass == null)
			return new TypeProxy(
				clazz.getSimpleName(),
				Type.getDescriptor(clazz),
				clazz.getModifiers(),
				clazz.getPackage().getName(),
				clazz.isPrimitive()
			);
		else
			return new TypeProxy(
				clazz.getSimpleName(),
				Type.getDescriptor(clazz),
				clazz.getModifiers(),
				from(parentClass),
				clazz.isPrimitive()
			);
	}

	/**
	 * Indicates whether the given object is a proxy for the same element as this.
	 * @param obj the object to perform
	 * @return true if it's equal
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof TypeProxy && super.equals(obj);
	}
}