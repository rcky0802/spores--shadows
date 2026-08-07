package moldmod.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class MoldyBlockHelper {

    public static boolean canBeInfected(BlockState state) {
        // Return false if WAXED == true
        if (state.contains(MoldyLogBlock.WAXED) && state.get(MoldyLogBlock.WAXED)) {
            return false;
        }
        
        // Return false if STRUCTURAL == true.
        // ALL generated structures (villages, shipwrecks, trees) are passive until interacted with.
        if (state.contains(MoldyLogBlock.STRUCTURAL) && state.get(MoldyLogBlock.STRUCTURAL)) {
            return false;
        }
        
        return true;
    }

    public static boolean hasRandomTicks(BlockState state) {
        return canBeInfected(state);
    }

    public static double calculateR(net.minecraft.world.WorldAccess world, BlockPos pos, boolean isWaxed, BlockState stateToCheck) {
        if (isWaxed) return 0.0;
        
        moldmod.config.ModConfig config = me.shedaniel.autoconfig.AutoConfig.getConfigHolder(moldmod.config.ModConfig.class).getConfig();
        
        float temp = world.getBiome(pos).value().getTemperature();
        
        // Early Exit: If temperature is frozen or desert, return 0.0 immediately
        double Tmult = (temp >= config.environment.min_temperature_survival && temp <= config.environment.max_temperature_survival) ? 1.0 : 0.0;
        if (Tmult == 0.0) return 0.0;

        boolean isRainingAt = false;
        if (world instanceof net.minecraft.world.World realWorld) {
            isRainingAt = realWorld.isRaining() && realWorld.isSkyVisible(pos.up());
        } else {
            isRainingAt = world.getBiome(pos).value().hasPrecipitation();
        }
        
        double baseHum = isRainingAt ? config.environment.rain_humidity_base : config.environment.dry_humidity_base;
        
        double depthModifier = 0.0;
        if (pos.getY() < 64) {
            // Cap depth modifier to 0.64 maximum
            depthModifier = Math.min(0.64, (64.0 - pos.getY()) * config.environment.depth_modifier_per_level);
        }

        double localHumidityBonus = 0.0;
        double catalystBonus = 0.0;

        int r = config.general.scan_radius;
        for (BlockPos iterPos : BlockPos.iterate(pos.add(-r, -r, -r), pos.add(r, r, r))) {
            BlockState nearbyState = world.getBlockState(iterPos);
            
            // Check for water/fluids
            if (!nearbyState.getFluidState().isEmpty()) {
                if (nearbyState.getFluidState().isOf(net.minecraft.fluid.Fluids.WATER) || nearbyState.getFluidState().isOf(net.minecraft.fluid.Fluids.FLOWING_WATER)) {
                    localHumidityBonus += config.environment.water_adjacent_bonus;
                }
            } else if (nearbyState.isOf(Blocks.MUD) || nearbyState.isOf(Blocks.WATER_CAULDRON)) {
                localHumidityBonus += config.environment.cauldron_adjacent_bonus;
                if (nearbyState.isOf(Blocks.MUD)) catalystBonus += config.catalysts.mud_bonus;
            } else if (nearbyState.isOf(Blocks.MYCELIUM) || nearbyState.isOf(Blocks.PODZOL)) {
                catalystBonus += config.catalysts.podzol_mycelium_bonus;
            } else if (nearbyState.isOf(Blocks.BROWN_MUSHROOM) || nearbyState.isOf(Blocks.RED_MUSHROOM) || 
                       nearbyState.isOf(Blocks.BROWN_MUSHROOM_BLOCK) || nearbyState.isOf(Blocks.RED_MUSHROOM_BLOCK) || 
                       nearbyState.isOf(Blocks.MUSHROOM_STEM)) {
                catalystBonus += config.catalysts.fungi_bonus;
            } else if (nearbyState.isOf(Blocks.SPORE_BLOSSOM)) {
                catalystBonus += config.catalysts.spore_blossom_bonus;
            }
        }
        
        // Cap local humidity bonus so a pool of water doesn't guarantee 100% moisture but helps significantly
        localHumidityBonus = Math.min(config.environment.max_local_humidity_bonus, localHumidityBonus);
        
        // Cap Effective Humidity at 1.0 (100%)
        double Heff = Math.min(1.0, baseHum + depthModifier + localHumidityBonus);
        
        int skyLight = world.getLightLevel(net.minecraft.world.LightType.SKY, pos);
        int blockLight = world.getLightLevel(net.minecraft.world.LightType.BLOCK, pos);
        int light = Math.max(skyLight, blockLight);
        double Luv = Math.max(0.0, (15.0 - light) / 15.0);
        
        double Smat = config.susceptibility.default_multiplier;
        if (stateToCheck != null) {
            String name = net.minecraft.registry.Registries.BLOCK.getId(stateToCheck.getBlock()).getPath();
            if (name.contains("stripped")) Smat = config.susceptibility.stripped_wood_multiplier;
            else if (name.contains("planks")) Smat = config.susceptibility.planks_multiplier;
        }
        
        // Make Temperature a global multiplier gate
        return ((Heff * Luv * Smat) + catalystBonus) * Tmult;
    }

    public static void setStage(World world, BlockPos pos, BlockState state, int newStage) {
        world.setBlockState(pos, state.with(MoldyLogBlock.STAGE, newStage));
        syncDoorHalf(world, pos, state, MoldyLogBlock.STAGE, newStage);
    }
    
    public static void setWaxed(World world, BlockPos pos, BlockState state, boolean isWaxed) {
        world.setBlockState(pos, state.with(MoldyLogBlock.WAXED, isWaxed));
        syncDoorHalf(world, pos, state, MoldyLogBlock.WAXED, isWaxed);
    }
    
    private static <T extends Comparable<T>> void syncDoorHalf(World world, BlockPos pos, BlockState state, net.minecraft.state.property.Property<T> property, T value) {
        if (state.getBlock() instanceof net.minecraft.block.DoorBlock) {
            net.minecraft.block.enums.DoubleBlockHalf half = state.get(net.minecraft.block.DoorBlock.HALF);
            BlockPos otherPos = half == net.minecraft.block.enums.DoubleBlockHalf.LOWER ? pos.up() : pos.down();
            BlockState otherState = world.getBlockState(otherPos);
            if (otherState.isOf(state.getBlock()) && otherState.get(net.minecraft.block.DoorBlock.HALF) != half) {
                BlockState newState = otherState.with(property, value);
                
                // Ensure we also sync the structural tag removal if the original block had it removed
                if (state.contains(MoldyLogBlock.STRUCTURAL) && !state.get(MoldyLogBlock.STRUCTURAL) &&
                    otherState.contains(MoldyLogBlock.STRUCTURAL) && otherState.get(MoldyLogBlock.STRUCTURAL)) {
                    newState = newState.with(MoldyLogBlock.STRUCTURAL, false);
                }
                
                world.setBlockState(otherPos, newState);
            }
        }
    }

    public static void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, net.minecraft.block.Block block) {
        moldmod.config.ModConfig config = me.shedaniel.autoconfig.AutoConfig.getConfigHolder(moldmod.config.ModConfig.class).getConfig();
        if (!config.general.enable_mold_growth) return;
        
        if (state.get(MoldyLogBlock.WAXED)) return;
        
        if (state.contains(MoldyLogBlock.STRUCTURAL) && state.get(MoldyLogBlock.STRUCTURAL)) {
            return;
        }

        int currentStage = state.get(MoldyLogBlock.STAGE);
        boolean willSpread = random.nextFloat() < config.general.spread_chance;
        
        // OTTIMIZZAZIONE CRITICA: Se il blocco è già al massimo stadio (3) e non ha passato
        // il tiro percentuale per diffondersi, non ha senso calcolare R (che scansiona 343 blocchi!).
        if (currentStage == 3 && !willSpread) return;

        double R = calculateR(world, pos, false, state);
        
        if (config.general.show_debug_in_chat) {
            System.out.println("Mold tick at " + pos + ", R = " + R);
        }
        
        if (R > config.general.infection_threshold) {
            if (currentStage < 3) {
                setStage(world, pos, state, currentStage + 1);
            }
            
            // Separation of Spread: use pre-calculated spread chance
            if (willSpread) {
                int r = config.general.scan_radius;
                BlockPos neighborPos = pos.add(random.nextInt(r*2 + 1) - r, random.nextInt(r*2 + 1) - r, random.nextInt(r*2 + 1) - r);
                BlockState neighborState = world.getBlockState(neighborPos);
                
                // Allow spreading to ANY valid moldable block
                if (neighborState.contains(MoldyLogBlock.STAGE) && neighborState.get(MoldyLogBlock.STAGE) < 3) {
                    // canBeInfected already checks for WAXED and STRUCTURAL (which are immune to infection)
                    if (canBeInfected(neighborState)) {
                        if (calculateR(world, neighborPos, false, neighborState) > config.general.infection_threshold) {
                            setStage(world, neighborPos, neighborState, neighborState.get(MoldyLogBlock.STAGE) + 1);
                        }
                    }
                }
            }
        }
    }
}
