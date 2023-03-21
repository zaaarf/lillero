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
		super(f.getName(), Type.getType(f.getType()), f.getModifiers(), Type.getInternalName(f.getDeclaringClass()));
	}

	/**
	 * Protected constructor, called only from the builder.
	 * @param name the name of the field
	 * @param type the {@link Type} of the field
	 * @param modifiers the modifiers of the field
	 * @param parent the FQN of the parent class of the field
	 */
	protected FieldProxy(String name, Type type, int modifiers, String parent) {
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
		 * Builds a {@link FieldProxy} of the given kind.
		 * @return the built {@link FieldProxy}
		 */
		@Override
		public FieldProxy build() {
			return new FieldProxy(this.name, this.type, this.modifiers, this.parent);
		}
	}
}
