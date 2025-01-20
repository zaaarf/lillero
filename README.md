# Lillero
Lillero is a lightweight and simple Java ASM patching framework built on top of [ObjectWeb's ASM library](https://asm.ow2.io/).

## How
This library provides the core interface, `IInjector`, as well as a small set of utils to make your life easier. Your patches should implement `IInjector`.

Some methods *must* be implemented, specifying which class and method will be patched:
 * `targetClass()`: returns the *fully qualified name* of the class to patch (example: `net.minecraft.client.Minecraft`).
 * `methodName()`: returns the name of the method to patch.
 * `methodDesc()`: returns descriptor (arguments and return type) of method to patch.
 * `inject(ClassNode, MethodNode)`: will be invoked providing you the ClassNode and MethodNode you requested. This is where the actual patching will happen.

There's some more optional methods you *don't have to* implement, but really should because they will make your life considerably easier when it's time to debug:
 * `name()`   : returns patch name
 * `reason()` :  returns patch description

Finally, you should mark your classes as service providers, by creating a text file called `ftbsc.lll.IInjector` in `src/main/resources/META-INF/services` on your project. Inside, put the fully qualified names of your patches (example: `ftbsc.bscv.asm.patches.TestPatch$TickPatch`).

Add this library into your build system of choice (Gradle is shown, but anything supporting Maven repositories will do):
```groovy
repositories {
	maven { url = 'https://maven.fantabos.co' }
}
dependencies {
    implementation 'ftbsc:lll:<whatever the latest version is>'
}
```

You are going to need an appropriate loader to use Lillero patches: **this is just a library and does nothing by itself**. You need to make it work by loading services implementing the `IInjector` interface, and by calling their `inject(ClassNode, MethodNode)` methods with the appropriate parameters.

It should also be noted that this library will be required at runtime for your patches to function. You will either have to bundle it using [Shadow](https://github.com/johnrengelman/shadow) or similar, or get this into your runtime classpath by some other means (our [loader](https://github.com/zaaarf/lillero-loader/), for instance, takes care of this for you).

Finally, know that you can spare yourself some trouble, by using this [annotation processor](https://github.com/zaaarf/lillero-processor/) to reduce boilerplate code to a minimum.

#### Tips specific to Minecraft patching
* In "raw" environments, you want to be using Notch (fully obfuscated) names whenever you are told to reference a class or method by name, since those are the ones that exist at runtime.
    - Use deobfuscated names if you are running from ForgeGradle's or loom's runClient task.
    - If you are using our loader (see below), use intermediary (unreadable but unique) names in every place you are told to use a name - ModLauncher will do the rest.
    - If you are loading this through [Fabric-ASM](https://github.com/Chocohead/Fabric-ASM), use intermediary representation.
* Use our [loader](https://github.com/zaaarf/lillero-loader/) that hooks into Forge's ModLauncher if you're writing a modern Forge mod.
* Use our [Mixin plugin](https://github.com/zaaarf/lillero-mixin/) if you are constrained to an environment that bundles it (like Fabric).
* Make sure to dunk on all the naysayers who tried to force you to use Mixin!

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

When loaded into Minecraft, this patch will crash the game with a NegativeArraySizeException as soon as it's done loading - so you know it's working.

The following is the service registration file, located at `src/main/resources/META-INF/services/ftbsc.lll.IInjector`:
```
  example.patches.SamplePatch
```

Happy patching! 
