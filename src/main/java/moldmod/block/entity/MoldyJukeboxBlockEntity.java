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
    private int jamTicksRemaining = -1;

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

    public int getJamTicksRemaining() {
        return jamTicksRemaining;
    }

    public void setJamTicksRemaining(int jamTicksRemaining) {
        this.jamTicksRemaining = jamTicksRemaining;
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
        if (jamTicksRemaining > 0) {
            nbt.putInt("JamTicksRemaining", jamTicksRemaining);
        }
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
        if (nbt.contains("JamTicksRemaining")) {
            this.jamTicksRemaining = nbt.getInt("JamTicksRemaining");
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, MoldyJukeboxBlockEntity blockEntity) {
        JukeboxBlockEntity.tick(world, pos, state, blockEntity);

        if (world.isClient) {
            return;
        }

        boolean isPlaying = blockEntity.getManager().isPlaying();
        if (!isPlaying) {
            blockEntity.jamTicksRemaining = -1;
            return;
        }

        int stage = blockEntity.getMoldStage();
        boolean waxed = blockEntity.isMoldWaxed();

        // Waxed jukeboxes or pristine Stage 0 wood run with 100% clean vanilla mechanics
        if (waxed || stage == 0) {
            blockEntity.jamTicksRemaining = -1;
            return;
        }

        net.minecraft.server.world.ServerWorld serverWorld = (net.minecraft.server.world.ServerWorld) world;
        net.minecraft.util.math.random.Random rng = world.random;

        switch (stage) {
            case 1 -> {
                // STAGE 1 (Umidità e lieve deformazione della cassa armonica):
                // Piccoli scricchiolii del legno e scatti occasionali della testina
                if (rng.nextInt(70) == 0) {
                    world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5,
                            SoundEvents.BLOCK_WOOD_HIT, SoundCategory.RECORDS, 0.35F, 0.75F + rng.nextFloat() * 0.35F);
                }
                if (rng.nextInt(150) == 0) {
                    world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5,
                            SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.RECORDS, 0.25F, 1.5F);
                }
            }

            case 2 -> {
                // STAGE 2 (Muffa viva & solchi impastati):
                // Attrito viscoso, soffio di spore e micro-rallentamenti del giradischi
                if (rng.nextInt(35) == 0) {
                    world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5,
                            SoundEvents.BLOCK_MUD_STEP, SoundCategory.RECORDS, 0.55F, 0.8F + rng.nextFloat() * 0.3F);
                }
                if (rng.nextInt(80) == 0) {
                    world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5,
                            SoundEvents.BLOCK_SCULK_CHARGE, SoundCategory.RECORDS, 0.25F, 1.4F);
                }
                if (rng.nextInt(110) == 0) {
                    world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5,
                            SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.RECORDS, 0.3F, 0.65F);
                }

                // Emissione di spore visibili dalla fessura
                if (rng.nextInt(20) == 0) {
                    serverWorld.spawnParticles(net.minecraft.particle.ParticleTypes.FALLING_SPORE_BLOSSOM,
                            pos.getX() + 0.5, pos.getY() + 0.9, pos.getZ() + 0.5, 2, 0.15, 0.05, 0.15, 0.0);
                    serverWorld.spawnParticles(net.minecraft.particle.ParticleTypes.MYCELIUM,
                            pos.getX() + 0.5, pos.getY() + 0.9, pos.getZ() + 0.5, 2, 0.15, 0.05, 0.15, 0.01);
                }
            }

            case 3 -> {
                // STAGE 3 (Completamente marcito & inceppamento meccanico):
                // Stridio continuo da attrito severo, polvere fungina e blocco forzato del motore
                if (blockEntity.jamTicksRemaining < 0) {
                    blockEntity.jamTicksRemaining = 140 + rng.nextInt(81);
                }

                blockEntity.jamTicksRemaining--;

                // Layering continuo di attriti rovinosi
                if (rng.nextInt(20) == 0) {
                    world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5,
                            SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.RECORDS, 0.55F, 1.8F);
                    world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5,
                            SoundEvents.BLOCK_SLIME_BLOCK_FALL, SoundCategory.RECORDS, 0.5F, 0.55F);
                }
                if (rng.nextInt(30) == 0) {
                    world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5,
                            SoundEvents.BLOCK_FUNGUS_BREAK, SoundCategory.RECORDS, 0.45F, 0.75F);
                }

                // Particelle di cenere e micelio che fuoriescono dall'apparecchio marcio
                if (rng.nextInt(10) == 0) {
                    serverWorld.spawnParticles(net.minecraft.particle.ParticleTypes.ASH,
                            pos.getX() + 0.5, pos.getY() + 0.9, pos.getZ() + 0.5, 3, 0.2, 0.05, 0.2, 0.02);
                    serverWorld.spawnParticles(net.minecraft.particle.ParticleTypes.MYCELIUM,
                            pos.getX() + 0.5, pos.getY() + 0.9, pos.getZ() + 0.5, 2, 0.2, 0.05, 0.2, 0.02);
                }

                // INCEPPAMENTO DEL MECCANISMO
                if (blockEntity.jamTicksRemaining <= 0) {
                    // Arresto forzato della riproduzione audio
                    blockEntity.getManager().stopPlaying(world, state);
                    blockEntity.jamTicksRemaining = -1;

                    // Suono violento di cedimento / arresto meccanico
                    world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5,
                            SoundEvents.BLOCK_WOOD_BREAK, SoundCategory.RECORDS, 1.0F, 0.55F);
                    world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5,
                            SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.RECORDS, 0.8F, 0.5F);
                    world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5,
                            SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.RECORDS, 0.9F, 1.95F);

                    // Sbuffo di fumo e polvere fungina dall'ingranaggio bloccato
                    serverWorld.spawnParticles(net.minecraft.particle.ParticleTypes.LARGE_SMOKE,
                            pos.getX() + 0.5, pos.getY() + 0.95, pos.getZ() + 0.5, 6, 0.15, 0.05, 0.15, 0.02);
                    serverWorld.spawnParticles(net.minecraft.particle.ParticleTypes.ASH,
                            pos.getX() + 0.5, pos.getY() + 0.95, pos.getZ() + 0.5, 12, 0.2, 0.05, 0.2, 0.04);
                }
            }
        }
    }
}
