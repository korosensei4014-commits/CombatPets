package su.nightexpress.combatpets.wardrobe.handler;

import java.util.List;
import java.util.stream.IntStream;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Slime;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import su.nightexpress.combatpets.wardrobe.util.VariantHandler;
import su.nightexpress.nightcore.util.NumberUtil;

@NullMarked
public class SizeVariantHandler extends VariantHandler<Integer> {

    @Override

    public List<Integer> values() {
        return IntStream.range(1, 17).boxed().toList();
    }

    @Override
    @Nullable
    public Integer parse(String raw) {
        return NumberUtil.getInteger(raw, 1);
    }

    @Override
    @Nullable
    public Integer read(LivingEntity entity) {
        if (entity instanceof Slime slime) {
            return slime.getSize();
        }
        if (entity instanceof Phantom phantom) {
            return phantom.getSize();
        }

        return null;
    }

    @Override

    public String getLocalized(Integer value) {
        return String.valueOf(value);
    }

    @Override

    public String getRaw(Integer value) {
        return value.toString();
    }

    @Override
    public boolean apply(LivingEntity entity, @Nullable Integer value) {
        if (value == null) return false;

        if (entity instanceof Slime slime) {
            slime.setSize(value);
        }
        if (entity instanceof Phantom phantom) {
            phantom.setSize(value);
        }

        return false;
    }
}
