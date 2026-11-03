package su.nightexpress.combatpets.nms.paper26p12.pets.companion;

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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ServerLevelAccessor;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.api.pet.PetSharedData;
import su.nightexpress.combatpets.nms.paper26p12.ai.AIUtils;
import su.nightexpress.combatpets.nms.paper26p12.ai.mob.GenericAi;

/**
 * Companion-only pet: follows its owner like other pets but never fights.
 */
@NullMarked
public class FrogPet extends Frog implements PetEntity {

    private final PetSharedData sharedData;

    public FrogPet(ServerLevel level, PetSharedData sharedData) {
        super(EntityType.FROG, level);
        this.sharedData = sharedData;
    }

    @Override
    public PetSharedData getSharedData() {
        return this.sharedData;
    }

    @Override
    protected Brain<Frog> makeBrain(Brain.Packed dynamic) {
        return GenericAi.companionBrainProvider(Frog.class).makeBrain(this, dynamic);
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        ProfilerFiller filler = Profiler.get();
        filler.push("companionPetBrain");
        this.getBrain().tick(level, this);
        filler.pop();
        GenericAi.updateCompanionActivity(this);
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
