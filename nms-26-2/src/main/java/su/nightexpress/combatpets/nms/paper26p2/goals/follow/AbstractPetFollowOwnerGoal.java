package su.nightexpress.combatpets.nms.paper26p2.goals.follow;

import java.util.EnumSet;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.jspecify.annotations.NonNull;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.api.pet.PetFollowSettings;
import su.nightexpress.nightcore.util.random.Rnd;

public abstract class AbstractPetFollowOwnerGoal extends Goal {

    protected Mob          pet;
    protected ActivePet    petHolder;
    protected ServerPlayer owner;

    public AbstractPetFollowOwnerGoal(@NonNull Mob pet) {
        this.pet = pet;
        this.petHolder = ((PetEntity) pet).getHolder();
        this.owner = ((CraftPlayer) this.petHolder.getOwner()).getHandle();
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    protected double getReach(@NonNull LivingEntity target) {
        return (Math.pow(this.pet.getBbWidth() * 2F, 2) + target.getBbWidth()) * 1.5;
    }

    public boolean isTargettedToEnemy() {
        LivingEntity target = this.pet.getTarget();
        if (target != null && !target.isAlive()) {
            this.pet.setTarget(null);
            return false;
        }

        return target != null && !target.equals(this.owner) && target.isAlive();
    }

    @Override
    public boolean canUse() {
        if (this.isTargettedToEnemy() || this.owner == null || !this.owner.isAlive()) return false;

        if (this.pet.passengers.contains(this.owner)) {
            return false;
        }

        if (!this.pet.level().equals(this.owner.level()) || this.pet.distanceTo(this.owner) > 32D) {
            this.moveToOwner();
            return false;
        }

        // Only start following when the owner is far enough away (no random nudges when close).
        double start = this.getStartDistance();
        return this.pet.distanceToSqr(this.owner) > start * start;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.isTargettedToEnemy() || this.owner == null || !this.owner.isAlive()) return false;
        if (this.pet.passengers.contains(this.owner)) return false;
        if (!this.pet.level().equals(this.owner.level())) return false;

        // Keep following until the pet is within the stop distance.
        double stop = this.getStopDistance();
        return this.pet.distanceToSqr(this.owner) > stop * stop;
    }

    /**
     * @return Distance at which the pet stops following: the configured value, or more for big pets.
     */
    protected double getStopDistance() {
        return Math.max(PetFollowSettings.getStopDistance(), Math.sqrt(this.getReach(this.owner)));
    }

    /**
     * @return Distance above which the pet starts following. Always a bit above the stop distance.
     */
    protected double getStartDistance() {
        return Math.max(PetFollowSettings.getStartDistance(), this.getStopDistance() + 1.5D);
    }

    public void moveToOwner() {
        this.pet.setTarget(null);
        this.pet.getBukkitEntity().teleport(this.owner.getBukkitEntity());
    }
}
