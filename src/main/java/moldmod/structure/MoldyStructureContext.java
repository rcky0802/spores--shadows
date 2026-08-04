package moldmod.structure;

import moldmod.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PillarBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;

public class MoldyStructureContext {

    private static final ThreadLocal<String> CURRENT_STRUCTURE = new ThreadLocal<>();

    public static void setStructure(String id) {
        CURRENT_STRUCTURE.set(id);
    }

    public static void clear() {
        CURRENT_STRUCTURE.remove();
    }

    public static BlockState processBlock(BlockState state, BlockPos pos, StructureWorldAccess world) {
        String structureId = CURRENT_STRUCTURE.get();
        if (structureId == null) return state;

        if (!isConvertible(state)) return state;

        Random random = world.getRandom();
        double r = random.nextDouble() * 100.0;

        int moldy = 0;
        int tainted = 0;
        int rotten = 0;
        
        // Category 1: Degrado Critico
        if (structureId.contains("shipwreck") || structureId.contains("swamp_hut")) {
            rotten = 60;
            tainted = 30;
            moldy = 10;
        }
        // Category 2: Degrado Alto
        else if (structureId.contains("mineshaft") || structureId.contains("village") && structureId.contains("zombie") || structureId.contains("trail_ruins")) {
            moldy = 35;
            tainted = 35;
            rotten = 20;
        }
        // Category 3: Degrado Moderato
        else if (structureId.contains("pillager_outpost") || structureId.contains("ruined_portal")) {
            moldy = 35;
            tainted = 15;
            rotten = 0;
        }
        // Category 4: Degrado Minimo
        else if (structureId.contains("village") || structureId.contains("mansion")) {
            moldy = 5; // Base probability very low
        }

        // Apply dynamic modifiers based on environment
        boolean isUnderwater = false;
        boolean hasSkyAccess = false;
        
        for (net.minecraft.util.math.Direction dir : net.minecraft.util.math.Direction.values()) {
            if (world.getBlockState(pos.offset(dir)).isOf(Blocks.WATER)) {
                isUnderwater = true;
                break;
            }
        }
        
        // Approximate sky access check (if above Y=60 and relatively high up in the local terrain)
        if (pos.getY() > 60 && world.isSkyVisible(pos.up())) {
            hasSkyAccess = true;
        }

        // Check if near ground (y <= surface approx) or near water
        boolean nearGround = false;
        for (int dy = -1; dy >= -2; dy--) {
            BlockState below = world.getBlockState(pos.add(0, dy, 0));
            if (below.isOf(Blocks.GRASS_BLOCK) || below.isOf(Blocks.DIRT) || below.isOf(Blocks.WATER) || below.isOf(Blocks.STONE)) {
                nearGround = true;
                break;
            }
        }

        // Rule: If touching water or deep underground (Y < 60), increase Rotten/Tainted
        if (isUnderwater || pos.getY() <= 60) {
            rotten = Math.min(100, rotten + 20); // +20% Rotten
            tainted = Math.min(100 - rotten, tainted + 20); // +20% Tainted
            if (!isUnderwater && pos.getY() <= 60 && moldy < 35) {
                // If just deep underground (not water), ensure some base mold
                moldy = Math.min(100 - rotten - tainted, moldy + 15);
            }
        }

        // Rule: If near ground, boost moldy heavily (simulating rising damp)
        if (nearGround) {
            moldy = Math.min(100 - rotten - tainted, moldy + 20);
        }

        // Rule: If exposed to air/rain (sky access), shift towards Moldy
        if (hasSkyAccess && !isUnderwater) {
            // Convert some Rotten/Tainted back to Moldy since fresh air preserves it slightly from rotting completely
            int recovered = Math.min(rotten, 15);
            rotten -= recovered;
            moldy += recovered;
            
            // Boost Moldy heavily
            moldy = Math.min(100 - rotten - tainted, moldy + 25);
        }
        
        // --- INFLUENCE BY PLAYER RISK FORMULA (R) ---
        double R = moldmod.block.MoldyBlockHelper.calculateR(world, pos, false, state);
        if (R > 0.8) {
            rotten = Math.min(100, rotten + (int)(R * 15));
            tainted = Math.min(100 - rotten, tainted + 15);
        } else if (R > 0.5) {
            moldy = Math.min(100, moldy + 15);
            tainted = Math.min(100 - moldy, tainted + 10);
        } else if (R < 0.2) {
            rotten = Math.max(0, rotten - 15);
            tainted = Math.max(0, tainted - 15);
            moldy = Math.max(0, moldy - 15);
        }
        // ---------------------------------------------
        
        if (r < rotten) {
            return getConvertedState(state, 2);
        } else if (r < rotten + tainted) {
            return getConvertedState(state, 1);
        } else if (r < rotten + tainted + moldy) {
            return getConvertedState(state, 0);
        }

        return state;
    }

    private static boolean isConvertible(BlockState state) {
        return moldmod.block.ModBlocks.VANILLA_TO_MOLDY.containsKey(state.getBlock());
    }

    private static BlockState getConvertedState(BlockState original, int stage) {
        if (moldmod.block.ModBlocks.VANILLA_TO_MOLDY.containsKey(original.getBlock())) {
            net.minecraft.block.Block moldyBlock = moldmod.block.ModBlocks.VANILLA_TO_MOLDY.get(original.getBlock());
            BlockState newState = moldyBlock.getDefaultState()
                    .with(moldmod.block.MoldyLogBlock.STAGE, stage);
            
            if (original.contains(PillarBlock.AXIS) && newState.contains(PillarBlock.AXIS)) {
                newState = newState.with(PillarBlock.AXIS, original.get(PillarBlock.AXIS));
            }
            
            if (newState.contains(moldmod.block.MoldyLogBlock.STRUCTURAL)) {
                newState = newState.with(moldmod.block.MoldyLogBlock.STRUCTURAL, true);
            }
            
            return newState;
        }
        return original;
    }
}
