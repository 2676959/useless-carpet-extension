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
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import uce.UselessCarpetExtensionServer;

import static uce.utils.ExplosiveProjectileHelper.setPowerFromEuler;
import static uce.UselessCarpetExtensionSettings.throwableFireCharge;

@Mixin(FireChargeItem.class)
public class FireChargeItemMixin extends Item {

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
        cir.setReturnValue(ActionResult.PASS);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!throwableFireCharge) {
            return TypedActionResult.fail(stack);
        }
        if (!world.isClient) {
            switch (hand) {
                case MAIN_HAND:
                    if (!user.isSneaking()) {
                        FireballEntity fireballEntity = new FireballEntity(world, user, 0, 0, 0, 1);
                        fireballEntity.setPosition(user.getEyePos());
                        fireballEntity.setItem(stack);
                        setPowerFromEuler(fireballEntity, user.getPitch(), user.getYaw(), 0.0F, 0.1F);
                        world.spawnEntity(fireballEntity);
                        user.incrementStat(Stats.USED.getOrCreateStat(this));
                        user.playSound(SoundEvents.ITEM_FIRECHARGE_USE, 1.0F, 1.0F);
                        world.playSound(user, user.getBlockPos(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS);
                        if (!user.getAbilities().creativeMode) {
                            stack.decrement(1);
                        }
                        break;
                    }
                    user.setCurrentHand(hand);
                    return TypedActionResult.fail(stack);
                case OFF_HAND:
                    SmallFireballEntity smallFireballEntity = new SmallFireballEntity(EntityType.SMALL_FIREBALL, world);
                    smallFireballEntity.setPosition(user.getEyePos());
                    setPowerFromEuler(smallFireballEntity, user.getPitch(), user.getYaw(), 0.0F, 0.1F);
                    world.spawnEntity(smallFireballEntity);
                    user.incrementStat(Stats.USED.getOrCreateStat(this));
                    user.playSound(SoundEvents.ITEM_FIRECHARGE_USE, 1.0F, 1.0F);
                    world.playSound(user, user.getBlockPos(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS);
                    if (!user.getAbilities().creativeMode) {
                        stack.decrement(1);
                    }
            }

        }
        return TypedActionResult.success(stack, world.isClient);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(throwableFireCharge && ! world.isClient() && (user instanceof PlayerEntity playerEntity))) {
            return;
        }
        int maxUseTime = this.getMaxUseTime(stack);
        int chargedTime = maxUseTime - remainingUseTicks;
        int explosionPower = Math.min(chargedTime / 20 + 1, 8);
        FireballEntity fireballEntity = new FireballEntity(world, playerEntity, 0, 0, 0, explosionPower);
        fireballEntity.setPosition(user.getEyePos());
        fireballEntity.setItem(stack);
        if (chargedTime > 20) {
            float basePower = explosionPower * 0.1F;
            setPowerFromEuler(fireballEntity, user.getPitch(), user.getYaw(), 0.0F, basePower);
        }
        world.spawnEntity(fireballEntity);
        playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
        playerEntity.playSound(SoundEvents.ITEM_FIRECHARGE_USE, 1.0F, 1.0F);
        world.playSound(playerEntity, playerEntity.getBlockPos(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS);
        if (!playerEntity.getAbilities().creativeMode) {
            stack.decrement(1);
        }
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!(throwableFireCharge && ! world.isClient() && (user instanceof PlayerEntity playerEntity))) {
            return;
        }
        int maxUseTime = this.getMaxUseTime(stack);
        int chargedTime = maxUseTime - remainingUseTicks;
        if (chargedTime % 20 == 0) {
            int explosionPower = Math.min(chargedTime / 20 + 1, 8);
            UselessCarpetExtensionServer.LOGGER.info("exp power: {}", explosionPower);
            MutableText p = Text.literal("■".repeat(explosionPower)).formatted(Formatting.GREEN).append(Text.literal("■".repeat(8 - explosionPower)).withColor(16777215));
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
}
