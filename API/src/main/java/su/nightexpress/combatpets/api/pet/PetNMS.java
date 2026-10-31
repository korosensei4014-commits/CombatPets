package su.nightexpress.combatpets.api.pet;

import java.util.Set;
import java.util.function.Function;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface PetNMS {

    Set<EntityType> getSupportedEntities();

    default boolean canSpawn(EntityType entityType) {
        return this.getSupportedEntities().contains(entityType);
    }

    ActivePet spawnPet(Template config, PetSharedData sharedData, Location location,
                       Function<LivingEntity, ActivePet> holderFunction);

    void setSaddle(LivingEntity entity);

    void sneak(LivingEntity entity, boolean value);

    boolean hasNavigationPath(LivingEntity entity);

    /**
     * Makes the pet attack the given target (or stop attacking when null). Works for both goal-based
     * and brain-based pets. Used by pet duels.
     */
    default void setAttackTarget(LivingEntity pet, @org.jspecify.annotations.Nullable LivingEntity target) {
        if (pet instanceof org.bukkit.entity.Mob mob) {
            mob.setTarget(target);
        }
    }
}
