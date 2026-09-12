package moldmod.block.purifier;

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
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class AirPurifierBlock extends BlockWithEntity {

    public static final MapCodec<AirPurifierBlock> CODEC = createCodec(AirPurifierBlock::new);

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final EnumProperty<PurifierStatus> STATUS = EnumProperty.of("status", PurifierStatus.class);
    public static final IntProperty FILTER_LEVEL = IntProperty.of("filter_level", 0, 4);

    public AirPurifierBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(STATUS, PurifierStatus.OFF)
                .with(FILTER_LEVEL, 0));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AirPurifierBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.AIR_PURIFIER, (w, pos, st, be) -> be.tick(w, pos, st));
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
        builder.add(FACING, STATUS, FILTER_LEVEL);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof AirPurifierBlockEntity purifierBe) {
                player.openHandledScreen(purifierBe);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof AirPurifierBlockEntity purifierBe) {
                ItemScatterer.spawn(world, pos, purifierBe);
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
        if (be instanceof AirPurifierBlockEntity purifierBe) {
            return purifierBe.getComparatorOutput();
        }
        return 0;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        PurifierStatus status = state.get(STATUS);

        if (status == PurifierStatus.RUNNING) {
            Direction facing = state.get(FACING);

            // 1. Emissione aria tersa/pulita verso l'alto dalla griglia superiore
            double topX = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.4;
            double topY = pos.getY() + 1.0;
            double topZ = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.4;
            world.addParticle(ParticleTypes.CLOUD, topX, topY, topZ, 0.0, 0.03 + random.nextDouble() * 0.02, 0.0);

            // 2. Aspirazione frontale di pulviscolo/spore verso il centro della turbina
            double offsetX = facing.getOffsetX() * 0.52;
            double offsetZ = facing.getOffsetZ() * 0.52;
            double sideSpread = (random.nextDouble() - 0.5) * 0.4;
            double frontX = pos.getX() + 0.5 + offsetX + (facing.getAxis() == Direction.Axis.Z ? sideSpread : 0);
            double frontY = pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 0.4;
            double frontZ = pos.getZ() + 0.5 + offsetZ + (facing.getAxis() == Direction.Axis.X ? sideSpread : 0);

            // Vettore di velocità orientato verso l'interno della bocchetta
            world.addParticle(ParticleTypes.ASH, frontX, frontY, frontZ,
                    -facing.getOffsetX() * 0.04, 0.0, -facing.getOffsetZ() * 0.04);

            // 3. Suono continuo sommesso della turbina
            if (random.nextFloat() < 0.12f) {
                world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        SoundEvents.BLOCK_BLASTFURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS,
                        0.25f, 0.85f + random.nextFloat() * 0.2f, false);
            }
        }
    }
}
