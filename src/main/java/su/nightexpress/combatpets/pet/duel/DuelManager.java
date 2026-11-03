package su.nightexpress.combatpets.pet.duel;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.SoundCategory;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.api.pet.CompanionPets;
import su.nightexpress.combatpets.bedrock.BedrockManager;
import su.nightexpress.combatpets.bedrock.BedrockText;
import su.nightexpress.combatpets.config.Config;
import su.nightexpress.combatpets.config.Lang;
import su.nightexpress.combatpets.data.impl.PetData;
import su.nightexpress.combatpets.data.impl.PetUser;
import su.nightexpress.combatpets.pet.PetManager;
import su.nightexpress.combatpets.pet.impl.PetInstance;
import su.nightexpress.nightcore.bridge.common.NightNbtHolder;
import su.nightexpress.nightcore.bridge.dialog.wrap.WrappedDialog;
import su.nightexpress.nightcore.bridge.dialog.wrap.base.WrappedDialogAfterAction;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.ui.dialog.Dialogs;
import su.nightexpress.nightcore.ui.dialog.build.DialogActions;
import su.nightexpress.nightcore.ui.dialog.build.DialogBases;
import su.nightexpress.nightcore.ui.dialog.build.DialogBodies;
import su.nightexpress.nightcore.ui.dialog.build.DialogButtons;
import su.nightexpress.nightcore.ui.dialog.build.DialogTypes;
import su.nightexpress.nightcore.util.NumberUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;

import static su.nightexpress.combatpets.Placeholders.*;

/**
 * Pet duels: {@code /pets duel <player>} challenges another player's summoned pet. After the challenge is accepted
 * and a countdown, both pets fight only each other. The losing pet really dies (normal pet death rules apply).
 * Wins and losses are stored per pet.
 */
public class DuelManager extends AbstractManager<PetsPlugin> {

    private static final String ACTION_ACCEPT  = "combatpets_duel_accept";
    private static final String ACTION_DECLINE = "combatpets_duel_decline";

    private final List<DuelRequest> requests;
    private final List<PetDuel>     duels;
    private final NamespacedKey     settingKey;

    public DuelManager(@NonNull PetsPlugin plugin) {
        super(plugin);
        this.requests = new ArrayList<>();
        this.duels = new ArrayList<>();
        this.settingKey = new NamespacedKey(plugin, "duels_enabled");
    }

    // ------------------------------------------------------------------------------------------
    // Per-player on/off setting (stored on the player, survives relogs and restarts)
    // ------------------------------------------------------------------------------------------

    public boolean isAcceptingDuels(@NonNull Player player) {
        Byte value = player.getPersistentDataContainer().get(this.settingKey, PersistentDataType.BYTE);
        return value == null ? Config.PET_DUEL_DEFAULT_ENABLED.get() : value != 0;
    }

    public void setAcceptingDuels(@NonNull Player player, boolean accepting) {
        player.getPersistentDataContainer().set(this.settingKey, PersistentDataType.BYTE, (byte) (accepting ? 1 : 0));

        // Turning duels off silently drops challenges waiting for this player.
        if (!accepting) {
            this.requests.removeIf(request -> request.targetId().equals(player.getUniqueId()));
        }
    }

    /**
     * {@code /pets duels [on|off]}: sets or toggles the player's setting and tells them the new state.
     */
    public void handleToggleCommand(@NonNull Player player, @Nullable String state) {
        boolean accepting;
        if (state != null && state.equalsIgnoreCase("on")) accepting = true;
        else if (state != null && state.equalsIgnoreCase("off")) accepting = false;
        else accepting = !this.isAcceptingDuels(player);

        this.setAcceptingDuels(player, accepting);
        (accepting ? Lang.DUEL_TOGGLE_ON : Lang.DUEL_TOGGLE_OFF).message().send(player);
    }

    @NonNull
    public String getStatusText(@NonNull Player player) {
        return (this.isAcceptingDuels(player) ? Lang.DUEL_STATUS_ON : Lang.DUEL_STATUS_OFF).text();
    }

