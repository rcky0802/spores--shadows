package moldmod.client.bot.scenario;

import moldmod.block.core.MoldyBlock;
import moldmod.client.bot.util.BotHelper;
import moldmod.item.ModItems;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class CaveScenario implements BotScenario {
    public static final int OX = 60;
    public static final int OZ = 0;
    public static final int BASE_Y = 120;

    @Override
    public String getName() {
        return "Scenario 2: Explorer with 3D Spore Mask in Cave";
    }

    @Override
    public String getScreenshotFilename() {
        return "scenario_02_spore_mask_character.png";
    }

    @Override
    public void prepare(ServerWorld world, ServerPlayerEntity player, MinecraftClient client) {
        BotHelper.fill(world, OX - 4, BASE_Y - 1, OZ - 4, OX + 4, BASE_Y + 6, OZ + 6, Blocks.AIR);

        // Cobbled deepslate cave floor
        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 6; z++) {
                world.setBlockState(new BlockPos(OX + x, BASE_Y - 1, OZ + z),
                    (Math.abs(x * 3 + z) % 2 == 0) ? Blocks.COBBLED_DEEPSLATE.getDefaultState() : Blocks.DEEPSLATE.getDefaultState());
            }
        }

        world.setBlockState(new BlockPos(OX - 2, BASE_Y - 1, OZ + 2), Blocks.WATER.getDefaultState());
        world.setBlockState(new BlockPos(OX - 1, BASE_Y - 1, OZ + 2), Blocks.WATER.getDefaultState());

        // Rough cavern walls
        for (int x = -4; x <= 4; x++) {
            for (int y = 0; y <= 5; y++) {
                world.setBlockState(new BlockPos(OX + x, BASE_Y + y, OZ + 5), Blocks.DEEPSLATE.getDefaultState());
                world.setBlockState(new BlockPos(OX - 4, BASE_Y + y, OZ + x), Blocks.COBBLED_DEEPSLATE.getDefaultState());
            }
        }

        // Moldy timber frame in cave
        net.minecraft.block.Block moldyLog = Registries.BLOCK.get(moldmod.SporesShadows.id("moldy_oak_log"));
        world.setBlockState(new BlockPos(OX - 2, BASE_Y, OZ + 3), moldyLog.getDefaultState().with(MoldyBlock.STAGE, 3));
        world.setBlockState(new BlockPos(OX - 2, BASE_Y + 1, OZ + 3), moldyLog.getDefaultState().with(MoldyBlock.STAGE, 3));
        world.setBlockState(new BlockPos(OX - 2, BASE_Y + 2, OZ + 3), moldyLog.getDefaultState().with(MoldyBlock.STAGE, 3));
        world.setBlockState(new BlockPos(OX + 2, BASE_Y, OZ + 3), moldyLog.getDefaultState().with(MoldyBlock.STAGE, 3));
        world.setBlockState(new BlockPos(OX + 2, BASE_Y + 1, OZ + 3), moldyLog.getDefaultState().with(MoldyBlock.STAGE, 3));
        world.setBlockState(new BlockPos(OX + 2, BASE_Y + 2, OZ + 3), moldyLog.getDefaultState().with(MoldyBlock.STAGE, 3));
        world.setBlockState(new BlockPos(OX - 1, BASE_Y + 2, OZ + 3), moldyLog.getDefaultState().with(MoldyBlock.STAGE, 3));
        world.setBlockState(new BlockPos(OX, BASE_Y + 2, OZ + 3), moldyLog.getDefaultState().with(MoldyBlock.STAGE, 3));
        world.setBlockState(new BlockPos(OX + 1, BASE_Y + 2, OZ + 3), moldyLog.getDefaultState().with(MoldyBlock.STAGE, 3));

        // Player equipment: 3D Spore Mask, Torch, Spore Detector
        player.equipStack(EquipmentSlot.HEAD, new ItemStack(ModItems.SPORE_MASK));
        player.equipStack(EquipmentSlot.OFFHAND, new ItemStack(Items.TORCH));
        player.equipStack(EquipmentSlot.MAINHAND, new ItemStack(ModItems.SPORE_DETECTOR));

        world.setBlockState(new BlockPos(OX, BASE_Y + 1, OZ - 2), Blocks.SOUL_LANTERN.getDefaultState());

        client.options.setPerspective(Perspective.THIRD_PERSON_FRONT);
        player.teleport(world, OX + 0.0, BASE_Y, OZ + 1.2, 0f, 0f);
        client.player.setYaw(0f);
        client.player.setPitch(-2f);
    }
}
