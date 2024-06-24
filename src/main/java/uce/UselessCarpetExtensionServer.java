package uce;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import net.fabricmc.api.ModInitializer;
import uce.utils.uceTranslations;
import java.util.Map;

public class UselessCarpetExtensionServer implements CarpetExtension, ModInitializer {

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
    public Map<String, String> canHasTranslations(String lang)
    {
        return uceTranslations.getTranslationFromResourcePath(lang);
    }
}
