package su.nightexpress.combatpets.nms.paper26p2.pets.animal;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.animal.polarbear.PolarBear;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ServerLevelAccessor;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.paper26p2.ai.AIUtils;
import su.nightexpress.combatpets.nms.paper26p2.goals.combat.PetAutoTargetGoal;
import su.nightexpress.combatpets.nms.paper26p2.goals.combat.PetMeleeAttackGoal;
import su.nightexpress.combatpets.nms.paper26p2.goals.follow.PetFollowOwnerGoal;

@NullMarked
public class PolarBearPet extends PolarBear implements PetEntity {

    public PolarBearPet(ServerLevel level) {
        super(EntityTypes.POLAR_BEAR, level);
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetAutoTargetGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PolarBearAttackGoal(this));
        this.goalSelector.addGoal(2, new PetFollowOwnerGoal(this));
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

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return this.level().isClientSide() ? InteractionResult.CONSUME : InteractionResult.SUCCESS_SERVER;
    }

    private static class PolarBearAttackGoal extends PetMeleeAttackGoal {

        private final PolarBearPet bearPet;

        public PolarBearAttackGoal(PolarBearPet bearPet) {
            super(bearPet, 1.25);
            this.bearPet = bearPet;
        }

        @Override
        protected void attack(LivingEntity target) {
            if (this.canPerformAttack(target)) {
                this.setAttackCooldown();
                this.bearPet.doHurtTarget(getServerLevel(this.pet), target);
                this.bearPet.setStanding(false);
                return;
            }

            if (this.bearPet.distanceToSqr(target) < (double) ((target.getBbWidth() + 3F) * (target
                .getBbWidth() + 3F))) {
                if (this.isTimeToAttack()) {
                    this.bearPet.setStanding(false);
                    this.setAttackCooldown();
                }

                if (this.getTicksUntilNextAttack() <= 10) {
                    this.bearPet.setStanding(true);
                    this.bearPet.playWarningSound();
                }

                return;
            }

            this.setAttackCooldown();
            this.bearPet.setStanding(false);
        }

        public void stop() {
            this.bearPet.setStanding(false);
            super.stop();
        }
    }

    // Vanilla Mob.canAttack() always rejects Ghasts, which drops them as a target right away. Pets may attack them.
    @Override
    public boolean canAttack(net.minecraft.world.entity.LivingEntity target) {
        return super.canAttack(target) || (target instanceof net.minecraft.world.entity.monster.Ghast && target.canBeSeenAsEnemy());
    }

    // ------------------------------------------------------------------------------------------
    // Riding (Pets.Mounts): the owner steers like on a horse; space jumps.
    // ------------------------------------------------------------------------------------------

    @Override
    public net.minecraft.world.entity.@org.jspecify.annotations.Nullable LivingEntity getControllingPassenger() {
        if (su.nightexpress.combatpets.api.pet.PetMountSettings.isMountEnabled(org.bukkit.entity.EntityType.POLAR_BEAR)
            && this.getFirstPassenger() instanceof net.minecraft.world.entity.player.Player rider) {
            return rider;
        }
        return super.getControllingPassenger();
    }

    @Override
    protected void tickRidden(net.minecraft.world.entity.player.Player rider, net.minecraft.world.phys.Vec3 riddenInput) {
        super.tickRidden(rider, riddenInput);
        this.setYRot(rider.getYRot());
        this.yRotO = this.getYRot();
        this.setXRot(rider.getXRot() * 0.5F);
        this.yBodyRot = this.getYRot();
        this.yHeadRot = this.getYRot();
        if (rider instanceof net.minecraft.server.level.ServerPlayer player && player.getLastClientInput().jump() && this.onGround()) {
            this.jumpFromGround();
        }
    }

    // The vanilla client doesn't know this mob can be steered, so it never moves it or sends its movement.
    // Let the server move it, using the rider's WASD keys (which the client always sends).
    @Override
    public boolean isClientAuthoritative() {
        if (this.getControllingPassenger() instanceof net.minecraft.world.entity.player.Player) return false;
        return super.isClientAuthoritative();
    }

    @Override
    protected net.minecraft.world.phys.Vec3 getRiddenInput(net.minecraft.world.entity.player.Player rider, net.minecraft.world.phys.Vec3 selfInput) {
        if (!(rider instanceof net.minecraft.server.level.ServerPlayer player)) return net.minecraft.world.phys.Vec3.ZERO;

        net.minecraft.world.entity.player.Input input = player.getLastClientInput();
        float forward = (input.forward() ? 1F : 0F) - (input.backward() ? 1F : 0F);
        float strafe = ((input.left() ? 1F : 0F) - (input.right() ? 1F : 0F)) * 0.5F;
        if (forward < 0F) forward *= 0.25F; // Slow when walking backwards, like horses.
        return new net.minecraft.world.phys.Vec3(strafe, 0D, forward);
    }

    @Override
    protected float getRiddenSpeed(net.minecraft.world.entity.player.Player rider) {
        return (float) this.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED);
    }
}
