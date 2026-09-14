package moldmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class MoldyNoteBlock extends NoteBlock implements MoldyBlock {

    public MoldyNoteBlock(Settings settings) {
        super(settings);
        this.setDefaultState(MoldyBlockHelper.initMoldyDefaultState(this.getDefaultState()));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        MoldyBlockHelper.appendMoldyProperties(builder);
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return MoldyBlockHelper.hasRandomTicks(state);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);
        MoldyBlockHelper.randomTick(state, world, pos, random);
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return MoldyBlockHelper.getPickStack(world, pos, state);
    }

    @Override
    protected boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        int stage = state.contains(MoldyBlock.STAGE) ? state.get(MoldyBlock.STAGE) : 0;

        // Stage 0 is clean/healthy wood -> delegate entirely to vanilla
        if (stage == 0) {
            return super.onSyncedBlockEvent(state, world, pos, type, data);
        }

        NoteBlockInstrument instrument = state.get(INSTRUMENT);
        RegistryEntry<SoundEvent> sound = getSoundForInstrument(instrument, world, pos);

        if (instrument.canBePitched()) {
            int note = state.get(NOTE);
            float basePitch = getNotePitch(note);

            if (stage == 1) {
                // Tainted: slightly flat microtonal detune (~-35 cents), dampened volume
                float pitch = Math.max(0.5F, basePitch * 0.98F);
                world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5,
                        sound, SoundCategory.RECORDS, 2.2F, pitch, world.random.nextLong());
                world.addParticle(ParticleTypes.NOTE, (double)pos.getX() + 0.5, (double)pos.getY() + 1.2, (double)pos.getZ() + 0.5, (double)note / 24.0, 0.0, 0.0);
                return true;
            } else if (stage == 2) {
                // Moldy: noticeably flat microtonal detune (~-89 cents), dampened volume + fungal spores
                float pitch = Math.max(0.5F, basePitch * 0.95F);
                world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5,
                        sound, SoundCategory.RECORDS, 2.0F, pitch, world.random.nextLong());
                world.addParticle(ParticleTypes.NOTE, (double)pos.getX() + 0.5, (double)pos.getY() + 1.2, (double)pos.getZ() + 0.5, (double)note / 24.0, 0.0, 0.0);
                world.addParticle(ParticleTypes.FALLING_SPORE_BLOSSOM, (double)pos.getX() + 0.5, (double)pos.getY() + 1.2, (double)pos.getZ() + 0.5, 0.0, 0.0, 0.0);
                world.addParticle(ParticleTypes.MYCELIUM, (double)pos.getX() + 0.5, (double)pos.getY() + 1.2, (double)pos.getZ() + 0.5, 0.0, 0.02, 0.0);
                return true;
            } else {
                // Rotten: resonance chamber ruined, dull wooden thud & mycelium / ash burst instead of musical note
                world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5,
                        SoundEvents.BLOCK_WOOD_BREAK, SoundCategory.RECORDS, 1.2F, 0.6F, world.random.nextLong());
                world.addParticle(ParticleTypes.MYCELIUM, (double)pos.getX() + 0.5, (double)pos.getY() + 1.2, (double)pos.getZ() + 0.5, 0.0, 0.05, 0.0);
                world.addParticle(ParticleTypes.ASH, (double)pos.getX() + 0.5, (double)pos.getY() + 1.2, (double)pos.getZ() + 0.5, 0.0, 0.05, 0.0);
                return true;
            }
        } else {
            // Unpitched instruments & mob heads
            if (stage == 3) {
                world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5,
                        SoundEvents.BLOCK_WOOD_BREAK, SoundCategory.RECORDS, 1.2F, 0.6F, world.random.nextLong());
                world.addParticle(ParticleTypes.MYCELIUM, (double)pos.getX() + 0.5, (double)pos.getY() + 1.2, (double)pos.getZ() + 0.5, 0.0, 0.05, 0.0);
                world.addParticle(ParticleTypes.ASH, (double)pos.getX() + 0.5, (double)pos.getY() + 1.2, (double)pos.getZ() + 0.5, 0.0, 0.05, 0.0);
                return true;
            }
            float pitch = stage == 1 ? 0.95F : 0.85F;
            float volume = stage == 1 ? 2.2F : 2.0F;
            world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5,
                    sound, SoundCategory.RECORDS, volume, pitch, world.random.nextLong());
            return true;
        }
    }

    private RegistryEntry<SoundEvent> getSoundForInstrument(NoteBlockInstrument instrument, World world, BlockPos pos) {
        if (instrument.hasCustomSound()) {
            if (world.getBlockEntity(pos.up()) instanceof net.minecraft.block.entity.SkullBlockEntity skull) {
                Identifier id = skull.getNoteBlockSound();
                if (id != null) {
                    return RegistryEntry.of(SoundEvent.of(id));
                }
            }
        }
        RegistryEntry<SoundEvent> sound = instrument.getSound();
        return sound != null ? sound : SoundEvents.BLOCK_NOTE_BLOCK_HARP;
    }
}
