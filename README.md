# Lillero

Lillero is a lightweight and simple Java ASM patching framework built on top of [ObjectWeb's ASM library](https://asm.ow2.io/).

## How
This library provides the core interface, `IInjector`, as well as a small set of utils to make your life easier. All patches should implement `IInjector` and be declared as [services](https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html).

Some methods *must* be implemented, specifying which class and method will be patched:
 * `targetClass()`: returns the *fully qualified name* of the class to patch (example: `net.minecraft.client.Minecraft`).
 * `methodName()`: returns the name of the method to patch.
 * `methodDesc()`: returns descriptor (arguments and return type) of method to patch.
 * `inject(ClassNode, MethodNode)`: will be invoked providing you the ClassNode and MethodNode you requested. This is where the actual patching will happen.

There's some more optional methods you *don't have to* implement, but really should because they will make your life considerably easier when it's time to debug:
 * `name()`   : returns patch name
 * `reason()` :  returns patch description

Finally, you should mark your classes as service providers, by creating a text file called `ftbsc.lll.IInjector` in `src/main/resources/META-INF/services` on your project. Inside, put the fully qualified names of your patches (example: `ftbsc.bscv.asm.patches.TestPatch$TickPatch`).

If you use Gradle (you do) don't forget to add this library as a dependency in your `build.gradle`:

```groovy
repositories {
	maven { url = 'https://maven.fantabos.co' }
}
dependencies {
    implementation 'ftbsc:lll:<whatever the latest version is>'
}
```

You are going to need an appropriate loader to use Lillero patches: **this is just a library and does nothing by itself**. You need to make it work by loading services implementing the `IInjector` interface, and by calling their `inject(ClassNode, MethodNode)` methods with the appropriate parameters.

Finally, know that you can spare yourself some trouble, by using this [annotation processor](https://git.fantabos.co/lillero-processor/) to reduce boilerplate to a minimum.

#### Tips specific to Minecraft patching
* You want to be using Notch (fully obfuscated) names whenever you are told to reference a class or method by name, since those are the ones that exist at runtime.
    - Use MCP (AKA unobfuscated) names if you are running from ForgeGradle's runClient task. 
    - If you are using our loader (see below), use Searge (obfuscated but unique) names in every place you are told to use a name - ModLauncher will do the rest.
* Use our [loader](https://git.fantabos.co/lillero-loader/) that hooks into Forge's ModLauncher if you're writing a Forge mod.
* Make sure to dunk on all the naysayers who tried to force you to use Mixin!

#### Example Minecraft patch
The following is an example patch, located at `src/main/java/example/patches/SamplePatch.java`:
```java
  package example.patches;
  import ftbsc.lll.IInjector;
  public class SamplePatch implements IInjector {
    public String name()        { return "SamplePatch"; }
    public String targetClass() { return "net.minecraft.client.Minecraft"; }
    public String methodName()  { return "func_71407_l"; } //Searge name for tick()
    public String methodDesc()  { return "()V"; } //void, no args
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
