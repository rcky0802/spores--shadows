package moldmod.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class MoldyBlockHelper {

    public static boolean hasRandomTicks(BlockState state) {
        if (state.contains(MoldyLogBlock.STRUCTURAL) && state.get(MoldyLogBlock.STRUCTURAL)) {
            return false; // Structural blocks do not tick
        }
        return !state.get(MoldyLogBlock.WAXED);
    }

    public static double calculateR(net.minecraft.world.WorldAccess world, BlockPos pos, boolean isWaxed, BlockState stateToCheck) {
        if (isWaxed) return 0.0;
        
        float temp = world.getBiome(pos).value().getTemperature();
        boolean hasPrecipitation = world.getBiome(pos).value().hasPrecipitation();
        
        double baseHum = hasPrecipitation ? 0.8 : (temp > 1.5 ? 0.0 : 0.2);
        
        double depthModifier = 0.0;
        if (pos.getY() < 64) {
            depthModifier = (64.0 - pos.getY()) / 100.0;
        }

        double localHumidityBonus = 0.0;
        double catalystBonus = 0.0;

        for (BlockPos iterPos : BlockPos.iterate(pos.add(-2, -2, -2), pos.add(2, 2, 2))) {
            BlockState nearbyState = world.getBlockState(iterPos);
            
            // Check for water/fluids
            if (!nearbyState.getFluidState().isEmpty()) {
                if (nearbyState.getFluidState().isOf(net.minecraft.fluid.Fluids.WATER) || nearbyState.getFluidState().isOf(net.minecraft.fluid.Fluids.FLOWING_WATER)) {
                    localHumidityBonus += 0.15;
                }
            } else if (nearbyState.isOf(Blocks.MUD) || nearbyState.isOf(Blocks.WATER_CAULDRON)) {
                localHumidityBonus += 0.1;
                if (nearbyState.isOf(Blocks.MUD)) catalystBonus += 0.05;
            } else if (nearbyState.isOf(Blocks.MYCELIUM) || nearbyState.isOf(Blocks.PODZOL)) {
                catalystBonus += 0.15;
            } else if (nearbyState.isOf(Blocks.BROWN_MUSHROOM) || nearbyState.isOf(Blocks.RED_MUSHROOM) || 
                       nearbyState.isOf(Blocks.BROWN_MUSHROOM_BLOCK) || nearbyState.isOf(Blocks.RED_MUSHROOM_BLOCK) || 
                       nearbyState.isOf(Blocks.MUSHROOM_STEM)) {
                catalystBonus += 0.25;
            } else if (nearbyState.isOf(Blocks.SPORE_BLOSSOM)) {
                catalystBonus += 0.8;
            }
        }
        
        // Cap local humidity bonus so a pool of water doesn't guarantee 100% moisture but helps significantly
        localHumidityBonus = Math.min(0.6, localHumidityBonus);
        
        double Heff = Math.min(1.0, baseHum + depthModifier + localHumidityBonus);
        double Tmult = (temp > 0.15 && temp < 1.5) ? 1.0 : 0.0;
        
        int light = world.getLightLevel(net.minecraft.world.LightType.BLOCK, pos);
        double Luv = Math.max(0.0, 1.0 - (light / 15.0));
        
        double Smat = 1.0;
        if (stateToCheck != null) {
            String name = net.minecraft.registry.Registries.BLOCK.getId(stateToCheck.getBlock()).getPath();
            if (name.contains("stripped")) Smat = 1.4;
            else if (name.contains("planks")) Smat = 0.8;
            else Smat = 1.0;
        }
        
        return (Heff * Tmult * Luv) * Smat + catalystBonus;
    }

    public static void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, net.minecraft.block.Block block) {
        moldmod.config.ModConfig config = me.shedaniel.autoconfig.AutoConfig.getConfigHolder(moldmod.config.ModConfig.class).getConfig();
        if (!config.enableMoldSpread) return;
        
        if (state.get(MoldyLogBlock.WAXED)) return;

        double R = calculateR(world, pos, state.get(MoldyLogBlock.WAXED), state) * config.globalMoldRiskMultiplier;
        
        if (config.showDebugInChat) {
            System.out.println("Mold tick at " + pos + ", R = " + R);
        }
        
        if (R > 0.65) {
            int currentStage = state.get(MoldyLogBlock.STAGE);
            if (currentStage < 3) {
                world.setBlockState(pos, state.with(MoldyLogBlock.STAGE, currentStage + 1));
            }
        }
        
        // Try to infect neighbors
        BlockPos neighborPos = pos.add(random.nextInt(3) - 1, random.nextInt(3) - 1, random.nextInt(3) - 1);
        BlockState neighborState = world.getBlockState(neighborPos);
        
        // We only infect if it's the SAME type of block (Log infects Log, Planks infect Planks)
        if (neighborState.isOf(block) && !neighborState.get(MoldyLogBlock.WAXED) && neighborState.get(MoldyLogBlock.STAGE) < 3) {
            if (calculateR(world, neighborPos, false, neighborState) * config.globalMoldRiskMultiplier > 0.65) {
                world.setBlockState(neighborPos, neighborState.with(MoldyLogBlock.STAGE, neighborState.get(MoldyLogBlock.STAGE) + 1));
            }
        }
    }

    public static ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, net.minecraft.block.Block strippedStateBlock) {
        if (stack.getItem() instanceof net.minecraft.item.AxeItem) {
            if (state.get(MoldyLogBlock.WAXED)) {
                world.setBlockState(pos, state.with(MoldyLogBlock.WAXED, false));
                world.playSound(null, pos, net.minecraft.sound.SoundEvents.ITEM_AXE_WAX_OFF, net.minecraft.sound.SoundCategory.BLOCKS, 1.0f, 1.0f);
                stack.damage(1, player, PlayerEntity.getSlotForHand(hand));
                return ItemActionResult.SUCCESS;
            } else if (state.get(MoldyLogBlock.STAGE) == 1) {
                world.setBlockState(pos, state.with(MoldyLogBlock.STAGE, 0));
                world.playSound(null, pos, net.minecraft.sound.SoundEvents.ITEM_AXE_SCRAPE, net.minecraft.sound.SoundCategory.BLOCKS, 1.0f, 1.0f);
                stack.damage(1, player, PlayerEntity.getSlotForHand(hand));
                return ItemActionResult.SUCCESS;
            } else if (state.get(MoldyLogBlock.STAGE) == 0 && strippedStateBlock != null) {
                BlockState stripped = strippedStateBlock.getDefaultState();
                if (state.contains(net.minecraft.state.property.Properties.AXIS)) {
                    stripped = stripped.with(net.minecraft.state.property.Properties.AXIS, state.get(net.minecraft.state.property.Properties.AXIS));
                }
                world.setBlockState(pos, stripped);
                world.playSound(null, pos, net.minecraft.sound.SoundEvents.ITEM_AXE_STRIP, net.minecraft.sound.SoundCategory.BLOCKS, 1.0f, 1.0f);
                stack.damage(1, player, PlayerEntity.getSlotForHand(hand));
                return ItemActionResult.SUCCESS;
            }
        } else if (stack.isOf(Items.HONEYCOMB)) {
            if (!state.get(MoldyLogBlock.WAXED)) {
                world.setBlockState(pos, state.with(MoldyLogBlock.WAXED, true));
                world.playSound(null, pos, net.minecraft.sound.SoundEvents.ITEM_HONEYCOMB_WAX_ON, net.minecraft.sound.SoundCategory.BLOCKS, 1.0f, 1.0f);
                stack.decrement(1);
                return ItemActionResult.SUCCESS;
            }
        }
        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
