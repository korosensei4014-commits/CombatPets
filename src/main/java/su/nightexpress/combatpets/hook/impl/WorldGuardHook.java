package su.nightexpress.combatpets.hook.impl;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import su.nightexpress.combatpets.PetsPlugin;

/**
 * WorldGuard integration. Only loaded when WorldGuard is installed (see {@link su.nightexpress.combatpets.hook.RegionHook}).
 * <ul>
 *     <li>{@code pet-pvp}: when DENY, pets can't hurt players or other pets and can't be hurt by them.</li>
 *     <li>{@code pet-summon}: when DENY, pets can't be summoned in or enter the region.</li>
 * </ul>
 * Both flags default to ALLOW, so regions without them behave as before.
 */
public final class WorldGuardHook {

    public static final String FLAG_PET_PVP    = "pet-pvp";
    public static final String FLAG_PET_SUMMON = "pet-summon";

    private static StateFlag petPvpFlag;
    private static StateFlag petSummonFlag;

    private WorldGuardHook() {
    }

    /**
     * Registers the flags. Must run in onLoad(), before WorldGuard is enabled.
     */
    public static void registerFlags(@NonNull PetsPlugin plugin) {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        petPvpFlag = register(plugin, registry, FLAG_PET_PVP);
        petSummonFlag = register(plugin, registry, FLAG_PET_SUMMON);
    }

    @Nullable
    private static StateFlag register(@NonNull PetsPlugin plugin, @NonNull FlagRegistry registry, @NonNull String name) {
        try {
            StateFlag flag = new StateFlag(name, true);
            registry.register(flag);
            return flag;
        }
        catch (FlagConflictException | IllegalStateException exception) {
            // Already registered (e.g. plugin reloaded) or registration closed: reuse the existing flag if possible.
            Flag<?> existing = registry.get(name);
            if (existing instanceof StateFlag stateFlag) {
                return stateFlag;
            }
            plugin.warn("Could not register WorldGuard flag '" + name + "': " + exception.getMessage());
            return null;
        }
    }

    public static boolean isReady() {
        return petPvpFlag != null || petSummonFlag != null;
    }

    /**
     * @return true if pet PvP is denied at the location.
     */
    public static boolean isPvpDenied(@NonNull Location location) {
        if (petPvpFlag == null || location.getWorld() == null) return false;

        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        return !query.testState(BukkitAdapter.adapt(location), (LocalPlayer) null, petPvpFlag);
    }

    /**
     * @return true if the player's pet may not be summoned / stay at the location.
     * Players with WorldGuard region bypass are never blocked.
     */
    public static boolean isSummonDenied(@NonNull Player owner, @NonNull Location location) {
        if (petSummonFlag == null || location.getWorld() == null) return false;

        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(owner);
        if (WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, BukkitAdapter.adapt(location.getWorld()))) {
            return false;
        }

        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        return !query.testState(BukkitAdapter.adapt(location), localPlayer, petSummonFlag);
    }
}
