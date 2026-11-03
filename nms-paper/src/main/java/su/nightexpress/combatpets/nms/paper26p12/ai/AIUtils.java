package su.nightexpress.combatpets.nms.paper26p12.ai;

import java.util.Optional;
import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.player.Player;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.api.pet.type.CombatMode;

@NullMarked
public final class AIUtils {

    private static final int MAX_TICKS_TO_AUTO_ATTACK = 50;

    public static final EquipmentSlot[] ARMOR_SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    private AIUtils() {
    }

    public static boolean isDamagedBySomeone(LivingEntity entity) {
        return entity.tickCount - entity.getLastHurtByMobTimestamp() <= MAX_TICKS_TO_AUTO_ATTACK && entity
            .getLastHurtByMob() != entity;
    }

    public static boolean isAttackedSomeone(LivingEntity entity) {
        return entity.tickCount - entity.getLastHurtMobTimestamp() <= MAX_TICKS_TO_AUTO_ATTACK && entity
            .getLastHurtMob() != entity;
    }

    @Nullable
    @Deprecated
    public static LivingEntity findTarget(LivingEntity pet, ActivePet activePet) {
        // Ridden pets only attack what their rider hits (set by the plugin), never pick targets themselves.
        if (pet.getFirstPassenger() instanceof Player) return null;

        Player owner = ((CraftPlayer) activePet.getOwner()).getHandle();
        CombatMode combatMode = activePet.getCombatMode();

        LivingEntity angerTarget = null;

        if (isDamagedBySomeone(pet) && combatMode != CombatMode.PASSIVE) {
            angerTarget = pet.getLastHurtByMob();
        }

        if (angerTarget == null && (combatMode == CombatMode.PROTECTIVE || combatMode == CombatMode.PROTECTIVE_AND_SUPPORTIVE)) {
            if (AIUtils.isDamagedBySomeone(owner)) {
                angerTarget = owner.getLastHurtByMob();
            }
        }

        if (angerTarget == null && (combatMode == CombatMode.SUPPORTIVE || combatMode == CombatMode.PROTECTIVE_AND_SUPPORTIVE)) {
            if (AIUtils.isAttackedSomeone(owner)) {
                angerTarget = owner.getLastHurtMob();

                // Projectiles set lastHurtMob even if damage event was cancelled
                // so need to double check if target was actually damaged.
                if (angerTarget != null && angerTarget.getLastHurtByMob() != owner) {
                    angerTarget = null;
                }
            }
        }

        return angerTarget;
    }

    public static Optional<? extends LivingEntity> findAngerOrAttackerTarget(ServerLevel level, Mob pet) {
        // Ridden pets only attack what their rider hits (set by the plugin), never pick targets themselves.
        if (pet.getFirstPassenger() instanceof Player) return Optional.empty();

        if (!(pet instanceof PetEntity entity)) return Optional.empty();

        CombatMode combatMode = entity.getSharedData().getCombatMode();
        if (combatMode == CombatMode.PASSIVE) return Optional.empty();

        ServerPlayer owner = MobUtils.getOwner(entity);
        if (owner == null) return Optional.empty();

        LivingEntity angerTarget = null;

        DamageSource source = pet.getLastDamageSource();
        if (source != null) {
            angerTarget = pet.getBrain().getMemory(MemoryModuleType.HURT_BY_ENTITY).orElse(null);
        }

        if (angerTarget == null) {
            angerTarget = findOwnerProtectiveAttackTarget(level, pet, owner, combatMode);
        }

        if (angerTarget == null) return Optional.empty();

        // Whether `attacker` is close enough and is current target or not invisible.
        if (!Sensor.isEntityAttackableIgnoringLineOfSight(level, pet, angerTarget)) {
            return Optional.empty();
        }

        // If pet's current target is closer than `attcker` one.
        if (BehaviorUtils.isOtherTargetMuchFurtherAwayThanCurrentAttackTarget(pet, angerTarget, 4D)) {
            return Optional.empty();
        }

        return Optional.of(angerTarget);
    }

