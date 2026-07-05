# 🎲 Random Item Generator Resurrected

Every break is a surprise — each drop is special. Built for **NeoForge 1.21.1**.

***

## ✨ What's Inside

*   **Random Item Block** — Break it to drop a random item from any loaded mod.
*   **Random Entity Block** — Break it to spawn a random entity, with built-in filters to keep things safe.
*   **Random Enchants Block** — Break it to drop a random enchanted book.
*   **Random Loot Block** — Break it to roll a random chest loot table and drop its contents.

All blocks regenerate on break. Silk Touch returns the block itself. Fortune increases the number of drops.

***

## ⚙️ Configuration

File: `config/random_item_generator_resurrected-common.toml`

Every block has its own config section with a **denylist** (block specific entries) and a **whitelist** (only allow specific entries). Just flip `use_whitelist = true` to switch modes.

The Entity Block also has a master toggle (`only_allow_mobs = true`) that keeps things simple by only allowing actual mobs to spawn, plus individual switches for hazards like lightning, explosives, and fireballs.

All pools are built from the registry at runtime, so modded content is supported automatically.

***

## 📊 Stats

Block usage is tracked in the vanilla **Statistics** screen under the **Custom** tab.

***

## Showcase ▶️

<span><iframe width="800" height="450" src="https://www.youtube.com/embed/4Q3l9kilx3I" frameborder="0" allowfullscreen="allowfullscreen"></iframe></span>
