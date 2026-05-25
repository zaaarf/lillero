# Lillero
Lillero is a lightweight, modular framework for runtime bytecode patching on the JVM, built on top of ObjectWeb's
[ASM library](https://asm.ow2.io/). Its goal is to provide a system that stays close to the (virtual) machine while
remaining approachable, even to those who are wholly new to ASM.

## What is Lillero?
In short, Lillero it is an ASM patching framework. It's modular by design, being made up of various components:
- The core library (this repository), which provides the patching interface and various utilities.
- The [annotation processor](https://github.com/zaaarf/lillero-processor), which reduces boilerplate and handles obfuscation.
- The [Gradle plugin](https://github.com/zaaarf/lillero-gradle), which automates and simplifies most of the configuration.
- Various loaders that integrate Lillero with each environment (see below for existing options).

You can use only the pieces you need, or use the full framework together. It will work anywhere ASM can transform classes
before they are loaded: while it was originally developed for Minecraft modding, it was designed to be environment-agnostic.

## Why use Lillero?
Lillero exists for developers who want direct bytecode manipulation without drowning in boilerplate.

Higher-level injection frameworks often entirely hide the bytecode behind layers of safe (?) abstractions. This comes at
the cost of flexibility and precision and, more importantly, of clarity: debugging, especially in complex environments
with multiple patchers interacting, becomes difficult if not impossible.

With Lillero you still write and inject bytecode yourself, while the framework takes care of the most tedious and
repetitive parts of it (descriptors, obfuscation remapping and so forth).

Importantly, Lillero does *not* rely on any reflection sorcery to accomplish this.
Everything is done via compile-time code generation: the boilerplate gets written by the machine for you, but it is not
hidden, and can be inspected (and thus debugged and understood) by any user at any time.
By design, nothing about Lillero should be mysterious; any user should, at any point, be able to easily understand what
it's doing and how the "magic" of patching comes together.

## Recommended usage
While the core library can be used (somewhat) standalone, the recommended setup is to use the broader Lillero ecosystem.

### [`lillero-gradle`](https://github.com/zaaarf/lillero-gradle)
Recommended for most projects, this Gradle plugin streamlines the otherwise tedious process of adding and configuring
the various Lillero components, providing sensible, environment-aware defaults while retaining the ability to alter
them as needed.

### [`lillero-processor`](https://github.com/zaaarf/lillero-processor)
This dependency, added implicitly by `lillero-gradle`, can also be added manually as needed.
It represents the main way the user should interact with the Lillero environment; its README provides detailed
instructions on how to use it and all of its features.

### Runtime loaders
Currently, the following ready-to-use loaders exist:
- [`lillero-mixin`](https://github.com/zaaarf/lillero-mixin), for all environments that bundle [Mixin](https://github.com/SpongePowered/Mixin) (i.e. Fabric, modern Forge and NeoForged)
- [`lillero-loader`](https://github.com/zaaarf/lillero-loader), for Forge's and NeoForged's [ModLauncher](https://github.com/MinecraftForge/ModLauncher)

On top of that, the following "example loaders" (i.e. not necessarily fully plug-and-play) are provided:
- [A core mod](https://gist.github.com/zaaarf/e911ae8024bc18605545c0f64db62952) for legacy Forge (1.12 and older)
- [An "early riser"](https://gist.github.com/zaaarf/2c55ad9984818b321b902d61e08b820d) for [Fabric-ASM](https://github.com/Chocohead/Fabric-ASM)

Even if your system is not listed here, don't give up just yet! Writing a loader for an environment that isn't officially
supported should be fairly simple, as long as the target system isn't unreasonably locked down. Unfortunately, specific
instructions cannot be provided as this is the part that remains heavily dependent on the details of each environment.
Reading the existing implementations should however provide a good blueprint for how to do it.

### Manual usage
As mentioned, the core library can be used independently of the rest of Lillero's tooling. `IInjector` is the central
interface implemented by all patches. As the processor's primary goal is generating these implementations, writing them
manually can be done as a fully-compatible replacement for it.

#### Required methods
These methods define the target and perform the patch itself:
* `targetClass()`: returns the *fully qualified* name of the class to patch (example: `net.minecraft.client.Minecraft`).
* `methodName()`: returns the target method name.
* `methodDesc()`: the JVM method descriptor for the target method.
* `inject(ClassNode, MethodNode)`: will be invoked providing you the ClassNode and MethodNode you requested; this is where the actual patching will happen.

### Optional but recommended methods
These methods are not required, but are extremely useful for debugging:
* `name()`: returns patch name
* `reason()` :  returns patch description

### Service registration
Most Lillero loaders will expect you to register your patch(es) as service providers. To do that, you must create a text
file called `src/main/resources/META-INF/services/ftbsc.lll.IInjector` and, inside it, list the fully qualified names of
your patches (like `org.your.patches.YourPatch`), one per line.

### Dependency setup
Any build system capable of using Maven repositories will work. Here's an example for Gradle:
```groovy
repositories {
	maven { url = 'https://maven.fantabos.co' }
}
dependencies {
    implementation 'ftbsc:lll:<whatever the latest version is>'
}
```

### Runtime requirements
This is a library, not a standalone runtime. Your environment (or a loader, see above) must:
1. Discover implementations of IInjector.
2. Load target classes before definition.
3. Invoke injector methods with the appropriate ASM nodes.

The library (as well as the loader, if you are using one) must also be present at runtime.
To this purpose you can either bundle it using [Shadow](https://github.com/GradleUp/shadow) or a similar tool, or place
it on the runtime classpath manually (see the [`lillero-loader`](https://github.com/zaaarf/lillero-loader) README for an
example of how to do that in a Minecraft environment).

### Minecraft-specific notes
Since the tool was originally written for this purpose, I can offer a few pointers specific to Minecraft modding.

#### Obfuscation
As Minecraft versions older than 26.1 are obfuscated, it is necessary to think about which obfuscation stage your
environment is in when the patches are applied. [`lillero-gradle`](https://github.com/zaaarf/lillero-gradle) tries to
mostly figure this out for you, but if that fails here's a few pointers:
- ForgeGradle's and Loom's `runClient` tasks run in fully deobfuscated environments.
- If you are using [`lillero-loader`](https://github.com/zaaarf/lillero-loader) or [`lillero-mixin`](https://github.com/zaaarf/lillero-mixin), use "intermediary" names.
- If you are using a CoreMod in old Forge, use Notch (fully obfuscated) names.

#### Example Minecraft patch
The following is an example patch, located at `src/main/java/example/patches/SamplePatch.java`:
```java
  package example.patches;
  import ftbsc.lll.IInjector;
  public class SamplePatch implements IInjector {
    public String name()        { return "SamplePatch"; }
    public String targetClass() { return "net.minecraft.client.Minecraft"; }
    public String methodName()  { return "func_71407_l"; } // searge name for tick()
    public String methodDesc()  { return "()V"; } // void, no args
    public void inject(ClassNode clazz, MethodNode main) {
      InsnList insnList = new InsnList();
      insnList.add(new InsnNode(POP));
      main.instructions.insert(insnList);
    }
  }
```

When loaded into Minecraft, this patch will crash the game with a NegativeArraySizeException as soon as it's done loading,
which tells you that it was successfully loaded and applied.

The following is its service registration file (see above):
```
  example.patches.SamplePatch
```

Happy patching!