    public static @Nullable LivingEntity findOwnerProtectiveAttackTarget(ServerLevel level,
                                                                         Mob pet,
                                                                         ServerPlayer owner,
                                                                         CombatMode combatMode) {
        boolean bothModes = combatMode == CombatMode.PROTECTIVE_AND_SUPPORTIVE;

        if ((combatMode == CombatMode.PROTECTIVE || bothModes) && isDamagedBySomeone(owner)) {
            return owner.getLastHurtByMob();
        }

        if ((combatMode == CombatMode.SUPPORTIVE || bothModes) && isAttackedSomeone(owner)) {
            LivingEntity angerTarget = owner.getLastHurtMob();

            // Projectiles set lastHurtMob even if damage event was cancelled
            // so need to double check if target was actually damaged.
            if (angerTarget != null && angerTarget.getLastHurtByMob() == owner) {
                return angerTarget;
            }
        }

        return null;
    }

    /*public static Optional<LivingEntity> getAngerTarget(LivingEntity pet) {
        return BehaviorUtils.getLivingEntityFromUUIDMemory(pet, MemoryModuleType.ANGRY_AT);
    }
    
    public static void setAngryAt(LivingEntity pet, LivingEntity target) {
        pet.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        pet.getBrain().setMemoryWithExpiry(MemoryModuleType.ANGRY_AT, target.getUUID(), 120L * 20L);
    }
    
     public static void handlePetWasAttacked(ServerLevel level, Mob pet, LivingEntity attacker) {
        // Whether `attacker` is close enough and is current target or not invisible.
        if (!Sensor.isEntityAttackableIgnoringLineOfSight(level, pet, attacker)) return;
    
        // If pet's current target is closer than `attcker` one.
        if (BehaviorUtils.isOtherTargetMuchFurtherAwayThanCurrentAttackTarget(pet, attacker, 4D)) return;
    
        // Set ANGRY_AT memory value, so pet's IDLE activity StartAttacking will trigger for this and switch to FIGHT activity.
        setAngryAt(pet, attacker);
    } */

    public static Optional<LivingEntity> getAttackTarget(Mob pet) {
        return pet.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
    }

    public static boolean hasAttackTarget(Mob mob) {
        return getAttackTarget(mob).isPresent();
    }

    // Reimplemention of the Entity#tunderHit method to cancel lightning effects and apply damage for certain mobs like
    // Mooshroms, Pigs.
    public static void thunderHit(LivingEntity entity, ServerLevel level, LightningBolt bolt) {
        entity.setRemainingFireTicks(entity.getRemainingFireTicks() + 1);
        org.bukkit.entity.Entity bukkitEntity = entity.getBukkitEntity();
        org.bukkit.entity.Entity bukkitStorm = bolt.getBukkitEntity();
        if (entity.getRemainingFireTicks() == 0) {
            EntityCombustByEntityEvent entityCombustEvent = new EntityCombustByEntityEvent(bukkitStorm, bukkitEntity, 8.0F);
            Bukkit.getPluginManager().callEvent(entityCombustEvent);
            if (!entityCombustEvent.isCancelled()) {
                entity.igniteForSeconds(entityCombustEvent.getDuration(), false);
            }
        }

        if (!entity.fireImmune()) {
            entity.hurtServer(level, entity.damageSources().lightningBolt().eventEntityDamager(bolt), 5.0F);
        }
    }

    // Simulate natural mob damage, but without the actual damager to prevent certain AI goals from triggering.
    public static boolean hurt(LivingEntity pet,
                               DamageSource original,
                               Function<DamageSource, Boolean> hurtServer) {
        DamageSource fixed = new DamageSource(original.typeHolder(), original.getDirectEntity(), null, original
            .sourcePositionRaw());

        if (original.getEntity() instanceof ServerPlayer player && pet instanceof PetEntity petEntity && petEntity
            .getOwnerId().equals(player.getUUID())) {
            return false;
        }

        boolean flag = hurtServer.apply(fixed);

        if (original.getEntity() instanceof LivingEntity mob) {
            pet.setLastHurtByMob(mob);
        }
        if (original.getEntity() instanceof ServerPlayer player) {
            pet.setLastHurtByPlayer(player, LivingEntity.PLAYER_HURT_EXPERIENCE_TIME);
        }
        pet.handleDamageEvent(original);

        return flag;
    }
}
