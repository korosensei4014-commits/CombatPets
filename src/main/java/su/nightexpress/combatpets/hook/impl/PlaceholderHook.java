package su.nightexpress.combatpets.hook.impl;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.data.impl.PetData;
import su.nightexpress.combatpets.pet.duel.DuelManager;
import su.nightexpress.combatpets.api.pet.Aspect;
import su.nightexpress.combatpets.api.pet.Stat;
import su.nightexpress.combatpets.config.Lang;
import su.nightexpress.combatpets.pet.AttributeRegistry;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.text.NightMessage;

public class PlaceholderHook {

    private static Expansion expansion;

    public static void setup(@NonNull PetsPlugin plugin) {
        if (expansion == null) {
            expansion = new Expansion(plugin);
            expansion.register();
        }
    }

    public static void shutdown() {
        if (expansion != null) {
            expansion.unregister();
            expansion = null;
        }
    }

    private static class Expansion extends PlaceholderExpansion {

        private final PetsPlugin plugin;

        public Expansion(@NonNull PetsPlugin plugin) {
            this.plugin = plugin;
        }

        @Override
        @NonNull
        public String getAuthor() {
            return plugin.getDescription().getAuthors().getFirst();
        }

        @Override
        @NonNull
        public String getIdentifier() {
            return plugin.getName().toLowerCase();
        }

        @Override
        @NonNull
        public String getVersion() {
            return plugin.getDescription().getVersion();
        }

        @Override
        public boolean persist() {
            return true;
        }

        @Override
        public String onPlaceholderRequest(@NonNull Player player, @NonNull String params) {
            if (params.equalsIgnoreCase("duels_enabled")) {
                DuelManager duels = this.plugin.getDuelManager();
                return duels == null ? Lang.DUEL_STATUS_OFF.text() : duels.getStatusText(player);
            }

            // Duel totals across all of the player's pets (work without a summoned pet).
            if (params.equalsIgnoreCase("duel_wins_total") || params.equalsIgnoreCase("duel_losses_total")) {
                boolean wins = params.equalsIgnoreCase("duel_wins_total");
                int total = this.plugin.getUserManager().getOrFetch(player).getPets().values().stream()
                    .mapToInt(data -> wins ? data.getDuelWins() : data.getDuelLosses()).sum();
                return NumberUtil.format(total);
            }

            ActivePet pet = this.plugin.getPetManager().getPlayerPet(player);
            if (pet == null) return "-";

            if (params.equalsIgnoreCase("duel_wins") || params.equalsIgnoreCase("duel_losses")) {
                PetData data = this.plugin.getUserManager().getOrFetch(player).getPet(pet.getTemplate(), pet.getTier());
                if (data == null) return "0";
                return NumberUtil.format(params.equalsIgnoreCase("duel_wins") ? data.getDuelWins() : data.getDuelLosses());
            }

            if (params.equalsIgnoreCase("health")) {
                return NumberUtil.format(pet.getEntity().getHealth());
            }
            if (params.equalsIgnoreCase("max_health")) {
                return NumberUtil.format(pet.getMaxHealth());
            }
            if (params.equalsIgnoreCase("xp")) {
                return NumberUtil.format(pet.getXP());
            }
            if (params.equalsIgnoreCase("level")) {
                return String.valueOf(pet.getLevel());
            }
            if (params.equalsIgnoreCase("tier_name")) {
                return NightMessage.asLegacy(pet.getTier().getName());
            }
            if (params.equalsIgnoreCase("required_xp")) {
                return NumberUtil.format(pet.getRequiredXP());
            }
            if (params.equalsIgnoreCase("name")) {
                return pet.getName();
            }
            if (params.equalsIgnoreCase("aspect_points")) {
                return NumberUtil.format(pet.getAspectPoints());
            }
            if (params.equalsIgnoreCase("combat_mode")) {
                return Lang.COMBAT_MODE.getLocalized(pet.getCombatMode());
            }
            if (params.startsWith("attribute_")) {
                String name = params.substring("attribute_".length());
                Stat stat = AttributeRegistry.getById(name);
                return stat == null ? "?" : NumberUtil.format(pet.getAttribute(stat));
            }
            if (params.startsWith("aspect_")) {
                String name = params.substring("aspect_".length());
                Aspect aspect = plugin.getPetManager().getAspect(name);
                return aspect == null ? "?" : NumberUtil.format(pet.getAspectValue(aspect));
            }

            return null;
        }

    }

}
