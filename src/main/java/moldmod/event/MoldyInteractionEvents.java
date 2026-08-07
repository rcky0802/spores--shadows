package moldmod.event;

import moldmod.block.MoldyLogBlock;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
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
        UseBlockCallback.EVENT.register((PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) -> {
            // Must be sneaking for mold/wax interactions
            if (!player.isSneaking()) {
                return ActionResult.PASS;
            }

            ItemStack stack = player.getStackInHand(hand);
            BlockState state = world.getBlockState(hitResult.getBlockPos());

            // Check if it's a moldy block (it has STAGE and WAXED)
            if (!state.contains(MoldyLogBlock.STAGE) || !state.contains(MoldyLogBlock.WAXED)) {
                return ActionResult.PASS;
            }

            boolean isWaxed = state.get(MoldyLogBlock.WAXED);
            int stage = state.get(MoldyLogBlock.STAGE);

            // Honeycomb: Waxing
            if (stack.isOf(Items.HONEYCOMB)) {
                if (!isWaxed) {
                    if (!world.isClient) {
                        BlockState newState = state.with(MoldyLogBlock.WAXED, true);
                        if (newState.contains(MoldyLogBlock.STRUCTURAL)) {
                            newState = newState.with(MoldyLogBlock.STRUCTURAL, false);
                        }
                        moldmod.block.MoldyBlockHelper.setWaxed(world, hitResult.getBlockPos(), newState, true);
                        world.playSound(null, hitResult.getBlockPos(), SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.BLOCKS, 1.0f, 1.0f);
                        if (!player.isCreative()) {
                            stack.decrement(1);
                        }
                    }
                    return ActionResult.SUCCESS;
                }
            }
            
            // Axe: Scrape wax or mold
            if (stack.getItem() instanceof AxeItem) {
                if (isWaxed) {
                    if (!world.isClient) {
                        BlockState newState = state.with(MoldyLogBlock.WAXED, false);
                        if (newState.contains(MoldyLogBlock.STRUCTURAL)) {
                            newState = newState.with(MoldyLogBlock.STRUCTURAL, false);
                        }
                        moldmod.block.MoldyBlockHelper.setWaxed(world, hitResult.getBlockPos(), newState, false);
                        world.playSound(null, hitResult.getBlockPos(), SoundEvents.ITEM_AXE_WAX_OFF, SoundCategory.BLOCKS, 1.0f, 1.0f);
                        stack.damage(1, player, PlayerEntity.getSlotForHand(hand));
                    }
                    return ActionResult.SUCCESS;
                } else {
                    // Not waxed: try to scrape mold
                    if (stage > 0 && stage < 3) {
                        if (!world.isClient) {
                            BlockState newState = state.with(MoldyLogBlock.STAGE, stage - 1);
                            if (newState.contains(MoldyLogBlock.STRUCTURAL)) {
                                newState = newState.with(MoldyLogBlock.STRUCTURAL, false);
                            }
                            moldmod.block.MoldyBlockHelper.setStage(world, hitResult.getBlockPos(), newState, stage - 1);
                            world.playSound(null, hitResult.getBlockPos(), SoundEvents.ITEM_AXE_SCRAPE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                            stack.damage(1, player, PlayerEntity.getSlotForHand(hand));
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
