package net.shadew.lode.loader.delivery;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipDelivery implements Delivery {
    private final ZipFile zip;
    private final File path;

    public ZipDelivery(File zip) throws IOException {
        this.zip = new ZipFile(zip);
        this.path = zip.getAbsoluteFile();
    }

    @Override
    public InputStream deliver(String path) throws IOException {
        ZipEntry entry = zip.getEntry(path);
        if(entry == null) {
            throw new FileNotFoundException(path + " not found");
        }
        return zip.getInputStream(entry);
    }

    @Override
    public Stream<URL> deliverURLs(String path) throws IOException {
        if(zip.getEntry(path) == null) return Stream.empty();
        return Stream.of(new URL("jar:" + path + "!/" + path));
    }

    @Override
    public void close() throws Exception {
        zip.close();
    }
}
