package net.shadew.lode.loader.delivery;

import java.io.*;
import java.net.URL;
import java.util.stream.Stream;

public class PathDelivery implements Delivery {
    private final File rootDir;

    public PathDelivery(File rootDir) throws FileNotFoundException {
        if (!rootDir.exists()) throw new FileNotFoundException(rootDir + " does not exist");
        if (!rootDir.isDirectory()) throw new FileNotFoundException(rootDir + " is not a directory");
        this.rootDir = rootDir;
    }

    @Override
    public InputStream deliver(String path) throws IOException {
        File file = new File(rootDir, path.replace('/', File.separatorChar));

        if (!file.exists()) {
            throw new FileNotFoundException(path + " not found");
        }
        if (file.isDirectory()) {
            throw new FileNotFoundException(path + " is a directory");
        }

        return new FileInputStream(file);
    }

    @Override
    public Stream<URL> deliverURLs(String path) throws IOException {
        File file = new File(rootDir, path.replace('/', File.separatorChar));
        if (!file.exists()) {
            return Stream.empty();
        }
        return Stream.of(file.toURI().toURL());
    }

    @Override
    public void close() {
    }
}
