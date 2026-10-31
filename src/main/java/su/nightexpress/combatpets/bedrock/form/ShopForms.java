package su.nightexpress.combatpets.bedrock.form;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.api.pet.Template;
import su.nightexpress.combatpets.api.pet.Tier;
import su.nightexpress.combatpets.bedrock.BedrockLang;
import su.nightexpress.combatpets.bedrock.BedrockManager;
import su.nightexpress.combatpets.config.Config;
import su.nightexpress.combatpets.shop.ShopManager;
import su.nightexpress.combatpets.shop.data.EggPrice;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static su.nightexpress.combatpets.Placeholders.GENERIC_PRICE;

/**
 * Bedrock forms for the egg shop (Java: Shop Tiers GUI, Egg Shop GUI and purchase confirmation).
 */
public class ShopForms extends FormSupport {

    public ShopForms(@NonNull PetsPlugin plugin, @NonNull BedrockManager manager) {
        super(plugin, manager);
    }

    @Nullable
    private ShopManager shop() {
        return this.plugin.getShopManager();
    }

    public boolean openTiers(@NonNull Player player) {
        ShopManager shop = this.shop();
        if (shop == null) return false;

        List<FormButton> buttons = new ArrayList<>();
        this.plugin.getPetManager().getTiers().stream()
            .filter(shop::isEggBuyable)
            .sorted(Comparator.comparingDouble(Tier::getWeight).reversed())
            .forEach(tier -> {
                String label = this.text(player, BedrockLang.SHOP_TIERS_BUTTON, tier.replacePlaceholders());
                buttons.add(this.button(label, Config.BEDROCK_ICON_TIER, () -> this.openEggs(player, tier, 0)));
            });

        return this.manager.sendButtons(player,
            this.text(player, BedrockLang.SHOP_TIERS_TITLE),
            this.text(player, BedrockLang.SHOP_TIERS_CONTENT),
            buttons
        );
    }

    public boolean openEggs(@NonNull Player player, @NonNull Tier tier, int requestedPage) {
        ShopManager shop = this.shop();
        if (shop == null) return false;

        List<Template> templates = this.plugin.getPetManager().getTemplates().stream()
            .filter(template -> shop.isEggBuyable(tier, template))
            .sorted(Comparator.comparing(Template::getDefaultName))
            .toList();

        int pages = BedrockManager.getPages(templates.size());
        int page = BedrockManager.clampPage(requestedPage, templates.size());

        List<FormButton> buttons = new ArrayList<>();
        for (Template template : BedrockManager.getPage(templates, page)) {
            EggPrice price = shop.getEggPrice(tier, template);
            if (price == null) continue;

            String label = this.text(player, BedrockLang.SHOP_EGGS_BUTTON,
                replace(GENERIC_PRICE, price.getCurrency().format(price.getPrice())),
                template.replacePlaceholders(),
                tier.replacePlaceholders()
            );
            buttons.add(this.button(label, Config.BEDROCK_ICON_EGG, () -> this.openConfirm(player, template, tier, page)));
        }

        this.addPagination(player, buttons, page, pages, newPage -> this.openEggs(player, tier, newPage));
        buttons.add(this.back(player, () -> shop.openTiersMenu(player)));

        return this.manager.sendButtons(player,
            this.text(player, BedrockLang.SHOP_EGGS_TITLE, tier.replacePlaceholders(), pages(page, pages)),
            this.text(player, templates.isEmpty() ? BedrockLang.SHOP_EGGS_CONTENT_EMPTY : BedrockLang.SHOP_EGGS_CONTENT),
            buttons
        );
    }

    /**
     * Purchase confirmation (Java: purchase confirmation GUI).
     */
    public boolean openConfirm(@NonNull Player player, @NonNull Template template, @NonNull Tier tier, int page) {
        ShopManager shop = this.shop();
        if (shop == null) return false;

        EggPrice price = shop.getEggPrice(tier, template);
        if (price == null) {
            return this.openEggs(player, tier, page);
        }

        return this.manager.sendConfirm(player,
            this.text(player, BedrockLang.SHOP_CONFIRM_TITLE),
            this.text(player, BedrockLang.SHOP_CONFIRM_CONTENT,
                replace(GENERIC_PRICE, price.getCurrency().format(price.getPrice())),
                template.replacePlaceholders(),
                tier.replacePlaceholders()
            ),
            this.text(player, BedrockLang.SHOP_CONFIRM_BUTTON),
            this.text(player, BedrockLang.BUTTON_CANCEL),
            () -> {
                // Price may have changed on reload: the purchase uses the current price.
                if (shop.getEggPrice(tier, template) == null) {
                    this.openEggs(player, tier, page);
                    return;
                }
                shop.purchaseEgg(player, template, tier);
            },
            () -> this.openEggs(player, tier, page)
        );
    }
}
