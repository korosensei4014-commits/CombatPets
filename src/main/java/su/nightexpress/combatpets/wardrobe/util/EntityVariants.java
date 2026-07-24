package su.nightexpress.combatpets.wardrobe.util;

import org.jspecify.annotations.NonNull;
import su.nightexpress.nightcore.language.entry.LangEnum;
import su.nightexpress.nightcore.util.StringUtil;

import java.util.function.Function;

public class EntityVariants {

    public static final Function<String, Boolean> PARSER_BOOLEAN = Boolean::parseBoolean;

    @NonNull
    public static <E extends Enum<E>> Function<String, E> enumParser(@NonNull Class<E> clazz) {
        return str -> StringUtil.getEnum(str, clazz).orElse(null);
    }

    @NonNull
    public static <E extends Enum<E>> Function<E, String> enumLocalizer(@NonNull LangEnum<E> langEnum) {
        return langEnum::getLocalized;
    }
}
