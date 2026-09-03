package moldmod.event;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.block.ModBlocks;
import moldmod.block.MoldyBlock;
import moldmod.block.MoldyBlockHelper;
import moldmod.config.ModConfig;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public class MoldyInteractionEvents {

    public static void register() {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, entity) -> {
            if (!world.isClient() && state.contains(MoldyBlock.STAGE) && state.get(MoldyBlock.STAGE) == 3 && (!state.contains(MoldyBlock.WAXED) || !state.get(MoldyBlock.WAXED))) {
                MoldyBlockHelper.grantAdvancement(player, "crumble");
            }
            return true;
        });

        UseBlockCallback.EVENT.register((PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) -> {
            // Must be sneaking for mold/wax interactions
            if (!player.isSneaking()) {
                return ActionResult.PASS;
            }

            ItemStack stack = player.getStackInHand(hand);
            BlockState state = world.getBlockState(hitResult.getBlockPos());

            // Check if it's a moldy block (it has STAGE and WAXED)
            if (!state.contains(MoldyBlock.STAGE) || !state.contains(MoldyBlock.WAXED)) {
                return ActionResult.PASS;
            }

            boolean isWaxed = state.get(MoldyBlock.WAXED);
            int stage = state.get(MoldyBlock.STAGE);

            // Honeycomb: Waxing
            if (stack.isOf(Items.HONEYCOMB)) {
                if (!isWaxed) {
                    if (!world.isClient) {
                        Block waxedBlock = ModBlocks.MOLDY_TO_WAXED.get(state.getBlock());
                        if (waxedBlock != null) {
                            BlockState newState = MoldyBlockHelper.copyMatchingProperties(state, waxedBlock.getDefaultState());
                            newState = newState.with(MoldyBlock.WAXED, true);
                            if (newState.contains(MoldyBlock.STRUCTURAL)) {
                                newState = newState.with(MoldyBlock.STRUCTURAL, false);
                            }
                            MoldyBlockHelper.setWaxed(world, hitResult.getBlockPos(), newState, true);
                            world.playSound(null, hitResult.getBlockPos(), SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.BLOCKS, 1.0f, 1.0f);
                            MoldyBlockHelper.grantAdvancement(player, "wax_block");
                            if (!player.isCreative()) {
                                stack.decrement(1);
                            }
                        }
                    }
                    return ActionResult.SUCCESS;
                }
            }
            
            // Axe: Scrape wax or mold
            if (stack.getItem() instanceof AxeItem) {
                int scrapeDamage = AutoConfig.getConfigHolder(ModConfig.class).getConfig().general.axe_scrape_damage;
                if (isWaxed) {
                    if (!world.isClient) {
                        Block moldyBlock = ModBlocks.WAXED_TO_MOLDY.get(state.getBlock());
                        if (moldyBlock != null) {
                            BlockState newState = MoldyBlockHelper.copyMatchingProperties(state, moldyBlock.getDefaultState());
                            newState = newState.with(MoldyBlock.WAXED, false);
                            if (newState.contains(MoldyBlock.STRUCTURAL)) {
                                newState = newState.with(MoldyBlock.STRUCTURAL, false);
                            }
                            MoldyBlockHelper.setWaxed(world, hitResult.getBlockPos(), newState, false);
                            world.playSound(null, hitResult.getBlockPos(), SoundEvents.ITEM_AXE_WAX_OFF, SoundCategory.BLOCKS, 1.0f, 1.0f);
                            stack.damage(scrapeDamage, player, PlayerEntity.getSlotForHand(hand));
                        }
                    }
                    return ActionResult.SUCCESS;
                } else {
                    // Not waxed: try to scrape mold
                    if (stage > 0 && stage < 3) {
                        if (!world.isClient) {
                            BlockState newState = state.with(MoldyBlock.STAGE, stage - 1);
                            if (newState.contains(MoldyBlock.STRUCTURAL)) {
                                newState = newState.with(MoldyBlock.STRUCTURAL, false);
                            }
                            MoldyBlockHelper.setStage(world, hitResult.getBlockPos(), newState, stage - 1);
                            world.playSound(null, hitResult.getBlockPos(), SoundEvents.ITEM_AXE_SCRAPE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                            stack.damage(scrapeDamage, player, PlayerEntity.getSlotForHand(hand));
                            MoldyBlockHelper.grantAdvancement(player, "scrape_mold");
                        }
                        return ActionResult.SUCCESS;
                    }
                    // If stage == 3, the axe has no effect (incurable). 
                    // We return SUCCESS to consume the interaction without stripping or damaging the axe!
                    if (stage == 3) {
                        return ActionResult.SUCCESS;
                    }
                }
            }

            return ActionResult.PASS;
        });
    }
}
