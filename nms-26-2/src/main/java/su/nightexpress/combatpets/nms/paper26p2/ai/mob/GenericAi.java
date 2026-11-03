package su.nightexpress.combatpets.nms.paper26p2.ai.mob;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.ActivityData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.schedule.Activity;
import su.nightexpress.combatpets.nms.paper26p2.ai.BrainUtils;
import su.nightexpress.combatpets.nms.paper26p2.ai.activity.ActivityBuilder;
import su.nightexpress.combatpets.nms.paper26p2.ai.activity.CoreActivities;
import su.nightexpress.combatpets.nms.paper26p2.ai.activity.FightActivities;
import su.nightexpress.combatpets.nms.paper26p2.ai.activity.IdleActivities;

public final class GenericAi {

    private GenericAi() {
    }

    public static <E extends PathfinderMob> Brain.Provider<E> brainProvider(Class<E> type) {
        return BrainUtils.brainProvider(pet -> initActivities(type));
    }

    /**
     * Brain for companion-only pets: follows the owner and idles, never picks targets or fights.
     */
    public static <E extends PathfinderMob> Brain.Provider<E> companionBrainProvider(Class<E> type) {
        return BrainUtils.brainProvider(pet -> List.of(initCoreActivity(type), initCompanionIdleActivity(type)));
    }

    /**
     * Companion brain for water mobs (Axolotl, Frog): without the "float" behaviour, which makes land mobs paddle
     * at the surface and made Axolotls keep jumping out of the water instead of swimming.
     */
    public static <E extends PathfinderMob> Brain.Provider<E> aquaticCompanionBrainProvider(Class<E> type) {
        return BrainUtils.brainProvider(pet -> List.of(initAquaticCoreActivity(type), initCompanionIdleActivity(type)));
    }

    private static <E extends PathfinderMob> ActivityData<E> initAquaticCoreActivity(Class<E> type) {
        return ActivityBuilder.init(Activity.CORE, type)
            .goal(CoreActivities.lookAtLookTarget())
            .goal(CoreActivities.tryReachWalkTargetOrAbandon())
            .goal(CoreActivities.stopAngerIfTargetDead())
            .build();
    }

    public static void updateCompanionActivity(PathfinderMob pet) {
        pet.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.IDLE));
        pet.setAggressive(false);
    }

    private static <E extends PathfinderMob> ActivityData<E> initCompanionIdleActivity(Class<E> type) {
        return ActivityBuilder.init(Activity.IDLE, type)
            .priorityOfFirstBehavior(10)
            .goal(IdleActivities.setWalkLookTargetToNearbyOwner())
            .goal(IdleActivities.teleportToOwnerWhenFarAway())
            .goal(IdleActivities.createIdleMovementBehaviors())
            .build();
    }

    /**
     * Like {@link #brainProvider(Class)}, plus the vanilla crossbow attack (charge, then shoot) when the pet holds
     * a crossbow. Used by Piglins.
     */
    public static <E extends PathfinderMob & net.minecraft.world.entity.monster.CrossbowAttackMob> Brain.Provider<E> crossbowBrainProvider(Class<E> type) {
        return BrainUtils.brainProvider(pet -> List.of(initCoreActivity(type), initIdleActivity(type), initCrossbowFightActivity(type)));
    }

    private static <E extends PathfinderMob & net.minecraft.world.entity.monster.CrossbowAttackMob> ActivityData<E> initCrossbowFightActivity(Class<E> type) {
        return ActivityBuilder.init(Activity.FIGHT, type)
            .priorityOfFirstBehavior(10)
            .memoryThatMustHaveValueAndWillBeErasedAfter(MemoryModuleType.ATTACK_TARGET)
            .goal(FightActivities.walkAttackTargetIfOutOfRange())
            .goal(FightActivities.meleeAttack())
            .goal(new net.minecraft.world.entity.ai.behavior.CrossbowAttack<E, net.minecraft.world.entity.LivingEntity>())
            .goal(FightActivities.stopAttackingIfFarFromOwner())
            .goal(FightActivities.stopAttackingIfTargetInvalid())
            .build();
    }

    /**
     * Like {@link #brainProvider(Class)}, with extra memory types the vanilla mob reads directly.
     */
    public static <E extends PathfinderMob> Brain.Provider<E> brainProvider(Class<E> type, List<? extends MemoryModuleType<?>> memories) {
        return BrainUtils.brainProvider(memories, pet -> initActivities(type));
    }

    public static void updateActivity(PathfinderMob pet) {
        pet.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
        pet.setAggressive(pet.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET));
    }

    public static <E extends PathfinderMob> List<ActivityData<E>> initActivities(Class<E> type) {
        return List.of(initCoreActivity(type), initIdleActivity(type), initFightActivity(type));
    }

    private static <E extends PathfinderMob> ActivityData<E> initCoreActivity(Class<E> type) {
        return ActivityBuilder.init(Activity.CORE, type)
            .goal(CoreActivities.swim())
            .goal(CoreActivities.lookAtLookTarget())
            .goal(CoreActivities.tryReachWalkTargetOrAbandon())
            .goal(CoreActivities.stopAngerIfTargetDead())
            .build();
    }

    private static <E extends PathfinderMob> ActivityData<E> initIdleActivity(Class<E> type) {
        return ActivityBuilder.init(Activity.IDLE, type)
            .priorityOfFirstBehavior(10)
            .goal(IdleActivities.startAttackingNearestValidTarget())
            .goal(IdleActivities.setWalkLookTargetToNearbyOwner())
            .goal(IdleActivities.teleportToOwnerWhenFarAway())
            .goal(IdleActivities.createIdleMovementBehaviors())
            .build();
    }

    private static <E extends PathfinderMob> ActivityData<E> initFightActivity(Class<E> type) {
        return ActivityBuilder.init(Activity.FIGHT, type)
            .priorityOfFirstBehavior(10)
            .memoryThatMustHaveValueAndWillBeErasedAfter(MemoryModuleType.ATTACK_TARGET)
            .goal(FightActivities.walkAttackTargetIfOutOfRange())
            .goal(FightActivities.meleeAttack())
            .goal(FightActivities.stopAttackingIfFarFromOwner())
            .goal(FightActivities.stopAttackingIfTargetInvalid())
            .build();
    }
}
