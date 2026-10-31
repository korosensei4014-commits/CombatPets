package su.nightexpress.combatpets.pet.dialog;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.config.Config;
import su.nightexpress.combatpets.config.Lang;
import su.nightexpress.combatpets.pet.PetManager;
import su.nightexpress.nightcore.bridge.common.NightNbtHolder;
import su.nightexpress.nightcore.bridge.dialog.wrap.WrappedDialog;
import su.nightexpress.nightcore.bridge.dialog.wrap.base.WrappedDialogAfterAction;
import su.nightexpress.nightcore.bridge.dialog.wrap.body.WrappedDialogBody;
import su.nightexpress.nightcore.ui.dialog.Dialogs;
import su.nightexpress.nightcore.ui.dialog.build.DialogActions;
import su.nightexpress.nightcore.ui.dialog.build.DialogBases;
import su.nightexpress.nightcore.ui.dialog.build.DialogBodies;
import su.nightexpress.nightcore.ui.dialog.build.DialogButtons;
import su.nightexpress.nightcore.ui.dialog.build.DialogInputs;
import su.nightexpress.nightcore.ui.dialog.build.DialogTypes;
import su.nightexpress.nightcore.util.text.NightMessage;

import java.util.ArrayList;
import java.util.List;

import static su.nightexpress.combatpets.Placeholders.*;

/**
 * Rename dialog for Java Edition players (Minecraft's native dialog screen, via nightcore).
 * Replaces the chat prompt: a text box with the current name plus Save / Go Back buttons.
 * The rename itself uses {@link PetManager#submitRename(Player, String)}, the same logic as the
 * chat prompt and the Bedrock form.
 */
public final class PetRenameDialog {

    private static final String INPUT_NAME  = "name";
    private static final String ACTION_SAVE = "combatpets_rename_save";
    private static final String ACTION_BACK = "combatpets_rename_back";
    private static final int    MAX_INPUT_LENGTH = 128;

    private PetRenameDialog() {
    }

    public static void show(@NonNull PetsPlugin plugin, @NonNull Player player) {
        PetManager petManager = plugin.getPetManager();
        ActivePet pet = petManager.getPlayerPet(player);
        if (pet == null) {
            Lang.PET_ERROR_NO_ACTIVE_PET.message().send(player);
            return;
        }

        String petName = pet.getName();
        String min = String.valueOf(Config.PET_NAME_LENGTH_MIN.get());
        String max = String.valueOf(Config.PET_NAME_LENGTH_MAX.get());

        List<WrappedDialogBody> body = new ArrayList<>();
        body.add(DialogBodies.plain(Lang.PET_RENAME_DIALOG_BODY.text()
            .replace(PET_NAME, petName)
            .replace(GENERIC_AMOUNT, min)
            .replace(GENERIC_MAX, max)).build());
        if (PetManager.isRenameNametagRequired()) {
            body.add(DialogBodies.plain(Lang.PET_RENAME_DIALOG_BODY_NAMETAG.text()).build());
        }

        WrappedDialog dialog = Dialogs.create(builder -> builder
            .base(DialogBases.builder(Lang.PET_RENAME_DIALOG_TITLE.text().replace(PET_NAME, petName))
                .canCloseWithEscape(true)
                .afterAction(WrappedDialogAfterAction.CLOSE)
                .body(body)
                .inputs(DialogInputs.text(INPUT_NAME, Lang.PET_RENAME_DIALOG_INPUT.text())
                    .initial(NightMessage.stripTags(petName))
                    .maxLength(MAX_INPUT_LENGTH)
                    .build())
                .build())
            .type(DialogTypes.multiAction(
                    DialogButtons.action(Lang.PET_RENAME_DIALOG_SAVE).action(DialogActions.customClick(ACTION_SAVE)).build(),
                    DialogButtons.action(Lang.PET_RENAME_DIALOG_BACK).action(DialogActions.customClick(ACTION_BACK)).build()
                )
                .columns(1)
                .build())
            .handleResponse(ACTION_SAVE, (viewer, identifier, nbt) -> {
                String input = readName(nbt);
                plugin.runTask(() -> onSave(plugin, player, input));
            })
            .handleResponse(ACTION_BACK, (viewer, identifier, nbt) -> plugin.runTask(() -> {
                if (player.isOnline()) petManager.openOverviewMenu(player);
            }))
        );

        Dialogs.showDialog(player, dialog);
    }

    @NonNull
    private static String readName(NightNbtHolder nbt) {
        return nbt == null ? "" : nbt.getText(INPUT_NAME, "").trim();
    }

    private static void onSave(@NonNull PetsPlugin plugin, @NonNull Player player, @NonNull String input) {
        if (!player.isOnline()) return;

        if (input.isEmpty()) {
            Lang.PET_RENAME_ERROR_EMPTY.message().send(player);
            show(plugin, player);
            return;
        }

        // Takes the Name Tag (if required) and refunds it if the name is rejected. Error messages are
        // shown as titles, which are visible because the dialog closes on click.
        if (plugin.getPetManager().submitRename(player, input)) {
            plugin.getPetManager().openOverviewMenu(player);
        }
    }
}
