package su.nightexpress.combatpets.bedrock;

import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.ModalForm;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.response.CustomFormResponse;
import org.geysermc.cumulus.util.FormImage;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.bedrock.bridge.BedrockBridge;
import su.nightexpress.combatpets.bedrock.form.FormButton;
import su.nightexpress.combatpets.bedrock.form.PetForms;
import su.nightexpress.combatpets.bedrock.form.ShopForms;
import su.nightexpress.combatpets.config.Config;
import su.nightexpress.nightcore.manager.AbstractManager;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Entry point of the Bedrock Edition menu system.
 * <p>
 * The pet/shop managers ask this manager whether a player is a Bedrock player. If so, a Cumulus form is
 * sent instead of the chest GUI. Form actions call the same {@code PetManager} / {@code ShopManager}
 * methods as the chest GUIs, so both editions share the same business logic.
 * <p>
 * This class (and everything in the {@code bedrock} package that uses Cumulus) is only loaded when
 * Floodgate or Geyser is installed.
 */
public class BedrockManager extends AbstractManager<PetsPlugin> {

    private final BedrockBridge                bridge;
    private final Map<UUID, BedrockSession>    sessions;

    private PetForms  petForms;
    private ShopForms shopForms;

    public BedrockManager(@NonNull PetsPlugin plugin, @NonNull BedrockBridge bridge) {
        super(plugin);
        this.bridge = bridge;
        this.sessions = new ConcurrentHashMap<>();
    }

    @Override
    protected void onLoad() {
        this.petForms = new PetForms(this.plugin, this);
        this.shopForms = new ShopForms(this.plugin, this);

        this.addListener(new BedrockListener(this.plugin, this));
    }

    @Override
    protected void onShutdown() {
        this.sessions.clear();
    }

    @NonNull
    public BedrockBridge getBridge() {
        return this.bridge;
    }

    @NonNull
    public PetForms getPetForms() {
        return this.petForms;
    }

    @NonNull
    public ShopForms getShopForms() {
        return this.shopForms;
    }

    /**
     * @return true if the player plays on Bedrock Edition (connected through Geyser).
     */
    public boolean isBedrock(@NonNull Player player) {
        try {
            return this.bridge.isBedrockPlayer(player.getUniqueId());
        }
        catch (Throwable throwable) {
            return false;
        }
    }

    public void clearSession(@NonNull Player player) {
        this.sessions.remove(player.getUniqueId());
    }

    @NonNull
    private BedrockSession getSession(@NonNull Player player) {
        return this.sessions.computeIfAbsent(player.getUniqueId(), uuid -> new BedrockSession());
    }

    // ------------------------------------------------------------------------------------------
    // Form primitives
    // ------------------------------------------------------------------------------------------

    /**
     * Sends a list of buttons. Closing the form (X) does nothing.
     * @return true if the form was sent.
     */
    public boolean sendButtons(@NonNull Player player, @NonNull String title, @NonNull String content, @NonNull List<FormButton> buttons) {
        long formId = this.getSession(player).nextForm();

        SimpleForm.Builder builder = SimpleForm.builder().title(title).content(content);
        for (FormButton button : buttons) {
            FormImage image = this.createImage(button.icon());
            if (image == null) {
                builder.button(button.text());
            }
            else {
                builder.button(button.text(), image);
            }
        }

        builder.validResultHandler(response -> {
            int index = response.clickedButtonId();
            if (index < 0 || index >= buttons.size()) return;

            this.dispatch(player, formId, true, buttons.get(index).action());
        });
        builder.closedOrInvalidResultHandler(() -> this.dispatch(player, formId, false, () -> {}));

        return this.send(player, builder.build());
    }

    /**
     * Sends a confirmation with two buttons. Closing the form (X) counts as cancel.
     * @return true if the form was sent.
     */
    public boolean sendConfirm(@NonNull Player player,
                               @NonNull String title,
                               @NonNull String content,
                               @NonNull String confirmText,
                               @NonNull String cancelText,
                               @NonNull Runnable onConfirm,
                               @NonNull Runnable onCancel) {
        long formId = this.getSession(player).nextForm();

        ModalForm form = ModalForm.builder()
            .title(title)
            .content(content)
            .button1(confirmText)
            .button2(cancelText)
            .validResultHandler(response -> this.dispatch(player, formId, true, response.clickedFirst() ? onConfirm : onCancel))
            .closedOrInvalidResultHandler(() -> this.dispatch(player, formId, false, onCancel))
            .build();

        return this.send(player, form);
    }

    /**
     * Sends a custom form (inputs, toggles, dropdowns). The builder must not have result handlers set.
     * @param onSubmit executed on the main thread with the response.
     * @param onClose  executed on the main thread when the form is closed without submitting.
     * @return true if the form was sent.
     */
    public boolean sendCustom(@NonNull Player player,
                              @NonNull CustomForm.Builder builder,
                              @NonNull Consumer<CustomFormResponse> onSubmit,
                              @NonNull Runnable onClose) {
        long formId = this.getSession(player).nextForm();

        builder.validResultHandler(response -> this.dispatch(player, formId, true, () -> onSubmit.accept(response)));
        builder.closedOrInvalidResultHandler(() -> this.dispatch(player, formId, false, onClose));

        return this.send(player, builder.build());
    }

    private boolean send(@NonNull Player player, @NonNull Form form) {
        try {
            if (this.bridge.sendForm(player.getUniqueId(), form)) return true;
        }
        catch (Throwable throwable) {
            this.plugin.warn("Could not send a Bedrock form to " + player.getName() + ": " + throwable.getMessage());
        }

        this.getSession(player).invalidate();
        return false;
    }

    /**
     * Runs the response action on the main thread, if the response is still valid.
     */
    private void dispatch(@NonNull Player player, long formId, boolean submission, @NonNull Runnable action) {
        UUID playerId = player.getUniqueId();

        this.plugin.runTask(() -> {
            Player online = this.plugin.getServer().getPlayer(playerId);
            if (online == null || !online.isOnline()) return;

            BedrockSession session = this.sessions.get(playerId);
            if (session == null || !session.accept(formId, submission, Config.BEDROCK_SUBMIT_COOLDOWN.get())) return;

            try {
                action.run();
            }
            catch (Exception exception) {
                this.plugin.error("Error while handling a Bedrock form response of " + online.getName() + ":", exception);
            }
        });
    }

    @Nullable
    private FormImage createImage(@Nullable String icon) {
        if (icon == null || icon.isBlank() || !Config.BEDROCK_ICONS_ENABLED.get()) return null;

        FormImage.Type type = icon.startsWith("http://") || icon.startsWith("https://") ? FormImage.Type.URL : FormImage.Type.PATH;
        return FormImage.of(type, icon);
    }

    // ------------------------------------------------------------------------------------------
    // Pagination helpers
    // ------------------------------------------------------------------------------------------

    public static int getPageSize() {
        return Math.max(1, Config.BEDROCK_PAGE_SIZE.get());
    }

    public static int getPages(int total) {
        return Math.max(1, (int) Math.ceil(total / (double) getPageSize()));
    }

    public static int clampPage(int page, int total) {
        return Math.max(0, Math.min(page, getPages(total) - 1));
    }

    @NonNull
    public static <T> List<T> getPage(@NonNull List<T> list, int page) {
        int size = getPageSize();
        int from = Math.min(list.size(), page * size);
        int to = Math.min(list.size(), from + size);
        return list.subList(from, to);
    }
}
