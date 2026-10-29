package su.nightexpress.combatpets.nms.paper26p3.goals.combat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import org.jspecify.annotations.NonNull;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.paper26p3.ai.AIUtils;

import java.util.EnumSet;

public class PetAutoTargetGoal extends TargetGoal {

    private final ActivePet activePet;

    public PetAutoTargetGoal(@NonNull Mob mob) {
        super(mob, false);
        this.activePet = ((PetEntity) mob).getHolder();
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean canUse() {
        LivingEntity target = AIUtils.findTarget(this.mob, this.activePet);
        if (target == null || !this.mob.canAttack(target)) return false;

        this.targetMob = target;
        return true;
    }
}
