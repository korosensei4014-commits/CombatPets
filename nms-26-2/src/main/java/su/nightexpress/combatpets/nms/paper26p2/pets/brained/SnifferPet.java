package su.nightexpress.combatpets.nms.paper26p2.pets.brained;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ServerLevelAccessor;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.api.pet.PetSharedData;
import su.nightexpress.combatpets.nms.paper26p2.ai.AIUtils;
import su.nightexpress.combatpets.nms.paper26p2.ai.mob.GenericAi;

@NullMarked
public class SnifferPet extends Sniffer implements PetEntity {

    private final PetSharedData sharedData;

    public SnifferPet(ServerLevel world, PetSharedData sharedData) {
        super(EntityTypes.SNIFFER, world);
        this.sharedData = sharedData;
    }

    @Override
    public PetSharedData getSharedData() {
        return this.sharedData;
    }

    protected Brain.Provider<Sniffer> brainProvider() {
        return GenericAi.brainProvider(Sniffer.class);
    }

    @Override
    protected Brain<Sniffer> makeBrain(Brain.Packed dynamic) {
        return this.brainProvider().makeBrain(this, dynamic);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor,
                                        DifficultyInstance difficulty,
                                        EntitySpawnReason reason,
                                        @Nullable SpawnGroupData groupData) {
        return new AgeableMob.AgeableMobGroupData(false);
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        ProfilerFiller filler = Profiler.get();
        filler.push("snifferBrain");
        this.getBrain().tick(level, this);
        filler.popPush("snifferActivityUpdate");
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
    public Sniffer storeExploredPosition(BlockPos blockposition) {
        return this;
    }
}
