package uce.mixins.rules.keepSpectatingOnTarget;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.SetCameraEntityS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import static uce.UselessCarpetExtensionSettings.keepSpectatingOnTarget;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin extends ServerCommonNetworkHandler implements ServerPlayPacketListener {

    @Shadow
    public ServerPlayerEntity player;

    public ServerPlayNetworkHandlerMixin(MinecraftServer server, ClientConnection connection, ConnectedClientData clientData) {
        super(server, connection, clientData);
    }

    // Send packet to notify client update CameraEntity
    @Inject(method = "onPlayerMove", at = @At(value = "TAIL"))
    private void playerMovePacketTest(PlayerMoveC2SPacket packet, CallbackInfo ci) {
        if (keepSpectatingOnTarget) {
            if (this.player.getCameraEntity() != this.player) {
                this.sendPacket(new SetCameraEntityS2CPacket(this.player.getCameraEntity()));
            }
        }
    }
}
