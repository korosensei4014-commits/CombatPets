package su.nightexpress.combatpets.nms.paper26p2.ai.activity;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import org.jspecify.annotations.NullMarked;

import com.mojang.datafixers.util.Pair;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.player.Player;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.paper26p2.ai.AIUtils;
import su.nightexpress.combatpets.nms.paper26p2.ai.MobUtils;

@NullMarked
public final class IdleActivities {

    private static final double OWNER_REACH_RANGE = 2D;

    private IdleActivities() {
    }

    public static RunOne<PathfinderMob> createIdleMovementBehaviors() {
        return new RunOne<>(
            List.of(
                Pair.of(RandomStroll.stroll(0.4F, 1, 1), 2),
                Pair.of(setLookTargetFromNearestEntities(entity -> true), 3),
                Pair.of(new DoNothing(30, 60), 6)
            )
        );
    }

    public static BehaviorControl<LivingEntity> setLookTargetFromNearestEntities(Predicate<LivingEntity> predicate) {
        return BehaviorBuilder.create(
            instance -> instance.group(
                instance.absent(MemoryModuleType.LOOK_TARGET),
                instance.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES))
                .apply(instance, (lookTarget, nearestEntities) -> (level, body, timestamp) -> {
                    float maxDist = (float) body.getAttributeValue(Attributes.FOLLOW_RANGE) / 2F;
                    float maxDistSqr = maxDist * maxDist;

                    LivingEntity target = instance.get(nearestEntities)
                        .find(predicate.and(mob -> mob.distanceToSqr(body) <= maxDistSqr))
                        .findFirst()
                        .orElse(null);
                    if (target == null) return false;

                    lookTarget.set(new EntityTracker(target, true));
                    return true;
                })
        );
    }

    public static BehaviorControl<Mob> setWalkLookTargetToNearbyOwner() {
        return BehaviorBuilder.create(instance -> {
            return instance.group(
                instance.registered(MemoryModuleType.LOOK_TARGET),
                instance.absent(MemoryModuleType.WALK_TARGET),
                instance.registered(MemoryModuleType.NEAREST_PLAYERS)
            )
                .apply(instance, (lookTarget, walkTarget, nearestPlayers) -> {
                    return (level, pet, timestamp) -> {
                        if (!(pet instanceof PetEntity petEntity)) return false;

                        UUID ownerId = petEntity.getSharedData().getOwnerId();
                        Player owner = instance.tryGet(nearestPlayers)
                            .orElse(List.of())
                            .stream()
                            .filter(player -> player.getUUID().equals(ownerId))
                            .findFirst()
                            .orElse(null);

                        if (owner == null || pet.closerThan(owner, OWNER_REACH_RANGE)) return false;

                        boolean forEyes = pet.getType() == EntityTypes.ALLAY;

                        //lookTarget.set(new EntityTracker(owner, true));
                        walkTarget.set(new WalkTarget(new EntityTracker(owner, forEyes), 1F, 1));
                        return true;
                    };
                });
        });
    }

    public static BehaviorControl<Mob> teleportToOwnerWhenFarAway() {
        return BehaviorBuilder.create(instance -> {
            return instance.group(
                instance.registered(MemoryModuleType.WALK_TARGET),
                instance.registered(MemoryModuleType.NEAREST_PLAYERS)
            ).apply(instance, (walkTarget, nearestPlayers) -> {
                return (level, pet, timestamp) -> {
                    if (!(pet instanceof PetEntity petEntity)) return false;

                    UUID ownerId = petEntity.getSharedData().getOwnerId();
                    Player owner = instance.tryGet(nearestPlayers)
                        .orElse(List.of())
                        .stream()
                        .filter(player -> player.getUUID().equals(ownerId))
                        .findFirst()
                        .orElse(null);

                    if (owner == null) {
                        ServerPlayer find = MobUtils.getOwner(ownerId);
                        if (find == null || pet.closerThan(find, OWNER_REACH_RANGE)) return false;

                        MobUtils.teleportToAround(pet, find.blockPosition());
                        walkTarget.erase();
                        nearestPlayers.set(List.of(find));
                        return true;
                    }

                    return false;
                };
            });
        });
    }

    /**
     * Sets pet's MemoryModuleType.ATTACK_TARGET value when pet has ANGRY_AT memory value.
     * 
     * @return
     */
    public static BehaviorControl<Mob> startAttackingNearestValidTarget() {
        return StartAttacking.create(AIUtils::findAngerOrAttackerTarget);
    }
}
