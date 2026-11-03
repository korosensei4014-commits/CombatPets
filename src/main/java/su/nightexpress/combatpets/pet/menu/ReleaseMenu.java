package su.nightexpress.combatpets.pet.menu;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.config.Config;
import su.nightexpress.combatpets.data.impl.PetData;
import su.nightexpress.combatpets.util.PetUtils;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.menu.MenuOptions;
import su.nightexpress.nightcore.menu.MenuSize;
import su.nightexpress.nightcore.menu.MenuViewer;
import su.nightexpress.nightcore.menu.item.MenuItem;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.Lists;

import java.util.ArrayList;
import java.util.List;

import static su.nightexpress.combatpets.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class ReleaseMenu extends ConfirmMenu {

    private static final String FILE = "pet_release.yml";

    public ReleaseMenu(@NonNull PetsPlugin plugin) {
        super(plugin, FileConfig.loadOrExtract(plugin, Config.DIR_MENU, FILE));
        this.load();

        this.getItems().forEach(PetUtils::applyMenuPlaceholders);
    }

    @Override
    protected void onConfirm(@NonNull MenuViewer viewer, @NonNull PetData petData) {
        this.plugin.getPetManager().releasePet(viewer.getPlayer(), petData);
        this.runNextTick(() -> viewer.getPlayer().closeInventory());
    }

    @Override
    protected void onDecline(@NonNull MenuViewer viewer, @NonNull PetData petData) {
        this.runNextTick(() -> plugin.getPetManager().openPetsCollection(viewer.getPlayer(), petData.getTier()));
    }

    @Override
    protected void onReady(@NonNull MenuViewer viewer, @NonNull Inventory inventory) {

    }

    @Override
    @NonNull
    protected MenuOptions createDefaultOptions() {
        return new MenuOptions(BLACK.enclose("Release the pet?"), MenuSize.CHEST_9);
    }

    @Override
    @NonNull
    protected List<MenuItem> createDefaultItems() {
        List<MenuItem> list = new ArrayList<>();

        ItemStack acceptItem = ItemUtil.getSkinHead(SKIN_CHECK_MARK);
        ItemUtil.editMeta(acceptItem, meta -> {
            meta.setDisplayName(LIGHT_GREEN.enclose(BOLD.enclose("Accept")));
        });
        list.add(new MenuItem(acceptItem).setPriority(10).setSlots(8).setHandler(this.acceptHandler));

        ItemStack denyItem = ItemUtil.getSkinHead(SKIN_WRONG_MARK);
        ItemUtil.editMeta(denyItem, meta -> {
            meta.setDisplayName(LIGHT_RED.enclose(BOLD.enclose("Cancel")));
        });
        list.add(new MenuItem(denyItem).setPriority(10).setSlots(0).setHandler(this.declineHandler));

        return list;
    }

    @Override
    protected void loadAdditional() {
        this.iconName = ConfigValue.create("PetIcon.Name",
            LIGHT_YELLOW.enclose(BOLD.enclose(PET_NAME))
        ).read(cfg);

        this.iconLore = ConfigValue.create("PetIcon.Lore", Lists.newList(
            LIGHT_GRAY.enclose("You are about to release this pet.")
        )).read(cfg);

        this.iconSlot = ConfigValue.create("PetIcon.Slot", 4).read(cfg);

        // 'Store in Egg' mode (Pets.Release.Mode: STORE) uses its own title and text.
        String storeTitle = ConfigValue.create("Store_In_Egg.Title",
            BLACK.enclose("Store the pet in an egg?"),
            "Title when Pets.Release.Mode is STORE."
        ).read(cfg);
        List<String> storeLore = ConfigValue.create("Store_In_Egg.PetIcon.Lore", Lists.newList(
            LIGHT_GRAY.enclose("Store this pet in an egg item."),
            LIGHT_GRAY.enclose("It keeps its name, level, aspects and stats,"),
            LIGHT_GRAY.enclose("and anyone can hatch it into their collection."),
            LIGHT_GRAY.enclose("Its inventory and equipment drop at your feet."),
            "",
            LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Cost: ") + PET_STORE_COST)
        ), "Pet icon text when Pets.Release.Mode is STORE.").read(cfg);

        if (Config.isReleaseStoreMode()) {
            this.getOptions().setTitle(su.nightexpress.nightcore.util.text.NightMessage.asLegacy(storeTitle));
            this.iconLore = storeLore;
        }
    }
}