    @Override
    protected void onLoad() {
        this.addListener(new DuelListener(this.plugin, this));
        this.addTask(this.plugin.createTask(this::tick).setSecondsInterval(1));
        this.addTask(this.plugin.createTask(this::tickTargets).setTicksInterval(2L));
    }

    @Override
    protected void onShutdown() {
        new ArrayList<>(this.duels).forEach(this::stopDuel);
        this.duels.clear();
        this.requests.clear();
    }

    @NonNull
    private PetManager pets() {
        return this.plugin.getPetManager();
    }

    // ------------------------------------------------------------------------------------------
    // Queries
    // ------------------------------------------------------------------------------------------

    @Nullable
    public PetDuel getDuel(@NonNull Player player) {
        return this.duels.stream().filter(duel -> duel.involves(player.getUniqueId())).findFirst().orElse(null);
    }

    @Nullable
    public PetDuel getDuel(@NonNull ActivePet pet) {
        return this.duels.stream().filter(duel -> duel.involves(pet)).findFirst().orElse(null);
    }

    public boolean isInDuel(@NonNull Player player) {
        return this.getDuel(player) != null;
    }

    public boolean isInDuel(@NonNull ActivePet pet) {
        return this.getDuel(pet) != null;
    }

    /**
     * Damage rules for pets in a duel.
     * @return TRUE/FALSE when a duel decides the outcome, or null when no duel is involved (normal rules apply).
     */
    @Nullable
    public Boolean checkDamage(@NonNull LivingEntity damager, @NonNull LivingEntity victim) {
        ActivePet damagerPet = this.pets().getPetByMob(damager);
        ActivePet victimPet = this.pets().getPetByMob(victim);

        PetDuel duel = damagerPet == null ? null : this.getDuel(damagerPet);
        if (duel == null && victimPet != null) duel = this.getDuel(victimPet);
        if (duel == null) return null;

        // Only the two duel pets may hurt each other, and only while fighting. Nothing else can interfere.
        boolean bothInDuel = damagerPet != null && victimPet != null && duel.getOpponent(damagerPet) == victimPet;
        return bothInDuel && duel.isFighting();
    }

    // ------------------------------------------------------------------------------------------
    // Requests
    // ------------------------------------------------------------------------------------------

    @Nullable
    private DuelRequest getRequest(@NonNull UUID challengerId, @NonNull UUID targetId) {
        return this.requests.stream()
            .filter(request -> request.challengerId().equals(challengerId) && request.targetId().equals(targetId))
            .findFirst().orElse(null);
    }

    /**
     * {@code /pets duel <target>}: accepts the target's pending challenge if there is one, otherwise challenges the target.
     */
    public void handleCommand(@NonNull Player player, @NonNull Player target) {
        DuelRequest incoming = this.getRequest(target.getUniqueId(), player.getUniqueId());
        if (incoming != null && !incoming.isExpired()) {
            this.accept(player, target);
            return;
        }
        this.challenge(player, target);
    }

