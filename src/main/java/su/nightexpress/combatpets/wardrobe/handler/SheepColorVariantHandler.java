package su.nightexpress.combatpets.wardrobe.handler;

import org.bukkit.DyeColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Sheep;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import su.nightexpress.combatpets.config.Lang;

@NullMarked
public class SheepColorVariantHandler extends EnumVariantHandler<DyeColor> {

    public SheepColorVariantHandler() {
        super(DyeColor.class);
    }

    @Override
    @Nullable
    public DyeColor read(LivingEntity entity) {
        if (!(entity instanceof Sheep sheep)) return null;

        return sheep.getColor();
    }

    @Override

    public String getLocalized(DyeColor value) {
        return Lang.DYE_COLOR.getLocalized(value);
    }

    @Override
    public boolean apply(LivingEntity entity, @Nullable DyeColor value) {
        if (!(entity instanceof Sheep sheep)) return false;

        sheep.setColor(value);
        return true;
    }
}
