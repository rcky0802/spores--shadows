package moldmod.mixin;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.block.MoldyBlock;
import moldmod.config.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(method = "randomDisplayTick", at = @At("HEAD"))
    private void spawnMoldParticles(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci) {
        if (state.contains(MoldyBlock.STAGE)) {
            int stage = state.get(MoldyBlock.STAGE);
            if (stage > 0) {
                if (state.contains(MoldyBlock.WAXED) && state.get(MoldyBlock.WAXED)) {
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
    private void spawnSporeCloudOnBreak(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfoReturnable<BlockState> cir) {
        if (state.contains(MoldyBlock.STAGE)) {
            int stage = state.get(MoldyBlock.STAGE);
            if (stage < 2) return;

            ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
            if (config == null || config.hardness == null || !config.hardness.enable_break_spore_cloud) return;

            boolean isWaxed = (state.contains(MoldyBlock.WAXED) && state.get(MoldyBlock.WAXED))
                    || Registries.BLOCK.getId(state.getBlock()).getPath().startsWith("waxed_");
            if (isWaxed) return;

            boolean hasSilkTouch = false;
            if (player != null) {
                ItemStack tool = player.getMainHandStack();
                hasSilkTouch = world.getRegistryManager().getOptional(RegistryKeys.ENCHANTMENT)
                        .flatMap(reg -> reg.getEntry(Enchantments.SILK_TOUCH))
                        .map(entry -> EnchantmentHelper.getLevel(entry, tool) > 0)
                        .orElse(false);
            }

            if (!hasSilkTouch) {
                if (world instanceof ServerWorld serverWorld) {
                    double cx = pos.getX() + 0.5;
                    double cy = pos.getY() + 0.5;
                    double cz = pos.getZ() + 0.5;

                    int countAir = (stage == 3) ? 35 : 20;
                    int countFalling = (stage == 3) ? 20 : 10;
                    int countMycelium = (stage == 3) ? 25 : 12;

                    serverWorld.spawnParticles(ParticleTypes.SPORE_BLOSSOM_AIR, cx, cy, cz, countAir, 0.4, 0.4, 0.4, 0.05);
                    serverWorld.spawnParticles(ParticleTypes.FALLING_SPORE_BLOSSOM, cx, cy, cz, countFalling, 0.3, 0.3, 0.3, 0.02);
                    serverWorld.spawnParticles(ParticleTypes.MYCELIUM, cx, cy, cz, countMycelium, 0.35, 0.35, 0.35, 0.02);

                    world.playSound(null, pos, SoundEvents.BLOCK_FUNGUS_BREAK, SoundCategory.BLOCKS, 1.0f, 0.8f);
                }
            }
        }
    }
}
