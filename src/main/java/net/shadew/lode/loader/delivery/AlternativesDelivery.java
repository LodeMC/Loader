package net.shadew.lode.loader.delivery;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class AlternativesDelivery implements Delivery {
    private final List<Delivery> deliveries = new ArrayList<>();

    private AlternativesDelivery(Delivery delivery) {
        this.deliveries.add(delivery);
    }

    @Override
    public InputStream deliver(String path) throws IOException {
        for(Delivery delivery : deliveries) {
            try {
                return delivery.deliver(path);
            } catch (FileNotFoundException ignored) {
            }
        }
        throw new FileNotFoundException(path + " not found");
    }

    @Override
    public Stream<URL> deliverURLs(String path) throws IOException {
        try {
            return deliveries.stream().flatMap(delivery -> {
                try {
                    return delivery.deliverURLs(path);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } catch (UncheckedIOException exc) {
            throw exc.getCause();
        }
    }

    @Override
    public void close() throws Exception {
        for(Delivery delivery : deliveries) {
            delivery.close();
        }
    }

    public AlternativesDelivery fallback(Delivery delivery) {
        deliveries.add(delivery);
        return this;
    }

    public static AlternativesDelivery delivering(Delivery delivery) {
        return new AlternativesDelivery(delivery);
    }
}
