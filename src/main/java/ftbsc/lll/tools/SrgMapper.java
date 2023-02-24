package ftbsc.lll.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

/**
 * Parses a .tsrg file into a mapper capable of converting from
 * deobfuscated names to srg names.
 * Obviously, it may only be used at runtime if the .tsrg file is
 * included in the resources. However, in that case, I'd recommend
 * using the built-in Forge one and refrain from including an extra
 * resource for no good reason.
 * @since 0.2.0
 */
public class SrgMapper {

	/**
	 * A Map using the deobfuscated names as keys,
	 * holding information for that Srg class as value.
	 */
	private final Map<String, ObfuscationData> mapper = new HashMap<>();

	/**
	 * The public constructor.
	 * Should be passed a Stream of Strings, one representing each line.
	 * Whether they contain line endings or not is irrelevant.
	 * @param str a Stream of strings
	 */
	public SrgMapper(Stream<String> str) {
		AtomicReference<String> currentClass = new AtomicReference<>("");
		str.forEach(l -> {
			if(l.startsWith("\t"))
				mapper.get(currentClass.get()).addMember(l);
			else {
				ObfuscationData s = new ObfuscationData(l);
				currentClass.set(s.mcpName);
				mapper.put(s.mcpName, s);
			}
		});
	}

	/**
	 * Gets the SRG-obfuscated name of the class.
	 * @param mcp the MCP (deobfuscated) internal name of the desired class
	 * @return the SRG name of the class, or null if it wasn't found
	 */
	public String getSrgClass(String mcp)  {
		ObfuscationData data = mapper.get(mcp);
		return data == null ? null : data.srgName;
	}

	/**
	 * Gets the SRG-obfuscated name of a class member (field or method).
	 * The method signature must be in this format: "methodName methodDescriptor",
	 * with a space, because that's how it is in .tsrg files.
	 * @param mcpClass the MCP (deobfuscated) internal name of the container class
	 * @param member the field name or method signature
	 * @return the SRG name of the given member, or null if it wasn't found
	 */
	public String getSrgMember(String mcpClass, String member) {
		ObfuscationData data = mapper.get(mcpClass);
		if(data == null)
			return null;
		return data.members.get(member);
	}

	/**
	 * Used internally. Gets the obfuscation data corresponding to the given SRG name.
	 * @return the desired ObfuscationData object, or null if it wasn't found
	 */
	private ObfuscationData getObfuscationData(String srg) {
		for(ObfuscationData s : mapper.values())
			if(s.srgName.equals(srg))
				return s;
		return null;
	}

	/**
	 * Gets the MCP (deobfuscated) name of the class.
	 * Due to how it's implemented, it's considerably less efficient than its
	 * opposite operation.
	 * @param srg the SRG-obfuscated internal name of the desired class
	 * @return the MCP name of the class, or null if it wasn't found
	 */
	public String getMcpClass(String srg) {
		ObfuscationData data = getObfuscationData(srg);
		return data == null ? null : data.mcpName;
	}

	/**
	 * Gets the MCP (deobfuscated) name of the given member.
	 * Due to how it's implemented, it's considerably less efficient than its
	 * opposite operation.
	 * @param srgClass the SRG-obfuscated internal name of the container class
	 * @param member the field name or method signature
	 * @return the MCP name of the given member, or null if it wasn't found
	 */
	public String getMcpMember(String srgClass, String member) {
		ObfuscationData data = getObfuscationData(srgClass);
		if(data == null)
			return null;
		for(String mcp : data.members.keySet())
			if(data.members.get(mcp).equals(member))
				return mcp;
		return null;
	}

	/**
	 * Private class used internally for storing information about each
	 * class. It's private because there is no good reason anyone would
	 * want to access this outside of this class.
	 */
	private static class ObfuscationData {
		/**
		 * The MCP internal name (FQN with '/' instad of '.') of the class.
		 */
		private final String mcpName;

		/**
		 * The SRG internal name (FQN with '/' instad of '.') of the class.
		 */
		private final String srgName;

		/**
		 * A Map tying each member's deobfuscatede name or signature to its
		 * SRG name.
		 */
		private final Map<String, String> members;


		/**
		 * The constructor. It takes in the line where the class is declared,
		 * which looks something like this:
		 * {@code internal/name/mcp internal/name/srg }
		 * @param s the String represeting the declaration line
		 */
		private ObfuscationData(String s) {
			String[] split = s.trim().split(" ");
			this.mcpName = split[0];
			this.srgName = split[1];
			this.members = new HashMap<>();
		}

		/**
		 * Adds a member to the target class. It takes in the line where the
		 * member is declared.
		 * For fields it looks like this:
		 * {@code fieldMcpName field_srg_name}
		 * For methods it looks like this:
		 * {@code methodName methodDescriptor method_srg_name}
		 * @param s the String representing the declaration line
		 */
		public void addMember(String s) {
			String[] split = s.trim().split(" ");
			if(split.length == 2) //field
				members.put(split[0], split[1]);
			else if (split.length == 3) //method
				members.put(split[0] + split[1], split[2]);
		}
	}
}
