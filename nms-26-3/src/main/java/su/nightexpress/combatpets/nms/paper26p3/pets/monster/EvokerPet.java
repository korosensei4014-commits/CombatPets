package su.nightexpress.combatpets.nms.paper26p3.pets.monster;

import java.lang.reflect.Constructor;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.illager.Evoker;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.scores.Team;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.paper26p3.ai.AIUtils;
import su.nightexpress.combatpets.nms.paper26p3.goals.combat.PetAutoTargetGoal;
import su.nightexpress.combatpets.nms.paper26p3.goals.follow.PetFollowOwnerGoal;
import su.nightexpress.nightcore.util.Reflex;

@NullMarked
public class EvokerPet extends Evoker implements PetEntity {

    private static final Constructor<?> CONSTRUCT_ATTACK_SPELL_GOAL;
    private static final Constructor<?> CONSTRUCT_CASTING_SPELL_GOAL;

    static {
        Class<?> classAttackSpellGoal = Reflex.safeInnerClass(Evoker.class.getName(), "EvokerAttackSpellGoal"); // a
        Class<?> clasCastSpellGoal = Reflex.safeInnerClass(Evoker.class.getName(), "EvokerCastingSpellGoal"); // b

        CONSTRUCT_ATTACK_SPELL_GOAL = Reflex.getConstructor(classAttackSpellGoal, Evoker.class);
        CONSTRUCT_CASTING_SPELL_GOAL = Reflex.getConstructor(clasCastSpellGoal, Evoker.class);
    }

    public EvokerPet(ServerLevel world) {
        super(EntityTypes.EVOKER, world);
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetAutoTargetGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PetFollowOwnerGoal(this));

        if (CONSTRUCT_ATTACK_SPELL_GOAL != null) {
            Goal goalA = (Goal) Reflex.invokeConstructor(CONSTRUCT_ATTACK_SPELL_GOAL, this);
            this.goalSelector.addGoal(5, goalA);
        }
        this.goalSelector.addGoal(4, new PetSummonVexGoal());

        if (CONSTRUCT_CASTING_SPELL_GOAL != null) {
            Goal goalB = (Goal) Reflex.invokeConstructor(CONSTRUCT_CASTING_SPELL_GOAL, this);
            this.goalSelector.addGoal(1, goalB);
        }
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
    public boolean canAttack(LivingEntity entity) {
        return entity.canBeSeenAsEnemy();
    }

    @Override
    public boolean isAlliedTo(@Nullable Team scoreboardteambase) {
        return false;
    }

    // ------------------------------------------------------------------------------------------
    // Vex summoning (Pets.Evoker.Summon_Vexes). Same spell timing as vanilla.
    // ------------------------------------------------------------------------------------------

    private final java.util.List<SummonedVex> summonedVexes = new java.util.ArrayList<>();

    private int countVexes() {
        this.summonedVexes.removeIf(vex -> vex.isRemoved() || !vex.isAlive());
        return this.summonedVexes.size();
    }

    private void summonVexes() {
        if (!(this.level() instanceof ServerLevel level)) return;

        int amount = Math.min(3, su.nightexpress.combatpets.api.pet.PetAbilitySettings.getEvokerMaxVexes() - this.countVexes());
        int lifeTicks = 20 * su.nightexpress.combatpets.api.pet.PetAbilitySettings.getEvokerVexLifetime();

        for (int i = 0; i < amount; i++) {
            net.minecraft.core.BlockPos pos = this.blockPosition().offset(-2 + this.random.nextInt(5), 1, -2 + this.random.nextInt(5));
            SummonedVex vex = new SummonedVex(level, this);
            vex.snapTo(pos, 0.0F, 0.0F);
            vex.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), EntitySpawnReason.MOB_SUMMONED, null);
            vex.setOwner(this);
            vex.setBoundOrigin(pos);
            vex.setLimitedLife(lifeTicks);
            if (level.addFreshEntity(vex, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.SPELL)) {
                this.summonedVexes.add(vex);
            }
        }
    }

    private class PetSummonVexGoal extends SpellcasterUseSpellGoal {

        @Override
        public boolean canUse() {
            if (!su.nightexpress.combatpets.api.pet.PetAbilitySettings.isEvokerVexes()) return false;
            if (!super.canUse()) return false;

            return countVexes() < su.nightexpress.combatpets.api.pet.PetAbilitySettings.getEvokerMaxVexes();
        }

        @Override
        protected int getCastingTime() {
            return 100;
        }

        @Override
        protected int getCastingInterval() {
            return 340;
        }

        @Override
        protected void performSpellCasting() {
            summonVexes();
        }

        @Override
        protected net.minecraft.sounds.SoundEvent getSpellPrepareSound() {
            return net.minecraft.sounds.SoundEvents.EVOKER_PREPARE_SUMMON;
        }

        @Override
        protected IllagerSpell getSpell() {
            return IllagerSpell.SUMMON_VEX;
        }
    }
}
