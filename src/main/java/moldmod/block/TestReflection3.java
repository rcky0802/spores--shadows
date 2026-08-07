package moldmod.block;
import net.minecraft.block.DoorBlock;
import java.lang.reflect.Method;
import java.io.FileWriter;
import java.io.PrintWriter;
public class TestReflection3 {
    public static void run() {
        try (PrintWriter out = new PrintWriter(new FileWriter("reflection_output3.txt"))) {
            out.println("DoorBlock methods:");
            for (Method m : DoorBlock.class.getDeclaredMethods()) {
                out.println(m.getName());
            }
        } catch (Exception e) {}
    }
}
