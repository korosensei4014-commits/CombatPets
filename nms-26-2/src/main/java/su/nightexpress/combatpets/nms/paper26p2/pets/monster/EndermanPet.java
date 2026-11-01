package su.nightexpress.combatpets.nms.paper26p2.pets.monster;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.ServerLevelAccessor;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.paper26p2.ai.AIUtils;
import su.nightexpress.combatpets.nms.paper26p2.goals.combat.PetAutoTargetGoal;
import su.nightexpress.combatpets.nms.paper26p2.goals.combat.PetMeleeAttackGoal;
import su.nightexpress.combatpets.nms.paper26p2.goals.follow.PetFollowOwnerGoal;
import su.nightexpress.nightcore.util.Reflex;

@NullMarked
public class EndermanPet extends EnderMan implements PetEntity {

    private static final String TARGET_CHANGE_TIME = "targetChangeTime";

    private boolean teleportAllowed;

    public EndermanPet(ServerLevel world) {
        super(EntityTypes.ENDERMAN, world);
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetAutoTargetGoal(this));
        //this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PetFollowOwnerGoal(this));
        this.goalSelector.addGoal(2, new PetMeleeAttackGoal(this));
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor,
                                                  DifficultyInstance difficulty,
                                                  EntitySpawnReason reason,
                                                  @Nullable SpawnGroupData groupData) {
        return groupData;
    }

    @Override
    protected void customServerAiStep(ServerLevel worldserver) {
        Reflex.setFieldValue(this, TARGET_CHANGE_TIME, this.tickCount + 600); // targetChangeTime
        super.customServerAiStep(worldserver);
        this.tickChaseTeleport(worldserver);
    }

    // ------------------------------------------------------------------------------------------
    // Chase teleport: like vanilla, teleports next to a target that is far away or can't be reached on foot.
    // ------------------------------------------------------------------------------------------

    private static final double CHASE_MIN_DISTANCE = 8D;  // Never teleport when closer than this.
    private static final double CHASE_FAR_DISTANCE = 16D; // Always teleport when farther than this.
    private static final int    CHASE_COOLDOWN     = 40;  // Ticks between teleports.

    private int chaseCooldown;

    private void tickChaseTeleport(ServerLevel level) {
        if (this.chaseCooldown > 0) {
            this.chaseCooldown--;
            return;
        }

        net.minecraft.world.entity.LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive() || target.level() != this.level()) return;

        double distance = this.distanceTo(target);
        if (distance < CHASE_MIN_DISTANCE) return;

        this.chaseCooldown = 10; // Re-check twice a second.
        if (distance <= CHASE_FAR_DISTANCE) {
            net.minecraft.world.level.pathfinder.Path path = this.getNavigation().createPath(target, 0);
            if (path != null && path.canReach()) return; // Reachable on foot: just walk.
        }

        double oldX = this.getX(), oldY = this.getY(), oldZ = this.getZ();
        su.nightexpress.combatpets.nms.paper26p2.ai.MobUtils.teleportToAround(this, target.blockPosition());
        if (this.getX() == oldX && this.getY() == oldY && this.getZ() == oldZ) return; // No safe spot found.

        level.playSound(null, oldX, oldY, oldZ, net.minecraft.sounds.SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 1.0F);
        this.playSound(net.minecraft.sounds.SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
        this.chaseCooldown = CHASE_COOLDOWN;
    }

    @Override
    public boolean hurtServer(ServerLevel worldserver, DamageSource damagesource, float f) {
        this.teleportAllowed = true;
        return super.hurtServer(worldserver, damagesource, f);
    }

    @Override
    protected void hurtArmor(DamageSource source, float amount) {
        this.doHurtEquipment(source, amount, AIUtils.ARMOR_SLOTS);
    }

    @Override
    public boolean isSensitiveToWater() {
        return false;
    }

    @Override
    public boolean teleport() {
        if (this.teleportAllowed) {
            this.teleportAllowed = false;
            return super.teleport();
        }
        return false;
    }

    // Vanilla Mob.canAttack() always rejects Ghasts, which drops them as a target right away. Pets may attack them.
    @Override
    public boolean canAttack(net.minecraft.world.entity.LivingEntity target) {
        return super.canAttack(target) || (target instanceof net.minecraft.world.entity.monster.Ghast && target.canBeSeenAsEnemy());
    }
}
