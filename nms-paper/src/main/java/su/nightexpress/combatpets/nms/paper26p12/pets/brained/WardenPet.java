package su.nightexpress.combatpets.nms.paper26p12.pets.brained;

import javax.annotation.Nullable;

import org.jspecify.annotations.NullMarked;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.monster.warden.WardenAi;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.api.pet.PetSharedData;
import su.nightexpress.combatpets.nms.paper26p12.ai.AIUtils;
import su.nightexpress.combatpets.nms.paper26p12.ai.mob.GenericAi;

@NullMarked
public class WardenPet extends Warden implements PetEntity {

    private final PetSharedData sharedData;

    public WardenPet(ServerLevel level, PetSharedData sharedData) {
        super(EntityType.WARDEN, level);
        this.sharedData = sharedData;
    }

    @Override
    public PetSharedData getSharedData() {
        return this.sharedData;
    }

    // TODO Better AI

    protected Brain.Provider<Warden> brainProvider() {
        return GenericAi.brainProvider(Warden.class);
    }

    @Override
    protected Brain<Warden> makeBrain(Brain.Packed dynamic) {
        return this.brainProvider().makeBrain(this, dynamic);
    }

    @Override
    public void increaseAngerAt(@Nullable Entity entity) {
        if (entity == null || !this.getOwnerId().equals(entity.getUUID())) {
            super.increaseAngerAt(entity);
        }
    }

    @Override
    protected void doPush(Entity entity) {
        if (!this.getOwnerId().equals(entity.getUUID())) {
            if (!this.isNoAi() && !this.getBrain().hasMemoryValue(MemoryModuleType.TOUCH_COOLDOWN)) {
                this.getBrain().setMemoryWithExpiry(MemoryModuleType.TOUCH_COOLDOWN, Unit.INSTANCE, 20L);
                this.increaseAngerAt(entity);
                WardenAi.setDisturbanceLocation(this, entity.blockPosition());
            }
        }
        entity.push(this);
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
    protected void customServerAiStep(ServerLevel level) {
        ProfilerFiller filler = Profiler.get();
        filler.push("wardenBrain");
        this.getBrain().tick(level, this);
        filler.pop();
        GenericAi.updateActivity(this);
    }
}
