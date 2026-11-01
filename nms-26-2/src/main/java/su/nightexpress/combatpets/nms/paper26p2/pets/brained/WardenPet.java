package su.nightexpress.combatpets.nms.paper26p2.pets.brained;

import javax.annotation.Nullable;

import org.jspecify.annotations.NullMarked;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import su.nightexpress.combatpets.api.pet.PetAbilitySettings;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.monster.warden.WardenAi;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.api.pet.PetSharedData;
import su.nightexpress.combatpets.nms.paper26p2.ai.AIUtils;
import su.nightexpress.combatpets.nms.paper26p2.ai.mob.GenericAi;

@NullMarked
public class WardenPet extends Warden implements PetEntity {

    private final PetSharedData sharedData;

    public WardenPet(ServerLevel level, PetSharedData sharedData) {
        super(EntityTypes.WARDEN, level);
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
        this.tickSonicBoom(level);
    }

    // ------------------------------------------------------------------------------------------
    // Sonic boom: like vanilla, used when the Warden can't hit its target in melee.
    // ------------------------------------------------------------------------------------------

    private static final int BOOM_DURATION   = 60; // ticks, vanilla
    private static final int BOOM_HIT_TICK   = 34; // ticks after the charge starts, vanilla
    private static final int BOOM_COOLDOWN   = 40; // ticks, vanilla
    private static final int MELEE_GRACE     = 40; // no boom this long after a melee hit or a new target
    private static final double BOOM_RANGE_H = 15D;
    private static final double BOOM_RANGE_V = 20D;

    private int boomTick = -1;
    private int boomCooldown;
    private int lastMeleeTick = -1000;
    private int targetSinceTick;
    @Nullable
    private LivingEntity lastTarget;

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        this.lastMeleeTick = this.tickCount;
        return super.doHurtTarget(level, target);
    }

    private void tickSonicBoom(ServerLevel level) {
        if (this.boomCooldown > 0) this.boomCooldown--;

        LivingEntity target = this.getTarget();
        if (target != this.lastTarget) {
            this.lastTarget = target;
            this.targetSinceTick = this.tickCount;
        }

        // Charging / firing.
        if (this.boomTick >= 0) {
            this.boomTick++;
            if (this.boomTick == BOOM_HIT_TICK && target != null && this.canSonicBoom(target)) {
                this.fireSonicBoom(level, target);
            }
            if (this.boomTick >= BOOM_DURATION || target == null) {
                this.boomTick = -1;
                this.boomCooldown = BOOM_COOLDOWN;
            }
            return;
        }

        if (!PetAbilitySettings.isWardenSonicBoom() || target == null || this.boomCooldown > 0) return;
        // Prefer melee: only boom when the Warden hasn't landed a hit for a while.
        if (this.tickCount - this.lastMeleeTick < MELEE_GRACE || this.tickCount - this.targetSinceTick < MELEE_GRACE) return;
        if (!this.canSonicBoom(target)) return;

        this.boomTick = 0;
        level.broadcastEntityEvent(this, (byte) 62); // sonic boom animation
        this.playSound(SoundEvents.WARDEN_SONIC_CHARGE, 3.0F, 1.0F);
    }

    private boolean canSonicBoom(LivingEntity target) {
        if (!target.isAlive() || target.level() != this.level()) return false;
        if (this.getOwnerId().equals(target.getUUID())) return false;
        if (target instanceof Player player && (player.isCreative() || player.isSpectator())) return false;

        return this.closerThan(target, BOOM_RANGE_H, BOOM_RANGE_V);
    }

    private void fireSonicBoom(ServerLevel level, LivingEntity target) {
        Vec3 start = this.position().add(0D, 1.6D, 0D);
        Vec3 delta = target.getEyePosition().subtract(start);
        Vec3 direction = delta.normalize();
        int steps = Mth.floor(delta.length()) + 7;
        for (int step = 1; step < steps; step++) {
            Vec3 point = start.add(direction.scale(step));
            level.sendParticles(ParticleTypes.SONIC_BOOM, point.x, point.y, point.z, 1, 0D, 0D, 0D, 0D);
        }
        this.playSound(SoundEvents.WARDEN_SONIC_BOOM, 3.0F, 1.0F);

        float damage = (float) PetAbilitySettings.getWardenSonicBoomDamage();
        if (damage <= 0F) return;

        if (target.hurtServer(level, this.damageSources().sonicBoom(this), damage)) {
            double resist = 1D - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
            target.push(direction.x * 2.5D * resist, direction.y * 0.5D * resist, direction.z * 2.5D * resist);
        }
    }
}
