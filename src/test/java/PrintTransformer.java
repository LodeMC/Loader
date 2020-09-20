import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.shadew.lode.loader.transformer.Transformer;

public class PrintTransformer implements Transformer {
    @Override
    public boolean canTransform(String internalName) {
        return internalName.equals("test/Test");
    }

    @Override
    public ClassNode transform(ClassNode node) {
        for (MethodNode met : node.methods) {
            if(met.name.equals("print")) {
                for (int i = 0; i < met.instructions.size(); i++) {
                    if (met.instructions.get(i).getOpcode() == Opcodes.LDC) {
                        ((LdcInsnNode) met.instructions.get(i)).cst = "This is ASM injected text";
                        break;
                    }
                }
            }
        }
        return node;
    }
}
