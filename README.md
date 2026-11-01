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

## Download

Pre-built JARs are on the **[Releases](../../releases)** page (no GitHub account needed). Every push also builds a JAR under **Actions → Build plugin JAR → Artifacts** (GitHub account needed, kept 90 days).

The fork's version is `<upstream version>-bedrock.<N>`, e.g. `2.8.4-bedrock.1`.

## Building

The repository has no Gradle wrapper; use Gradle 9.1+ with JDK 25.

```bash
gradle build
# -> build/libs/CombatPets-2.8.4-bedrock.1.jar
```

**Publishing a release:** bump `version` in `build.gradle.kts` (e.g. `2.8.4-bedrock.2`) and push. GitHub Actions builds the plugin and, because that version has no release yet, creates the tag `v2.8.4-bedrock.2` and a Release with the JAR attached. Pushes that don't change the version only produce a build artifact. Pushing a `v*` tag yourself also works.

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

## Rename dialog for Java players

Java Edition players rename their pet in Minecraft's dialog screen instead of typing in chat. The screen has the current name in a text box, the length rules, the Name Tag cost (if enabled), and **Save** / **Go Back** buttons. It uses nightcore's dialog system, the same one NightExpress plugins use for their editors.

- Option: `Pets.Name.Rename.Use_Dialog: true` (set it to `false` to go back to the chat prompt).
- Texts: `Pet.Rename.Dialog.*` and `Pet.Rename.Error.Empty` in the language file.
- Rules are unchanged (length, blocked words, bypass permissions). The Name Tag is taken only when **Save** is pressed and is refunded if the name is rejected. Pressing Esc or **Go Back** costs nothing.

## Pet duels

Players can make their pets fight each other with **`/pets duel <player>`** (permission `combatpets.command.duel`).

