package su.nightexpress.combatpets.nms.paper26p2.pets.monster;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.level.Level;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.jspecify.annotations.NullMarked;

/**
 * A Vex summoned by an Evoker pet. It only ever attacks its Evoker's current target, is removed together with the
 * Evoker, and is never saved to the world. Damage rules (friendly fire, PvP, duels) are applied by the plugin as if
 * the Evoker itself dealt the damage.
 */
@NullMarked
public class SummonedVex extends Vex {

    private final Mob summoner;

    public SummonedVex(Level level, Mob summoner) {
        super(EntityTypes.VEX, level);
        this.summoner = summoner;
        // No own targeting (vanilla Vexes go after nearby players): only the summoner's target.
        this.targetSelector.getAvailableGoals().clear();
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide()) {
            if (this.summoner.isRemoved() || !this.summoner.isAlive()) {
                this.discard(EntityRemoveEvent.Cause.DESPAWN);
                return;
            }

            LivingEntity target = this.summoner.getTarget();
            if (target != this.getTarget()) {
                this.setTarget(target, EntityTargetEvent.TargetReason.CUSTOM);
            }
        }
        super.tick();
    }

    // Vanilla Mob.canAttack() rejects Ghasts; allow them like for pets.
    @Override
    public boolean canAttack(LivingEntity target) {
        return super.canAttack(target) || (target instanceof net.minecraft.world.entity.monster.Ghast && target.canBeSeenAsEnemy());
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }
}
