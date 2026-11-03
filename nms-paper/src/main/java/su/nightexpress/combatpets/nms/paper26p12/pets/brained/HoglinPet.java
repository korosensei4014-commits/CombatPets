package su.nightexpress.combatpets.nms.paper26p12.pets.brained;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.hoglin.HoglinBase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.api.pet.PetSharedData;
import su.nightexpress.combatpets.nms.paper26p12.ai.AIUtils;
import su.nightexpress.combatpets.nms.paper26p12.ai.BrainUtils;
import su.nightexpress.combatpets.nms.paper26p12.ai.mob.GenericAi;
import su.nightexpress.combatpets.nms.paper26p12.ai.mob.HoglinReAi;

@NullMarked
public class HoglinPet extends Hoglin implements PetEntity {

    private final PetSharedData sharedData;

    public HoglinPet(ServerLevel level, PetSharedData sharedData) {
        super(EntityType.HOGLIN, level);
        this.sharedData = sharedData;
        this.setImmuneToZombification(true);
        this.cannotBeHunted = true;
    }

    @Override
    public PetSharedData getSharedData() {
        return sharedData;
    }

    protected Brain.Provider<Hoglin> brainProvider() {
        return BrainUtils.brainProvider(
            /* Lists.newList(
                SensorType.NEAREST_LIVING_ENTITIES,
                SensorType.HURT_BY
            ), */
            pet -> HoglinReAi.initActivities()
        );
    }

    @Override
    protected Brain<Hoglin> makeBrain(Brain.Packed dynamic) {
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
        filler.push("hoglinBrain");
        this.getBrain().tick(level, this);
        filler.pop();
        GenericAi.updateActivity(this);
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity entity) {
        if (entity instanceof LivingEntity target) {
            this.handleEntityEvent((byte) 4);
            this.level().broadcastEntityEvent(this, (byte) 4);
            this.playSound(SoundEvents.HOGLIN_ATTACK, 1.0F, this.getVoicePitch());
            return HoglinBase.hurtAndThrowTarget(level, this, target);
        }
        return false;
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return level.getBlockState(pos.below()).is(Blocks.CRIMSON_NYLIUM) ? 10.0F : 0.0F;
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
    public boolean isImmuneToZombification() {
        return true;
    }

    @Override
    protected void ageBoundaryReached() {
    }

    @Override
    public boolean canBeHunted() {
        return false;
    }

    @Override
    public boolean isConverting() {
        return false;
    }

    // Vanilla Mob.canAttack() always rejects Ghasts, which drops them as a target right away. Pets may attack them.
    @Override
    public boolean canAttack(net.minecraft.world.entity.LivingEntity target) {
        return super.canAttack(target) || (target instanceof net.minecraft.world.entity.monster.Ghast && target.canBeSeenAsEnemy());
    }

    // ------------------------------------------------------------------------------------------
    // Riding (Pets.Mounts): the owner steers like on a horse; space jumps.
    // ------------------------------------------------------------------------------------------

    @Override
    public net.minecraft.world.entity.@org.jspecify.annotations.Nullable LivingEntity getControllingPassenger() {
        if (su.nightexpress.combatpets.api.pet.PetMountSettings.isMountEnabled(org.bukkit.entity.EntityType.HOGLIN)
            && this.getFirstPassenger() instanceof net.minecraft.world.entity.player.Player rider) {
            return rider;
        }
        return super.getControllingPassenger();
    }

    @Override
    protected void tickRidden(net.minecraft.world.entity.player.Player rider, net.minecraft.world.phys.Vec3 riddenInput) {
        super.tickRidden(rider, riddenInput);
        this.setYRot(rider.getYRot());
        this.yRotO = this.getYRot();
        this.setXRot(rider.getXRot() * 0.5F);
        this.yBodyRot = this.getYRot();
        this.yHeadRot = this.getYRot();
        if (rider instanceof net.minecraft.server.level.ServerPlayer player && player.getLastClientInput().jump() && this.onGround()) {
            this.jumpFromGround();
        }
    }

    // The vanilla client doesn't know this mob can be steered, so it never moves it or sends its movement.
    // Let the server move it, using the rider's WASD keys (which the client always sends).
    @Override
    public boolean isClientAuthoritative() {
        if (this.getControllingPassenger() instanceof net.minecraft.world.entity.player.Player) return false;
        return super.isClientAuthoritative();
    }

    @Override
    protected net.minecraft.world.phys.Vec3 getRiddenInput(net.minecraft.world.entity.player.Player rider, net.minecraft.world.phys.Vec3 selfInput) {
        if (!(rider instanceof net.minecraft.server.level.ServerPlayer player)) return net.minecraft.world.phys.Vec3.ZERO;

        net.minecraft.world.entity.player.Input input = player.getLastClientInput();
        float forward = (input.forward() ? 1F : 0F) - (input.backward() ? 1F : 0F);
        float strafe = ((input.left() ? 1F : 0F) - (input.right() ? 1F : 0F)) * 0.5F;
        if (forward < 0F) forward *= 0.25F; // Slow when walking backwards, like horses.
        return new net.minecraft.world.phys.Vec3(strafe, 0D, forward);
    }

    @Override
    protected float getRiddenSpeed(net.minecraft.world.entity.player.Player rider) {
        return (float) this.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED);
    }
}
