package su.nightexpress.combatpets.nms.paper26p12.pets.monster;

import java.util.EnumSet;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ServerLevelAccessor;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.paper26p12.ai.AIUtils;
import su.nightexpress.combatpets.nms.paper26p12.goals.combat.PetAutoTargetGoal;
import su.nightexpress.combatpets.nms.paper26p12.goals.follow.PetFollowOwnerGoal;
import su.nightexpress.nightcore.util.Reflex;

@NullMarked
public class SlimePet extends Slime implements PetEntity {

    public SlimePet(ServerLevel world) {
        super(EntityType.SLIME, world);
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetAutoTargetGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PetFollowOwnerGoal(this));
        this.goalSelector.addGoal(2, new SlimeFollowTargetGoal(this));
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
    protected int getJumpDelay() {
        return 15;
    }

    @Override
    public void push(Entity entity) {
        LivingEntity target = this.getTarget();
        if (target == null || entity != target || entity.getBukkitEntity() == this.getHolder().getOwner()) return;

        if (!(entity instanceof IronGolem)) {
            this.dealDamage(target);
        }

        super.push(entity);
    }

    @Override
    public void playerTouch(Player entity) {
        if (this.holder().isEmpty()) return;
        if (entity.getBukkitEntity() == this.getHolder().getOwner()) return;

        super.playerTouch(entity);
    }

    @Override
    protected boolean isDealsDamage() {
        return true;
    }

    public static class SlimeFollowTargetGoal extends Goal {

        private final Slime slime;

        public SlimeFollowTargetGoal(Slime entityslime) {
            this.slime = entityslime;
            this.setFlags(EnumSet.of(Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity entityliving = this.slime.getTarget();
            return entityliving != null && entityliving.isAlive();
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity entityliving = this.slime.getTarget();
            return entityliving != null && entityliving.isAlive();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity target = this.slime.getTarget();
            if (target == null) return;

            this.slime.lookAt(target, 10.0F, 10.0F);

            Reflex.setFieldValue(slime.getMoveControl(), "yRot", slime.getYRot());
            slime.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1);
        }
    }

    // Vanilla mobs never target Ghasts (the target gets dropped right away). Pets may attack anything.
    @Override
    public boolean canAttackType(net.minecraft.world.entity.EntityType<?> type) {
        return true;
    }
}
