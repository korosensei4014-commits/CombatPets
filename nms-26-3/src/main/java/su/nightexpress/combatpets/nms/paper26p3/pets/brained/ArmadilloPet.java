package su.nightexpress.combatpets.nms.paper26p3.pets.brained;

import org.jspecify.annotations.NullMarked;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.player.Player;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.api.pet.PetSharedData;
import su.nightexpress.combatpets.nms.paper26p3.ai.AIUtils;
import su.nightexpress.combatpets.nms.paper26p3.ai.mob.GenericAi;

@NullMarked
public class ArmadilloPet extends Armadillo implements PetEntity {

    private final PetSharedData sharedData;

    public ArmadilloPet(ServerLevel level, PetSharedData sharedData) {
        super(EntityTypes.ARMADILLO, level);
        this.sharedData = sharedData;
    }

    @Override
    public PetSharedData getSharedData() {
        return this.sharedData;
    }

    protected Brain.Provider<Armadillo> brainProvider() {
        return GenericAi.brainProvider(Armadillo.class);
    }

    @Override
    protected Brain<Armadillo> makeBrain(Brain.Packed dynamic) {
        return this.brainProvider().makeBrain(this, dynamic);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void customServerAiStep(ServerLevel level) {
        ProfilerFiller filler = Profiler.get();
        filler.push("armadilloBrain");
        ((Brain<LivingEntity>) this.brain).tick(level, this);
        filler.pop();
        filler.push("armadilloActivityUpdate");
        GenericAi.updateActivity(this);
        filler.pop();
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
    public boolean isScaredBy(LivingEntity entityliving) {
        return false;
    }

    @Override
    public void rollUp() {
    }

    // Vanilla mobs never target Ghasts (the target gets dropped right away). Pets may attack anything.
    @Override
    public boolean canAttackType(net.minecraft.world.entity.EntityType<?> type) {
        return true;
    }
}
