package su.nightexpress.combatpets.bedrock.form;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * A button of a Bedrock simple form together with the action executed when it is pressed.
 *
 * @param text   already formatted button text (may contain one line break).
 * @param icon   Bedrock texture path or image URL, or null for no icon.
 * @param action action executed on the main thread when the button is pressed.
 */
public record FormButton(@NonNull String text, @Nullable String icon, @NonNull Runnable action) {
}
