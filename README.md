# GreenTPA

GreenTPA is a professional-grade, feature-rich teleportation and management plugin for Minecraft Paper 1.21.11+. It offers a sleek green theme, clickable chat interactions, and deep integration with economy systems.

## 🌟 Features

- **Robust TPA System**: Traditional teleport requests (TPA/TPAHere) with clickable [Accept] and [Deny] buttons.
- **Global Economy Support**: Fully integrated with Vault and Treasury. Supports a wide range of providers including EssentialsX, CMI, Xconomy, UltraEconomy, and more.
- **Per-World Pricing**: Configure unique costs for every command based on the target world.
- **RTP (Random Teleport)**: Asynchronous, safe location scanning with per-world region boundaries.
- **Multi-Home System**: Persistent player homes with configurable limits and permission-based ranks.
- **World Spawn Management**: Set and manage spawn points for every world on your server.
- **Visual Warmup System**: Immersive teleport countdown featuring bold titles on the main screen and Creeper-primed sound effects.
- **Movement Detection**: Automatically cancels teleports if the player moves during the warmup period.
- **Flexible Management**: Ignore system, player blocking, auto-accept toggles, and administrative force-teleports.
- **Dynamic Configuration**: Enable or disable any command entirely via `commands.yml`.
- **MiniMessage Support**: Full support for Adventure MiniMessage in all messages and prefixes.

## 📜 Commands

### Core TPA Commands
- `/tpa <player>`: Request to teleport to a player.
- `/tpahere <player>`: Request a player to teleport to you.
- `/tpaccept [player]`: Accept a teleport request.
- `/tpdeny [player]`: Deny a teleport request.
- `/tpcancel <player>`: Cancel a request you sent.
- `/tpalist`: View your pending incoming requests.

### RTP System
- `/rtp`: Teleport to a random safe location in your current world.
- `/rtp world <world>`: RTP into a specific world.
- `/rtp nether`: RTP into the nether.
- `/rtp end`: RTP into the end.

### Home System
- `/home [name]`: Teleport to one of your homes.
- `/homes`: List all of your set homes.
- `/sethome [name]`: Set a home at your current location.
- `/delhome [name]`: Delete a specific home.

### Spawn System
- `/spawn [world <world>]`: Teleport to the world's spawn point.
- `/setspawn`: Set the spawn point for the current world (Admin).
- `/delspawn [world]`: Delete the spawn point for a world (Admin).

### Management & Settings
- `/back`: Return to your last location or death point.
- `/tptoggle`: Toggle receiving teleport requests.
- `/tpblock <player>`: Block a player from sending you requests.
- `/tpunblock <player>`: Unblock a player.
- `/tpaignore <player>`: Ignore requests from a specific player.
- `/tpaignoreall`: Toggle ignoring all teleport requests.
- `/tpaauto`: Toggle automatic acceptance of requests.

### Admin Commands
- `/tpahereall`: Request all online players to teleport to you.
- `/tpo <player>`: Force teleport to a player (bypasses requests/economy).
- `/tpohere <player>`: Force a player to teleport to you.
- `/gtpreload`: Reload all plugin configurations.

## 🔑 Permissions

- `greentpa.user`: Access to basic player commands (default: true).
- `greentpa.free`: Bypass all economy costs.
- `greentpa.homes.unlimited`: Ability to set unlimited homes.
- `greentpa.homes.<number>`: Set the maximum number of homes for a player/rank.
- `greentpa.admin.tpahereall`: Access to `/tpahereall`.
- `greentpa.admin.tpo`: Access to `/tpo`.
- `greentpa.admin.tpohere`: Access to `/tpohere`.
- `greentpa.admin.reload`: Access to `/gtpreload` (and /gtp reload).
- `greentpa.admin.setspawn`: Access to `/setspawn`.
- `greentpa.admin.delspawn`: Access to `/delspawn`.
- `greentpa.admin.nocooldown`: Bypass request cooldowns.

## ⚙️ Configuration

GreenTPA uses several YAML files for deep customization:
- `config.yml`: Core settings, economy mode, pricing, and RTP regions.
- `messages.yml`: All player-visible text (supports MiniMessage).
- `commands.yml`: Toggle individual commands on or off.
- `data.yml`: Persistent player settings (blocks, ignores, toggles).
- `homes.yml`: Persistent storage for player homes.
- `spawns.yml`: Persistent storage for world spawn points.

## 🛠️ Development

- **Language**: Java 21
- **API**: Paper 1.21.11-R0.1-SNAPSHOT
- **Build Tool**: Maven
- **Dependencies**: Vault API

To build the project:
```bash
mvn clean package
```
The compiled shaded JAR will be located in the `target/` directory.
