package moldmod.test.gametest.integration.jade;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.SporesShadows;
import moldmod.block.ModBlocks;
import moldmod.block.MoldyBlock;
import moldmod.block.MoldyLogBlock;
import moldmod.config.ModConfig;
import moldmod.integration.jade.MoldyBlockProvider;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import snownee.jade.api.BlockAccessor;

public class JadeMoldyBlockProviderGameTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMoldyBlockProviderUid(TestContext context) {
        Identifier uid = MoldyBlockProvider.INSTANCE.getUid();
        context.assertTrue(uid.getNamespace().equals(SporesShadows.MOD_ID),
                "Provider namespace must be " + SporesShadows.MOD_ID + ", got: " + uid.getNamespace());
        context.assertTrue(uid.getPath().equals("moldy_info"),
                "Provider path must be 'moldy_info', got: " + uid.getPath());
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMoldyBlockStateProperties(TestContext context) {
        BlockState normalLog = Blocks.OAK_LOG.getDefaultState();
        context.assertFalse(normalLog.contains(MoldyBlock.STAGE),
                "Vanilla oak log must not contain STAGE property");

        BlockState moldyLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        context.assertTrue(moldyLog.contains(MoldyBlock.STAGE),
                "Moldy oak log must contain STAGE property");
        context.assertTrue(moldyLog.contains(MoldyBlock.WAXED),
                "Moldy oak log must contain WAXED property");

        // Verify stage values
        BlockState stage1 = moldyLog.with(MoldyLogBlock.STAGE, 1);
        context.assertTrue(stage1.get(MoldyLogBlock.STAGE) == 1, "Stage must be 1");

        BlockState stage3 = moldyLog.with(MoldyLogBlock.STAGE, 3);
        context.assertTrue(stage3.get(MoldyLogBlock.STAGE) == 3, "Stage must be 3");

        BlockState waxed = moldyLog.with(MoldyLogBlock.WAXED, true);
        context.assertTrue(waxed.get(MoldyLogBlock.WAXED), "Waxed must be true");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testInfectionThresholdConfig(TestContext context) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        context.assertTrue(config.general.infection_threshold > 0.0 && config.general.infection_threshold <= 1.0,
                "Infection threshold must be in (0.0, 1.0]");
        context.complete();
    }

    private static BlockAccessor createMockAccessor(net.minecraft.world.World world, BlockPos pos, BlockState state, net.minecraft.nbt.NbtCompound serverData) {
        return (BlockAccessor) java.lang.reflect.Proxy.newProxyInstance(
                BlockAccessor.class.getClassLoader(),
                new Class<?>[]{BlockAccessor.class},
                (proxy, method, args) -> {
                    String name = method.getName();
                    if ("getLevel".equals(name)) return world;
                    if ("getPosition".equals(name)) return pos;
                    if ("getBlockState".equals(name)) return state;
                    if ("getServerData".equals(name)) return serverData != null ? serverData : new net.minecraft.nbt.NbtCompound();
                    if ("getBlock".equals(name)) return state.getBlock();
                    if ("isFakeBlock".equals(name)) return false;
                    if ("isServerConnected".equals(name)) return true;
                    if ("showDetails".equals(name)) return false;
                    return null;
                }
        );
    }

