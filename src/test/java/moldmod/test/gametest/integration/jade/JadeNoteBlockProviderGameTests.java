package moldmod.test.gametest.integration.jade;

import moldmod.SporesShadows;
import moldmod.block.ModBlocks;
import moldmod.block.MoldyBlock;
import moldmod.integration.jade.MoldyNoteBlockProvider;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.state.property.Properties;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;

import java.util.ArrayList;
import java.util.List;

public class JadeNoteBlockProviderGameTests {

        private static BlockAccessor createMockAccessor(net.minecraft.world.World world, BlockPos pos,
                        BlockState state) {
                return (BlockAccessor) java.lang.reflect.Proxy.newProxyInstance(
                                BlockAccessor.class.getClassLoader(),
                                new Class<?>[] { BlockAccessor.class },
                                (proxy, method, args) -> {
                                        String name = method.getName();
                                        if ("getLevel".equals(name))
                                                return world;
                                        if ("getPosition".equals(name))
                                                return pos;
                                        if ("getBlockState".equals(name))
                                                return state;
                                        if ("getBlock".equals(name))
                                                return state.getBlock();
                                        return null;
                                });
        }

        private static ITooltip createMockTooltip(List<Text> list) {
                return (ITooltip) java.lang.reflect.Proxy.newProxyInstance(
                                ITooltip.class.getClassLoader(),
                                new Class<?>[] { ITooltip.class },
                                (proxy, method, args) -> {
                                        if ("add".equals(method.getName()) && args != null && args.length > 0
                                                        && args[0] instanceof Text text) {
                                                list.add(text);
                                        }
                                        return null;
                                });
        }

        @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
        public void testNoteBlockProviderUid(TestContext context) {
                Identifier uid = MoldyNoteBlockProvider.INSTANCE.getUid();
                context.assertTrue(uid.getNamespace().equals(SporesShadows.MOD_ID),
                                "Provider namespace must be " + SporesShadows.MOD_ID + ", got: " + uid.getNamespace());
                context.assertTrue(uid.getPath().equals("moldy_noteblock"),
                                "Provider path must be 'moldy_noteblock', got: " + uid.getPath());
                context.complete();
        }

        @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
        public void testNoteCalculations(TestContext context) {
                // Test standard Jade pitch names
                context.assertTrue("F♯/G♭".equals(MoldyNoteBlockProvider.getNoteName(0)), "Note 0 must be F♯/G♭");
                context.assertTrue("C".equals(MoldyNoteBlockProvider.getNoteName(6)), "Note 6 must be C");
                context.assertTrue("F♯/G♭".equals(MoldyNoteBlockProvider.getNoteName(12)), "Note 12 must be F♯/G♭");
                context.assertTrue("F♯/G♭".equals(MoldyNoteBlockProvider.getNoteName(24)), "Note 24 must be F♯/G♭");

                // Test octave colors
                context.assertTrue(MoldyNoteBlockProvider.getOctaveColor(0) == Formatting.AQUA,
                                "Octave 0 must be AQUA");
                context.assertTrue(MoldyNoteBlockProvider.getOctaveColor(11) == Formatting.AQUA,
                                "Octave 0 upper bound must be AQUA");
                context.assertTrue(MoldyNoteBlockProvider.getOctaveColor(12) == Formatting.GREEN,
                                "Octave 1 lower bound must be GREEN");
                context.assertTrue(MoldyNoteBlockProvider.getOctaveColor(23) == Formatting.GREEN,
                                "Octave 1 upper bound must be GREEN");
                context.assertTrue(MoldyNoteBlockProvider.getOctaveColor(24) == Formatting.YELLOW,
                                "Octave 2 must be YELLOW");

                context.complete();
        }

        @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
        public void testNoteBlockTooltipOutputAcrossStages(TestContext context) {
                BlockPos pos = new BlockPos(1, 1, 1);
                BlockState moldyNoteBlock = ModBlocks.MOLDY_NOTE_BLOCK.getDefaultState()
                                .with(Properties.INSTRUMENT, NoteBlockInstrument.HARP)
                                .with(Properties.NOTE, 12); // F♯/G♭ (Octave 1)

                // Stage 0: Provider adds nothing (leaves vanilla Jade intact)
                List<Text> lines0 = new ArrayList<>();
                MoldyNoteBlockProvider.INSTANCE.appendTooltip(createMockTooltip(lines0),
                                createMockAccessor(context.getWorld(), pos,
                                                moldyNoteBlock.with(MoldyBlock.STAGE, 0).with(MoldyBlock.WAXED, false)),
                                null);
                context.assertTrue(lines0.isEmpty(),
                                "Stage 0 should not produce custom line (handled by vanilla Jade)");

                // Stage 1 (Tainted): Shows note F♯/G♭ and muffled descriptor
                List<Text> lines1 = new ArrayList<>();
                MoldyNoteBlockProvider.INSTANCE.appendTooltip(createMockTooltip(lines1),
                                createMockAccessor(context.getWorld(), pos,
                                                moldyNoteBlock.with(MoldyBlock.STAGE, 1).with(MoldyBlock.WAXED, false)),
                                null);
                context.assertTrue(lines1.size() == 1, "Stage 1 should produce exactly 1 line");
                String text1 = lines1.get(0).getString();
                context.assertTrue(text1.contains("Harp"), "Must mention instrument Harp");
                context.assertTrue(text1.contains("F♯/G♭"), "Must mention note F♯/G♭");

                // Stage 2 (Moldy): Shows note F♯/G♭ and dampened descriptor
                List<Text> lines2 = new ArrayList<>();
                MoldyNoteBlockProvider.INSTANCE.appendTooltip(createMockTooltip(lines2),
                                createMockAccessor(context.getWorld(), pos,
                                                moldyNoteBlock.with(MoldyBlock.STAGE, 2).with(MoldyBlock.WAXED, false)),
                                null);
                context.assertTrue(lines2.size() == 1, "Stage 2 should produce exactly 1 line");
                String text2 = lines2.get(0).getString();
                context.assertTrue(text2.contains("Harp"), "Must mention instrument Harp");
                context.assertTrue(text2.contains("F♯/G♭"), "Must mention note F♯/G♭");

                // Stage 3 (Rotten): Shows note and ruined resonance indicator
                List<Text> lines3 = new ArrayList<>();
                MoldyNoteBlockProvider.INSTANCE.appendTooltip(createMockTooltip(lines3),
                                createMockAccessor(context.getWorld(), pos,
                                                moldyNoteBlock.with(MoldyBlock.STAGE, 3).with(MoldyBlock.WAXED, false)),
                                null);
                context.assertTrue(lines3.size() == 1, "Stage 3 should produce exactly 1 line");
                String text3 = lines3.get(0).getString();
                context.assertTrue(text3.contains("Harp"), "Must mention instrument Harp");
                context.assertTrue(text3.contains("F♯/G♭"), "Must mention note F♯/G♭");

                // Waxed Moldy Note Block (e.g. Stage 2): Preserves mold tooltip
                List<Text> linesWaxed = new ArrayList<>();
                MoldyNoteBlockProvider.INSTANCE.appendTooltip(createMockTooltip(linesWaxed),
                                createMockAccessor(context.getWorld(), pos,
                                                moldyNoteBlock.with(MoldyBlock.STAGE, 2).with(MoldyBlock.WAXED, true)),
                                null);
                context.assertTrue(linesWaxed.size() == 1, "Waxed moldy note block must produce stage descriptor line");

                context.complete();
        }
}
