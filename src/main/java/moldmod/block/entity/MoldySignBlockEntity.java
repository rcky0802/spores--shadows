package moldmod.block.entity;

import moldmod.block.MoldyBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

public class MoldySignBlockEntity extends SignBlockEntity {

    private int moldStage = 0;
    private boolean moldWaxed = false;

    public MoldySignBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.MOLDY_SIGN, pos, state);
    }

    public MoldySignBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
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
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        BlockState state = getCachedState();
        if (state != null) {
            if (state.contains(MoldyBlock.STAGE)) {
                this.moldStage = state.get(MoldyBlock.STAGE);
            }
            if (state.contains(MoldyBlock.WAXED)) {
                this.moldWaxed = state.get(MoldyBlock.WAXED);
            }
        }
        nbt.putInt("MoldStage", this.moldStage);
        nbt.putBoolean("MoldWaxed", this.moldWaxed);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains("MoldStage")) {
            this.moldStage = nbt.getInt("MoldStage");
        }
        if (nbt.contains("MoldWaxed")) {
            this.moldWaxed = nbt.getBoolean("MoldWaxed");
        }
    }
}