    /**
     * Common checks for challenging / accepting. Sends the error message and returns false if a check fails.
     */
    private boolean canDuel(@NonNull Player player, @NonNull Player target) {
        if (player.equals(target)) {
            Lang.DUEL_ERROR_SELF.message().send(player);
            return false;
        }
        // Players who turned duels off can't challenge or accept, and can't be challenged.
        if (!this.isAcceptingDuels(player)) {
            Lang.DUEL_ERROR_SELF_DISABLED.message().send(player);
            return false;
        }
        if (!this.isAcceptingDuels(target)) {
            Lang.DUEL_ERROR_TARGET_DISABLED.message().send(player, replacer -> replacer.replace(PLAYER_NAME, target.getName()));
            return false;
        }
        if (this.isInDuel(player)) {
            Lang.DUEL_ERROR_IN_DUEL.message().send(player);
            return false;
        }
        if (this.isInDuel(target)) {
            Lang.DUEL_ERROR_TARGET_IN_DUEL.message().send(player, replacer -> replacer.replace(PLAYER_NAME, target.getName()));
            return false;
        }
        if (this.pets().getPlayerPet(player) == null) {
            Lang.DUEL_ERROR_NO_PET.message().send(player);
            return false;
        }
        if (this.pets().getPlayerPet(target) == null) {
            Lang.DUEL_ERROR_TARGET_NO_PET.message().send(player, replacer -> replacer.replace(PLAYER_NAME, target.getName()));
            return false;
        }
        if (CompanionPets.isCompanion(this.pets().getPlayerPet(player).getTemplate().getEntityType())) {
            Lang.DUEL_ERROR_COMPANION.message().send(player);
            return false;
        }
        if (CompanionPets.isCompanion(this.pets().getPlayerPet(target).getTemplate().getEntityType())) {
            Lang.DUEL_ERROR_TARGET_COMPANION.message().send(player, replacer -> replacer.replace(PLAYER_NAME, target.getName()));
            return false;
        }

        double maxDistance = Config.PET_DUEL_MAX_DISTANCE.get();
        if (!player.getWorld().equals(target.getWorld()) || player.getLocation().distanceSquared(target.getLocation()) > maxDistance * maxDistance) {
            Lang.DUEL_ERROR_TOO_FAR.message().send(player, replacer -> replacer
                .replace(PLAYER_NAME, target.getName())
                .replace(GENERIC_AMOUNT, NumberUtil.format(maxDistance)));
            return false;
        }
        return true;
    }

    public void challenge(@NonNull Player challenger, @NonNull Player target) {
        if (!this.canDuel(challenger, target)) return;

        DuelRequest existing = this.getRequest(challenger.getUniqueId(), target.getUniqueId());
        if (existing != null && !existing.isExpired()) {
            Lang.DUEL_ERROR_ALREADY_SENT.message().send(challenger, replacer -> replacer.replace(PLAYER_NAME, target.getName()));
            return;
        }
        if (existing != null) this.requests.remove(existing);

        int timeout = Math.max(5, Config.PET_DUEL_REQUEST_TIMEOUT.get());
        this.requests.add(new DuelRequest(challenger.getUniqueId(), target.getUniqueId(), System.currentTimeMillis() + timeout * 1000L));

        Lang.DUEL_REQUEST_SENT.message().send(challenger, replacer -> replacer
            .replace(PLAYER_NAME, target.getName())
            .replace(GENERIC_AMOUNT, String.valueOf(timeout)));
        Lang.DUEL_REQUEST_RECEIVED.message().send(target, replacer -> replacer.replace(PLAYER_NAME, challenger.getName()));

        this.showRequest(target, challenger);
    }

    public void accept(@NonNull Player target, @NonNull Player challenger) {
        DuelRequest request = this.getRequest(challenger.getUniqueId(), target.getUniqueId());
        if (request == null || request.isExpired()) return;

        this.requests.remove(request);
        if (!challenger.isOnline() || !this.canDuel(target, challenger)) return;

        // Any other pending challenges of these two players are dropped.
        this.requests.removeIf(other -> other.challengerId().equals(target.getUniqueId()) || other.targetId().equals(target.getUniqueId())
            || other.challengerId().equals(challenger.getUniqueId()) || other.targetId().equals(challenger.getUniqueId()));

        this.startDuel(challenger, target);
    }

    public void decline(@NonNull Player target, @NonNull Player challenger) {
        DuelRequest request = this.getRequest(challenger.getUniqueId(), target.getUniqueId());
        if (request == null) return;

        this.requests.remove(request);
        if (challenger.isOnline()) {
            Lang.DUEL_REQUEST_DECLINED.message().send(challenger, replacer -> replacer.replace(PLAYER_NAME, target.getName()));
        }
    }

