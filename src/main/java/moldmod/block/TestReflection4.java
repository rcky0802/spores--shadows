package moldmod.block;
import net.minecraft.block.DoorBlock;
import java.lang.reflect.Method;
import java.io.FileWriter;
import java.io.PrintWriter;
public class TestReflection4 {
    public static void run() {
        try (PrintWriter out = new PrintWriter(new FileWriter("reflection_output4.txt"))) {
            for (Method m : DoorBlock.class.getDeclaredMethods()) {
                if (m.getName().equals("onUse") || m.getName().equals("onUseWithItem")) {
                    out.print(m.getReturnType().getName() + " " + m.getName() + "(");
                    Class<?>[] pTypes = m.getParameterTypes();
                    for (int i = 0; i < pTypes.length; i++) {
                        out.print(pTypes[i].getName());
                        if (i < pTypes.length - 1) out.print(", ");
                    }
                    out.println(")");
                }
            }
        } catch (Exception e) {}
    }
}
