package moldmod.block;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.SporesShadows;
import moldmod.config.ModConfig;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import java.util.List;

public class MoldyBlockHelper {

    public static BlockState initMoldyDefaultState(BlockState state) {
        return state
                .with(MoldyBlock.STAGE, 0)
                .with(MoldyBlock.WAXED, false)
                .with(MoldyBlock.STRUCTURAL, false);
    }

    public static void appendMoldyProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(MoldyBlock.STAGE, MoldyBlock.WAXED, MoldyBlock.STRUCTURAL);
    }

    public static boolean tryBreakRottenBlock(World world, BlockPos pos, BlockState state, float chance) {
        if (!world.isClient && state.contains(MoldyBlock.STAGE) && state.contains(MoldyBlock.WAXED)) {
            int stage = state.get(MoldyBlock.STAGE);
            boolean waxed = state.get(MoldyBlock.WAXED);
            if (stage == 3 && !waxed) {
                if (world.random.nextFloat() < chance) {
                    world.breakBlock(pos, false);
                    world.playSound(null, pos, SoundEvents.BLOCK_WOOD_BREAK,
                            SoundCategory.BLOCKS, 1.0f, 0.8f);
                    return true;
                }
            }
        }
        return false;
    }

    public static BlockState copyMatchingProperties(BlockState from, BlockState to) {
        BlockState result = to;
        for (Property<?> property : from.getProperties()) {
            if (result.contains(property)) {
                result = copyProperty(from, result, property);
            }
        }
        return result;
    }

    private static <T extends Comparable<T>> BlockState copyProperty(BlockState from, BlockState to,
            Property<T> property) {
        return to.with(property, from.get(property));
    }

    public static boolean canBeInfected(BlockState state) {
        // Return false if WAXED == true
        if (state.contains(MoldyBlock.WAXED) && state.get(MoldyBlock.WAXED)) {
            return false;
        }

        // Return false if STRUCTURAL == true AND structures are immune.
        // ALL generated structures (villages, shipwrecks) are passive until interacted
        // with (unless disabled in config).
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        if (state.contains(MoldyBlock.STRUCTURAL) && state.get(MoldyBlock.STRUCTURAL)) {
            if (config.general.structures_immune) {
                return false;
            }
        }

        return true;
    }

    public static boolean hasRandomTicks(BlockState state) {
        if (Registries.BLOCK.getId(state.getBlock()).getPath().startsWith("waxed_")) {
            return false;
        }
        return canBeInfected(state);
    }

    public static double calculateR(WorldAccess world, BlockPos pos, boolean isWaxed,
            BlockState stateToCheck) {
        return MoldRiskCalculator.calculateR(world, pos, isWaxed, stateToCheck);
    }

    public static void setStage(World world, BlockPos pos, BlockState state, int newStage) {
        world.setBlockState(pos, state.with(MoldyBlock.STAGE, newStage));
        syncDoorHalf(world, pos, state, MoldyBlock.STAGE, newStage);
    }

    public static void setWaxed(World world, BlockPos pos, BlockState state, boolean isWaxed) {
        world.setBlockState(pos, state.with(MoldyBlock.WAXED, isWaxed));
        syncDoorHalf(world, pos, state, MoldyBlock.WAXED, isWaxed);
    }

    private static <T extends Comparable<T>> void syncDoorHalf(World world, BlockPos pos, BlockState state,
            Property<T> property, T value) {
        if (state.getBlock() instanceof DoorBlock) {
            DoubleBlockHalf half = state.get(DoorBlock.HALF);
            BlockPos otherPos = half == DoubleBlockHalf.LOWER ? pos.up() : pos.down();
            BlockState otherState = world.getBlockState(otherPos);
            if (otherState.isOf(state.getBlock()) && otherState.get(DoorBlock.HALF) != half) {
                BlockState newState = otherState.with(property, value);

                // Ensure we also sync the structural tag removal if the original block had it removed
                if (state.contains(MoldyBlock.STRUCTURAL) && !state.get(MoldyBlock.STRUCTURAL) &&
                        otherState.contains(MoldyBlock.STRUCTURAL) && otherState.get(MoldyBlock.STRUCTURAL)) {
                    newState = newState.with(MoldyBlock.STRUCTURAL, false);
                }

                world.setBlockState(otherPos, newState);
            }
        }
    }

    public static void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        if (!config.general.enable_mold_growth)
            return;

        int currentStage = state.get(MoldyBlock.STAGE);

        // CRITICAL OPTIMIZATION: If the block is already at max stage (3),
        // it makes no sense to calculate R.
        if (currentStage == 3)
            return;

        double R = calculateR(world, pos, false, state);

        if (config.general.show_debug_in_chat) {
            System.out.println("Mold tick at " + pos + ", R = " + R);
        }

        if (R > config.general.infection_threshold) {
            if (currentStage < 3) {
                setStage(world, pos, state, currentStage + 1);
            }
        }
    }

    public static void grantAdvancement(PlayerEntity player, String advancementName) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            AdvancementEntry entry = serverPlayer.getServer().getAdvancementLoader()
                    .get(Identifier.of(SporesShadows.MOD_ID, advancementName));
            if (entry != null) {
                AdvancementProgress progress = serverPlayer.getAdvancementTracker()
                        .getProgress(entry);
                if (!progress.isDone()) {
                    for (String criterion : progress.getUnobtainedCriteria()) {
                        serverPlayer.getAdvancementTracker().grantCriterion(entry, criterion);
                    }
                }
            }
        }
    }

    public static ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        int stage = state.contains(MoldyBlock.STAGE) ? state.get(MoldyBlock.STAGE) : 0;
        boolean waxed = state.contains(MoldyBlock.WAXED) && state.get(MoldyBlock.WAXED);

        List<Item> items = ModBlocks.MOLDY_ITEMS_BY_BLOCK.get(block);
        if (items != null && items.size() == 7) {
            if (stage == 0 && !waxed) {
                Block moldyBlock = ModBlocks.WAXED_TO_MOLDY.getOrDefault(block, block);
                Block vanillaBlock = ModBlocks.MOLDY_TO_VANILLA.get(moldyBlock);
                if (vanillaBlock != null) {
                    return new ItemStack(vanillaBlock.asItem());
                }
            }
            int index = (stage == 0) ? 0 : (stage * 2 - (waxed ? 0 : 1));
            if (index >= 0 && index < items.size()) {
                return new ItemStack(items.get(index));
            }
        }
        return new ItemStack(block.asItem());
    }
}
