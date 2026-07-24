package su.nightexpress.combatpets.pet.impl;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import su.nightexpress.combatpets.api.pet.PetSharedData;
import su.nightexpress.combatpets.api.pet.Template;
import su.nightexpress.combatpets.api.pet.type.CombatMode;
import su.nightexpress.combatpets.api.pet.type.ExhaustReason;
import su.nightexpress.combatpets.data.impl.PetData;
import su.nightexpress.nightcore.util.Players;

@NullMarked
public class PetSharedImpl implements PetSharedData {

    private final Template template;
    private final PetData  data;
    private final UUID     ownerId;

    private CombatMode mode;

    public PetSharedImpl(UUID ownerId, PetData data) {
        this.ownerId = ownerId;
        this.data = data;
        this.template = data.getTemplate();

        this.mode = data.getCombatMode();
    }

    @Override
    public UUID getOwnerId() {
        return this.ownerId;
    }

    @Override
    public @Nullable Player getOwner() {
        return Players.getPlayer(this.ownerId);
    }

    @Override
    public Optional<Player> owner() {
        return Optional.ofNullable(this.getOwner());
    }

    @Override
    public CombatMode getCombatMode() {
        return this.data.getCombatMode();
    }

    @Override
    public void setCombatMode(CombatMode mode) {
        this.mode = mode;
    }

    @Override
    public void doExhaust(ExhaustReason reason) {
        this.doExhaust(this.template.getExhaustModifier(reason));
    }

    @Override
    public void doExhaust(double amount) {
        this.data.setFoodLevel(Math.max(0, this.data.getFoodLevel() - amount));
    }
}
