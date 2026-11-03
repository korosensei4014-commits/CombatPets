package su.nightexpress.combatpets.api.pet;

import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Set;

/**
 * Pet types that can be ridden and steered by their owner (Pets.Mounts.*). Set by the plugin from config.yml and
 * read by the version-specific pet classes. Horse-type pets are always rideable and aren't listed here.
 */
public final class PetMountSettings {

    private static volatile Set<EntityType> enabled = Set.of();
    private static volatile int             syncInterval = 2;

    private PetMountSettings() {
    }

    public static void setEnabled(@NonNull Collection<EntityType> types) {
        enabled = Set.copyOf(types);
    }

    /**
     * @param interval Ticks between movement updates sent to players while a mount is ridden (Pets.Mounts.Sync_Interval).
     */
    public static void setSyncInterval(int interval) {
        syncInterval = Math.max(1, interval);
    }

    public static int getSyncInterval() {
        return syncInterval;
    }

    public static boolean isMountEnabled(@NonNull EntityType type) {
        return enabled.contains(type);
    }
}
