package su.nightexpress.combatpets.wardrobe.handler;

import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import su.nightexpress.combatpets.config.Lang;

@NullMarked
public class HorseStyleVariantHandler extends EnumVariantHandler<Horse.Style> {

    public HorseStyleVariantHandler() {
        super(Horse.Style.class);
    }

    @Override
    public Horse.@Nullable Style read(LivingEntity entity) {
        if (!(entity instanceof Horse horse)) return null;

        return horse.getStyle();
    }

    @Override

    public String getLocalized(Horse.Style value) {
        return Lang.HORSE_STYLE.getLocalized(value);
    }

    @Override
    public boolean apply(LivingEntity entity, Horse.@Nullable Style value) {
        if (value == null) return false;
        if (!(entity instanceof Horse horse)) return false;

        horse.setStyle(value);
        return true;
    }
}
