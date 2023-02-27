package ftbsc.lll.tools.debug;

import org.apache.logging.log4j.Logger;
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
	 * Prints the bytecode of a method using System.out.print().
	 * @param main the method to print
	 */
	public static void printAsmMethod(final MethodNode main) {
		for (AbstractInsnNode i : main.instructions.toArray())
			System.out.print(insnToString(i));
	}

	/**
	 * Logs the bytecode of a method using the ASM logger.
	 * @param main the method to print
	 * @param logger the Log4j {@link Logger} to print it with
	 */
	public static void logAsmMethod(final MethodNode main, final Logger logger) {
		for (AbstractInsnNode i : main.instructions.toArray())
			logger.debug(insnToString(i));
	}

	/**
	 * Logs the bytecode of a method to a file.
	 * @param main the method to print
	 * @param path the file to log it to
	 */
	public static void logAsmMethod(final MethodNode main, String path) {
		StringBuilder out = new StringBuilder();
		for (AbstractInsnNode i : main.instructions.toArray())
			out.append(insnToString(i));
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