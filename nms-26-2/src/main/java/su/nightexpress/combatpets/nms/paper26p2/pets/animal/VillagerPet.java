package su.nightexpress.combatpets.nms.paper26p2.pets.animal;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.ServerLevelAccessor;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.paper26p2.ai.AIUtils;
import su.nightexpress.combatpets.nms.paper26p2.goals.combat.PetAutoTargetGoal;
import su.nightexpress.combatpets.nms.paper26p2.goals.combat.PetMeleeAttackGoal;
import su.nightexpress.combatpets.nms.paper26p2.goals.follow.PetFollowOwnerGoal;

@NullMarked
public class VillagerPet extends Villager implements PetEntity {

    public VillagerPet(ServerLevel level) {
        super(EntityTypes.VILLAGER, level);
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

    // TODO
    /* protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return this.brainProvider().makeBrain(dynamic);
    }
    
    public void refreshBrain(ServerLevel worldserver) {
        Brain<Villager> behaviorcontroller = this.getBrain();
        behaviorcontroller.stopAll(worldserver, this);
        this.brain = behaviorcontroller.copyWithoutBehaviors();
    } */

    @Override
    protected void hurtArmor(DamageSource source, float amount) {
        this.doHurtEquipment(source, amount, AIUtils.ARMOR_SLOTS);
    }

    @Override
    protected void ageBoundaryReached() {
    }

    @Override
    public boolean canRestock() {
        return false;
    }

    @Override
    public void restock() {
    }

    @Override
    public boolean shouldRestock(ServerLevel worldserver) {
        return false;
    }

    @Override
    public void releaseAllPois() {
    }

    @Override
    public boolean canBreed() {
        return false;
    }

    @Override
    public void setOffers(MerchantOffers merchantrecipelist) {

    }

    @Override
    public void increaseMerchantCareer(ServerLevel worldserver) {
    }

    @Override
    public void thunderHit(ServerLevel level, LightningBolt bolt) {
        AIUtils.thunderHit(this, level, bolt);
    }

    @Override
    public boolean wantsToPickUp(ServerLevel worldserver, ItemStack itemstack) {
        return false;
    }

    @Override
    protected void updateTrades(ServerLevel worldserver) {
    }

    @Override
    public void gossip(ServerLevel worldserver, Villager entityvillager, long i) {
    }

    @Override
    public boolean wantsToSpawnGolem(long i) {
        return false;
    }

    @Override
    public void startSleeping(BlockPos blockposition) {
    }

    @Override
    public void stopSleeping() {
    }

    // Vanilla Mob.canAttack() always rejects Ghasts, which drops them as a target right away. Pets may attack them.
    @Override
    public boolean canAttack(net.minecraft.world.entity.LivingEntity target) {
        return super.canAttack(target) || (target instanceof net.minecraft.world.entity.monster.Ghast && target.canBeSeenAsEnemy());
    }
}
