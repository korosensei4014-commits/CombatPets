package su.nightexpress.combatpets.nms.paper26p12.pets.companion;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ServerLevelAccessor;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.paper26p12.ai.AIUtils;
import su.nightexpress.combatpets.nms.paper26p12.goals.follow.PetFollowOwnerGoal;

/**
 * Companion-only pet: follows its owner like other pets but never fights.
 */
@NullMarked
public class ParrotPet extends Parrot implements PetEntity {

    public ParrotPet(ServerLevel world) {
        super(EntityType.PARROT, world);
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
}
