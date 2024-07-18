package uce;

import carpet.api.settings.Rule;
import static carpet.api.settings.RuleCategory.*;

public class UselessCarpetExtensionSettings {

    public static final String UCE = "uce";

    @Rule(categories = {UCE, FEATURE})
    public static boolean playerKilledByChargedCreeperDropHead = false;

    @Rule(categories = {UCE, BUGFIX})
    public static boolean keepSpectatingOnTarget = false;

    @Rule(
            categories = {UCE, COMMAND},
            options = {"true", "false", "0", "1", "2", "3", "4", "ops"}
    )
    public static String commandReplaceItemFrame = "false";
}
