package su.nightexpress.combatpets.nms.paper26p2.pets.brained;

import org.jspecify.annotations.NonNull;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ServerLevelAccessor;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.paper26p2.ai.AIUtils;
import su.nightexpress.combatpets.nms.paper26p2.ai.mob.GenericAi;

@Deprecated // TODO Need Rework
public class AxolotlPet extends Axolotl implements PetEntity {

    public AxolotlPet(@NonNull ServerLevel level) {
        super(EntityTypes.AXOLOTL, level);
    }

    @Override
    public void setGoals() {
    }

    // TODO
    /* @Override
    protected Brain.Provider<Axolotl> brainProvider() {
        return PetBrain.brainProvider(this);
    }
    
    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return PetBrain.refreshBrain(this, this.brainProvider().makeBrain(dynamic));
    } */

    @Override
    protected void customServerAiStep(ServerLevel level) {
        ProfilerFiller filler = Profiler.get();
        filler.push("axolotlBrain");
        this.getBrain().tick(level, this);
        filler.pop();
        filler.push("axolotlActivityUpdate");
        GenericAi.updateActivity(this);
        filler.pop();
    }

    @Override
    public SpawnGroupData finalizeSpawn(
                                        ServerLevelAccessor accessor,
                                        DifficultyInstance difficulty,
                                        EntitySpawnReason reason,
                                        SpawnGroupData groupData) {
        return groupData;
    }

    @Override
    protected void handleAirSupply(ServerLevel worldserver, int i) {
        this.rehydrate();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return this.level().isClientSide() ? InteractionResult.CONSUME : InteractionResult.SUCCESS_SERVER;
    }

    @Override
    protected void hurtArmor(DamageSource source, float amount) {
        this.doHurtEquipment(source, amount, AIUtils.ARMOR_SLOTS);
    }

    // Vanilla Mob.canAttack() always rejects Ghasts, which drops them as a target right away. Pets may attack them.
    @Override
    public boolean canAttack(net.minecraft.world.entity.LivingEntity target) {
        return super.canAttack(target) || (target instanceof net.minecraft.world.entity.monster.Ghast && target.canBeSeenAsEnemy());
    }
}
