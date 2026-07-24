package su.nightexpress.combatpets.pet.impl;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import su.nightexpress.combatpets.Placeholders;
import su.nightexpress.combatpets.api.pet.Aspect;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class PetAspect implements Aspect {

    private final String         id;
    private final String         name;
    private final ItemStack      icon;
    private final Set<String>    attributes;
    private final PlaceholderMap placeholderMap;

    public PetAspect(@NonNull String id, @NonNull String name, @NonNull ItemStack icon,
                     @NonNull Set<String> attributes) {
        this.id = id.toLowerCase();
        this.name = name;
        this.icon = icon;
        this.attributes = attributes;
        this.placeholderMap = Placeholders.forAspect(this);
    }

    @NonNull
    public static PetAspect read(@NonNull FileConfig config, @NonNull String path, @NonNull String id) {
        String name = config.getString(path + ".Name", StringUtil.capitalizeUnderscored(id));

        ItemStack icon = ConfigValue.create(path + ".Icon", new ItemStack(Material.LIME_DYE),
            "Sets aspect icon.",
            Placeholders.WIKI_ITEMS_URL
        ).read(config);

        Set<String> attributes = ConfigValue.create(path + ".Attached_Attributes",
            new HashSet<>(),
            "Sets pet's attributes affected by this aspect.",
            Placeholders.WIKI_ATTRIBUTES_URL,
            Placeholders.WIKI_ASPECTS_URL
        ).onRead(set -> set.stream().map(String::toLowerCase).collect(Collectors.toSet())).read(config);

        return new PetAspect(id, name, icon, attributes);
    }

    public void write(@NonNull FileConfig config, @NonNull String path) {
        config.set(path + ".Name", this.getName());
        config.setItem(path + ".Icon", this.getIcon());
        config.set(path + ".Attached_Attributes", this.getAttributes());
    }

    @Override
    @NonNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @NonNull
    public String getId() {
        return this.id;
    }

    @NonNull
    public String getName() {
        return this.name;
    }

    @NonNull
    @Override
    public ItemStack getIcon() {
        return new ItemStack(this.icon);
    }

    @NonNull
    public Set<String> getAttributes() {
        return this.attributes;
    }
}
