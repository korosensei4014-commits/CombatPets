package su.nightexpress.combatpets.bedrock.bridge;

import org.bukkit.Bukkit;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import su.nightexpress.combatpets.PetsPlugin;

import java.util.Locale;

/**
 * Detects which Bedrock API is available on the server.
 * <p>
 * The plugin checks are done before any Geyser / Floodgate / Cumulus class is touched, so when none of
 * these plugins is installed nothing from them is ever loaded and the plugin keeps working for Java players.
 */
public final class BedrockBridges {

    public static final String FLOODGATE = "floodgate";
    public static final String GEYSER    = "Geyser-Spigot";

    private BedrockBridges() {
    }

    /**
     * @param plugin   plugin instance (for logging).
     * @param provider 'auto', 'floodgate' or 'geyser'.
     * @return a bridge, or null if no supported API is available.
     */
    @Nullable
    public static BedrockBridge detect(@NonNull PetsPlugin plugin, @NonNull String provider) {
        String mode = provider.toLowerCase(Locale.ROOT).trim();

        boolean tryFloodgate = mode.equals("auto") || mode.equals("floodgate");
        boolean tryGeyser = mode.equals("auto") || mode.equals("geyser");
        if (!tryFloodgate && !tryGeyser) {
            plugin.warn("Unknown Bedrock provider '" + provider + "'. Use 'auto', 'floodgate' or 'geyser'. Falling back to 'auto'.");
            tryFloodgate = true;
            tryGeyser = true;
        }

        if (tryFloodgate && isEnabled(FLOODGATE)) {
            try {
                return new FloodgateBridge();
            }
            catch (Throwable throwable) {
                plugin.warn("Floodgate is installed, but its API could not be used: " + throwable.getMessage());
            }
        }

        if (tryGeyser && isEnabled(GEYSER)) {
            try {
                return new GeyserBridge();
            }
            catch (Throwable throwable) {
                plugin.warn("Geyser is installed, but its API could not be used: " + throwable.getMessage());
            }
        }

        return null;
    }

    private static boolean isEnabled(@NonNull String pluginName) {
        return Bukkit.getPluginManager().isPluginEnabled(pluginName);
    }
}