1. **Challenge:** `/pets duel Steve`. Both players need a summoned pet and must be within `Pets.Duel.Max_Distance` blocks of each other.
2. **Accept:** Steve gets an **Accept / Decline** screen (a dialog on Java, a form on Bedrock) showing both pets. He can also accept by typing `/pets duel <challenger>`. Challenges expire after `Pets.Duel.Request_Timeout` seconds.
3. **Countdown:** both pets are healed to full, then a 3… 2… 1… **FIGHT!** title appears, with a note-block "pling" on every number (rising pitch) and a high pling at the start.
4. **Fight:** the pets attack only each other. **No healing** during the fight (pet regeneration and the Wither's self-heal are blocked, `Pets.Duel.Disable_Healing`), so fights between tanky pets actually end. Nothing else can interfere: players, other pets and mobs can't hurt them, and they can't hurt anything else. Owners can't feed or return their pet during the fight.
5. **Result:** the losing pet **really dies**, using the server's normal pet death rules (revive cost, auto-revive time, `Pets.Permanent_Death`, item drops). The winner gets a **VICTORY** title, the loser **DEFEAT**.

**Surrender:** `/pets surrender` gives up your current duel (countdown or fight). It counts as a **loss** for you and a **win** for your opponent. Your pet **survives** by default; set `Pets.Duel.Surrender_Kills_Pet: true` to make it die like a real loss.

Other endings, all without a winner and without any pet dying:
- **Time limit:** after `Pets.Duel.Max_Duration` seconds (default 120) the duel ends in a **draw**. Raise it for pets with a lot of health.
- **Cancelled:** a player logs out or dies, a pet is no longer summoned, or the pets get more than twice `Max_Distance` apart.

**Turning duels off:** players who don't want duels can use **`/pets duels off`**. All challenges to them are then declined automatically, with no popup; the challenger sees "<player> isn't accepting pet duels." `/pets duels on` turns them back on, and `/pets duels` alone toggles.
- **Duels off works both ways:** players with duels off also can't challenge others. They're asked to turn duels on first.
- **Where it's saved:** on the player (survives relogs and restarts on this server). New players start with duels **on** (`Pets.Duel.Default_Enabled`).
- **Bedrock:** a toggle in **Pet Settings**, "Accept Pet Duel Challenges".
- **Java:** a **Duel Challenges** button in the Pet Menu (slot 31) for newly generated menu files. On an existing server, add this under `Content:` in `menu/pet_overview.yml`, or delete the file to regenerate it:
  ```yaml
    duel_toggle:
      Priority: 10
      Item:
        Material: IRON_SWORD
        Name: '<lyellow><b>Duel Challenges</b></lyellow>'
        Lore:
        - '<lyellow>▪ <lgray>Status: </lgray>%duels_status%</lyellow>'
        - ''
        - '<lgray>When on, other players can challenge</lgray>'
        - '<lgray>your pet with <lyellow>/pets duel <player></lyellow>.</lgray>'
        - ''
        - '<lyellow>[▶] </lyellow><lgray>Click to <lyellow>toggle</lyellow>.</lgray>'
      Slots: '31'
      Type: duel_toggle
  ```
- **Placeholder:** `%combatpets_duels_enabled%` (On/Off, from the `Duel.Status.*` texts).

Duels work anywhere, even in `pet-pvp: deny` regions and with `Pets.PVP_Allowed: false`, because both players agreed. There are no rewards and no level limits.

**Stats** are saved per pet:
- **Bedrock forms:** shown automatically in the Pet Menu and the collection pet details.
- **Java menus:** shown in the Pet Menu stats item and the collection lore for newly generated menu files. On an existing server, add a line like this to the lore in `menu/pet_overview.yml` and `menu/pet_collection.yml`, or delete those files to regenerate them:
  ```yaml
  - '<lyellow>▪ <lgray>Duels: </lgray><lgreen>%pet_duel_wins% won</lgreen><lgray> / </lgray><lred>%pet_duel_losses% lost</lred></lyellow>'
  ```
- **Plugin placeholders:** `%pet_duel_wins%`, `%pet_duel_losses%`.
- **PlaceholderAPI:** `%combatpets_duel_wins%` / `%combatpets_duel_losses%` for the summoned pet, and `%combatpets_duel_wins_total%` / `%combatpets_duel_losses_total%` for all of a player's pets.

```yaml
Pets:
  Duel:
    Enabled: true
    Default_Enabled: true # new players accept challenges until they use /pets duels off
    Request_Timeout: 30   # seconds
    Countdown: 3          # seconds
    Max_Duration: 120     # seconds, then draw
    Max_Distance: 24.0    # blocks between players to start; cancelled at 2x between pets
    Disable_Healing: true # no regeneration / self-heal during the fight
    Surrender_Kills_Pet: false
    Sounds:               # any sound key; empty = no sound
      Countdown: minecraft:block.note_block.pling   # 3, 2, 1 with rising pitch
      Start: minecraft:block.note_block.pling       # high pitch on FIGHT!
      Win: minecraft:ui.toast.challenge_complete
      Lose: minecraft:block.note_block.didgeridoo
      Draw: minecraft:block.note_block.bell
```

Texts are in the language file under `Duel.*`, `Command.Duel.Desc` and `Command.Surrender.Desc`.

### Ranged pets (Ghast, Wither, Blaze, …)

With `Pets.Attack_Damage_For_Projectiles: true` (upstream default), a pet's projectile damage is replaced by the pet's **Attack Damage** + 25% of the projectile's normal damage. Pets without a melee attack have Attack Damage **1**, so a Ghast fireball (normally 6+) only did ~2.5, less than the target regenerated. This fork adds `Pets.Projectile_Damage_Not_Below_Original: true`: projectiles never deal **less** than their normal damage. This applies everywhere, not only in duels, so ranged pets are a bit stronger in normal combat too. Set it to `false` for the upstream behaviour.

**Fire-immune targets:** in vanilla a fireball's hit is fire damage, so the Wither, Warden, Ghast, Blaze and other fire-immune mobs take nothing from it (only from the small explosion, which rarely reaches). With `Pets.Fireballs_Ignore_Fire_Immunity: true`, a pet's fireball that hits a target the pet may attack deals its hit as normal projectile damage (Ghast 6, Blaze 5, before the rules above). Fire immunity still works for everything else.

**Ghast pets** now keep about 8 blocks from ground targets (a few blocks above them) instead of sitting on top of them, and hover above their owner against flying targets. They shoot from the side facing the target, straight at it.

**Wither pets** no longer break blocks around them after taking damage (`Pets.Wither.Destroy_Blocks: false`; set `true` for vanilla behaviour).

### Warden sonic boom

Upstream Warden pets only punch. They now also use the vanilla **sonic boom** when they haven't landed a melee hit for 2 seconds, e.g. against a flying pet or a player who pillared up. This works everywhere, not only in duels.

- Range 15 blocks sideways, 20 up/down; vanilla charge-up (~1.7 s) and cooldown.
- Like vanilla it goes through blocks and ignores armor and shields.
- Normal pet damage rules apply: `Pets.PVP_Allowed`, the WorldGuard `pet-pvp` flag, no friendly fire, and in a duel only the opponent can be hit.

```yaml
Pets:
  Fireballs_Ignore_Fire_Immunity: true
  Wither:
    Destroy_Blocks: false
  Warden:
    Sonic_Boom: true          # false = Warden pets only use melee (upstream behaviour)
    Sonic_Boom_Damage: 10.0   # vanilla: 10
```

## Pet XP: scaled by mob health

Upstream gives the same XP for a Warden as for a zombie: XP comes from 4 broad groups in `leveling.yml` (`XPSources`: animals, monsters, illagers, others). This fork **multiplies that XP by the mob's max health**:

`XP = random amount from the mob's group × (Max Health ÷ Base_Health, from 1 up to Max_Multiplier) × share of damage dealt by the pet`

| Mob | Max health | Multiplier | XP when the pet deals all damage (`monsters`: 20–60, 50% chance) |
|---|---|---|---|
| Zombie | 20 | ×1 | 20–60 |
| Iron golem (natural) | 100 | ×5 | 100–300 |
| Wither | 300 | ×15 | 300–900 |
| Warden | 500 | ×25 | 500–1500 |

Settings in `leveling.yml`:
```yaml
Settings:
  Health_Scaling:
    Enabled: true
    Base_Health: 20.0      # health that gives x1
    Max_Multiplier: 25.0   # cap (protects against custom mobs with huge health)
```
This only applies when `Use_Custom_XP_Table: true` (the default). The group's chance roll and the damage share still apply: if a player does most of the damage to a boss, the pet only gets its share.

## WorldGuard region flags

If [WorldGuard](https://enginehub.org/worldguard) is installed, CombatPets registers two region flags. Both default to `allow`, so regions without them behave exactly as before.

| Flag | Effect when set to `deny` |
|---|---|
| `pet-pvp` | Pets can't hurt players or other players' pets, and players and pets can't hurt pets in the region. Pets drop player and pet targets. Pets still fight hostile mobs. |
| `pet-summon` | Pets can't be summoned in the region (`Pet.Error.RegionSummon` message), and a summoned pet that walks or teleports in is returned to the collection. Players with WorldGuard region bypass (`/rg bypass`) aren't affected. |

Examples:
```
/rg flag spawn pet-pvp deny      # pets allowed at spawn, but no pet fights with players
/rg flag lobby pet-summon deny   # no pets in the lobby at all
```

Outside these regions, `Pets.PVP_Allowed` and `Pets.Disabled_Worlds` work as before. WorldGuard is optional; without it nothing changes.

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

- **nightcore `ConfigValue` deprecation.** nightcore marks its `ConfigValue` config class as "for removal" (211 uses here). It can't be migrated yet: nightcore itself still loads every plugin's `config.yml` through `ConfigValue` (`FileConfig.initializeOptions`), and its replacement (`ConfigProperty`) can't be used for a plugin's main config and changes some file formats (e.g. per-rank limits). This will be migrated when nightcore finishes its new config system, or when the plugin drops nightcore.

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
- `feat`: pet XP scaled by mob max health (`Settings.Health_Scaling` in leveling.yml).
- `fix`: pets could stay registered forever (and keep their owner "busy") if removal hit a missing owner; open dialogs are forgotten when a player quits.
- `fix`: the leveling damage tracker never forgot mobs (memory leak); records are now removed on death and when a mob leaves the world.
- `feat`: pet duels (`/pets duel <player>`), real death for the loser, per-pet win/loss stats and placeholders.
- `feat`: WorldGuard flags `pet-pvp` and `pet-summon` (optional WorldGuard soft dependency).
- `feat`: configurable follow distances (`Pets.Follow.Start_Distance` / `Stop_Distance`); Java rename dialog (`Pets.Name.Rename.Use_Dialog`).
- `refactor`: nightcore economy calls that are marked for removal were replaced with their current API.
- `build`: fork version `2.8.4-bedrock.N`, GitHub Actions build and automatic Releases on `v*` tags.
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
