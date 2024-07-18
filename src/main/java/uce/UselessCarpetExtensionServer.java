package uce;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uce.commands.ReplaceItemFrameCommand;
import uce.utils.UceTranslations;
import java.util.Map;

public class UselessCarpetExtensionServer implements CarpetExtension, ModInitializer {

    public static final String MOD_ID = "uce";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public String version() {
        return "Useless Carpet Extension";
    }

    public static void loadExtension() {
        CarpetServer.manageExtension(new UselessCarpetExtensionServer());
    }

    @Override
    public void onInitialize() {
        UselessCarpetExtensionServer.loadExtension();
    }

    @Override
    public void onGameStarted() {
        CarpetServer.settingsManager.parseSettingsClass(UselessCarpetExtensionSettings.class);
    }

    @Override
    public void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        ReplaceItemFrameCommand.register(dispatcher);
    }

    @Override
    public Map<String, String> canHasTranslations(String lang)
    {
        return UceTranslations.getTranslationFromResourcePath(lang);
    }
}
