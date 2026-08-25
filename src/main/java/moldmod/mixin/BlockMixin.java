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
}
