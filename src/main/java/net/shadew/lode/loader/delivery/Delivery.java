package net.shadew.lode.loader.delivery;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.stream.Stream;

public interface Delivery extends AutoCloseable {
    InputStream deliver(String path) throws IOException;
    Stream<URL> deliverURLs(String path) throws IOException;

    static Delivery path(File path) throws FileNotFoundException {
        return new PathDelivery(path);
    }

    static Delivery jar(File path) throws IOException {
        return new JarDelivery(path);
    }

    static Delivery zip(File path) throws IOException {
        return new ZipDelivery(path);
    }

    static AlternativesDelivery alternatives(Delivery delivery) {
        return AlternativesDelivery.delivering(delivery);
    }
}
