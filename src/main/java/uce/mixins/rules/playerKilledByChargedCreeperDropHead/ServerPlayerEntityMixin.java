package uce.mixins.rules.playerKilledByChargedCreeperDropHead;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import static uce.UselessCarpetExtensionSettings.playerKilledByChargedCreeperDropHead;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(at = @At("TAIL"), method = "onDeath")
    private void dropPlayerHead(DamageSource damageSource, CallbackInfo info) {
        if (!playerKilledByChargedCreeperDropHead) {
            return;
        }
        CreeperEntity creeperEntity;
        Entity entity = damageSource.getAttacker();
        // Different from vanilla mechanics, all players killed by the same creeper will drop their heads.
        if (entity instanceof CreeperEntity) {
            creeperEntity = (CreeperEntity) entity;
            creeperEntity.onHeadDropped();
            NbtCompound playerHeadNbt = new NbtCompound();
            playerHeadNbt.putString("SkullOwner", super.getName().getString());
            ItemStack playerHeadItem = new ItemStack(Items.PLAYER_HEAD);
            playerHeadItem.setNbt(playerHeadNbt);
            this.dropStack(playerHeadItem);
        }
    }
}