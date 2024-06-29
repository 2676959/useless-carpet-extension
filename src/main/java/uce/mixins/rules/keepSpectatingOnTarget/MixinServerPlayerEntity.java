package uce.mixins.rules.keepSpectatingOnTarget;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import static uce.UselessCarpetExtensionSettings.keepSpectatingOnTarget;

import java.util.Set;
import java.util.UUID;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity extends PlayerEntity {

    @Shadow @Final
    public MinecraftServer server;

    @Shadow
    public abstract Entity getCameraEntity();

    @Shadow
    public abstract void setCameraEntity(@Nullable Entity entity);

    @Shadow
    public abstract void teleport(ServerWorld targetWorld, double x, double y, double z, float yaw, float pitch);

    public MixinServerPlayerEntity(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    // Set the condition in: if (Entity.isAlive) always true.
    @Redirect(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isAlive()Z"))
    private boolean skipAliveTest(Entity entity) {
        return keepSpectatingOnTarget || entity.isAlive();
    }

    @Inject(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;updatePositionAndAngles(DDDFF)V"))
    private void updateCameraEntity(CallbackInfo ci) {
        if (!keepSpectatingOnTarget) {
            return;
        }
        Entity camEntity = this.getCameraEntity();
        if (!camEntity.isAlive()) {
            if (camEntity.getRemovalReason() == RemovalReason.CHANGED_DIMENSION) {
                UUID uuid = camEntity.getUuid();
                for (ServerWorld serverWorld : this.server.getWorlds()) {
                    Entity transferedEntity = serverWorld.getEntity(uuid);
                    if (transferedEntity != null) {
                        this.teleport(serverWorld, transferedEntity.getX(), transferedEntity.getY(), transferedEntity.getZ(), Set.of(), this.getYaw(), this.getPitch());
                        this.setCameraEntity(transferedEntity);
                    }
                }
            }
            else {
                this.setCameraEntity(this);
            }
        }
        else if (camEntity.isPlayer()) {
            if (this.getWorld().getDimension() != camEntity.getWorld().getDimension()) {
                this.teleport(((ServerPlayerEntity) camEntity).getServerWorld(), camEntity.getX(), camEntity.getY(), camEntity.getZ(), camEntity.getPitch(), camEntity.getYaw());
                this.setCameraEntity(camEntity);
            }
        }
    }
}