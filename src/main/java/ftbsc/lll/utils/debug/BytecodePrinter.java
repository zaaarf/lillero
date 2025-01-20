package ftbsc.lll.utils.debug;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Consumer;

/**
 * A collection of static methods for debugging by printing the ASM bytecode.
 * These methods are only for debug, so most of the time they should stay unused.
 */
public class BytecodePrinter {

	/**
	 * Used for converting visit events to text, acts pretty much like a buffer in our case.
	 */
	private static final Printer PRINTER = new Textifier();

	/**
	 * MethodVisitor that visits the method and prints it to a given printer.
	 */
	private static final TraceMethodVisitor MP = new TraceMethodVisitor(PRINTER);

	/**
	 * Logs the bytecode of a method on System.out.
	 * @param main the method to print
	 */
	public static void logMethod(MethodNode main) {
		logMethod(main, System.out::println);
	}

	/**
	 * Logs the bytecode of a method into a given sink.
	 * @param main the method to print
	 * @param logFn a consumer for the string, typically a logging function
	 * @since 0.6.0
	 */
	public static void logMethod(MethodNode main, Consumer<String> logFn) {
		for(AbstractInsnNode i : main.instructions.toArray()) {
			logFn.accept(insnToString(i));
		}
	}

	/**
	 * Logs the bytecode of a method to a file.
	 * @param main the method to print
	 * @param path the file to log it to
	 */
	public static void logMethod(MethodNode main, String path) {
		StringBuilder out = new StringBuilder();
		logMethod(main, out::append);
		try {
			Files.write(Paths.get(path), out.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Converts an instruction node to a String.
	 * @param insn the node to convert
	 * @return the converted string
	 */
	public static String insnToString(AbstractInsnNode insn) {
		insn.accept(MP);
		StringWriter sw = new StringWriter();
		PRINTER.print(new PrintWriter(sw));
		PRINTER.getText().clear();
		return sw.toString();
	}
}
