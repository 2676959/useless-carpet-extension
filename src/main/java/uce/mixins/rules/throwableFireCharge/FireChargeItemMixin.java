package uce.mixins.rules.throwableFireCharge;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.FireChargeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import uce.UselessCarpetExtensionServer;

import static uce.utils.ExplosiveProjectileHelper.setPowerFromEuler;
import static uce.UselessCarpetExtensionSettings.throwableFireCharge;

@Mixin(FireChargeItem.class)
public abstract class FireChargeItemMixin extends Item {

    @Shadow protected abstract void playUseSound(World world, BlockPos pos);

    public FireChargeItemMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "useOnBlock", at = @At(value = "HEAD"), cancellable = true)
    private void disableUseOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (!throwableFireCharge) {
            return;
        }
        PlayerEntity player = context.getPlayer();
        if (player != null) {
            this.use(context.getWorld(), context.getPlayer(), context.getHand());
        }
        cir.setReturnValue(ActionResult.CONSUME);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!throwableFireCharge) {
            return TypedActionResult.fail(stack);
        }
        if (!world.isClient) {
            UselessCarpetExtensionServer.LOGGER.info("use");
            switch (hand) {
                case MAIN_HAND:
                    if (!user.isSneaking()) {
                        this.shootFireball((ServerWorld) world, stack, user, 1, 0.1F);
                        break;
                    }
                    user.setCurrentHand(hand);
                    return TypedActionResult.fail(stack);
                case OFF_HAND:
                    this.shootSmallFireBall((ServerWorld) world, stack, user, 0.1F);
            }

        }
        return TypedActionResult.success(stack, world.isClient);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(throwableFireCharge && ! world.isClient() && (user instanceof PlayerEntity playerEntity))) {
            return;
        }

        UselessCarpetExtensionServer.LOGGER.info("stop use");
        int chargedTime = this.getMaxUseTime(stack) - remainingUseTicks;
        int explosionPower = this.getExplosionPower(chargedTime);
        float basePower = (chargedTime < 20) ? 0.0F : Math.max(explosionPower * 0.025F, 0.1F);
        this.shootFireball((ServerWorld) world, stack, playerEntity, explosionPower, basePower);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!(throwableFireCharge && ! world.isClient() && (user instanceof PlayerEntity playerEntity))) {
            return;
        }
        if (remainingUseTicks % 20 == 0) {
            int chargedTime = this.getMaxUseTime(stack) - remainingUseTicks;
            int explosionPower = this.getExplosionPower(chargedTime);
            UselessCarpetExtensionServer.LOGGER.info("exp power: {}", explosionPower);
            MutableText p = Text.literal("■".repeat(explosionPower)).withColor(15641624).append(Text.literal("■".repeat(8 - explosionPower)).withColor(5646848));
            playerEntity.sendMessage(p, true);
        }
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Unique
    private int getExplosionPower(int chargedTime) {
        return Math.min(chargedTime / 20 + 1, 8);
    }

    @Unique
    private void shootFireball(ServerWorld world, ItemStack stack, PlayerEntity user, int explosionPower, float basePower) {
        FireballEntity fireballEntity = new FireballEntity(world, user, 0, 0, 0, explosionPower);
        fireballEntity.setPosition(user.getEyePos());
        fireballEntity.setItem(stack);
        setPowerFromEuler(fireballEntity, user.getPitch(), user.getYaw(), 0.0F, basePower);
        world.spawnEntity(fireballEntity);
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        this.playUseSound(world, user.getBlockPos());
        if (!user.getAbilities().creativeMode) {
            stack.decrement(1);
        }
    }

    @Unique
    private void shootSmallFireBall(ServerWorld world, ItemStack stack, PlayerEntity user, float basePower) {
        SmallFireballEntity smallFireballEntity = new SmallFireballEntity(EntityType.SMALL_FIREBALL, world);
        smallFireballEntity.setPosition(user.getEyePos());
        setPowerFromEuler(smallFireballEntity, user.getPitch(), user.getYaw(), 0.0F, basePower);
        world.spawnEntity(smallFireballEntity);
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        user.playSound(SoundEvents.ITEM_FIRECHARGE_USE, 1.0F, 1.0F);
        world.playSound(user, user.getBlockPos(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS);
        if (!user.getAbilities().creativeMode) {
            stack.decrement(1);
        }
    }
}
