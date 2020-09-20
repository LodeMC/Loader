package net.shadew.lode.loader;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.shadew.lode.loader.delivery.Delivery;
import net.shadew.lode.loader.transformer.Transformer;
import net.shadew.util.misc.IO;

public class ClassSource {
    private final Delivery delivery;
    private final List<Transformer> transformers = new ArrayList<>();

    private ClassSource(Delivery delivery) {
        this.delivery = delivery;
    }

    public ClassSource transformed(Transformer transformer) {
        transformers.add(transformer);
        return this;
    }

    public Stream<URL> getResources(String resourcePath) throws IOException {
        return delivery.deliverURLs(resourcePath);
    }

    public byte[] getClassBytes(String internalName) throws ClassNotFoundException {
        try {
            InputStream in = delivery.deliver(internalName + ".class");
            byte[] bytes = IO.readAll(in);

            List<Transformer> applicable = transformers.stream()
                                                       .filter(tsf -> tsf.canTransform(internalName))
                                                       .collect(Collectors.toList());

            if (!applicable.isEmpty()) {
                ClassNode node = new ClassNode();
                ClassReader reader = new ClassReader(bytes);
                reader.accept(node, ClassReader.EXPAND_FRAMES);

                for (Transformer tsf : applicable) {
                    node = tsf.transform(node);
                }

                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                node.accept(writer);
                bytes = writer.toByteArray();
            }

            return bytes;
        } catch (FileNotFoundException exc) {
            throw new ClassNotFoundException(internalName);
        } catch (IOException exc) {
            throw new UncheckedIOException(exc);
        }
    }

    public static ClassSource delivered(Delivery delivery) {
        return new ClassSource(delivery);
    }
}
