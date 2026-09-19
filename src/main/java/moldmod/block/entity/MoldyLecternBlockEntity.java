package moldmod.block.entity;

import moldmod.block.MoldyBlock;
import moldmod.block.MoldyLecternBlock;
import moldmod.screen.MoldStageHolder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;

public class MoldyLecternBlockEntity extends LecternBlockEntity {

    private int moldStage = 0;
    private boolean moldWaxed = false;

    public MoldyLecternBlockEntity(BlockPos pos, BlockState state) {
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
        return ModBlockEntities.MOLDY_LECTERN != null ? ModBlockEntities.MOLDY_LECTERN : super.getType();
    }

    @Override
    public boolean supports(BlockState state) {
        return state.getBlock() instanceof MoldyLecternBlock || super.supports(state);
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        ScreenHandler handler = super.createMenu(syncId, playerInventory, player);
        if (handler instanceof MoldStageHolder holder) {
            holder.spores_shadows$setMoldStage(getMoldStage());
        }
        return handler;
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
