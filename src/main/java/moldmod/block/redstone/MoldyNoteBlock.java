package moldmod.block.redstone;
import moldmod.block.core.MoldyBlock;
import moldmod.block.core.MoldyBlockHelper;

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
import net.minecraft.util.math.MathHelper;
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
        if (instrument.hasCustomSound()) {
            if (getCustomSound(world, pos) == null) {
                return false;
            }
        }

        int note = state.get(NOTE);

        playMoldyNote(world, pos, instrument, note, stage);

        // Visual particles feedback based on stage
        if (instrument.canBePitched() && stage <= 2) {
            world.addParticle(ParticleTypes.NOTE, (double)pos.getX() + 0.5, (double)pos.getY() + 1.2, (double)pos.getZ() + 0.5, (double)note / 24.0, 0.0, 0.0);
        }
        if (stage == 2) {
            world.addParticle(ParticleTypes.FALLING_SPORE_BLOSSOM, (double)pos.getX() + 0.5, (double)pos.getY() + 1.2, (double)pos.getZ() + 0.5, 0.0, 0.0, 0.0);
            world.addParticle(ParticleTypes.MYCELIUM, (double)pos.getX() + 0.5, (double)pos.getY() + 1.2, (double)pos.getZ() + 0.5, 0.0, 0.02, 0.0);
        } else if (stage == 3) {
            world.addParticle(ParticleTypes.MYCELIUM, (double)pos.getX() + 0.5, (double)pos.getY() + 1.2, (double)pos.getZ() + 0.5, 0.0, 0.05, 0.0);
            world.addParticle(ParticleTypes.ASH, (double)pos.getX() + 0.5, (double)pos.getY() + 1.2, (double)pos.getZ() + 0.5, 0.0, 0.05, 0.0);
        }

        return true;
    }

    /**
     * Core audio distortion logic for moldy note blocks.
     * Manipulates volume decay, pitch sagging (slackened cords / swollen casing)
     * and layers vanilla transient sounds to simulate dampening and fungal rot.
     */
    public void playMoldyNote(World world, BlockPos pos, NoteBlockInstrument instrument, int noteIndex, int moldStage) {
        Random rng = world.random;
        RegistryEntry<SoundEvent> instrumentSound = getSoundForInstrument(instrument, world, pos);

        // 1. Calcolo del pitch standard temperato equabile
        float basePitch = instrument.canBePitched()
                ? (float) Math.pow(2.0, (noteIndex - 12) / 12.0)
                : 1.0F;

        switch (moldStage) {
            case 1 -> {
                // STAGE 1 (Umidita' iniziale): Pitch instabile calante + lieve smorzamento
                // Pitch abbassato di ~3% con micro-oscillazione casuale (corda allentata/legno gonfio)
                float pitchDetune = basePitch * (0.97F + (rng.nextFloat() - 0.5F) * 0.04F);
                float finalPitch = MathHelper.clamp(pitchDetune, 0.5F, 2.0F);

                // Volume leggermente ridotto (2.3F vs 3.0F vanilla)
                world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5,
                        instrumentSound.value(), SoundCategory.RECORDS, 2.3F, finalPitch);

                // Transiente di legno umido e pesante
                world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5,
                        SoundEvents.BLOCK_WOOD_HIT, SoundCategory.RECORDS, 0.45F, 0.65F);
            }

            case 2 -> {
                // STAGE 2 (Muffa viva): Dissonanza, filtro passa-basso simulato e transiente viscoso
                // Due voci sfasate verso il basso per creare battimenti sgradevoli (cassa armonica marcita)
                float primaryPitch = MathHelper.clamp(basePitch * 0.91F, 0.5F, 2.0F);
                float parasitePitch = MathHelper.clamp(basePitch * 0.86F, 0.5F, 2.0F);

                world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5,
                        instrumentSound.value(), SoundCategory.RECORDS, 1.3F, primaryPitch);
                world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5,
                        instrumentSound.value(), SoundCategory.RECORDS, 0.9F, parasitePitch);

                // Sovrapposizione di rumore organico: squelch bagnato + sibilo soffocato
                world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5,
                        SoundEvents.BLOCK_MUD_STEP, SoundCategory.RECORDS, 0.7F, 1.1F);
                world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5,
                        SoundEvents.BLOCK_SCULK_CHARGE, SoundCategory.RECORDS, 0.25F, 1.8F);
            }

            case 3 -> {
                // STAGE 3 (Completamente marcito/otturato): Smorzamento quasi totale della nota
                // La nota nativa viene spinta al pitch minimo di OpenAL a volume bassissimo
                world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5,
                        instrumentSound.value(), SoundCategory.RECORDS, 0.35F, 0.5F);

                // La risonanza e' morta: viene sostituita da un impatto sordo e gommoso
                world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5,
                        SoundEvents.BLOCK_SLIME_BLOCK_FALL, SoundCategory.RECORDS, 1.2F, 0.55F);
                world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5,
                        SoundEvents.BLOCK_FUNGUS_BREAK, SoundCategory.RECORDS, 0.8F, 0.7F);
            }

            default -> {
                // STAGE 0 (Incontaminato): Comportamento vanilla al 100%
                world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5,
                        instrumentSound.value(), SoundCategory.RECORDS, 3.0F, basePitch);
            }
        }
    }

    private RegistryEntry<SoundEvent> getSoundForInstrument(NoteBlockInstrument instrument, World world, BlockPos pos) {
        if (instrument.hasCustomSound()) {
            Identifier id = getCustomSound(world, pos);
            if (id != null) {
                return RegistryEntry.of(SoundEvent.of(id));
            }
        }
        RegistryEntry<SoundEvent> sound = instrument.getSound();
        return sound != null ? sound : SoundEvents.BLOCK_NOTE_BLOCK_HARP;
    }

    private Identifier getCustomSound(World world, BlockPos pos) {
        if (world.getBlockEntity(pos.up()) instanceof net.minecraft.block.entity.SkullBlockEntity skull) {
            return skull.getNoteBlockSound();
        }
        return null;
    }
}
