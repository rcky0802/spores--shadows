package moldmod.block.entity;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.block.MoldyTrappedChestBlock;
import moldmod.config.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MoldyTrappedChestBlockEntity extends MoldyChestBlockEntity {

    private boolean jammed = false;

    public MoldyTrappedChestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MOLDY_TRAPPED_CHEST != null ? ModBlockEntities.MOLDY_TRAPPED_CHEST : BlockEntityType.TRAPPED_CHEST, pos, state);
    }

    public boolean isJammed() {
        return jammed;
    }

    public void setJammed(boolean jammed) {
        this.jammed = jammed;
    }

    @Override
    public boolean supports(BlockState state) {
        return state.getBlock() instanceof MoldyTrappedChestBlock || super.supports(state);
    }

    @Override
    protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
        super.onViewerCountUpdate(world, pos, state, oldViewerCount, newViewerCount);

        if (oldViewerCount == 0 && newViewerCount > 0) {
            // First player opening the chest -> roll for jamming on server
            if (!world.isClient()) {
                int stage = getMoldStage();
                float failChance = getFailChanceForStage(stage);

                if (failChance > 0.0f && world.random.nextFloat() < failChance) {
                    this.jammed = true;
                    // Audio feedback: dull fail click
                    world.playSound(null, pos, SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.BLOCKS, 0.8f, 0.7f);
                    world.playSound(null, pos, SoundEvents.BLOCK_WOOD_HIT, SoundCategory.BLOCKS, 0.6f, 0.5f);
                    // Particles: spore puff from lock
                    if (world instanceof ServerWorld serverWorld) {
                        serverWorld.spawnParticles(ParticleTypes.MYCELIUM,
                                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                                12, 0.2, 0.2, 0.2, 0.02);
                        serverWorld.spawnParticles(ParticleTypes.ASH,
                                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                                8, 0.15, 0.15, 0.15, 0.01);
                    }
                } else {
                    this.jammed = false;
                }
            }
        } else if (newViewerCount == 0) {
            // All players closed the chest -> reset jammed state
            this.jammed = false;
        }

        if (oldViewerCount != newViewerCount) {
            Block block = state.getBlock();
            world.updateNeighborsAlways(pos, block);
            world.updateNeighborsAlways(pos.down(), block);
        }
    }

    private float getFailChanceForStage(int stage) {
        ModConfig config = null;
        try {
            config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        } catch (Exception ignored) {}

        float stage1 = (config != null && config.redstone != null) ? config.redstone.trapped_chest_fail_stage_1 : 0.15f;
        float stage2 = (config != null && config.redstone != null) ? config.redstone.trapped_chest_fail_stage_2 : 0.50f;
        float stage3 = (config != null && config.redstone != null) ? config.redstone.trapped_chest_fail_stage_3 : 0.85f;

        return switch (stage) {
            case 1 -> stage1;
            case 2 -> stage2;
            case 3 -> stage3;
            default -> 0.0f;
        };
    }
}
