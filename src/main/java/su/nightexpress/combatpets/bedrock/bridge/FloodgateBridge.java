package su.nightexpress.combatpets.bedrock.bridge;

import org.geysermc.cumulus.form.Form;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

/**
 * Bedrock bridge backed by the Floodgate API.
 * <p>
 * Works on standalone servers and behind Velocity / BungeeCord as long as Floodgate is installed on
 * this (backend) server. Forms are routed to the player's Geyser instance by Floodgate.
 */
public final class FloodgateBridge implements BedrockBridge {

    private final FloodgateApi api;

    public FloodgateBridge() {
        this.api = FloodgateApi.getInstance();
    }

    @Override
    @NonNull
    public String getName() {
        return "Floodgate";
    }

    @Override
    public boolean isBedrockPlayer(@NonNull UUID playerId) {
        return this.api.isFloodgatePlayer(playerId);
    }

    @Override
    public boolean sendForm(@NonNull UUID playerId, @NonNull Form form) {
        return this.api.sendForm(playerId, form);
    }
}
