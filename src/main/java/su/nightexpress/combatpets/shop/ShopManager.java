package su.nightexpress.combatpets.shop;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.api.pet.Template;
import su.nightexpress.combatpets.api.pet.Tier;
import su.nightexpress.combatpets.bedrock.form.ShopForms;
import su.nightexpress.combatpets.config.Lang;
import su.nightexpress.combatpets.shop.data.EggPrice;
import su.nightexpress.combatpets.shop.menu.EggConfirmMenu;
import su.nightexpress.combatpets.shop.menu.ShopEggsMenu;
import su.nightexpress.combatpets.shop.menu.ShopTiersMenu;
import su.nightexpress.nightcore.bridge.currency.Currency;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.integration.currency.CurrencyId;
import su.nightexpress.nightcore.integration.currency.EconomyBridge;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.Players;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static su.nightexpress.combatpets.Placeholders.GENERIC_PRICE;

public class ShopManager extends AbstractManager<PetsPlugin> {

    public static final String FILE_NAME = "shop.yml";

    private final Map<String, Map<String, EggPrice>> eggPriceMap;
    private final Map<String, EggPrice>              defaultEggPriceMap;

    private ShopTiersMenu  tiersMenu;
    private ShopEggsMenu   eggsMenu;
    private EggConfirmMenu eggConfirmMenu;

    public ShopManager(@NonNull PetsPlugin plugin) {
        super(plugin);
        this.eggPriceMap = new HashMap<>();
        this.defaultEggPriceMap = new HashMap<>();
    }

    @Override
    protected void onLoad() {
        FileConfig config = this.getConfig();

        this.loadPrices(config);
        this.loadUI();

        config.saveChanges();
    }

    @Override
    protected void onShutdown() {
        if (this.eggConfirmMenu != null) this.eggConfirmMenu.clear();
        if (this.tiersMenu != null) this.tiersMenu.clear();
        if (this.eggsMenu != null) this.eggsMenu.clear();

        this.eggPriceMap.clear();
    }

    private void loadPrices(@NonNull FileConfig config) {
        //AtomicInteger modifier = new AtomicInteger(1);

        plugin.getPetManager().getTiers().stream().sorted(Comparator.comparingDouble(Tier::getWeight).reversed())
            .forEach(tier -> {
                double oldModifier = tier.getConfig().getDouble("Egg_Cost_Modifier", 1D);

                // ----------- UPDATE OLD DATA - START -----------
                for (Template petConfig : plugin.getPetManager().getTemplates()) {
                    FileConfig petFile = petConfig.getConfig();
                    if (!petFile.contains("Egg_Cost")) continue;

                    String path = "EggPrice.Custom." + tier.getId() + "." + petConfig.getId();
                    double oldPrice = petFile.getDouble("Egg_Cost", -1) * oldModifier;

                    config.addMissing(path + ".Price", oldPrice);
                    config.addMissing(path + ".Currency", CurrencyId.VAULT);
                    petFile.remove("Egg_Cost");
                }
                // ----------- UPDATE OLD DATA - END -----------


                // Load default prices.
                String path = "EggPrice.Default." + tier.getId();
                double price = ConfigValue.create(path + ".Price", 1000D).read(config);
                if (price <= 0D) return;

                String currencyId = CurrencyId.reroute(ConfigValue.create(path + ".Currency", CurrencyId.VAULT).read(
                    config));
                Currency currency = EconomyBridge.api().getCurrency(currencyId);
                if (currency == null) {
                    this.plugin.warn("Invalid currency '" + currencyId + "' for default tier price for '" + tier
                        .getId() + "' tier.");
                    return;
                }

                this.defaultEggPriceMap.put(tier.getId(), new EggPrice(currency, price));
            });

        if (config.getSection("EggPrice.Custom").isEmpty()) {
            config.set("EggPrice.Custom.common.zombie", 500);
            config.set("EggPrice.Custom.rare.zombie", 1500);
        }

        // Load custom per egg prices.
        config.getSection("EggPrice.Custom").forEach(tierId -> {
            Tier tier = this.plugin.getPetManager().getTier(tierId);
            if (tier == null) return;

            config.getSection("EggPrice.Custom." + tierId).forEach(petId -> {
                Template petConfig = this.plugin.getPetManager().getTemplate(petId);
                if (petConfig == null) return;

                String path = "EggPrice.Custom." + tier.getId() + "." + petConfig.getId();

                double price = ConfigValue.create(path + ".Price", 0D).read(config);
                if (price <= 0D) return;

                String currencyId = CurrencyId.reroute(ConfigValue.create(path + ".Currency", CurrencyId.VAULT).read(
                    config));
                Currency currency = EconomyBridge.api().getCurrency(currencyId);
                if (currency == null) {
                    this.plugin.warn("Invalid currency '" + currencyId + "' for '" + tier.getId() + " -> " + petConfig
                        .getId() + "' pet egg price.");
                    return;
                }

                this.eggPriceMap.computeIfAbsent(tier.getId(), k -> new HashMap<>()).put(petConfig.getId(),
                    new EggPrice(currency, price));
            });
        });

        //        if (Config.isWardrobeEnabled()) {
        //            config.getSection("Price.Customizations").forEach(name -> {
        //                EntityVariant<?> variant = VariantRegistry.getVariant(name);
        //                if (variant == null) return;
        //
        //                config.getSection("Price.Customizations." + name).forEach(value -> {
        //                    Object object = variant.getHandler().parse(value);
        //                    if (object == null) return;
        //
        //                    String path = "Price.Customizations." + name + "." + value;
        //
        //                    double price = ConfigValue.create(path + ".Price", 0D).read(config);
        //                    if (price <= 0D) return;
        //
        //                    String currencyId = ConfigValue.create(path + ".Currency", VaultEconomyHandler.ID).read(config);
        //                    Currency currency = this.plugin.getCurrencyManager().getCurrency(currencyId);
        //                    if (currency == null) {
        //                        this.plugin.warn("Invalid currency '" + currencyId + "' for '" + name + " -> " + value + "' pet customization price.");
        //                        return;
        //                    }
        //
        //                    this.customsPriceMap.computeIfAbsent(variant.getName(), k -> new HashMap<>()).put(value.toLowerCase(), new EggPrice(currency, price));
        //                });
        //            });
        //        }
    }