    /**
     * Shows the accept / decline screen: a Bedrock form for Bedrock players, a dialog for Java players.
     */
    private void showRequest(@NonNull Player target, @NonNull Player challenger) {
        ActivePet theirPet = this.pets().getPlayerPet(challenger);
        ActivePet yourPet = this.pets().getPlayerPet(target);
        if (theirPet == null || yourPet == null) return;

        String title = Lang.DUEL_DIALOG_TITLE.text().replace(PLAYER_NAME, challenger.getName());
        String body = Lang.DUEL_DIALOG_BODY.text()
            .replace(PLAYER_NAME, challenger.getName())
            .replace(GENERIC_NAME, this.petInfo(theirPet))
            .replace(PET_NAME, this.petInfo(yourPet));

        Runnable onAccept = () -> {
            if (target.isOnline()) this.accept(target, challenger);
        };
        Runnable onDecline = () -> {
            if (target.isOnline()) this.decline(target, challenger);
        };

        BedrockManager bedrock = this.plugin.getBedrockManager();
        if (bedrock != null && this.plugin.useBedrockForms(target)) {
            boolean sent = bedrock.sendConfirm(target,
                BedrockText.format(target, title, UnaryOperator.identity()),
                BedrockText.format(target, body, UnaryOperator.identity()),
                BedrockText.plain(Lang.DUEL_DIALOG_ACCEPT.value().label()),
                BedrockText.plain(Lang.DUEL_DIALOG_DECLINE.value().label()),
                onAccept,
                onDecline);
            if (sent) return;
        }

        WrappedDialog dialog = Dialogs.create(builder -> builder
            .base(DialogBases.builder(title)
                .canCloseWithEscape(true)
                .afterAction(WrappedDialogAfterAction.CLOSE)
                .body(DialogBodies.plain(body).build())
                .build())
            .type(DialogTypes.confirmation(
                DialogButtons.action(Lang.DUEL_DIALOG_ACCEPT).action(DialogActions.customClick(ACTION_ACCEPT)).build(),
                DialogButtons.action(Lang.DUEL_DIALOG_DECLINE).action(DialogActions.customClick(ACTION_DECLINE)).build()))
            .handleResponse(ACTION_ACCEPT, (viewer, identifier, nbt) -> this.plugin.runTask(onAccept))
            .handleResponse(ACTION_DECLINE, (viewer, identifier, nbt) -> this.plugin.runTask(onDecline))
        );
        Dialogs.showDialog(target, dialog);
    }

    @NonNull
    private String petInfo(@NonNull ActivePet pet) {
        String text = Lang.DUEL_PET_INFO.text().replace(GENERIC_AMOUNT, NumberUtil.format(pet.getEntity().getHealth()));
        text = pet.replacePlaceholders().apply(text);
        return pet.getTier().replacePlaceholders().apply(text);
    }

    // ------------------------------------------------------------------------------------------
    // Duels
    // ------------------------------------------------------------------------------------------

    private void startDuel(@NonNull Player first, @NonNull Player second) {
        ActivePet firstPet = this.pets().getPlayerPet(first);
        ActivePet secondPet = this.pets().getPlayerPet(second);
        if (firstPet == null || secondPet == null) return;

        // Fair start: both pets at full health, no previous targets.
        // No riding during a duel.
        firstPet.getEntity().eject();
        secondPet.getEntity().eject();
        heal(firstPet);
        heal(secondPet);
        this.plugin.getPetNMS().setAttackTarget(firstPet.getEntity(), null);
        this.plugin.getPetNMS().setAttackTarget(secondPet.getEntity(), null);

        PetDuel duel = new PetDuel(first, firstPet, second, secondPet,
            Math.max(0, Config.PET_DUEL_COUNTDOWN.get()), Math.max(10, Config.PET_DUEL_MAX_DURATION.get()));
        this.duels.add(duel);
        // Shows the first countdown number immediately; every next step follows exactly 1 second later
        // (each duel has its own clock, so the first number is never cut short).
        duel.setNextStepAt(System.currentTimeMillis() + 1000L);
        this.tickDuel(duel);
    }

    private static void heal(@NonNull ActivePet pet) {
        LivingEntity entity = pet.getEntity();
        AttributeInstance attribute = entity.getAttribute(Attribute.MAX_HEALTH);
        double max = attribute == null ? pet.getMaxHealth() : attribute.getValue();
        entity.setHealth(Math.max(1D, max));
        pet.updateHealthBar();
    }

