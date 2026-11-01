package su.nightexpress.combatpets.nms.paper26p3.pets.monster;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.illager.Pillager;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.scores.Team;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.paper26p3.ai.AIUtils;
import su.nightexpress.combatpets.nms.paper26p3.goals.combat.PetAutoTargetGoal;
import su.nightexpress.combatpets.nms.paper26p3.goals.combat.PetCrossbowAttackGoal;
import su.nightexpress.combatpets.nms.paper26p3.goals.combat.PetMeleeAttackGoal;
import su.nightexpress.combatpets.nms.paper26p3.goals.follow.PetFollowOwnerGoal;

@NullMarked
public class PillagerPet extends Pillager implements PetEntity {

    public PillagerPet(ServerLevel level) {
        super(EntityTypes.PILLAGER, level);
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetAutoTargetGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PetFollowOwnerGoal(this));
        this.goalSelector.addGoal(3, new PetCrossbowAttackGoal<>(this, 1.0D, 8.0F));
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

    @Override
    public boolean isAlliedTo(@Nullable Team team) {
        return false;
    }

    @Override
    public boolean canAttack(LivingEntity entity) {
        return entity.canBeSeenAsEnemy();
    }

    @Override
    public boolean canJoinPatrol() {
        return false;
    }

    @Override
    public boolean canJoinRaid() {
        return false;
    }

    @Override
    public void setCanJoinRaid(boolean flag) {

    }

    @Override
    public void setCurrentRaid(@Nullable Raid raid) {

    }

    @Override
    public @Nullable Raid getCurrentRaid() {
        return null;
    }

    @Override
    protected boolean isPatrolling() {
        return false;
    }

    @Override
    public boolean isPatrolLeader() {
        return false;
    }

    @Override
    protected void setPatrolling(boolean var0) {

    }

    @Override
    public void setPatrolLeader(boolean var0) {

    }

    @Override
    public void setPatrolTarget(BlockPos var0) {

    }

    @Override
    public void setWave(int i) {

    }

    @Override
    public boolean isCelebrating() {
        return false;
    }

    @Override
    protected void pickUpItem(ServerLevel worldserver, ItemEntity entityitem) {

    }

    @Override
    public boolean canBeLeader() {
        return false;
    }

    @Override
    public void findPatrolTarget() {

    }

    @Override
    public void applyRaidBuffs(ServerLevel worldserver, int i, boolean flag) {

    }

    // Vanilla mobs never target Ghasts (the target gets dropped right away). Pets may attack anything.
    @Override
    public boolean canAttackType(net.minecraft.world.entity.EntityType<?> type) {
        return true;
    }
}
