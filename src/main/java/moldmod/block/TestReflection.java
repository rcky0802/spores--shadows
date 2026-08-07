package moldmod.block;
import net.minecraft.block.AbstractPressurePlateBlock;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.io.FileWriter;
import java.io.PrintWriter;
public class TestReflection {
    public static void run() {
        try (PrintWriter out = new PrintWriter(new FileWriter("reflection_output2.txt"))) {
            for (Method m : net.minecraft.world.WorldAccess.class.getDeclaredMethods()) {
                if (m.getName().contains("schedule")) out.println("WorldAccess." + m.getName() + " " + m.getParameterCount());
            }
            for (Method m : net.minecraft.world.World.class.getDeclaredMethods()) {
                if (m.getName().contains("schedule")) out.println("World." + m.getName() + " " + m.getParameterCount());
            }
            for (Method m : net.minecraft.block.ButtonBlock.class.getDeclaredMethods()) {
                out.println("ButtonBlock." + m.getName() + " " + m.getParameterCount());
            }
        } catch (Exception e) {}
    }
}
