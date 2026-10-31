package su.nightexpress.combatpets.bedrock.form;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.bedrock.BedrockLang;
import su.nightexpress.combatpets.bedrock.BedrockManager;
import su.nightexpress.combatpets.bedrock.BedrockText;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.locale.entry.TextLocale;

import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.UnaryOperator;

/**
 * Shared helpers for the Bedrock form builders.
 */
abstract class FormSupport {

    protected final PetsPlugin     plugin;
    protected final BedrockManager manager;

    protected FormSupport(@NonNull PetsPlugin plugin, @NonNull BedrockManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    /**
     * Formats a lang text for a Bedrock form.
     */
    @NonNull
    protected String text(@NonNull Player player, @NonNull TextLocale locale) {
        return BedrockText.format(player, locale.text(), UnaryOperator.identity());
    }

    /**
     * Formats a lang text for a Bedrock form, applying the given placeholder replacers in order.
     */
    @NonNull
    @SafeVarargs
    protected final String text(@NonNull Player player, @NonNull TextLocale locale, @NonNull UnaryOperator<String>... replacers) {
        return BedrockText.format(player, locale.text(), chain(replacers));
    }

    @NonNull
    @SafeVarargs
    protected static UnaryOperator<String> chain(@NonNull UnaryOperator<String>... replacers) {
        return string -> {
            String result = string;
            for (UnaryOperator<String> replacer : replacers) {
                result = replacer.apply(result);
            }
            return result;
        };
    }

    @NonNull
    protected static UnaryOperator<String> replace(@NonNull String placeholder, @NonNull String value) {
        return string -> string.replace(placeholder, value);
    }

    @Nullable
    protected static String icon(@NonNull ConfigValue<String> value) {
        return value.get();
    }

    @NonNull
    protected FormButton button(@NonNull String text, @NonNull ConfigValue<String> icon, @NonNull Runnable action) {
        return new FormButton(text, icon(icon), action);
    }

    @NonNull
    protected FormButton back(@NonNull Player player, @NonNull Runnable action) {
        return new FormButton(this.text(player, BedrockLang.BUTTON_BACK), icon(su.nightexpress.combatpets.config.Config.BEDROCK_ICON_NAVIGATION), action);
    }

    /**
     * Adds previous / next page buttons when needed.
     * @param openPage opens the given page (0-based).
     */
    protected void addPagination(@NonNull Player player, @NonNull List<FormButton> buttons, int page, int pages, @NonNull IntConsumer openPage) {
        String navIcon = icon(su.nightexpress.combatpets.config.Config.BEDROCK_ICON_NAVIGATION);
        if (page > 0) {
            buttons.add(new FormButton(this.text(player, BedrockLang.BUTTON_PREV_PAGE), navIcon, () -> openPage.accept(page - 1)));
        }
        if (page < pages - 1) {
            buttons.add(new FormButton(this.text(player, BedrockLang.BUTTON_NEXT_PAGE), navIcon, () -> openPage.accept(page + 1)));
        }
    }

    @NonNull
    protected static UnaryOperator<String> pages(int page, int pages) {
        return chain(replace(BedrockLang.PAGE, String.valueOf(page + 1)), replace(BedrockLang.PAGES, String.valueOf(pages)));
    }
}
