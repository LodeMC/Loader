import java.io.File;

import net.shadew.lode.loader.ClassSource;
import net.shadew.lode.loader.ModdedClassLoader;
import net.shadew.lode.loader.delivery.Delivery;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) throws Exception {
        ClassSource src = ClassSource.delivered(Delivery.path(new File("build/classes/java/test")))
                                     .transformed(new PrintTransformer());
        ModdedClassLoader loader = ModdedClassLoader.loading(src);

        Class<?> cls = loader.loadClass("test.Test");
        cls.getMethod("print").invoke(null);
    }
}
