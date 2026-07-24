package su.nightexpress.combatpets.api.pet.event.capture;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jspecify.annotations.NonNull;
import su.nightexpress.combatpets.api.pet.Template;
import su.nightexpress.combatpets.api.pet.Tier;

public abstract class PetCaptureEvent extends Event {

    protected final Player       player;
    protected final LivingEntity entity;
    protected final Template     template;
    protected final Tier         tier;

    public PetCaptureEvent(@NonNull Player player, @NonNull LivingEntity entity, @NonNull Template template,
                           @NonNull Tier tier) {
        this.player = player;
        this.entity = entity;
        this.template = template;
        this.tier = tier;
    }

    @NonNull
    public Player getPlayer() {
        return player;
    }

    @NonNull
    public LivingEntity getEntity() {
        return entity;
    }

    @NonNull
    public Template getTemplate() {
        return template;
    }

    @NonNull
    public Tier getTier() {
        return tier;
    }
}
