package uce;

import carpet.api.settings.Rule;
import static carpet.api.settings.RuleCategory.*;

public class UselessCarpetExtensionSettings {

    public static final String UCE = "uce";
    public static final String JUKEBOX = "jukebox";

    @Rule(categories = {UCE, FEATURE})
    public static boolean playerKilledByChargedCreeperDropHead = false;

    @Rule(categories = {UCE, BUGFIX})
    public static boolean keepSpectatingOnTarget = false;

    @Rule(
            categories = {UCE, COMMAND},
            options = {"true", "false", "0", "1", "2", "3", "4", "ops"}
    )
    public static String commandReplaceItemFrame = "false";

    @Rule(categories = {UCE, FEATURE})
    public static boolean throwableFireCharge = false;

    @Rule(categories = {UCE, FEATURE})
    public static boolean retainTridentDamage = false;

    @Rule(categories = {UCE, FEATURE, JUKEBOX})
    public static boolean muteJukebox = false;

    @Rule(categories = {UCE, FEATURE, JUKEBOX})
    public static boolean jukeboxNoteblockMode = false;
}
