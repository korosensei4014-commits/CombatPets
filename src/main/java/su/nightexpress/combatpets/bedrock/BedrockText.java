package su.nightexpress.combatpets.bedrock;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import su.nightexpress.combatpets.config.Config;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.text.NightMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converts plugin texts (nightcore tag format, e.g. {@code <yellow>Text</yellow>}) into the format
 * Bedrock forms understand: legacy section-sign color codes and real line breaks.
 * <p>
 * Bedrock has no hex colors, so {@code §x§r§r§g§g§b§b} sequences are mapped to the closest
 * classic color. Formats Bedrock doesn't render (strikethrough, underline) are removed.
 */
public final class BedrockText {

    private static final Pattern LINE_BREAK = Pattern.compile("(?i)<br\\s*/?>|\\r?\\n");
    private static final Pattern HEX_COLOR  = Pattern.compile("§x((?:§[0-9a-fA-F]){6})");
    private static final Pattern UNSUPPORTED_FORMAT = Pattern.compile("§[mMnN]");

    /** Classic colors as {code, rgb} pairs. */
    private static final int[][] CLASSIC_COLORS = {
        {'0', 0x000000}, {'1', 0x0000AA}, {'2', 0x00AA00}, {'3', 0x00AAAA},
        {'4', 0xAA0000}, {'5', 0xAA00AA}, {'6', 0xFFAA00}, {'7', 0xAAAAAA},
        {'8', 0x555555}, {'9', 0x5555FF}, {'a', 0x55FF55}, {'b', 0x55FFFF},
        {'c', 0xFF5555}, {'d', 0xFF55FF}, {'e', 0xFFFF55}, {'f', 0xFFFFFF}
    };

    private BedrockText() {
    }

    /**
     * Applies placeholders and converts the text for a Bedrock form.
     */
    @NonNull
    public static String format(@NonNull Player player, @NonNull String text, @NonNull UnaryOperator<String> replacer) {
        String replaced = replacer.apply(text);
        if (Config.isGUIPlaceholdersEnabled() && Plugins.hasPlaceholderAPI()) {
            replaced = PlaceholderAPI.setPlaceholders(player, replaced);
        }
        return toBedrock(replaced);
    }

    /**
     * Converts a text in nightcore tag format into Bedrock-compatible text.
     */
    @NonNull
    public static String toBedrock(@NonNull String text) {
        String[] lines = LINE_BREAK.split(text, -1);
        List<String> converted = new ArrayList<>(lines.length);
        for (String line : lines) {
            converted.add(line.isEmpty() ? "" : convertLine(line));
        }
        return String.join("\n", converted);
    }

    /**
     * Removes all tags and color codes (e.g. for dropdown options or input defaults).
     */
    @NonNull
    public static String plain(@NonNull String text) {
        return toBedrock(text).replaceAll("§.", "");
    }

    @NonNull
    private static String convertLine(@NonNull String line) {
        String legacy = NightMessage.asLegacy(line);
        legacy = downsampleHex(legacy);
        legacy = UNSUPPORTED_FORMAT.matcher(legacy).replaceAll("");
        return legacy;
    }

    @NonNull
    private static String downsampleHex(@NonNull String legacy) {
        Matcher matcher = HEX_COLOR.matcher(legacy);
        StringBuilder builder = new StringBuilder();
        while (matcher.find()) {
            String hex = matcher.group(1).replace("§", "");
            int rgb = Integer.parseInt(hex, 16);
            matcher.appendReplacement(builder, "§" + closestClassic(rgb));
        }
        matcher.appendTail(builder);
        return builder.toString();
    }

    private static char closestClassic(int rgb) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;

        char best = 'f';
        long bestDistance = Long.MAX_VALUE;
        for (int[] color : CLASSIC_COLORS) {
            int cRed = (color[1] >> 16) & 0xFF;
            int cGreen = (color[1] >> 8) & 0xFF;
            int cBlue = color[1] & 0xFF;

            long distance = (long) (red - cRed) * (red - cRed) + (long) (green - cGreen) * (green - cGreen) + (long) (blue - cBlue) * (blue - cBlue);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = (char) color[0];
            }
        }
        return best;
    }
}
