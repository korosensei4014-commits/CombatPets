package su.nightexpress.combatpets.nms.paper26p2.pets.monster;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.paper26p2.ai.AIUtils;
import su.nightexpress.combatpets.nms.paper26p2.goals.combat.PetAutoTargetGoal;
import su.nightexpress.combatpets.nms.paper26p2.goals.combat.PetBowAttackGoal;
import su.nightexpress.combatpets.nms.paper26p2.goals.combat.PetMeleeAttackGoal;
import su.nightexpress.combatpets.nms.paper26p2.goals.follow.PetFollowOwnerGoal;

@NullMarked
public class SkeletonPet extends Skeleton implements PetEntity {

    private PetBowAttackGoal<Skeleton> bowGoal;
    private PetMeleeAttackGoal         meleeGoal;

    public SkeletonPet(ServerLevel level) {
        super(EntityTypes.SKELETON, level);
        this.setFreezeConverting(false);
    }

    @Override
    public void setGoals() {
        this.bowGoal = new PetBowAttackGoal<>(this, 1D, 15F);
        this.meleeGoal = new PetMeleeAttackGoal(this);

        this.targetSelector.addGoal(1, new PetAutoTargetGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PetFollowOwnerGoal(this));
        this.reassessWeaponGoal();
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
    public void reassessWeaponGoal() {
        if (this.meleeGoal == null || this.bowGoal == null) return;

        Level level = this.level();
        if (level == null || level.isClientSide()) return;

        this.goalSelector.removeGoal(this.meleeGoal);
        this.goalSelector.removeGoal(this.bowGoal);
        ItemStack itemstack = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, Items.BOW));
        if (itemstack.is(Items.BOW)) {
            this.goalSelector.addGoal(4, this.bowGoal);
        }
        else {
            this.goalSelector.addGoal(4, this.meleeGoal);
        }
    }

    @Override
    public boolean isFreezeConverting() {
        return false;
    }
}