    private static snownee.jade.api.ITooltip createMockTooltip(java.util.List<net.minecraft.text.Text> list) {
        return (snownee.jade.api.ITooltip) java.lang.reflect.Proxy.newProxyInstance(
                snownee.jade.api.ITooltip.class.getClassLoader(),
                new Class<?>[]{snownee.jade.api.ITooltip.class},
                (proxy, method, args) -> {
                    if ("add".equals(method.getName()) && args != null && args.length > 0 && args[0] instanceof net.minecraft.text.Text text) {
                        list.add(text);
                    }
                    return null;
                }
        );
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testShouldRequestData(TestContext context) {
        BlockPos pos = new BlockPos(1, 1, 1);
        BlockState normalLog = Blocks.OAK_LOG.getDefaultState();
        BlockState normalPlanks = Blocks.OAK_PLANKS.getDefaultState();
        BlockState stone = Blocks.STONE.getDefaultState();
        BlockState air = Blocks.AIR.getDefaultState();

        BlockState moldyLogStage1 = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE, 1);
        BlockState moldyLogStage3 = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE, 3);
        BlockState moldyLogWaxed = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.WAXED, true);

        context.assertTrue(MoldyBlockProvider.INSTANCE.shouldRequestData(createMockAccessor(context.getWorld(), pos, normalLog, null)),
                "Vanilla oak log should request server data");
        context.assertTrue(MoldyBlockProvider.INSTANCE.shouldRequestData(createMockAccessor(context.getWorld(), pos, normalPlanks, null)),
                "Vanilla oak planks should request server data");
        context.assertFalse(MoldyBlockProvider.INSTANCE.shouldRequestData(createMockAccessor(context.getWorld(), pos, stone, null)),
                "Stone should not request server data");
        context.assertFalse(MoldyBlockProvider.INSTANCE.shouldRequestData(createMockAccessor(context.getWorld(), pos, air, null)),
                "Air should not request server data");
        context.assertTrue(MoldyBlockProvider.INSTANCE.shouldRequestData(createMockAccessor(context.getWorld(), pos, moldyLogStage1, null)),
                "Moldy log stage 1 should request server data");
        context.assertFalse(MoldyBlockProvider.INSTANCE.shouldRequestData(createMockAccessor(context.getWorld(), pos, moldyLogStage3, null)),
                "Rotten log stage 3 should not request server data");
        context.assertFalse(MoldyBlockProvider.INSTANCE.shouldRequestData(createMockAccessor(context.getWorld(), pos, moldyLogWaxed, null)),
                "Waxed log should not request server data");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testAppendServerDataMatchesMoldRiskCalculator(TestContext context) {
        BlockPos relPos = new BlockPos(2, 2, 2);
        BlockPos absPos = context.getAbsolutePos(relPos);
        BlockState moldyState = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE, 1);
        context.setBlockState(relPos, moldyState);

        net.minecraft.nbt.NbtCompound nbt = new net.minecraft.nbt.NbtCompound();
        BlockAccessor accessor = createMockAccessor(context.getWorld(), absPos, moldyState, null);
        MoldyBlockProvider.INSTANCE.appendServerData(nbt, accessor);

        context.assertTrue(nbt.contains("MoldRisk"), "Server data NBT must contain 'MoldRisk'");
        double expectedR = moldmod.risk.MoldRiskCalculator.calculate(context.getWorld(), absPos, false, moldyState).R();
        context.assertTrue(Math.abs(nbt.getDouble("MoldRisk") - expectedR) < 1e-6,
                "Server data MoldRisk (" + nbt.getDouble("MoldRisk") + ") must match MoldRiskCalculator.calculate.R (" + expectedR + ")");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testAppendTooltipUsesServerData(TestContext context) {
        BlockPos pos = new BlockPos(1, 1, 1);
        BlockState moldyState = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE, 1);

        // Case 1: High risk (above threshold 0.60)
        net.minecraft.nbt.NbtCompound highRiskNbt = new net.minecraft.nbt.NbtCompound();
        highRiskNbt.putDouble("MoldRisk", 0.85);
        BlockAccessor highAccessor = createMockAccessor(context.getWorld(), pos, moldyState, highRiskNbt);

        java.util.List<net.minecraft.text.Text> highList = new java.util.ArrayList<>();
        MoldyBlockProvider.INSTANCE.appendTooltip(createMockTooltip(highList), highAccessor, null);
        context.assertTrue(highList.size() == 1, "Expected 1 tooltip line for high risk, got: " + highList.size());
        context.assertTrue(highList.get(0).getString().contains("85%"), "Tooltip must display 85%");

        // Case 2: Low risk (below threshold 0.60)
        net.minecraft.nbt.NbtCompound lowRiskNbt = new net.minecraft.nbt.NbtCompound();
        lowRiskNbt.putDouble("MoldRisk", 0.20);
        BlockAccessor lowAccessor = createMockAccessor(context.getWorld(), pos, moldyState, lowRiskNbt);

        java.util.List<net.minecraft.text.Text> lowList = new java.util.ArrayList<>();
        MoldyBlockProvider.INSTANCE.appendTooltip(createMockTooltip(lowList), lowAccessor, null);
        context.assertTrue(lowList.size() == 1, "Expected 1 tooltip line for low risk, got: " + lowList.size());
        context.assertTrue(lowList.get(0).getString().contains("20%"), "Tooltip must display 20%");

        // Case 3: Waxed block produces no tooltip
        BlockState waxedState = moldyState.with(MoldyLogBlock.WAXED, true);
        BlockAccessor waxedAccessor = createMockAccessor(context.getWorld(), pos, waxedState, highRiskNbt);
        java.util.List<net.minecraft.text.Text> waxedList = new java.util.ArrayList<>();
        MoldyBlockProvider.INSTANCE.appendTooltip(createMockTooltip(waxedList), waxedAccessor, null);
        context.assertTrue(waxedList.isEmpty(), "Waxed block must produce no infection tooltip");

        // Case 4: Rotten block produces no tooltip
        BlockState rottenState = moldyState.with(MoldyLogBlock.STAGE, 3);
        BlockAccessor rottenAccessor = createMockAccessor(context.getWorld(), pos, rottenState, highRiskNbt);
        java.util.List<net.minecraft.text.Text> rottenList = new java.util.ArrayList<>();
        MoldyBlockProvider.INSTANCE.appendTooltip(createMockTooltip(rottenList), rottenAccessor, null);
        context.assertTrue(rottenList.isEmpty(), "Rotten block must produce no infection tooltip");

        context.complete();
    }
}
