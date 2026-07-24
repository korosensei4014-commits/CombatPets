package su.nightexpress.combatpets.api.pet.event;

import org.bukkit.event.Event;
import org.jspecify.annotations.NonNull;
import su.nightexpress.combatpets.api.pet.ActivePet;

public abstract class PetEvent extends Event {

    protected ActivePet pet;

    public PetEvent(@NonNull ActivePet pet) {
        this.pet = pet;
    }

    @NonNull
    public ActivePet getPet() {
        return this.pet;
    }
}
