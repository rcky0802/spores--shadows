package moldmod.block.entity;

import moldmod.block.MoldyBlock;
import moldmod.block.MoldyJukeboxBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MoldyJukeboxBlockEntity extends JukeboxBlockEntity {

    private int moldStage = 0;
    private boolean moldWaxed = false;

    public MoldyJukeboxBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        if (state != null) {
            if (state.contains(MoldyBlock.STAGE)) {
                this.moldStage = state.get(MoldyBlock.STAGE);
            }
            if (state.contains(MoldyBlock.WAXED)) {
                this.moldWaxed = state.get(MoldyBlock.WAXED);
            }
        }
    }

    public int getMoldStage() {
        if (hasWorld()) {
            BlockState state = getCachedState();
            if (state != null && state.contains(MoldyBlock.STAGE)) {
                return state.get(MoldyBlock.STAGE);
            }
        }
        return moldStage;
    }

    public void setMoldStage(int moldStage) {
        this.moldStage = moldStage;
        markDirty();
    }

    public boolean isMoldWaxed() {
        if (hasWorld()) {
            BlockState state = getCachedState();
            if (state != null && state.contains(MoldyBlock.WAXED)) {
                return state.get(MoldyBlock.WAXED);
            }
        }
        return moldWaxed;
    }

    public void setMoldWaxed(boolean moldWaxed) {
        this.moldWaxed = moldWaxed;
        markDirty();
    }

    @Override
    public BlockEntityType<?> getType() {
        return ModBlockEntities.MOLDY_JUKEBOX != null ? ModBlockEntities.MOLDY_JUKEBOX : super.getType();
    }

    @Override
    public boolean supports(BlockState state) {
        return state.getBlock() instanceof MoldyJukeboxBlock || super.supports(state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.putInt("MoldStage", getMoldStage());
        nbt.putBoolean("MoldWaxed", isMoldWaxed());
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        if (nbt.contains("MoldStage")) {
            this.moldStage = nbt.getInt("MoldStage");
        }
        if (nbt.contains("MoldWaxed")) {
            this.moldWaxed = nbt.getBoolean("MoldWaxed");
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, MoldyJukeboxBlockEntity blockEntity) {
        JukeboxBlockEntity.tick(world, pos, state, blockEntity);
        if (!world.isClient) {
            int stage = blockEntity.getMoldStage();
            boolean waxed = blockEntity.isMoldWaxed();
            if (!waxed && stage == 3 && state.contains(MoldyJukeboxBlock.HAS_RECORD) && state.get(MoldyJukeboxBlock.HAS_RECORD)) {
                if (world.random.nextInt(60) == 0) {
                    world.playSound(null, pos, SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.RECORDS, 0.4f, 1.8f);
                }
            }
        }
    }
}
