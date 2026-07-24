package su.nightexpress.combatpets.api.pet;

import java.util.Optional;
import java.util.UUID;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface PetEntity {

    default PetSharedData getSharedData() {
        throw new UnsupportedOperationException("Not implemented"); // TODO Remove default
    }

    @Deprecated
    default void setGoals() {

    }

    @Deprecated
    default Optional<ActivePet> holder() {
        return Optional.ofNullable(PetEntityBridge.getByPet(this));
    }

    @Deprecated
    default ActivePet getHolder() {
        return PetEntityBridge.getByPet(this);
    }

    @Deprecated
    default UUID getOwnerId() {
        return this.getHolder().getOwner().getUniqueId();
    }
}
