# Lillero

Lillero is a lightweight and simple ASM patching framework, empowering you to easily modify block game (byte)code, dunking on all the naysayers who said you absolutely had to use Mixin.

## How
This library provides the core interface, `IInjector`, as well as a small set of utils to make your life easier. All patches should implement `IInjector` and be declared as [services](https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html).

Some methods *must* be implemented, specifying which class and method will be patched:
 * `targetClass()`: returns the *fully qualified name* of the class to patch (example: `net.minecraft.client.Minecraft`).
 * `methodName()`: returns the "Searge name" of the method to patch.
 * `methodDesc()`: returns descriptor (arguments and return type) of method to patch.
 * `inject(ClassNode clazz, MethodNode method)`: will be invoked providing you the ClassNode and MethodNode you requested. This is where the actual patching will happen.

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
    implementation 'ftbsc:lll:0.2.0'
}
```

**You are going to need a loader to use patches created with this library**. You can use our [loader](https://git.fantabos.co/lillero-loader/) or write your own: as long as it uses `IInjector` as patch interface, it won't break compatibility.

### Example
The following is an example patch, located at `src/main/java/example/patches/SamplePatch.java`:
```java
  package example.patches;
  import ftbsc.lll.IInjector;
  public class SamplePatch implements IInjector {
    public String name()        { return "SamplePatch"; }
    public String targetClass() { return "net.minecraft.client.Minecraft"; }
    public String methodName()  { return "func_71407_l"; } // tick()
    public String methodDesc()  { return "()V"; }
    public void inject(ClassNode clazz, MethodNode main) {
      InsnList insnList = new InsnList();
      insnList.add(new InsnNode(POP));
      main.instructions.insert(insnList);
    }
  }
```

The patch will crash the game with a NegativeArraySizeException as soon as it's done loading - so you know it's working.

The following is the service registration file, located at `src/main/resources/META-INF/services/ftbsc.lll.IInjector`:
```
  example.patches.SamplePatch
```

Happy patching! And remember...
### "Don't do coremods!"
