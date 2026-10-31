package su.nightexpress.combatpets.hook;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.hook.impl.WorldGuardHook;

/**
 * Safe entry point for region checks. Never touches WorldGuard classes unless WorldGuard is installed,
 * so the plugin works normally without it.
 */
public final class RegionHook {

    private static boolean worldGuard;

    private RegionHook() {
    }

    /**
     * Call from onLoad(). Registers the CombatPets WorldGuard flags if WorldGuard is installed.
     */
    public static void load(@NonNull PetsPlugin plugin) {
        worldGuard = false;
        if (Bukkit.getPluginManager().getPlugin(HookId.WORLD_GUARD) == null) return;

        try {
            WorldGuardHook.registerFlags(plugin);
            worldGuard = WorldGuardHook.isReady();
            if (worldGuard) {
                plugin.getLogger().info("Registered WorldGuard flags: " + WorldGuardHook.FLAG_PET_PVP + ", " + WorldGuardHook.FLAG_PET_SUMMON);
            }
        }
        catch (Throwable throwable) {
            plugin.getLogger().warning("WorldGuard is installed, but the CombatPets flags could not be registered: " + throwable.getMessage());
        }
    }

    public static boolean isWorldGuardEnabled() {
        return worldGuard;
    }

    /**
     * @return true if pets may not fight players / other pets at the location (flag {@code pet-pvp: deny}).
     */
    public static boolean isPetPvpDenied(@NonNull Location location) {
        if (!worldGuard) return false;
        try {
            return WorldGuardHook.isPvpDenied(location);
        }
        catch (Throwable throwable) {
            return false;
        }
    }

    /**
     * @return true if the owner's pet may not be summoned / stay at the location (flag {@code pet-summon: deny}).
     */
    public static boolean isPetSummonDenied(@NonNull Player owner, @NonNull Location location) {
        if (!worldGuard) return false;
        try {
            return WorldGuardHook.isSummonDenied(owner, location);
        }
        catch (Throwable throwable) {
            return false;
        }
    }
}
