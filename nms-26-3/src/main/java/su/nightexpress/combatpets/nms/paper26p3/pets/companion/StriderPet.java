package su.nightexpress.combatpets.nms.paper26p3.pets.companion;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ServerLevelAccessor;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.paper26p3.ai.AIUtils;
import su.nightexpress.combatpets.nms.paper26p3.goals.follow.PetFollowOwnerGoal;

/**
 * Companion-only pet: follows its owner like other pets but never fights.
 */
@NullMarked
public class StriderPet extends Strider implements PetEntity {

    public StriderPet(ServerLevel world) {
        super(EntityTypes.STRIDER, world);
    }

    @Override
    public void setGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
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
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return this.level().isClientSide() ? InteractionResult.CONSUME : InteractionResult.SUCCESS_SERVER;
    }

    @Override
    protected void hurtArmor(DamageSource source, float amount) {
        this.doHurtEquipment(source, amount, AIUtils.ARMOR_SLOTS);
    }

    // Companion-only pet: never has a target. Frogs and Axolotls read their target from a brain memory that the
    // companion brain doesn't have, which threw "Unregistered memory fetched: attack_target".
    @Override
    public @Nullable LivingEntity getTarget() {
        return null;
    }

    // Companion-only pet: never attacks anything.
    @Override
    public boolean canAttack(LivingEntity target) {
        return false;
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        return false;
    }

    // ------------------------------------------------------------------------------------------
    // Riding (Pets.Mounts): the owner steers like on a horse; space jumps.
    // ------------------------------------------------------------------------------------------

    @Override
    public net.minecraft.world.entity.@org.jspecify.annotations.Nullable LivingEntity getControllingPassenger() {
        if (su.nightexpress.combatpets.api.pet.PetMountSettings.isMountEnabled(org.bukkit.entity.EntityType.STRIDER)
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
