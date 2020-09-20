package net.shadew.lode.loader.transformer;

import org.objectweb.asm.tree.ClassNode;

public interface Transformer {
    boolean canTransform(String internalName);
    ClassNode transform(ClassNode node);
}
