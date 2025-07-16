package uce.mixins.rules.retainTridentDamage;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static uce.UselessCarpetExtensionSettings.retainTridentDamage;

@Mixin(TridentEntity.class)
public abstract class TridentEntityMixin extends PersistentProjectileEntity {

    @Shadow private boolean dealtDamage;

    //#if MC>=12004
    protected TridentEntityMixin(EntityType<? extends PersistentProjectileEntity> type, World world, ItemStack stack) {
        super(type, world, stack);
    }
    //#else
    //$$ protected TridentEntityMixin(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
    //$$     super(entityType, world);
    //$$ }
    //#endif

    // skip inGroundTime > 4
    @Redirect(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/projectile/TridentEntity;inGroundTime:I", opcode = Opcodes.GETFIELD))
    private int modifyInGroundTime(TridentEntity tridentEntity) {
        return retainTridentDamage ? 0 : this.inGroundTime;
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void modifyDealtDamage(CallbackInfo ci) {
        if (retainTridentDamage) {
            this.dealtDamage = false;
        }
    }
}