    /**
     * Keeps fighting duel pets locked on each other. Some pet AI drops its target (e.g. a Wither clears it right
     * after being hit by a Ghast), which made it stop fighting until the next once-per-second refresh.
     */
    private void tickTargets() {
        long now = System.currentTimeMillis();
        for (PetDuel duel : new ArrayList<>(this.duels)) {
            // Once-per-second duel step (countdown, checks, timer), timed per duel.
            if (duel.isStepDue(now)) {
                duel.setNextStepAt(Math.max(duel.getNextStepAt() + 1000L, now));
                this.tickDuel(duel);
                if (!this.duels.contains(duel)) continue;
            }

            if (!duel.isFighting()) continue;

            this.keepTarget(duel.getFirstPet(), duel.getSecondPet());
            this.keepTarget(duel.getSecondPet(), duel.getFirstPet());
        }
    }

    private void keepTarget(@NonNull ActivePet pet, @NonNull ActivePet opponent) {
        LivingEntity entity = pet.getEntity();
        LivingEntity target = opponent.getEntity();
        if (!entity.isValid() || entity.isDead() || !target.isValid() || target.isDead()) return;
        if (entity instanceof org.bukkit.entity.Mob mob && mob.getTarget() == target) return;

        this.plugin.getPetNMS().setAttackTarget(entity, target);
    }

    private void tick() {
        // Expired challenges.
        new ArrayList<>(this.requests).forEach(request -> {
            if (!request.isExpired()) return;

            this.requests.remove(request);
            Player challenger = this.plugin.getServer().getPlayer(request.challengerId());
            Player target = this.plugin.getServer().getPlayer(request.targetId());
            if (challenger != null && target != null) {
                Lang.DUEL_REQUEST_EXPIRED.message().send(challenger, replacer -> replacer.replace(PLAYER_NAME, target.getName()));
                Lang.DUEL_REQUEST_EXPIRED.message().send(target, replacer -> replacer.replace(PLAYER_NAME, challenger.getName()));
            }
        });

        // Duels are stepped by their own clocks in tickTargets().
    }

    private void tickDuel(@NonNull PetDuel duel) {
        Player first = this.plugin.getServer().getPlayer(duel.getFirstId());
        Player second = this.plugin.getServer().getPlayer(duel.getSecondId());
        if (first == null || second == null) {
            this.cancel(duel, Lang.DUEL_CANCEL_LEFT.text());
            return;
        }

        // Both pets must still be the summoned pets of their owners.
        if (!this.isStillSummoned(first, duel.getFirstPet()) || !this.isStillSummoned(second, duel.getSecondPet())) {
            this.cancel(duel, Lang.DUEL_CANCEL_PET_GONE.text());
            return;
        }

        Location a = duel.getFirstPet().getEntity().getLocation();
        Location b = duel.getSecondPet().getEntity().getLocation();
        double maxDistance = Config.PET_DUEL_MAX_DISTANCE.get() * 2D;
        if (!a.getWorld().equals(b.getWorld()) || a.distanceSquared(b) > maxDistance * maxDistance) {
            this.cancel(duel, Lang.DUEL_CANCEL_TOO_FAR.text());
            return;
        }

        if (duel.getState() == PetDuel.State.COUNTDOWN) {
            int count = duel.getCountdown();
            if (count > 0) {
                Lang.DUEL_COUNTDOWN.message().send(first, replacer -> replacer
                    .replace(GENERIC_TIME, String.valueOf(count)).replace(PLAYER_NAME, second.getName()));
                Lang.DUEL_COUNTDOWN.message().send(second, replacer -> replacer
                    .replace(GENERIC_TIME, String.valueOf(count)).replace(PLAYER_NAME, first.getName()));

                // Rising pitch: ... 3 = 0.8, 2 = 1.0, 1 = 1.2
                float pitch = (float) Math.max(0.5D, Math.min(2D, 1.4D - count * 0.2D));
                playSound(first, Config.PET_DUEL_SOUND_COUNTDOWN.get(), pitch);
                playSound(second, Config.PET_DUEL_SOUND_COUNTDOWN.get(), pitch);

                duel.setCountdown(count - 1);
                return;
            }

            duel.setState(PetDuel.State.FIGHTING);
            Lang.DUEL_START.message().send(first, replacer -> replacer
                .replace(PET_NAME, duel.getFirstPet().getName()).replace(PLAYER_NAME, second.getName()));
            Lang.DUEL_START.message().send(second, replacer -> replacer
                .replace(PET_NAME, duel.getSecondPet().getName()).replace(PLAYER_NAME, first.getName()));
            playSound(first, Config.PET_DUEL_SOUND_START.get(), 2F);
            playSound(second, Config.PET_DUEL_SOUND_START.get(), 2F);
        }

        // Fighting: keep both pets on each other (AI may drop targets, e.g. after losing sight).
        this.plugin.getPetNMS().setAttackTarget(duel.getFirstPet().getEntity(), duel.getSecondPet().getEntity());
        this.plugin.getPetNMS().setAttackTarget(duel.getSecondPet().getEntity(), duel.getFirstPet().getEntity());

        int left = duel.getSecondsLeft() - 1;
        duel.setSecondsLeft(left);
        if (left <= 0) {
            this.stopDuel(duel);
            Lang.DUEL_DRAW.message().send(first);
            Lang.DUEL_DRAW.message().send(second);
            playSound(first, Config.PET_DUEL_SOUND_DRAW.get(), 1F);
            playSound(second, Config.PET_DUEL_SOUND_DRAW.get(), 1F);
        }
    }

