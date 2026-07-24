package su.nightexpress.combatpets.nms.paper26p12.pets.monster;

import java.lang.reflect.Constructor;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.illager.Illusioner;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.scores.Team;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.paper26p12.ai.AIUtils;
import su.nightexpress.combatpets.nms.paper26p12.goals.combat.PetAutoTargetGoal;
import su.nightexpress.combatpets.nms.paper26p12.goals.combat.PetBowAttackGoal;
import su.nightexpress.combatpets.nms.paper26p12.goals.follow.PetFollowOwnerGoal;
import su.nightexpress.nightcore.util.Reflex;

@NullMarked
public class IllusionerPet extends Illusioner implements PetEntity {

    //private static final Constructor<?> CONSTRUCT_BLIND_SPELL_GOAL;
    private static final Constructor<?> CONSTRUCT_MIRROR_SPELL_GOAL;

    static {
        //Class<?> blindSpellGoalClass = Illusioner.class.getDeclaredClasses()[0];
        Class<?> mirrorSpellGoalClass = Illusioner.class.getDeclaredClasses()[1];

        //CONSTRUCT_BLIND_SPELL_GOAL = Reflex.getConstructor(blindSpellGoalClass, Illusioner.class);
        CONSTRUCT_MIRROR_SPELL_GOAL = Reflex.getConstructor(mirrorSpellGoalClass, Illusioner.class);
    }

    public IllusionerPet(ServerLevel world) {
        super(EntityType.ILLUSIONER, world);
    }

    @Override
    public void setGoals() {
        this.targetSelector.addGoal(1, new PetAutoTargetGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PetFollowOwnerGoal(this));

        //Goal blindSpellGoal = (Goal) Reflex.invokeConstructor(CONSTRUCT_BLIND_SPELL_GOAL, this);
        Goal mirrorSpellGoal = (Goal) Reflex.invokeConstructor(CONSTRUCT_MIRROR_SPELL_GOAL, this);

        this.goalSelector.addGoal(1, new SpellcasterCastingSpellGoal());
        this.goalSelector.addGoal(4, mirrorSpellGoal);
        //this.goalSelector.addGoal(5, blindSpellGoal);
        this.goalSelector.addGoal(6, new PetBowAttackGoal<>(this, 0.5, 15.0f));
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
}
