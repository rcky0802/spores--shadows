package moldmod.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LightType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;

public class PlacedOakLogBlock extends PillarBlock {
    public PlacedOakLogBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);

        // Probabilità base molto bassa ad ogni random tick
        double moldChance = 0.05; 

        // 1. Sottoterra (sotto Y=60 e niente luce solare)
        if (pos.getY() < 60 && world.getLightLevel(LightType.SKY, pos) == 0) {
            moldChance += 0.20; 
        }

        // 2. Vicino all'acqua (raggio 2 blocchi)
        boolean nearWater = false;
        for (BlockPos p : BlockPos.iterate(pos.add(-2, -2, -2), pos.add(2, 2, 2))) {
            if (world.getFluidState(p).isIn(FluidTags.WATER)) {
                nearWater = true;
                break;
            }
        }
        if (nearWater) {
            moldChance += 0.30;
        }

        // 3. Clima del bioma
        RegistryEntry<Biome> biomeEntry = world.getBiome(pos);
        float temp = biomeEntry.value().getTemperature();
        if (temp > 0.8f) { // Caldo/Umido (es. Giungla)
            moldChance += 0.15;
        } else if (temp < 0.2f) { // Freddo (es. Neve)
            moldChance -= 0.10;
        }

        // Prova ad ammuffire
        if (moldChance > 0) {
            if (random.nextDouble() < moldChance) {
                world.setBlockState(pos, ModBlocks.MOLDY_OAK_LOG_STAGE_1.getDefaultState().with(AXIS, state.get(AXIS)));
            }
        }
    }
}
