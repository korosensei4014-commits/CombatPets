package su.nightexpress.combatpets.pet.duel;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import su.nightexpress.combatpets.api.pet.ActivePet;

import java.util.UUID;

/**
 * A running duel between two players' summoned pets.
 */
public final class PetDuel {

    public enum State { COUNTDOWN, FIGHTING }

    private final UUID      firstId;
    private final UUID      secondId;
    private final ActivePet firstPet;
    private final ActivePet secondPet;

    private State state;
    private int   countdown;
    private int   secondsLeft;

    public PetDuel(@NonNull Player first, @NonNull ActivePet firstPet, @NonNull Player second, @NonNull ActivePet secondPet,
                   int countdown, int duration) {
        this.firstId = first.getUniqueId();
        this.secondId = second.getUniqueId();
        this.firstPet = firstPet;
        this.secondPet = secondPet;
        this.state = State.COUNTDOWN;
        this.countdown = countdown;
        this.secondsLeft = duration;
    }

    public boolean involves(@NonNull UUID playerId) {
        return this.firstId.equals(playerId) || this.secondId.equals(playerId);
    }

    public boolean involves(@NonNull ActivePet pet) {
        return this.firstPet == pet || this.secondPet == pet;
    }

    /**
     * @return The other pet of the duel, or null if the given pet is not part of it.
     */
    @Nullable
    public ActivePet getOpponent(@NonNull ActivePet pet) {
        if (pet == this.firstPet) return this.secondPet;
        if (pet == this.secondPet) return this.firstPet;
        return null;
    }

    @NonNull
    public UUID getFirstId() {
        return this.firstId;
    }

    @NonNull
    public UUID getSecondId() {
        return this.secondId;
    }

    @NonNull
    public ActivePet getFirstPet() {
        return this.firstPet;
    }

    @NonNull
    public ActivePet getSecondPet() {
        return this.secondPet;
    }

    @NonNull
    public State getState() {
        return this.state;
    }

    public void setState(@NonNull State state) {
        this.state = state;
    }

    public boolean isFighting() {
        return this.state == State.FIGHTING;
    }

    public int getCountdown() {
        return this.countdown;
    }

    public void setCountdown(int countdown) {
        this.countdown = countdown;
    }

    public int getSecondsLeft() {
        return this.secondsLeft;
    }

    public void setSecondsLeft(int secondsLeft) {
        this.secondsLeft = secondsLeft;
    }
}
