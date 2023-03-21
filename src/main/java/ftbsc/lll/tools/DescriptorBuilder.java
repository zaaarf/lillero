package ftbsc.lll.tools;

import org.objectweb.asm.Type;

import java.util.ArrayList;

/**
 * Builds a method descriptor for you.
 * See the <a href="https://asm.ow2.io/asm4-guide.pdf">documentation</a> to better understand what this is.
 * Parameters must be given in a specific order.
 * Return type should always be specified for clarity, but defaults to void.
 */
public class DescriptorBuilder {
	/**
	 * The descriptor of the return type.
	 */
	private String returnType;

	/**
	 * The descriptors of the parameters.
	 */
	private final ArrayList<String> params;

	/**
	 * Public constructor.
	 * Initialises default values.
	 */
	public DescriptorBuilder() {
		this.returnType = Type.getDescriptor(void.class);
		this.params = new ArrayList<>();
	}

	/**
	 * Sets the return type to the given type.
	 * @implNote Passing a {@link Class} may cause problems if used with objects outside
	 *           the Java SDK. Pass the fully qualified name as a {@link String} rather
	 *           than the {@link Class} object for non-standard types (such as Minecraft
	 *           classes).
	 * @param returnType the Class object corresponding to the return type
	 * @return the builder's state after the change
	 */
	public DescriptorBuilder setReturnType(Class<?> returnType) {
		this.returnType = Type.getDescriptor(returnType);
		return this;
	}

	/**
	 * Sets the return type to the Object specified here as a fully
	 * qualified name. Example: java.lang.String.
	 * No validity checks are performed: it's up to the user to ensure the name is correct.
	 * @param returnType the fully qualified name of the desired Object.
	 * @return the builder's state after the change
	 */
	public DescriptorBuilder setReturnType(String returnType) {
		return this.setReturnType(returnType, 0);
	}

	/**
	 * Sets the return type to the Object specified here as a fully
	 * qualified name (example: java.lang.String), with the specified array level.
	 * No validity checks are performed: it's up to the user to ensure the name is correct.
	 * @param returnType the fully qualified name of the desired Object.
	 * @param arrayLevel how many levels of array are there
	 *                   (example: String is 0, String[] is 1, String[][] is 2, etc.)
	 * @return the builder's state after the change
	 */
	public DescriptorBuilder setReturnType(String returnType, int arrayLevel) {
		this.returnType = nameToDescriptor(returnType, arrayLevel);
		return this;
	}

	/**
	 * Adds a parameter of the given class type to the method.
	 * Parameter order matters.
	 * @implNote Passing a {@link Class} may cause problems if used with objects outside
	 *           the Java SDK. Pass the fully qualified name as a {@link String} rather
	 *           than the {@link Class} object for non-standard types (such as Minecraft
	 *           classes).
	 * @param param the Class object corresponding to the parameter
	 * @return the builder's state after the change
	 */
	public DescriptorBuilder addParameter(Class<?> param) {
		this.params.add(Type.getDescriptor(param));
		return this;
	}

	/**
	 * Adds a parameter with the type specified by the given fully
	 * qualified name to the method. Example: java.lang.String.
	 * Parameter order matters.
	 * No validity checks are performed: it's up to the user to ensure the name is correct.
	 * @param param the fully qualified name of the parameter type
	 * @return the builder's state after the change
	 */
	public DescriptorBuilder addParameter(String param) {
		return this.addParameter(param, 0);
	}

	/**
	 * Adds a parameter with the type specified by the given fully
	 * qualified name (example: java.lang.String) to the method, with
	 * the specified array level.
	 * Parameter order matters.
	 * No validity checks are performed: it's up to the user to ensure the name is correct.
	 * @param param the fully qualified name of the parameter type
	 * @param arrayLevel how many levels of array are there
	 *                   (example: String is 0, String[] is 1, String[][] is 2, etc.)
	 * @return the builder's state after the change
	 */
	public DescriptorBuilder addParameter(String param, int arrayLevel) {
		this.params.add(nameToDescriptor(param, arrayLevel));
		return this;
	}

	/**
	 * Builds the descriptor into a string.
	 * Example result: int m(Object[] o) -> ([Ljava/lang/Object;)I
	 * @return the resulting descriptor
	 */
	public String build() {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		for(String p : params)
			sb.append(p);
		sb.append(')').append(returnType);
		return sb.toString();
	}

	/**
	 * Converts a fully qualified name and array level to a descriptor.
	 * @param name the fully qualified name of the object type
	 * @param arrayLevel how many levels of array are there
	 *                   (example: String is 0, String[] is 1, String[][] is 2, etc.)
	 * @return object descriptor
	 */
	public static String nameToDescriptor(String name, int arrayLevel) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < arrayLevel; i++)
			sb.append('[');
		sb.append('L').append(name.replace('.', '/')).append(';');
		return sb.toString();
	}
}
