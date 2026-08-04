package moldmod.mixin;

import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import moldmod.structure.MoldyStructureContext;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

@Mixin(StructureStart.class)
public abstract class StructureStartMixin {

    @Shadow public abstract Structure getStructure();

    @Inject(method = "place", at = @At("HEAD"))
    private void onPlaceStart(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, CallbackInfo ci) {
        Structure structure = this.getStructure();
        if (structure != null) {
            Identifier id = world.getRegistryManager().get(RegistryKeys.STRUCTURE).getId(structure);
            if (id != null) {
                MoldyStructureContext.setStructure(id.getPath());
            }
        }
    }

    @Inject(method = "place", at = @At("RETURN"))
    private void onPlaceEnd(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, CallbackInfo ci) {
        MoldyStructureContext.clear();
    }
}
