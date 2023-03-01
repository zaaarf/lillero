package ftbsc.lll.proxies;

import ftbsc.lll.tools.DescriptorBuilder;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;

/**
 * A container for information about class fields to be used
 * in ASM patching.
 * @since 0.3.0
 */
public class FieldProxy extends AbstractProxy {

	/**
	 * The descriptor of the field's type.
	 */
	private final String typeDescriptor;

	/**
	 * A public constructor, builds a proxy from a {@link Field}
	 * obtained from reflection.
	 * @param f the {@link Field} object corresponding to this.
	 */
	public FieldProxy(Field f) {
		super(f.getName(), f.getModifiers(), Type.getInternalName(f.getDeclaringClass()));
		this.typeDescriptor = Type.getDescriptor(f.getType());
	}

	/**
	 * A protected constructor, called only from the builder.
	 * @param srgName the SRG name of the field
	 * @param modifiers the modifiers of the field
	 * @param parent the FQN of the parent class of the field
	 * @param typeDescriptor the type descriptor of the field
	 */
	FieldProxy(String srgName, int modifiers, String parent, String typeDescriptor) {
		super(srgName, modifiers, parent);
		this.typeDescriptor = typeDescriptor;
	}

	/**
	 * @return the field's type descriptor
	 */
	@Override
	public String getDescriptor() {
		return typeDescriptor;
	}

	/**
	 * Returns a new instance of {@link FieldProxy.Builder}.
	 * @param srgName the SRG name of the field
	 * @return the builder object for field proxies
	 */
	public static Builder builder(String srgName) {
		return new Builder(srgName);
	}

	public static class Builder extends AbstractProxy.Builder<FieldProxy> {
		/**
		 * The descriptor of the field's type.
		 */
		private String typeDescriptor;

		/**
		 * The constructor of the builder, used only internally.
		 * @param srgName the SRG name of the field
		 */
		Builder(String srgName) {
			super(srgName);
		}

		/**
		 * Sets the descriptor of the field type to the given {@link String}.
		 * @param typeDescriptor the descriptor of the field type
		 * @return the builder's state after the change
		 */
		public Builder setDescriptor(String typeDescriptor) {
			this.typeDescriptor = typeDescriptor;
			return this;
		}

		/**
		 * Sets the descriptor of the field type to match the give {@link Class}.
		 * @param fqn the fully qualified name of the field type
		 * @param arrayLevel the array level of the field type
		 * @return the builder's state after the change
		 */
		public Builder setType(String fqn, int arrayLevel) {
			this.typeDescriptor = DescriptorBuilder.nameToDescriptor(fqn, arrayLevel);
			return this;
		}

		/**
		 * Sets the descriptor of the field type to match the give {@link Class}.
		 * @param type a {@link Class} object representing the field type
		 * @return the builder's state after the change
		 */
		public Builder setType(Class<?> type) {
			this.typeDescriptor = Type.getDescriptor(type);
			return this;
		}

		/**
		 * Builds a {@link FieldProxy} of the given kind.
		 * @return the built {@link FieldProxy}
		 */
		@Override
		public FieldProxy build() {
			return new FieldProxy(this.srgName, this.modifiers, this.parent, this.typeDescriptor);
		}
	}
}
