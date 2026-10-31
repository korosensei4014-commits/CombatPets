# Bedrock Forms — Test Checklist

The code was type-checked against the sources of the exact library versions, but it was **not run on a live server** (the build environment had no access to Maven repositories or game clients). Run through this list before production.

**Test server:** Paper 26.3 (Java 25), nightcore 2.16.7, CombatPets fork JAR, Geyser-Spigot and Floodgate on the same server, a Vault economy (for revive/shop), and optionally PlaceholderAPI.
**Test accounts:** one Java client and one Bedrock client (mobile or Windows, for touch/controller checks).

Mark each line ✅ / ❌ and note anything unexpected.

---

## 0. Startup

- [ ] Console shows `Bedrock support enabled using the Floodgate API`.
- [ ] `config.yml` contains the `Bedrock:` section; the lang file contains the `Bedrock.*` keys.
- [ ] No errors on startup or on `/pets reload`.

## 1. Java Edition (regression: must behave exactly as before)

| # | Menu / action | Expected |
|---|---|---|
| J1 | `/pets collection` → tier | Collection chest GUI opens |
| J2 | Left-click idle pet | Pet summoned, GUI closes |
| J3 | Left-click summoned pet | Pet despawned, GUI refreshes |
| J3b | Left-click idle pet of the same mob type but another tier while one is summoned | Current pet returned, clicked pet summoned |
| J4 | Left-click dead pet | Revive GUI; accept → paid & revived; decline → back to collection |
| J5 | **Q** on idle pet | Release GUI; accept → released; decline → back |
| J6 | **Q** on summoned pet | Message: return the pet to your collection before releasing it |
| J7 | Sneak/right-click own pet | Pet Menu GUI |
| J8 | Equipment **left-click** | Lock toggles, lore updates |
| J9 | Equipment **right-click** | All pet armour/items go to your inventory |
| J10 | Combat Mode click | Cycles Passive → Protective → Supportive → Protective & Supportive |
| J11 | Silent click | Toggles |
| J12 | Rename | **Dialog window** with the current name; Save → renamed, Name Tag taken, Pet Menu reopens; invalid name → error title + refund; empty → "Please enter a name." + dialog reopens; Esc / Go Back → nothing taken |
| J12b | Rename with `Pets.Name.Rename.Use_Dialog: false` | Old chat prompt is used |
| J13 | Inventory | Pet storage opens |
| J14 | Aspects → click aspect | 1 point spent, value +1 (blocked at max / 0 points) |
| J15 | Aspects → Reallocate (if enabled) | Points refunded |
| J16 | Return to Collection | Pet despawned, GUI closes |
| J17 | `/pets shop` → tier → egg → accept | Money taken, egg received; decline → back |

## 2. Bedrock Edition

| # | Action | Expected |
|---|---|---|
| B1 | `/pets collection` | **Form** (not a chest) listing tiers with pet counts |
| B2 | Tap tier | Pet list with status (Summoned / Idle / Dead) and page "1/N" |
| B3 | Tap idle pet → **Summon Pet** | Pet spawns (previous pet returned first) |
| B4 | Tap summoned pet → **Return to Collection** | Pet despawned, collection reopens on the same page |
| B5 | Tap summoned pet → **Open Pet Menu** | Pet Menu form |
| B6 | Dead pet → **Revive Pet** → Revive | Cost charged, pet alive, pet form reopens |
| B7 | Revive with too little money | Error message, pet stays dead |
| B8 | Revive → Cancel / X | Back to the pet form, nothing charged |
| B9 | Idle pet → **Release Pet** → Release | Pet removed, collection reopens |
| B10 | Release → Cancel / X | Back to the pet form, pet still owned |
| B11 | Summoned pet | No Release button; hint text shown |
| B12 | `Pets.Release.Allowed: false` | No Release button anywhere |
| B13 | Interact with your pet (sneak if configured) | Pet Menu **form** with stats and settings |
| B14 | **Pet Settings**: change combat mode | Menu shows the new mode; pet behaviour changes |
| B15 | **Pet Settings**: toggle Silent | Pet ambient sounds stop/resume |
| B16 | **Pet Settings**: toggle Equipment (tier with equipment) | Lock changes; interacting with the pet while holding an item equips it when unlocked |
| B17 | Tier **without** equipment | No equipment toggle, no Unequip button |
| B18 | **Unequip All Items** | Items moved to your inventory, form reopens |
| B19 | **Pet Inventory** (tier with inventory) | Native Bedrock chest opens; items can be moved and are saved |
| B20 | **Rename Pet** → valid name | Name changes, 1 Name Tag taken (if required) |
| B21 | Rename → empty input | "Please enter a value." + form reopens, no Name Tag taken |
| B22 | Rename → too long / too short / blocked word | Upstream error message, form reopens, Name Tag refunded |
| B23 | Rename without a Name Tag (requirement on) | Error message, no form opened |
| B24 | Rename → X | Back to Pet Menu, nothing taken |
| B25 | **Aspects** → aspect → **Upgrade (+1)** | Point spent, value +1, detail form reopens |
| B26 | Upgrade at max level / with 0 points | No Upgrade button |
| B27 | **Reallocate Points** → confirm | Points refunded (only if enabled) |
| B28 | **Return to Collection** (pet menu) | Pet despawned |
| B29 | `/pets shop` → tier → egg → **Buy** | Price charged, egg item received |
| B30 | Buy with too little money | Upstream "not enough funds" message |
| B31 | Buy → Cancel / X | Back to the egg list, same page |
| B32 | Pagination (more pets/eggs than `Page_Size`) | Previous/Next buttons, correct page numbers |
| B33 | Back buttons on every list form | Returns to the parent form |
| B34 | Rapidly double-tap a button (lag simulation) | Action runs once |
| B35 | No chest GUI ever appears for B1–B33 except B19 | ✅ |

## 3. WorldGuard flags (if WorldGuard is installed)

- [ ] Startup log: `Registered WorldGuard flags: pet-pvp, pet-summon`.
- [ ] `/rg flag <region> pet-summon deny` → summoning inside is blocked with a message; walking in with a pet returns it.
- [ ] Same region with `/rg bypass` on → summoning works.
- [ ] `/rg flag <region> pet-pvp deny` (with `Pets.PVP_Allowed: true`) → players can't hit pets, pets don't attack players or other pets; pets still fight zombies.
- [ ] Outside the region, PvP with pets follows `Pets.PVP_Allowed` as before.
- [ ] Server without WorldGuard → no errors, everything else unchanged.

## 4. Mixed / lifecycle

- [ ] Java and Bedrock players use menus at the same time: each gets their own UI, and actions don't affect each other.
- [ ] Bedrock player disconnects while a form is open: no errors; after rejoining, menus work.
- [ ] A pet is released by an admin (`/pets remove`) while its form is open → tapping a button gives "That pet is no longer available".
- [ ] `/pets reload` while a Bedrock form is open → late responses are ignored, no errors; new forms work.
- [ ] Remove Floodgate and Geyser → server starts, logs "not found", everyone gets chest GUIs.
- [ ] Only Geyser-Spigot (no Floodgate) → logs "using the Geyser API", forms work.
- [ ] `Bedrock.Enabled: false` → Bedrock players get chest GUIs.
- [ ] `Bedrock.Provider: geyser` with only Floodgate installed → warning and chest GUIs.
- [ ] Velocity: Geyser + Floodgate on the proxy and Floodgate on the backend → forms work.
- [ ] Restart the server: pet names, combat modes, silent flags, equipment, aspects, inventories and collections persist.
- [ ] Server shutdown with Bedrock players online → no errors.
