package su.nightexpress.combatpets.nms.paper26p2.pets.brained;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ServerLevelAccessor;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.api.pet.PetSharedData;
import su.nightexpress.combatpets.nms.paper26p2.ai.AIUtils;
import su.nightexpress.combatpets.nms.paper26p2.ai.mob.GenericAi;

@NullMarked
public class PiglinBrutePet extends PiglinBrute implements PetEntity {

    private final PetSharedData sharedData;

    public PiglinBrutePet(ServerLevel level, PetSharedData sharedData) {
        super(EntityTypes.PIGLIN_BRUTE, level);
        this.setImmuneToZombification(true);
        this.sharedData = sharedData;
    }

    @Override
    public PetSharedData getSharedData() {
        return this.sharedData;
    }

    protected Brain.Provider<PiglinBrute> brainProvider() {
        return GenericAi.brainProvider(PiglinBrute.class);
    }

    @Override
    protected Brain<PiglinBrute> makeBrain(Brain.Packed dynamic) {
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
        filler.push("piglinBruteBrain");
        this.getBrain().tick(level, this);
        filler.pop();
        GenericAi.updateActivity(this);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        return AIUtils.hurt(this, damageSource, fixed -> super.hurtServer(level, fixed, amount));
    }

    @Override
    protected void hurtArmor(DamageSource source, float amount) {
        this.doHurtEquipment(source, amount, AIUtils.ARMOR_SLOTS);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return this.level().isClientSide() ? InteractionResult.CONSUME : InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public boolean canHunt() {
        return false;
    }

    @Override
    public boolean isConverting() {
        return false;
    }

    public boolean isImmuneToZombification() {
        return true;
    }

    @Override
    protected void playAngrySound() {
    }

    @Override
    public boolean startRiding(Entity entity, boolean flag, boolean flag2) {
        return false;
    }

    // Vanilla mobs never target Ghasts (the target gets dropped right away). Pets may attack anything.
    @Override
    public boolean canAttackType(net.minecraft.world.entity.EntityType<?> type) {
        return true;
    }
}
