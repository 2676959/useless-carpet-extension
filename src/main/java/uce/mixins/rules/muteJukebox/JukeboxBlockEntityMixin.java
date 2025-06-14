package uce.mixins.rules.muteJukebox;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static uce.UselessCarpetExtensionSettings.muteJukebox;
import static uce.UselessCarpetExtensionSettings.jukeboxNoteblockMode;

@Mixin(JukeboxBlockEntity.class)
public abstract class JukeboxBlockEntityMixin extends BlockEntity {

    @Shadow protected abstract void spawnNoteParticle(World world, BlockPos pos);

    @Shadow private boolean isPlaying;

    @Shadow public abstract void startPlaying();

    public JukeboxBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Redirect(method = "startPlaying",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;syncWorldEvent(Lnet/minecraft/entity/player/PlayerEntity;ILnet/minecraft/util/math/BlockPos;I)V")
    )
    private void redirectSyncWorldEvent(World instance, PlayerEntity playerEntity, int eventId, BlockPos blockPos, int data) {
        if (muteJukebox) {
            return;
        } else if (jukeboxNoteblockMode) {
            if (this.world.getBlockState(pos.up()).isAir()) {
                this.world.syncWorldEvent(playerEntity, eventId, blockPos, data);
            }
        } else {
            this.world.syncWorldEvent(playerEntity, eventId, blockPos, data);
        }
    }

    @Redirect(method = "tick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/JukeboxBlockEntity;spawnNoteParticle(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V")
    )
    private void redirectSpawnNoteParticle(JukeboxBlockEntity jukeboxBlockEntity, World world, BlockPos pos) {
        if (muteJukebox) {
            return;
        } else if (jukeboxNoteblockMode) {
            if (this.world.getBlockState(pos.up()).isAir()) {
                this.spawnNoteParticle(world, pos);
            }
        } else {
            this.spawnNoteParticle(world, pos);
        }
    }

    @Inject(method = "tick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V",
            at = @At(value = "TAIL")
    )
    private void injectTick(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (jukeboxNoteblockMode) {
            if (!this.world.getBlockState(pos.up()).isAir()) {
                this.world.emitGameEvent(GameEvent.JUKEBOX_STOP_PLAY, this.getPos(), GameEvent.Emitter.of(this.getCachedState()));
                this.world.syncWorldEvent(WorldEvents.JUKEBOX_STOPS_PLAYING, this.getPos(), 0);
            }
        }
    }
}
