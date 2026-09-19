package moldmod.block.entity;

import moldmod.block.MoldyBlock;
import moldmod.block.MoldyChestBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

public class MoldyChestBlockEntity extends ChestBlockEntity {

    private int moldStage = 0;
    private boolean moldWaxed = false;

    public MoldyChestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MOLDY_CHEST != null ? ModBlockEntities.MOLDY_CHEST : BlockEntityType.CHEST, pos, state);
        initMoldState(state);
    }

    protected MoldyChestBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        initMoldState(state);
    }

    private void initMoldState(BlockState state) {
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
        BlockState state = getCachedState();
        if (state != null && state.contains(MoldyBlock.STAGE)) {
            return state.get(MoldyBlock.STAGE);
        }
        return moldStage;
    }

    public void setMoldStage(int moldStage) {
        this.moldStage = moldStage;
        markDirty();
    }

    public boolean isMoldWaxed() {
        BlockState state = getCachedState();
        if (state != null && state.contains(MoldyBlock.WAXED)) {
            return state.get(MoldyBlock.WAXED);
        }
        return moldWaxed;
    }

    public void setMoldWaxed(boolean moldWaxed) {
        this.moldWaxed = moldWaxed;
        markDirty();
    }

    @Override
    public boolean supports(BlockState state) {
        return state.getBlock() instanceof MoldyChestBlock || super.supports(state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("MoldStage", getMoldStage());
        nbt.putBoolean("MoldWaxed", isMoldWaxed());
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
