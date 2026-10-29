package su.nightexpress.combatpets.nms.paper26p3.goals.combat;

import net.minecraft.world.entity.monster.zombie.Zombie;
import org.jspecify.annotations.NonNull;

public class PetZombieAttackGoal extends PetMeleeAttackGoal {

    private int raiseArmTicks;

    public PetZombieAttackGoal(@NonNull Zombie pet, double speedModifier) {
        super(pet, speedModifier);
    }

    @Override
    public void start() {
        super.start();
        this.raiseArmTicks = 0;
    }

    @Override
    public void stop() {
        super.stop();
        this.pet.setAggressive(false);
    }

    @Override
    public void tick() {
        super.tick();

        ++this.raiseArmTicks;

        this.pet.setAggressive(this.raiseArmTicks >= 5 && this.getTicksUntilNextAttack() < this
            .getAttackSpeedTicks() / 2);
    }
}
