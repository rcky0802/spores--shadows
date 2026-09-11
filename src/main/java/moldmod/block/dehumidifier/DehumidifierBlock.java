package moldmod.block.dehumidifier;

import com.mojang.serialization.MapCodec;
import moldmod.block.entity.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class DehumidifierBlock extends BlockWithEntity {

    public static final MapCodec<DehumidifierBlock> CODEC = createCodec(DehumidifierBlock::new);

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final EnumProperty<DehumidifierStatus> STATUS = EnumProperty.of("status", DehumidifierStatus.class);
    public static final IntProperty WATER_LEVEL = IntProperty.of("water_level", 0, 4);
    public static final EnumProperty<DehumidifierMode> MODE = EnumProperty.of("mode", DehumidifierMode.class);

    public DehumidifierBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(STATUS, DehumidifierStatus.OFF)
                .with(WATER_LEVEL, 0)
                .with(MODE, DehumidifierMode.DEHUMIDIFY));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DehumidifierBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.DEHUMIDIFIER, (w, pos, st, be) -> be.tick(w, pos, st));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, STATUS, WATER_LEVEL, MODE);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof DehumidifierBlockEntity dehumidifierBe) {
                ItemStack handStack = player.getStackInHand(Hand.MAIN_HAND);

                // Interazione con Secchio Vuoto: se ci sono almeno 1000 mB, svuota e restituisce secchio d'acqua
                if (handStack.isOf(Items.BUCKET) && dehumidifierBe.getWaterMb() >= 1000) {
                    dehumidifierBe.drainWater(1000);
                    if (!player.getAbilities().creativeMode) {
                        handStack.decrement(1);
                        ItemStack waterBucket = new ItemStack(Items.WATER_BUCKET);
                        if (handStack.isEmpty()) {
                            player.setStackInHand(Hand.MAIN_HAND, waterBucket);
                        } else if (!player.getInventory().insertStack(waterBucket)) {
                            player.dropItem(waterBucket, false);
                        }
                    }
                    world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    return ActionResult.SUCCESS;
                }

                // Interazione con Secchio d'Acqua: se c'è spazio nel serbatoio (almeno 1000 mB), riempie e restituisce secchio vuoto
                if (handStack.isOf(Items.WATER_BUCKET) && dehumidifierBe.getWaterMb() <= dehumidifierBe.getCapacityMb() - 1000) {
                    dehumidifierBe.addWater(1000);
                    if (!player.getAbilities().creativeMode) {
                        handStack.decrement(1);
                        ItemStack emptyBucket = new ItemStack(Items.BUCKET);
                        if (handStack.isEmpty()) {
                            player.setStackInHand(Hand.MAIN_HAND, emptyBucket);
                        } else if (!player.getInventory().insertStack(emptyBucket)) {
                            player.dropItem(emptyBucket, false);
                        }
                    }
                    world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    return ActionResult.SUCCESS;
                }

                // Altrimenti, apri la GUI
                player.openHandledScreen(dehumidifierBe);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof DehumidifierBlockEntity dehumidifierBe) {
                ItemScatterer.spawn(world, pos, dehumidifierBe);
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof DehumidifierBlockEntity dehumidifierBe) {
            return dehumidifierBe.getComparatorOutput();
        }
        return 0;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        DehumidifierStatus status = state.get(STATUS);

        if (status == DehumidifierStatus.RUNNING) {
            DehumidifierMode mode = state.contains(MODE) ? state.get(MODE) : DehumidifierMode.DEHUMIDIFY;

            if (mode == DehumidifierMode.HUMIDIFY) {
                // Modalità Umidificazione: Vapore denso (cloud/smoke) e goccioline d'acqua che salgono dalla griglia
                double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.45;
                double y = pos.getY() + 1.02;
                double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.45;
                world.addParticle(ParticleTypes.CLOUD, x, y, z, 0.0, 0.04 + random.nextDouble() * 0.03, 0.0);
                if (random.nextFloat() < 0.35f) {
                    world.addParticle(ParticleTypes.SPLASH, x, y, z, (random.nextDouble() - 0.5) * 0.05, 0.06, (random.nextDouble() - 0.5) * 0.05);
                }
                // Sibilo vapore acqueo
                if (random.nextFloat() < 0.08f) {
                    world.playSound(pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5,
                            SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS,
                            0.2f, 1.3f + random.nextFloat() * 0.3f, false);
                }
            } else {
                // Modalità Deumidificazione: Crepitio leggero, gorgoglio di condensa e fumo/aria secca
                if (random.nextFloat() < 0.15f) {
                    world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS,
                            0.3f, 0.8f + random.nextFloat() * 0.2f, false);
                }
                if (random.nextFloat() < 0.05f) {
                    world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS,
                            0.15f, 1.2f + random.nextFloat() * 0.2f, false);
                }
                double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.4;
                double y = pos.getY() + 1.0;
                double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.4;
                world.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.02, 0.0);
            }
        } else if (status == DehumidifierStatus.FULL) {
            // Gocciolamento quando saturo
            if (random.nextFloat() < 0.2f) {
                double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
                double y = pos.getY() - 0.05;
                double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
                world.addParticle(ParticleTypes.DRIPPING_WATER, x, y, z, 0.0, 0.0, 0.0);
            }
        }
    }
}
