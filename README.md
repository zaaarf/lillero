# lillero
*don't do coremods*

A simple and slim ASM patching framework, allowing to modify block game code.

## How
This library provides the core interface: `IInjector`. All patches which implement this interface and are defined as services will be loaded and applied at startup.

Some methods must be implemented, specifying which class and method will be patched:
 * `targetClass()` : returns full name of class to patch (such as `net.minecraft.client.Minecraft`)
 * `methodName()`  : returns name (Searge obfuscated) of method to patch
 * `methodDesc()`  : returns descriptor (arguments and return type) of method to patch
 * `inject(ClassNode clazz, MethodNode method)` : will be invoked providing correct class and method. This is where the actual patching happens
Some extra methods should also be implemented to help identify your patch:
 * `name()`   : returns patch name
 * `reason()` :  returns patch description

To make your classes service providers, a simple text file should be defined under `src/main/resources/META-INF/services` in your mod project: `ftbsc.lll.IInjector`. Inside such file, put full class paths of your patches.

To load patches created with this library, a Launch Plugin loader is necessary (such as our [lillero-loader](https://git.fantabos.co/lillero-loader/))

### Example
```java
// file src/main/java/example/patches/SamplePatch.java
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

// file src/main/resources/META-INF/services/ftbsc.lll.IInjector
  example.patches.SamplePatch
```
