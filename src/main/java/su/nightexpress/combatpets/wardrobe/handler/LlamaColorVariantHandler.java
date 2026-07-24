package su.nightexpress.combatpets.wardrobe.handler;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import su.nightexpress.combatpets.config.Lang;

@NullMarked
public class LlamaColorVariantHandler extends EnumVariantHandler<Llama.Color> {

    public LlamaColorVariantHandler() {
        super(Llama.Color.class);
    }

    @Override
    public Llama.@Nullable Color read(LivingEntity entity) {
        if (!(entity instanceof Llama llama)) return null;

        return llama.getColor();
    }

    @Override

    public String getLocalized(Llama.Color value) {
        return Lang.LLAMA_COLOR.getLocalized(value);
    }

    @Override
    public boolean apply(LivingEntity entity, Llama.@Nullable Color value) {
        if (value == null) return false;
        if (!(entity instanceof Llama llama)) return false;

        llama.setColor(value);
        return true;
    }
}
