package su.nightexpress.combatpets.api.pet;

import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NonNull;

import java.util.Set;

/**
 * Companion-only pet types: they follow their owner like other pets but never fight, and can't be used in duels.
 */
public final class CompanionPets {

    private static final Set<EntityType> TYPES = Set.of(
        EntityType.CAMEL,
        EntityType.CAMEL_HUSK,
        EntityType.COPPER_GOLEM,
        EntityType.STRIDER,
        EntityType.PARROT,
        EntityType.FROG,
        EntityType.AXOLOTL
    );

    private CompanionPets() {
    }

    public static boolean isCompanion(@NonNull EntityType type) {
        return TYPES.contains(type);
    }

    @NonNull
    public static Set<EntityType> getTypes() {
        return TYPES;
    }
}
