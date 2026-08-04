package moldmod.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import moldmod.SporesShadows;
import moldmod.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PillarBlock;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.Registries;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class MoldyStructureProcessor extends StructureProcessor {

    public static final MapCodec<MoldyStructureProcessor> CODEC = MapCodec.unit(MoldyStructureProcessor::new);
    public static StructureProcessorType<MoldyStructureProcessor> TYPE;

    // We use a ThreadLocal to pass context if needed, but normally StructureProcessors are added per structure template
    public MoldyStructureProcessor() {
    }

    public static void register() {
        TYPE = Registry.register(
            Registries.STRUCTURE_PROCESSOR,
            SporesShadows.id("moldy_processor"),
            () -> CODEC
        );
    }

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo process(WorldView world, BlockPos pos, BlockPos pivot, StructureTemplate.StructureBlockInfo originalBlockInfo, StructureTemplate.StructureBlockInfo currentBlockInfo, StructurePlacementData data) {
        // To be implemented once all percentages are provided
        return currentBlockInfo;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return TYPE;
    }
}
