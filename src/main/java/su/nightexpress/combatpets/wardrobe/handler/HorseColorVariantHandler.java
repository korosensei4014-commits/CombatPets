package su.nightexpress.combatpets.wardrobe.handler;

import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import su.nightexpress.combatpets.config.Lang;

@NullMarked
public class HorseColorVariantHandler extends EnumVariantHandler<Horse.Color> {

    public HorseColorVariantHandler() {
        super(Horse.Color.class);
    }

    @Override
    public Horse.@Nullable Color read(LivingEntity entity) {
        if (!(entity instanceof Horse horse)) return null;

        return horse.getColor();
    }

    @Override

    public String getLocalized(Horse.Color value) {
        return Lang.HORSE_COLOR.getLocalized(value);
    }

    @Override
    public boolean apply(LivingEntity entity, Horse.@Nullable Color value) {
        if (value == null) return false;
        if (!(entity instanceof Horse horse)) return false;

        horse.setColor(value);
        return true;
    }
}
