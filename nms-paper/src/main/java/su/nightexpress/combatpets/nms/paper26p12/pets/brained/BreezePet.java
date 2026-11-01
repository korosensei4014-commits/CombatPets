package su.nightexpress.combatpets.nms.paper26p12.pets.brained;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ServerLevelAccessor;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.api.pet.PetSharedData;
import su.nightexpress.combatpets.nms.paper26p12.ai.AIUtils;
import su.nightexpress.combatpets.nms.paper26p12.ai.BrainUtils;
import su.nightexpress.combatpets.nms.paper26p12.ai.mob.BreezeReAi;
import su.nightexpress.combatpets.nms.paper26p12.ai.mob.GenericAi;

@NullMarked
public class BreezePet extends Breeze implements PetEntity {

    private final PetSharedData sharedData;

    public BreezePet(ServerLevel world, PetSharedData sharedData) {
        super(EntityType.BREEZE, world);
        this.sharedData = sharedData;
    }

    @Override
    public PetSharedData getSharedData() {
        return this.sharedData;
    }

    protected Brain.Provider<Breeze> brainProvider() {
        return BrainUtils.brainProvider(
            pet -> BreezeReAi.initActivities()
        );
    }

    @Override
    protected Brain<Breeze> makeBrain(Brain.Packed dynamic) {
        return this.brainProvider().makeBrain(this, dynamic);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor,
                                                  DifficultyInstance difficulty,
                                                  EntitySpawnReason reason,
                                                  @Nullable SpawnGroupData groupData) {
        return groupData;
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        ProfilerFiller filler = Profiler.get();
        filler.push("breezeBrain");
        this.getBrain().tick(level, this);
        filler.popPush("breezeActivityUpdate");
        GenericAi.updateActivity(this);
        filler.pop();
    }

    @Override
    public boolean canAttack(LivingEntity type) {
        return true;
    }

    public boolean isInvulnerableTo(ServerLevel worldserver, DamageSource damagesource) {
        return this.isRemoved() || this.isInvulnerable() && !damagesource.is(
            DamageTypeTags.BYPASSES_INVULNERABILITY) && !damagesource.isCreativePlayer() || damagesource.is(
                DamageTypeTags.IS_FIRE) && this.fireImmune() || damagesource.is(DamageTypeTags.IS_FALL) && this.is(
                    EntityTypeTags.FALL_DAMAGE_IMMUNE);
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
    public boolean startRiding(Entity entity, boolean flag, boolean flag1) {
        return false;
    }

    // Vanilla mobs never target Ghasts (the target gets dropped right away). Pets may attack anything.
    @Override
    public boolean canAttackType(net.minecraft.world.entity.EntityType<?> type) {
        return true;
    }
}
