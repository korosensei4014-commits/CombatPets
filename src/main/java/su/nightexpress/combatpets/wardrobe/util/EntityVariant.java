package su.nightexpress.combatpets.wardrobe.util;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import su.nightexpress.combatpets.PetAPI;

public class EntityVariant<T> {

    private final String            name;
    private final NamespacedKey     key;
    private final VariantHandler<T> handler;

    private String    displayName;
    private ItemStack icon;

    public EntityVariant(@NonNull String name, @NonNull VariantHandler<T> handler) {
        this.name = name.toLowerCase();
        this.key = new NamespacedKey(PetAPI.plugin, "accessorydata." + this.name);
        this.handler = handler;

        this.setDisplayName(this.getName());
        this.setIcon(new ItemStack(Material.PAPER));
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public NamespacedKey getKey() {
        return this.key;
    }

    @NonNull
    public VariantHandler<T> getHandler() {
        return handler;
    }

    @NonNull
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(@NonNull String displayName) {
        this.displayName = displayName;
    }

    @NonNull
    public ItemStack getIcon() {
        return new ItemStack(this.icon);
    }

    public void setIcon(@NonNull ItemStack icon) {
        this.icon = new ItemStack(icon);
    }
}