    private void loadUI() {
        this.tiersMenu = new ShopTiersMenu(this.plugin, this);
        this.eggsMenu = new ShopEggsMenu(this.plugin, this);
        this.eggConfirmMenu = new EggConfirmMenu(this.plugin, this);
    }

    @NonNull
    public FileConfig getConfig() {
        return FileConfig.loadOrExtract(this.plugin, FILE_NAME);
    }

    /**
     * @param tier   Pet tier
     * @param config Pet config
     * @return Price for the pet egg with specified tier. If no price set, null will be returned.
     */
    @Nullable
    public EggPrice getEggPrice(@NonNull Tier tier, @NonNull Template config) {
        return this.eggPriceMap.getOrDefault(tier.getId(), Collections.emptyMap()).getOrDefault(config.getId(), this
            .getDefaultEggPrice(tier));
    }

    //    @Nullable
    //    public EggPrice getCustomizationPrice(@NonNull EntityVariant<?> variant, @NonNull String value) {
    //        return this.customsPriceMap.getOrDefault(variant.getName(), Collections.emptyMap()).get(value.toLowerCase());
    //    }

    @Nullable
    public EggPrice getDefaultEggPrice(@NonNull Tier tier) {
        return this.defaultEggPriceMap.get(tier.getId());
    }

    public boolean isEggBuyable(@NonNull Tier tier) {
        return this.eggPriceMap.containsKey(tier.getId()) || this.getDefaultEggPrice(tier) != null;
    }

    public boolean isEggBuyable(@NonNull Tier tier, @NonNull Template config) {
        EggPrice price = this.getEggPrice(tier, config);
        return price != null && price.getPrice() >= 0D;
    }

    //    public boolean isCustomizationBuyable(@NonNull EntityVariant<?> variant, @NonNull String value) {
    //        EggPrice price = this.getCustomizationPrice(variant, value);
    //        return price != null && price.getPrice() >= 0D;
    //    }

    /**
     * Purchases a pet egg for the player. Used by both the Java confirmation GUI and the Bedrock forms.
     * @return true if the egg was purchased.
     */
    public boolean purchaseEgg(@NonNull Player player, @NonNull Template template, @NonNull Tier tier) {
        EggPrice price = this.getEggPrice(tier, template);
        if (price == null) return false;

        Currency currency = price.getCurrency();
        double cost = price.getPrice();

        double balance = currency.queryBalance(player);
        if (balance < cost) {
            Lang.SHOP_PURCHASE_ERROR_NOT_ENOUGH_FUNDS.message().send(player, replacer -> replacer
                .replace(GENERIC_PRICE, currency.format(cost))
                .replace(template.replacePlaceholders())
                .replace(tier.replacePlaceholders())
            );
            return false;
        }

        currency.withdrawAsync(player.getUniqueId(), cost);
        Players.addItem(player, template.createEgg(tier));

        Lang.SHOP_PURCHASE_SUCCESS.message().send(player, replacer -> replacer
            .replace(GENERIC_PRICE, currency.format(cost))
            .replace(template.replacePlaceholders())
            .replace(tier.replacePlaceholders())
        );
        return true;
    }

    /**
     * Only call after {@link PetsPlugin#useBedrockForms(Player)} returned true.
     */
    @NonNull
    private ShopForms bedrockForms() {
        return java.util.Objects.requireNonNull(this.plugin.getBedrockManager()).getShopForms();
    }

    public void openTiersMenu(@NonNull Player player) {
        if (this.plugin.useBedrockForms(player) && this.bedrockForms().openTiers(player)) return;

        this.tiersMenu.open(player);
    }

    public void openEggsMenu(@NonNull Player player, @NonNull Tier tier) {
        if (this.plugin.useBedrockForms(player) && this.bedrockForms().openEggs(player, tier, 0)) return;

        this.eggsMenu.open(player, tier);
    }

    public void openEggPurchaseConfirm(@NonNull Player player, @NonNull Template petConfig, @NonNull Tier tier) {
        if (this.plugin.useBedrockForms(player) && this.bedrockForms().openConfirm(player, petConfig, tier, 0)) return;

        this.eggConfirmMenu.open(player, new EggConfirmMenu.BuyInfo(petConfig, tier));
    }
}
