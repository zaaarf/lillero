package ftbsc.lll.loader;

import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;

import ftbsc.lll.IInjector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class LilleroLoader implements ILaunchPluginService {
	public static final Logger LOGGER = LogManager.getLogger("LilleroLoader");
	public static final String NAME = "lillero-loader";

	private List<IInjector> injectors = new ArrayList<>();

	public LilleroLoader() {
		LOGGER.info("Lillero Patch Loader initialized");
	}

	@Override
	public String name() {
		return NAME;
	}


	// Load mods requesting patches from resources

	@Override
	public void offerResource(Path resource, String name) {
		LOGGER.warn(String.format("Resource offered to us: %s @ '%s'", name, resource.toString()));
	}

	@Override
	public void addResources(List<Map.Entry<String, Path>> resources) {
		LOGGER.info("Resources being added:");
		for (Map.Entry<String, Path> row : resources) {
			LOGGER.info(String.format("> %s @ '%s'", row.getKey(), row.getValue().toString()));
			try {
				URL jarUrl = new URL("file:" + row.getValue().toString());
				URLClassLoader loader = new URLClassLoader(new URL[] { jarUrl });
				for (IInjector inj : ServiceLoader.load(IInjector.class, loader)) {
					LOGGER.info(String.format("Registering injector %s", inj.name()));
					this.injectors.add(inj);
				}
			} catch (MalformedURLException e) {
				LOGGER.error(String.format("Malformed URL for resource %s - 'file:%s'", row.getKey(), row.getValue().toString()));
			}
		}
	}


	// Filter only classes we need to patch

	@Override
	public EnumSet<Phase> handlesClass(Type classType, final boolean isEmpty) {
		throw new IllegalStateException("Outdated ModLauncher"); //mixin does it
	}

	private static final EnumSet<Phase> YAY = EnumSet.of(Phase.BEFORE);
	private static final EnumSet<Phase> NAY = EnumSet.noneOf(Phase.class);

	@Override
	public EnumSet<Phase> handlesClass(Type classType, final boolean isEmpty, final String reason) {
		if (isEmpty) return NAY;
		// TODO can I make a set of target classes to make this faster
		for (IInjector inj : this.injectors) {
			if (inj.targetClass().equals(classType.getClassName()))
				return YAY;
		}
		return NAY;
	}


	// Process classes and inject methods

	@Override
	public int processClassWithFlags(Phase phase, ClassNode classNode, Type classType, String reason) {
		LOGGER.debug("Processing class {} in phase {} of {}", classType.getClassName(), phase.name(), reason);
		List<IInjector> relevantInjectors = this.injectors.stream()
			.filter(i -> i.targetClass().equals(classType.getClassName()))
			.collect(Collectors.toList());
		boolean modified = false;
		for (MethodNode method : classNode.methods) {
			for (IInjector inj : relevantInjectors) {
				if (
					inj.methodName().equals(method.name) &&
					inj.methodDesc().equals(method.desc)
				) {
					LOGGER.info(String.format("Patching %s.%s with %s", classType.getClassName(), method.name, inj.name()));
					inj.inject(classNode, method); // TODO catch patching exceptions
					modified = true;
				}
			}
		}

		return modified ? ComputeFlags.COMPUTE_FRAMES : ComputeFlags.NO_REWRITE;
	}
}
