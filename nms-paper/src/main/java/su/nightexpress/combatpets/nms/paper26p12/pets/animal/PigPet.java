package su.nightexpress.combatpets.nms.paper26p12.pets.animal;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ServerLevelAccessor;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.paper26p12.ai.AIUtils;
import su.nightexpress.combatpets.nms.paper26p12.goals.combat.PetAutoTargetGoal;
import su.nightexpress.combatpets.nms.paper26p12.goals.combat.PetMeleeAttackGoal;
import su.nightexpress.combatpets.nms.paper26p12.goals.follow.PetFollowOwnerGoal;

@NullMarked
public class PigPet extends Pig implements PetEntity {

    public PigPet(ServerLevel world) {
        super(EntityType.PIG, world);
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetAutoTargetGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PetFollowOwnerGoal(this));
        this.goalSelector.addGoal(4, new PetMeleeAttackGoal(this));
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor,
                                                  DifficultyInstance difficulty,
                                                  EntitySpawnReason reason,
                                                  @Nullable SpawnGroupData groupData) {
        return groupData;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return this.level().isClientSide() ? InteractionResult.CONSUME : InteractionResult.SUCCESS_SERVER;
    }

    @Override
    protected void hurtArmor(DamageSource source, float amount) {
        this.doHurtEquipment(source, amount, AIUtils.ARMOR_SLOTS);
    }

    @Override
    public void thunderHit(ServerLevel level, LightningBolt bolt) {
        AIUtils.thunderHit(this, level, bolt);
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
        if (su.nightexpress.combatpets.api.pet.PetMountSettings.isMountEnabled(org.bukkit.entity.EntityType.PIG)
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

    @Override
    protected net.minecraft.world.phys.Vec3 getRiddenInput(net.minecraft.world.entity.player.Player rider, net.minecraft.world.phys.Vec3 selfInput) {
        float strafe = rider.xxa * 0.5F;
        float forward = rider.zza;
        if (forward <= 0F) forward *= 0.25F;
        return new net.minecraft.world.phys.Vec3(strafe, 0D, forward);
    }

    @Override
    protected float getRiddenSpeed(net.minecraft.world.entity.player.Player rider) {
        return (float) this.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED);
    }
}
