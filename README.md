<h1 align="center">Random Item Generator Resurrected</h1>

Every break is a surprise — each drop is special. This branch targets **Minecraft 1.16.5 with Forge**, as listed in `gradle.properties`. Other game versions and loaders use their own files.

***

<p align="center"><a href="https://www.curseforge.com/minecraft/mc-mods/rigr">Download on CurseForge</a> · <a href="https://discord.com/invite/mUGHmQxHG8">Discord</a> · <a href="https://github.com/DKLYNTLY/Random-Item-Generator-Resurrected">GitHub source</a></p>

## What you can do

*   **Random Item Block** — Break it to drop a random item from any loaded mod.
*   **Random Entity Block** — Break it to spawn a random entity, with built-in filters to keep things safe.
*   **Random Enchants Block** — Break it to drop a random enchanted book.
*   **Random Loot Block** — Break it to roll a random chest loot table and drop its contents.

All blocks regenerate on break. Silk Touch returns the block itself. Fortune increases the number of drops.

<details>
<summary><strong>Play: controls, commands, and progression</strong></summary>

### Stats

Block usage is tracked in the vanilla **Statistics** screen under the **Custom** tab.

---

### Showcase

<span><iframe width="800" height="450" src="https://www.youtube.com/embed/4Q3l9kilx3I" frameborder="0" allowfullscreen="allowfullscreen"></iframe></span>

</details>

<details>
<summary><strong>Configure and customize</strong></summary>

### Configuration

File: `config/random_item_generator_resurrected-common.toml`

Every block has its own config section with a **denylist** (block specific entries) and a **whitelist** (only allow specific entries). Just flip `use_whitelist = true` to switch modes.

The Entity Block also has a master toggle (`only_allow_mobs = true`) that keeps things simple by only allowing actual mobs to spawn, plus individual switches for hazards like lightning, explosives, and fireballs.

All pools are built from the registry at runtime, so modded content is supported automatically.

</details>

<details>
<summary><strong>Backups and recovery</strong></summary>

### Before updating

*Back up your world and configuration files before updating or changing settings. Keep a working copy of the old instance until you have checked the new setup.*

</details>

<details>
<summary><strong>About this project</strong></summary>

### Author and links

I create and maintain Random Item Generator Resurrected as **Dnydd (formerly known as dklyntly)**.

[Official CurseForge page](https://www.curseforge.com/minecraft/mc-mods/rigr) · [Discord](https://discord.com/invite/mUGHmQxHG8)

### License

Read [LICENSE](LICENSE) for this branch’s terms. Permissions granted with earlier releases remain in effect.

</details>

## Support my work

If you enjoy my projects, you can leave a tip on Ko-fi. Support is always optional.

[![Support me on Ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/A3G129JCLF)