    private boolean isStillSummoned(@NonNull Player owner, @NonNull ActivePet pet) {
        return this.pets().getPlayerPet(owner) == pet && pet.getEntity().isValid() && !pet.getEntity().isDead();
    }

    /**
     * Called for every pet death. If the pet was in a fight, its opponent wins.
     */
    public void handlePetDeath(@NonNull ActivePet loserPet) {
        PetDuel duel = this.getDuel(loserPet);
        if (duel == null) return;

        ActivePet winnerPet = duel.getOpponent(loserPet);
        if (!duel.isFighting() || winnerPet == null) {
            this.cancel(duel, Lang.DUEL_CANCEL_PET_GONE.text());
            return;
        }

        this.stopDuel(duel);

        Player winner = this.plugin.getServer().getPlayer(winnerPet == duel.getFirstPet() ? duel.getFirstId() : duel.getSecondId());
        Player loser = this.plugin.getServer().getPlayer(loserPet == duel.getFirstPet() ? duel.getFirstId() : duel.getSecondId());

        this.recordResult(winner, winnerPet, true);
        this.recordResult(loser, loserPet, false);

        String winnerName = winner == null ? "?" : winner.getName();
        String loserName = loser == null ? "?" : loser.getName();
        if (winner != null) {
            Lang.DUEL_WIN.message().send(winner, replacer -> replacer
                .replace(PET_NAME, winnerPet.getName()).replace(PLAYER_NAME, loserName));
            playSound(winner, Config.PET_DUEL_SOUND_WIN.get(), 1F);
        }
        if (loser != null) {
            Lang.DUEL_LOSE.message().send(loser, replacer -> replacer
                .replace(PET_NAME, loserPet.getName()).replace(PLAYER_NAME, winnerName));
            playSound(loser, Config.PET_DUEL_SOUND_LOSE.get(), 1F);
        }
    }

    private void recordResult(@Nullable Player owner, @NonNull ActivePet pet, boolean win) {
        if (!(pet instanceof PetInstance instance)) return;

        PetData data = instance.getData();
        if (win) data.setDuelWins(data.getDuelWins() + 1);
        else data.setDuelLosses(data.getDuelLosses() + 1);

        if (owner != null) {
            PetUser user = this.plugin.getUserManager().getOrFetch(owner);
            this.plugin.getUserManager().save(user);
        }
    }

