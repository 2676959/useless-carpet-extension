package uce.utils;

import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.util.math.MathHelper;

public class ExplosiveProjectileHelper {

    public static <T extends ExplosiveProjectileEntity> void setPowerFromEuler(T entity, float pitch, float yaw, float roll, float basePower) {
        float x = -MathHelper.sin(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
        float y = -MathHelper.sin((pitch + roll) * 0.017453292F);
        float z = MathHelper.cos(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
        double d = MathHelper.sqrt(x*x + y*y + z*z);
        if (d != 0) {
            entity.powerX = x / d * basePower;
            entity.powerY = y / d * basePower;
            entity.powerZ = z / d * basePower;
        }
    }
}
