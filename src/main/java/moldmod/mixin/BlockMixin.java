package moldmod.mixin;

import moldmod.block.MoldyLogBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.particle.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(method = "randomDisplayTick", at = @At("HEAD"))
    private void spawnMoldParticles(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci) {
        if (state.contains(MoldyLogBlock.STAGE)) {
            int stage = state.get(MoldyLogBlock.STAGE);
            if (stage > 0) {
                if (state.contains(MoldyLogBlock.WAXED) && state.get(MoldyLogBlock.WAXED)) {
                    return;
                }

                if (random.nextInt(10) < stage) {
                    // Scegliamo una faccia casuale del blocco
                    Direction dir = Direction.random(random);
                    BlockPos adjacentPos = pos.offset(dir);
                    BlockState adjacentState = world.getBlockState(adjacentPos);

                    // Creiamo le particelle SOLO se la faccia scelta non è bloccata da un altro blocco solido
                    if (!adjacentState.isOpaqueFullCube(world, adjacentPos)) {
                        // Calcoliamo le coordinate esattamente sulla faccia esposta
                        double x = pos.getX() + 0.5 + (dir.getOffsetX() * 0.55) + (random.nextDouble() - 0.5) * 0.8;
                        double y = pos.getY() + 0.5 + (dir.getOffsetY() * 0.55) + (random.nextDouble() - 0.5) * 0.8;
                        double z = pos.getZ() + 0.5 + (dir.getOffsetZ() * 0.55) + (random.nextDouble() - 0.5) * 0.8;
                        
                        // Il blocco è circondato da un fluido (es. Acqua)?
                        if (!adjacentState.getFluidState().isEmpty()) {
                            // Sott'acqua non mettiamo nessuna particella, l'acqua "lava" via le spore.
                        } else {
                            // All'aria aperta! Pulviscolo tossico
                            world.addParticle(ParticleTypes.SPORE_BLOSSOM_AIR, x, y, z, 0.0, 0.0, 0.0);
                            
                            // Spore verdi cadenti (se la faccia è inferiore o laterale)
                            if (dir != Direction.UP && random.nextBoolean()) {
                                world.addParticle(ParticleTypes.FALLING_SPORE_BLOSSOM, x, y, z, 0.0, 0.0, 0.0);
                            }

                            // Micelio in stadio avanzato (dal basso o lati)
                            if (stage >= 2 && dir != Direction.DOWN && random.nextBoolean()) {
                                world.addParticle(ParticleTypes.MYCELIUM, x, y, z, 0.0, 0.0, 0.0);
                            }
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "onBreak", at = @At("HEAD"))
    private void spawnSporeCloudOnBreak(World world, BlockPos pos, BlockState state, net.minecraft.entity.player.PlayerEntity player, org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<BlockState> cir) {
        if (state.contains(MoldyLogBlock.STAGE)) {
            int stage = state.get(MoldyLogBlock.STAGE);
            if (stage < 2) return;

            moldmod.config.ModConfig config = me.shedaniel.autoconfig.AutoConfig.getConfigHolder(moldmod.config.ModConfig.class).getConfig();
            if (config == null || config.hardness == null || !config.hardness.enable_break_spore_cloud) return;

            boolean isWaxed = (state.contains(MoldyLogBlock.WAXED) && state.get(MoldyLogBlock.WAXED))
                    || net.minecraft.registry.Registries.BLOCK.getId(state.getBlock()).getPath().startsWith("waxed_");
            if (isWaxed) return;

            boolean hasSilkTouch = false;
            if (player != null) {
                net.minecraft.item.ItemStack tool = player.getMainHandStack();
                var silkTouchEntry = world.getRegistryManager().get(net.minecraft.registry.RegistryKeys.ENCHANTMENT).getEntry(net.minecraft.enchantment.Enchantments.SILK_TOUCH);
                if (silkTouchEntry.isPresent()) {
                    hasSilkTouch = net.minecraft.enchantment.EnchantmentHelper.getLevel(silkTouchEntry.get(), tool) > 0;
                }
            }

            if (!hasSilkTouch) {
                if (world instanceof net.minecraft.server.world.ServerWorld serverWorld) {
                    double cx = pos.getX() + 0.5;
                    double cy = pos.getY() + 0.5;
                    double cz = pos.getZ() + 0.5;

                    int countAir = (stage == 3) ? 35 : 20;
                    int countFalling = (stage == 3) ? 20 : 10;
                    int countMycelium = (stage == 3) ? 25 : 12;

                    serverWorld.spawnParticles(ParticleTypes.SPORE_BLOSSOM_AIR, cx, cy, cz, countAir, 0.4, 0.4, 0.4, 0.05);
                    serverWorld.spawnParticles(ParticleTypes.FALLING_SPORE_BLOSSOM, cx, cy, cz, countFalling, 0.3, 0.3, 0.3, 0.02);
                    serverWorld.spawnParticles(ParticleTypes.MYCELIUM, cx, cy, cz, countMycelium, 0.35, 0.35, 0.35, 0.02);

                    world.playSound(null, pos, net.minecraft.sound.SoundEvents.BLOCK_FUNGUS_BREAK, net.minecraft.sound.SoundCategory.BLOCKS, 1.0f, 0.8f);
                }
            }
        }
    }
}
