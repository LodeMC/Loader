package net.shadew.lode.loader;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class ModdedClassLoader extends ClassLoader {
    private final List<ClassSource> sources = new ArrayList<>();

    private ModdedClassLoader(ClassSource src) {
        super(null);
        sources.add(src);
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] b = loadClassFromFile(name);
        return defineClass(name, b, 0, b.length);
    }

    @Override
    protected Enumeration<URL> findResources(String name) throws IOException {
        try {
            Stream<URL> urls = sources.stream().flatMap(src -> {
                try {
                    return src.getResources(name);
                } catch (IOException exc) {
                    throw new UncheckedIOException(exc);
                }
            });
            Iterator<URL> itr = urls.iterator();
            return new Enumeration<URL>() {
                @Override
                public boolean hasMoreElements() {
                    return itr.hasNext();
                }

                @Override
                public URL nextElement() {
                    return itr.next();
                }
            };
        } catch (UncheckedIOException exc) {
            throw exc.getCause();
        }
    }

    @Override
    protected URL findResource(String name) {
        Stream<URL> urls = sources.stream().flatMap(src -> {
            try {
                return src.getResources(name);
            } catch (IOException exc) {
                throw new UncheckedIOException(exc);
            }
        });
        return urls.findFirst().orElse(null);
    }

    private byte[] loadClassFromFile(String name) throws ClassNotFoundException {
        String internalName = name.replace('.', '/');
        for(ClassSource src : sources) {
            try {
                return src.getClassBytes(internalName);
            } catch (ClassNotFoundException ignored) {
            }
        }
        throw new ClassNotFoundException(name);
    }

    public ModdedClassLoader and(ClassSource src) {
        sources.add(src);
        return this;
    }

    public static ModdedClassLoader loading(ClassSource src) {
        return new ModdedClassLoader(src);
    }
}
