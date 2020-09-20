package net.shadew.lode.loader.delivery;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class JarDelivery implements Delivery {
    private final JarFile jar;
    private final File path;

    public JarDelivery(File jar) throws IOException {
        this.jar = new JarFile(jar);
        this.path = jar.getAbsoluteFile();
    }

    @Override
    public InputStream deliver(String path) throws IOException {
        JarEntry entry = jar.getJarEntry(path);
        if(entry == null) {
            throw new FileNotFoundException(path + " not found");
        }
        return jar.getInputStream(entry);
    }

    @Override
    public Stream<URL> deliverURLs(String path) throws IOException {
        if(jar.getJarEntry(path) == null) return Stream.empty();
        return Stream.of(new URL("jar:" + path + "!/" + path));
    }

    @Override
    public void close() throws Exception {
        jar.close();
    }
}
