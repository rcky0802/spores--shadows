package moldmod.integration.jade;

import moldmod.SporesShadows;
import moldmod.block.core.MoldyBlock;
import moldmod.block.redstone.MoldyNoteBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.state.property.Properties;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum MoldyNoteBlockProvider implements IBlockComponentProvider {
    INSTANCE;

    private static final String[] PITCH_NAMES = {
        "F♯/G♭", "G", "G♯/A♭", "A", "A♯/B♭", "B", "C", "C♯/D♭", "D", "D♯/E♭", "E", "F"
    };

    public static String getNoteName(int note) {
        int index = ((note % 12) + 12) % 12;
        return PITCH_NAMES[index];
    }

    public static Formatting getOctaveColor(int note) {
        int octave = note / 12;
        if (octave == 0) return Formatting.AQUA;
        if (octave == 1) return Formatting.GREEN;
        return Formatting.YELLOW;
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        String[] parts = str.split("_");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) sb.append(" ");
            if (!parts[i].isEmpty()) {
                sb.append(Character.toUpperCase(parts[i].charAt(0)))
                  .append(parts[i].substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        BlockState state = accessor.getBlockState();
        if (!(state.getBlock() instanceof MoldyNoteBlock)) {
            return;
        }

        int stage = state.contains(MoldyBlock.STAGE) ? state.get(MoldyBlock.STAGE) : 0;
        if (stage == 0) {
            return;
        }

        NoteBlockInstrument instrument = state.contains(Properties.INSTRUMENT) ? state.get(Properties.INSTRUMENT) : NoteBlockInstrument.HARP;
        String instName = capitalize(instrument.asString());

        MutableText line = Text.literal(instName).formatted(Formatting.WHITE);

        if (instrument.canBePitched()) {
            int note = state.contains(Properties.NOTE) ? state.get(Properties.NOTE) : 0;
            String noteName = getNoteName(note);
            Formatting noteColor = getOctaveColor(note);
            line.append(Text.literal(" "));
            line.append(Text.literal(noteName).formatted(noteColor));
        }

        line.append(Text.literal(" "));
        if (stage == 1) {
            line.append(Text.literal("(").formatted(Formatting.DARK_GRAY));
            line.append(Text.translatable("tooltip." + SporesShadows.MOD_ID + ".jade.noteblock.muffled").formatted(Formatting.YELLOW));
            line.append(Text.literal(")").formatted(Formatting.DARK_GRAY));
        } else if (stage == 2) {
            line.append(Text.literal("(").formatted(Formatting.DARK_GRAY));
            line.append(Text.translatable("tooltip." + SporesShadows.MOD_ID + ".jade.noteblock.dampened").formatted(Formatting.GOLD));
            line.append(Text.literal(")").formatted(Formatting.DARK_GRAY));
        } else {
            line.append(Text.literal("(").formatted(Formatting.DARK_GRAY));
            line.append(Text.translatable("tooltip." + SporesShadows.MOD_ID + ".jade.noteblock.rotten").formatted(Formatting.RED, Formatting.ITALIC));
            line.append(Text.literal(")").formatted(Formatting.DARK_GRAY));
        }

        tooltip.add(line);
    }

    @Override
    public Identifier getUid() {
        return Identifier.of(SporesShadows.MOD_ID, "moldy_noteblock");
    }
}
