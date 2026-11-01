package su.nightexpress.combatpets.nms.paper26p12.pets.monster;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.ClimbOnTopOfPowderSnowGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.level.ServerLevelAccessor;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.paper26p12.ai.AIUtils;
import su.nightexpress.combatpets.nms.paper26p12.goals.combat.PetAutoTargetGoal;
import su.nightexpress.combatpets.nms.paper26p12.goals.combat.PetMeleeAttackGoal;
import su.nightexpress.combatpets.nms.paper26p12.goals.follow.PetFollowOwnerGoal;

@NullMarked
public class SilverfishPet extends Silverfish implements PetEntity {

    public SilverfishPet(ServerLevel level) {
        super(EntityType.SILVERFISH, level);
    }

    @Override
    public void setGoals() {

        this.targetSelector.addGoal(1, new PetAutoTargetGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ClimbOnTopOfPowderSnowGoal(this, this.level()));
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
    protected void hurtArmor(DamageSource source, float amount) {
        this.doHurtEquipment(source, amount, AIUtils.ARMOR_SLOTS);
    }

    // Vanilla mobs never target Ghasts (the target gets dropped right away). Pets may attack anything.
    @Override
    public boolean canAttackType(net.minecraft.world.entity.EntityType<?> type) {
        return true;
    }
}
