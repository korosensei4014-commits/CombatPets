package su.nightexpress.combatpets.nms.paper26p12.pets.monster;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.projectile.hurtingprojectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.api.pet.type.ExhaustReason;
import su.nightexpress.combatpets.nms.paper26p12.ai.AIUtils;
import su.nightexpress.combatpets.nms.paper26p12.goals.combat.AbstractPetAttackGoal;
import su.nightexpress.combatpets.nms.paper26p12.goals.combat.PetAutoTargetGoal;
import su.nightexpress.combatpets.nms.paper26p12.goals.follow.AbstractPetFollowOwnerGoal;

@NullMarked
public class GhastPet extends Ghast implements PetEntity {

    public GhastPet(ServerLevel world) {
        super(EntityType.GHAST, world);
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetAutoTargetGoal(this));
        this.goalSelector.addGoal(2, new GhastFollowTargetGoal(this));
        this.goalSelector.addGoal(3, new GhastShootFireballGoal(this));
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor,
                                                  DifficultyInstance difficulty,
                                                  EntitySpawnReason reason,
                                                  @Nullable SpawnGroupData groupData) {
        return groupData;
    }

    @Override
    protected void hurtArmor(DamageSource source, float amount) {
        this.doHurtEquipment(source, amount, AIUtils.ARMOR_SLOTS);
    }

    private static class GhastShootFireballGoal extends AbstractPetAttackGoal {

        private final Ghast ghast;

        public GhastShootFireballGoal(Ghast ghast) {
            super(ghast, 1D);
            this.ghast = ghast;
        }

        @Override
        public boolean canUse() {
            return this.ghast.getTarget() != null;
        }

        @Override
        public void start() {

        }

        @Override
        public void stop() {
            this.ghast.setCharging(false);
        }

        // While fighting, the Ghast keeps this distance (blocks, horizontal) from its target.
        private static final double FIGHT_DISTANCE = 8D;

        /**
         * Hover a few blocks away from the target instead of on top of it, so fireballs can reach it.
         * Ground targets: slightly above them. Flying targets (e.g. a Wither, which always rises above its target):
         * stay at a fixed height above the owner instead of chasing them upwards.
         */
        private void keepFightPosition(LivingEntity target) {
            // Ghasts use ground navigation, which would overwrite the wanted position with ground-level path points.
            this.ghast.getNavigation().stop();

            double dx = this.ghast.getX() - target.getX();
            double dz = this.ghast.getZ() - target.getZ();
            double length = Math.sqrt(dx * dx + dz * dz);
            if (length < 0.1D) {
                dx = 1D;
                dz = 0D;
                length = 1D;
            }

            double x = target.getX() + dx / length * FIGHT_DISTANCE;
            double z = target.getZ() + dz / length * FIGHT_DISTANCE;
            double y;
            if (target.onGround() || target.isInWater()) {
                y = target.getY() + 3D;
            }
            else {
                org.bukkit.entity.Player owner = this.petHolder.getOwner();
                y = (owner != null ? owner.getLocation().getY() : target.getY()) + 4D;
            }

            this.ghast.getMoveControl().setWantedPosition(x, y, z, 1D);
            this.ghast.getLookControl().setLookAt(target, 30F, 30F);
            this.ghast.lookAt(target, 30F, 30F);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity target = this.ghast.getTarget();
            if (target == null) return;

            this.keepFightPosition(target);

            if (target.distanceToSqr(this.ghast) < 4096 && this.ghast.hasLineOfSight(target)) {
                Level world = this.ghast.level();
                this.ticksUntilNextAttack--;

                if (this.ticksUntilNextAttack <= 10 && !this.ghast.isSilent()) {
                    world.levelEvent(null, 1015, this.ghast.blockPosition(), 0);
                }

                if (this.isTimeToAttack()) {
                    // Fire from the side of the Ghast that faces the target (its mouth), straight at the target's
                    // body. The old code used the view direction horizontally only, so shots at targets below
                    // or above the Ghast came out of its top and flew past.
                    Vec3 center = new Vec3(this.ghast.getX(), this.ghast.getY(0.5), this.ghast.getZ());
                    Vec3 aimPoint = new Vec3(target.getX(), target.getY(0.5), target.getZ());
                    Vec3 toTarget = aimPoint.subtract(center);
                    if (toTarget.lengthSqr() < 1.0E-4) toTarget = this.ghast.getViewVector(1.0F);
                    Vec3 spawn = center.add(toTarget.normalize().scale(2.5D));
                    Vec3 shot = aimPoint.subtract(spawn);
                    if (shot.lengthSqr() < 1.0E-4) shot = toTarget;

                    if (!this.ghast.isSilent()) {
                        world.levelEvent(null, 1016, this.ghast.blockPosition(), 0);
                    }

                    LargeFireball fireball = new LargeFireball(world, this.ghast, shot.normalize(), this.ghast
                        .getExplosionPower());

                    fireball.bukkitYield = (float) (fireball.explosionPower = this.ghast.getExplosionPower());
                    fireball.setPos(spawn.x, spawn.y, spawn.z);
                    world.addFreshEntity(fireball);
                    this.setAttackCooldown();
                    this.petHolder.doExhaust(ExhaustReason.COMBAT);
                }
            }
            else {
                this.setAttackCooldown();
            }

            this.ghast.setCharging(this.ticksUntilNextAttack <= 10);
        }
    }

    private static class GhastFollowTargetGoal extends AbstractPetFollowOwnerGoal {

        public GhastFollowTargetGoal(Mob pet) {
            super(pet);
        }

        // Only follows the owner. While the Ghast has a target, the shooting goal moves it
        // (both goals use the MOVE flag, so this one must stop or the Ghast never shoots).
        @Override
        public boolean canUse() {
            LivingEntity target = this.pet.getTarget();
            if (target != null && target.isAlive()) {
                return false;
            }
            //LivingEntity target = this.pet.getTarget();
            //if (target != null && target.isAlive()) return false;
            /*
            if (target != null && !target.isAlive()) {
                this.pet.setTarget(null);
            }
            
            if (this.pet.passengers.contains(this.owner)) {
                return false;
            }
            
            MoveControl control = this.pet.getMoveControl();
            if (!control.hasWanted()) {
                return true;
            }
            double x = control.getWantedX() - this.pet.getX();
            double y = control.getWantedY() - this.pet.getY();
            double z = control.getWantedZ() - this.pet.getZ();
            double sqr = (x * x) + (y * y) + (z * z);
            
            return sqr < 1D || sqr > 3600D;*/
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = this.pet.getTarget();
            return target == null || !target.isAlive();
        }

        @Override
        public void start() {//tick() {
            LivingEntity follow = this.owner;
            LivingEntity target = this.pet.getTarget();
            if (target != null && target.isAlive()) {
                follow = target;
            }

            this.pet.getNavigation().moveTo(follow, 1D);
            this.pet.lookAt(this.owner, 30f, 30f);
        }

        @Override
        public void tick() {//start() {
            LivingEntity follow = this.owner;
            LivingEntity target = this.pet.getTarget();
            if (target != null && target.isAlive()) {
                return;
            }

            Location location = follow.getBukkitEntity().getLocation();
            Vector direction = location.getDirection();
            location = location.clone().subtract(direction.multiply(4));

            double x = location.getX();
            double z = location.getZ();
            double y = follow.getY() + 4D;

            // Move & Look to the owner.
            this.pet.getMoveControl().setWantedPosition(x, y, z, 1D);
            this.pet.lookAt(follow, 30f, 30f);
        }

    }
}
