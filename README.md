# CombatPets — Bedrock Forms Fork

A fork of [nulli0n/CombatPets](https://github.com/nulli0n/CombatPets) (v2.8.4, `main` branch) that adds **native Bedrock Edition menus** for players who join through [Geyser](https://geysermc.org/).

The original chest GUIs rely on left-click, right-click and the Q/Drop key. Bedrock players can't use all of these reliably, and can't use Q at all. This fork shows Bedrock players **Bedrock forms** (buttons, toggles, dropdowns and text inputs) instead. Every action has its own clearly labelled button. Java players keep the original chest GUIs, unchanged.

All upstream features, configs, permissions, commands, data storage and integrations are kept.

---

## Supported versions

| Component | Version |
|---|---|
| Server | **Paper 26.1.2, 26.2, 26.3** (same as upstream v2.8.4) |
| Java | 25 |
| nightcore | 2.16.7+ (required, same as upstream) |
| Floodgate | 2.2.x (optional, recommended for Bedrock support) |
| Geyser-Spigot | 2.x (optional, used only if Floodgate is missing) |

## Installation

1. Install **nightcore** on the server, as upstream requires.
2. Drop `CombatPets-<version>.jar` into `plugins/`.
3. For Bedrock support, install **Floodgate** on this server (see the setups below).
4. Start the server. The console logs one of:
   - `Bedrock support enabled using the Floodgate API...`
   - `Bedrock support: Floodgate/Geyser not found. All players will use the chest GUIs.`

### Network setups

| Setup | What to install where | Bedrock forms |
|---|---|---|
| Standalone Paper + Geyser-Spigot + Floodgate | Both on the Paper server | ✅ (Floodgate API) |
| Standalone Paper + Geyser-Spigot only | Geyser on the Paper server | ✅ (Geyser API) |
| Velocity/BungeeCord + Geyser on the proxy | Floodgate on the **proxy and on every backend** running CombatPets (`send-floodgate-data: true`) | ✅ (Floodgate API) |
| Velocity/BungeeCord + Geyser on the proxy, **no Floodgate on the backend** | — | ❌ The backend can't tell who is on Bedrock, so everyone gets the chest GUIs |
| No Geyser/Floodgate at all | — | Not needed; Java only |

Bedrock players are detected through the Floodgate/Geyser APIs. Usernames, prefixes and manual lists are never used, so Java players can't be misdetected, and linked accounts are handled by Floodgate.

## Building

```bash
./gradlew build
# -> build/libs/CombatPets-2.8.4.jar
```

The build needs these Maven repositories (already configured in `build.gradle.kts`): Maven Central, `repo.papermc.io`, `repo.nightexpressdev.com`, `repo.extendedclip.com`, `mvn.lumine.io`, `repo.magmaguy.com`, and **`repo.opencollab.dev/main`** (new, for the Floodgate API).

The Floodgate API is `compileOnly("org.geysermc.floodgate:api:2.2.4-SNAPSHOT")`. If OpenCollab ever prunes that snapshot, bump it to the current `2.2.x-SNAPSHOT`. Only the stable `isFloodgatePlayer` / `sendForm` methods are used.

---

## How Bedrock menus work

When a menu opens (from a command, by interacting with your pet, or from a back button), the plugin checks the player's edition:

- **Java player:** the original chest GUI opens, exactly as before.
- **Bedrock player:** a Bedrock form opens instead. If the form can't be sent for some reason, the chest GUI opens as a fallback.

### Interaction mapping

| Java chest GUI | Java click | Bedrock form |
|---|---|---|
| **Collection** → pet | Left-click (idle pet) | **Summon Pet** button |
| | Left-click (summoned pet) | **Return to Collection** button (+ **Open Pet Menu** shortcut) |
| | Left-click (dead pet) | **Revive Pet** button → confirmation |
| | **Q / Drop key** | **Release Pet** button → confirmation (red warning) |
| **Pet Menu** → Equipment | **Left-click** (toggle lock) | **Pet Settings** → "Equipment Unlocked" toggle |
| | **Right-click** (unequip all) | **Unequip All Items** button |
| **Pet Menu** → Combat Mode | Click (cycle) | **Pet Settings** → Combat Mode dropdown |
| **Pet Menu** → Silent | Click (toggle) | **Pet Settings** → Silent Mode toggle |
| **Pet Menu** → Rename | Click → chat prompt | **Rename Pet** → text input form |
| **Pet Menu** → Inventory | Click | **Pet Inventory** button (opens the pet's storage container, which Bedrock handles natively) |
| **Pet Menu** → Aspects | Click | **Aspects** button |
| **Pet Menu** → Return | Click | **Return to Collection** button |
| **Aspects** → aspect | Click (spend 1 point) | Aspect details (bonuses per level) → **Upgrade (+1)** button |
| **Aspects** → Reallocate | Click | **Reallocate Points** → confirmation |
| **Tiers / Shop Tiers** | Click | Tier buttons |
| **Egg Shop** | Click | Paginated egg list → **Buy** confirmation |
| **Revive / Release / Buy** confirm GUIs | Accept / Decline | Confirm / Cancel buttons |

### Behaviour details

- **Same logic for both editions.** Java GUIs and Bedrock forms call the same `PetManager` / `ShopManager` methods (`spawnPet`, `despawnPet`, `revivePet`, `releasePet`, `toggleEquipmentLock`, `unequipAll`, `setCombatMode`, `toggleSilent`, `upgradeAspect`, `reallocateAspects`, `completeRename`, `purchaseEgg`, …). Permission checks, costs, messages and events are identical.
- **Navigation.** List forms have **Back** buttons, and the collection remembers its page. Dismissing a confirmation, settings or rename form (X) returns to the previous form. Dismissing a list form closes it.
- **Pagination.** Collections and egg lists are split into pages (`Bedrock.Page_Size`).
- **Input validation.** Empty rename input is rejected and the form reopens. Length, blocked-words and Name Tag rules are applied by the same code the chat rename uses. Invalid form data reopens the form.
- **Double submissions.** Each form gets a per-player id. Responses for an old or already-answered form are ignored, and `Bedrock.Submit_Cooldown_Millis` adds a short cooldown between submissions. Every action re-checks state when it runs; for example, a pet released in the meantime gives "That pet is no longer available".
- **Disconnects.** Form state is cleared on quit, and late responses from offline players are ignored.
- **Name Tag cost (rename).** On both editions the Name Tag is taken **when the name is submitted** and refunded if the name is rejected. Closing the form or prompt costs nothing.

---

## Configuration (`config.yml`)

New section, added automatically on first start:

```yaml
Bedrock:
  Enabled: true              # Bedrock forms on/off. Off = everyone gets chest GUIs.
  Provider: auto             # auto | floodgate | geyser   (auto prefers Floodgate)
  Page_Size: 10              # pets / eggs per form page
  Submit_Cooldown_Millis: 300  # min. time between two submissions (0 = off)
  Icons:
    Enabled: true            # show icons on buttons
    Tier: textures/items/egg
    Pet: textures/items/name_tag
    Summon: textures/items/lead
    Despawn: textures/items/ender_pearl
    Revive: textures/items/totem
    Release: textures/items/feather
    Manage: textures/items/book_writable
    Inventory: textures/blocks/chest_front
    Unequip: textures/items/iron_chestplate
    Aspects: textures/items/experience_bottle
    Reallocate: textures/items/clock_item
    Rename: textures/items/name_tag
    Settings: textures/items/redstone_dust
    Egg: textures/items/egg
    Navigation: ''           # Back / page buttons
```

Icons are Bedrock resource-pack texture paths, or image URLs starting with `http(s)://`. Leave an icon empty to hide it.

All existing options still apply to Bedrock forms, including `Pets.Release.Allowed`, `Pets.Allow_Reallocate_Aspect_Points`, `Pets.Name.*` and `General.PlaceholderAPI_In_GUI` (also applied to form texts).

## Localization

All form texts are in the language file under the new **`Bedrock.*`** section (titles, contents, buttons, statuses and three chat messages). They use the usual nightcore tags. Bedrock only supports the 16 classic colors, so hex colors (for example in tier names) are converted to the closest classic color automatically. `<br>` makes a new line, and button labels may use two lines.

## Commands and permissions

Unchanged. `/pets collection`, `/pets menu` and `/pets shop`, and right-clicking or sneak-right-clicking your pet, open forms for Bedrock players automatically.

---

## Known limitations

- **Custom chest-menu layouts aren't mirrored.** The Bedrock forms are built in code, not read from `menu/*.yml`. If you removed or renamed buttons in those files, the Bedrock forms still show the standard actions. Restrict actions with the existing config options instead.
- **Pet inventory is a container on both editions.** Moving items needs a real inventory; Geyser shows it as a native Bedrock chest, so no click types are needed.
- **The proxy needs Floodgate on the backend** (see the network setups table).
- **Icons depend on Bedrock texture paths.** The defaults are vanilla item textures; a resource pack that renames them shows blank icons.

## Upstream bugs fixed in this fork

These bugs exist in upstream CombatPets v2.8.4 and are fixed here:

1. **Horse style names overwrote fox type names.** `Lang.HORSE_STYLE` used the lang path `PetVariant.FoxType`. It now uses `PetVariant.HorseStyle`.
2. **Egg shop Cancel could crash after `/pets reload`.** If the purchase data is gone, Cancel now opens the shop tier list instead of throwing a NullPointerException.
3. **Wrong pet when summoning the same mob type from another tier (Java).** Left-clicking an idle pet whose mob type matches the summoned pet but whose tier differs used to only despawn the current pet. It now summons the clicked pet; only clicking the summoned pet itself returns it.
4. **No feedback when pressing Q on the summoned pet (Java).** It now shows the new message `Pet.Release.Error.Active`: "Return the pet to your collection before releasing it!".
5. **Release ignored the bypass permission in menus.** With `Pets.Release.Allowed: false`, players with `combatpets.bypass.release.disallow` can now release from the menus (Q on Java, the Release button on Bedrock), as `PetManager#releasePet` already allowed.
6. **Name Tag lost when the rename prompt was abandoned (Java).** The Name Tag is now only checked when the prompt opens and taken when the name is entered (refunded if the name is rejected), the same as on Bedrock. Leaving the prompt or letting it time out costs nothing.

## Testing

See [`docs/BEDROCK_TESTING.md`](docs/BEDROCK_TESTING.md) for the full in-game test checklist (Java, Bedrock, mixed, disconnects, missing dependencies, restarts).

## Changes in this fork

- `refactor`: the click-handler logic moved out of the chest GUIs into `PetManager` / `ShopManager` methods. The Java GUIs call them with unchanged behaviour.
- `build`: optional Floodgate API (compile-only), soft-depends on `floodgate` and `Geyser-Spigot`, new `Bedrock.*` config options.
- `feat`: new `su.nightexpress.combatpets.bedrock` package:
  - `BedrockManager`: form sending, response validation, sessions and routing target
  - `bridge/`: `FloodgateBridge`, `GeyserBridge`, provider detection
  - `form/PetForms`, `form/ShopForms`: one form per chest GUI
  - `BedrockLang`: all form texts
  - `BedrockText`: color/line-break conversion
  - `BedrockSession`: double-submit protection
  - `BedrockListener`: cleanup on quit
- `PetManager` / `ShopManager` `open*` methods route Bedrock players to forms, with the chest GUI as a fallback.

## License

GPL-3.0, same as upstream (see `LICENSE`). Original plugin © NightExpress.
