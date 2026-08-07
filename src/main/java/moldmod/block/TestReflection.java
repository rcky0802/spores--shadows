package moldmod.block;
import net.minecraft.block.AbstractPressurePlateBlock;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.io.FileWriter;
import java.io.PrintWriter;
public class TestReflection {
    public static void run() {
        try (PrintWriter out = new PrintWriter(new FileWriter("reflection_output2.txt"))) {
            out.println("AbstractPressurePlateBlock methods:");
            for (Method m : AbstractPressurePlateBlock.class.getDeclaredMethods()) {
                out.println(m.getName());
            }
            for (Field f : AbstractPressurePlateBlock.class.getDeclaredFields()) {
                out.println(f.getName() + " type " + f.getType().getName());
            }
        } catch (Exception e) {}
    }
}