    /**
     * {@code /pets surrender}: the player gives up their current duel. The opponent wins and it counts as a loss.
     * The surrendering pet survives unless Pets.Duel.Surrender_Kills_Pet is enabled.
     */
    public void surrender(@NonNull Player player) {
        PetDuel duel = this.getDuel(player);
        if (duel == null) {
            Lang.DUEL_ERROR_NOT_IN_DUEL.message().send(player);
            return;
        }

        boolean first = duel.getFirstId().equals(player.getUniqueId());
        ActivePet loserPet = first ? duel.getFirstPet() : duel.getSecondPet();

        if (Config.PET_DUEL_SURRENDER_KILLS_PET.get()) {
            // Real death: the normal death flow records the result and shows victory / defeat.
            duel.setState(PetDuel.State.FIGHTING);
            loserPet.getEntity().setHealth(0D);
            return;
        }

        ActivePet winnerPet = first ? duel.getSecondPet() : duel.getFirstPet();
        Player winner = this.plugin.getServer().getPlayer(first ? duel.getSecondId() : duel.getFirstId());

        this.stopDuel(duel);
        this.recordResult(winner, winnerPet, true);
        this.recordResult(player, loserPet, false);

        Lang.DUEL_SURRENDER_LOSE.message().send(player, replacer -> replacer.replace(PLAYER_NAME, winner == null ? "?" : winner.getName()));
        playSound(player, Config.PET_DUEL_SOUND_LOSE.get(), 1F);
        if (winner != null) {
            Lang.DUEL_SURRENDER_WIN.message().send(winner, replacer -> replacer.replace(PLAYER_NAME, player.getName()));
            playSound(winner, Config.PET_DUEL_SOUND_WIN.get(), 1F);
        }
    }

    /**
     * @return true if healing of this entity should be blocked (it's a pet in a duel and healing is disabled).
     */
    public boolean isHealingBlocked(@NonNull LivingEntity entity) {
        if (!Config.PET_DUEL_DISABLE_HEALING.get()) return false;

        ActivePet pet = this.pets().getPetByMob(entity);
        return pet != null && this.isInDuel(pet);
    }

    private static void playSound(@NonNull Player player, @Nullable String sound, float pitch) {
        if (sound == null || sound.isBlank()) return;
        try {
            player.playSound(player.getLocation(), sound.trim(), SoundCategory.PLAYERS, 1F, pitch);
        }
        catch (IllegalArgumentException ignored) {
            // Invalid sound key in config: ignore.
        }
    }

    public void handleOwnerDeath(@NonNull Player player) {
        PetDuel duel = this.getDuel(player);
        if (duel != null) {
            this.cancel(duel, Lang.DUEL_CANCEL_DIED.text());
        }
    }

    public void handleQuit(@NonNull Player player) {
        this.requests.removeIf(request -> request.challengerId().equals(player.getUniqueId()) || request.targetId().equals(player.getUniqueId()));

        PetDuel duel = this.getDuel(player);
        if (duel != null) {
            this.cancel(duel, Lang.DUEL_CANCEL_LEFT.text());
        }
    }

    /**
     * Ends the duel without a winner (no pet dies, no stats) and tells both players why.
     */
    private void cancel(@NonNull PetDuel duel, @NonNull String reason) {
        this.stopDuel(duel);

        Player first = this.plugin.getServer().getPlayer(duel.getFirstId());
        Player second = this.plugin.getServer().getPlayer(duel.getSecondId());
        if (first != null) this.sendCancel(first, second, reason);
        if (second != null) this.sendCancel(second, first, reason);
    }

    private void sendCancel(@NonNull Player player, @Nullable Player other, @NonNull String reason) {
        MessageLocale message = Lang.DUEL_CANCELLED;
        message.message().send(player, replacer -> replacer
            .replace(PLAYER_NAME, other == null ? "?" : other.getName())
            .replace(GENERIC_TYPE, reason));
    }

    /**
     * Removes the duel and makes both pets stop attacking each other.
     */
    private void stopDuel(@NonNull PetDuel duel) {
        this.duels.remove(duel);

        for (ActivePet pet : new ActivePet[]{duel.getFirstPet(), duel.getSecondPet()}) {
            if (pet.getEntity().isValid() && !pet.getEntity().isDead()) {
                this.plugin.getPetNMS().setAttackTarget(pet.getEntity(), null);
            }
        }
    }
}
