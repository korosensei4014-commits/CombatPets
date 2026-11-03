package su.nightexpress.combatpets.nms.paper26p3.pets.brained;

import java.util.function.BiConsumer;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

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
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.api.pet.PetSharedData;
import su.nightexpress.combatpets.nms.paper26p3.ai.AIUtils;
import su.nightexpress.combatpets.nms.paper26p3.ai.mob.GenericAi;

@NullMarked
public class AllayPet extends Allay implements PetEntity {

    private final PetSharedData sharedData;

    public AllayPet(ServerLevel world, PetSharedData sharedData) {
        super(EntityTypes.ALLAY, world);
        this.sharedData = sharedData;
    }

    @Override
    public PetSharedData getSharedData() {
        return this.sharedData;
    }

    protected Brain.Provider<Allay> brainProvider() {
        // Allays read LIKED_PLAYER directly (when hit, and when a player is nearby); without it registered they
        // threw "Unregistered memory fetched: liked_player" and disappeared right after being summoned.
        return GenericAi.brainProvider(Allay.class, java.util.List.of(
            net.minecraft.world.entity.ai.memory.MemoryModuleType.LIKED_PLAYER,
            net.minecraft.world.entity.ai.memory.MemoryModuleType.LIKED_NOTEBLOCK_POSITION,
            net.minecraft.world.entity.ai.memory.MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS));
    }

    @Override
    protected Brain<Allay> makeBrain(Brain.Packed dynamic) {
        return this.brainProvider().makeBrain(this, dynamic);
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        ProfilerFiller filler = Profiler.get();
        // Like a vanilla Allay given an item by its player: the owner is its liked player (it never hurts them).
        if (!this.getBrain().hasMemoryValue(net.minecraft.world.entity.ai.memory.MemoryModuleType.LIKED_PLAYER)) {
            this.getBrain().setMemory(net.minecraft.world.entity.ai.memory.MemoryModuleType.LIKED_PLAYER, this.getSharedData().getOwnerId());
        }
        filler.push("allayBrain");
        this.getBrain().tick(level, this);
        filler.pop();
        filler.push("allayActivityUpdate");
        GenericAi.updateActivity(this);
        filler.pop();
    }

    @Override
    public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> var0) {
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

    @Override
    protected void dropEquipment(ServerLevel worldserver) {
    }

    @Override
    public boolean wantsToPickUp(ServerLevel worldserver, ItemStack itemstack) {
        return false;
    }

    @Override
    public boolean isPanicking() {
        return false;
    }

    @Override
    public boolean canDuplicate() {
        return false;
    }

    // Vanilla Mob.canAttack() always rejects Ghasts, which drops them as a target right away. Pets may attack them.
    @Override
    public boolean canAttack(net.minecraft.world.entity.LivingEntity target) {
        return super.canAttack(target) || (target instanceof net.minecraft.world.entity.monster.Ghast && target.canBeSeenAsEnemy());
    }
}
