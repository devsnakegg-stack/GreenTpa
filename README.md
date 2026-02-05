# GreenTPA

GreenTPA is a full-featured teleportation request plugin for Paper 1.21.1, featuring a green theme and clickable chat messages.

## Features

- **TPA & TPAHere**: Request to teleport to others or have them teleport to you.
- **Clickable Messages**: Easy [Accept] and [Deny] buttons in chat.
- **Warmup & Cooldown**: Configurable teleport delays and request limits.
- **Movement Detection**: Teleport cancels if the player moves during warmup.
- **Back Command**: Return to your last location or death point.
- **Toggles & Blocks**: Control who can send you requests.
- **Auto-Accept**: Optional automatic acceptance of requests.
- **Admin Tools**: Force teleport commands and global requests.
- **MiniMessage Support**: Full color and formatting support in messages.

## Commands

- `/tpa <player>`: Send a teleport request.
- `/tpahere <player>`: Request a player to teleport to you.
- `/tpaccept [player]`: Accept a request.
- `/tpdeny [player]`: Deny a request.
- `/tpcancel [player]`: Cancel your sent request.
- `/tpalist`: View pending requests.
- `/tptoggle`: Toggle receiving requests.
- `/tpblock <player>`: Block a player.
- `/tpunblock <player>`: Unblock a player.
- `/tpaignore <player>`: Ignore a player.
- `/tpaignoreall`: Ignore everyone.
- `/tpaauto`: Toggle auto-accept.
- `/back`: Return to last location.
- `/tpahereall`: Request everyone to teleport to you (Admin).
- `/tpo <player>`: Force teleport to a player (Admin).
- `/tpohere <player>`: Force a player to teleport to you (Admin).
- `/tpareload`: Reload configuration (Admin).

## Permissions

- `greentpa.user`: Access to basic commands (default: true).
- `greentpa.admin.tpahereall`: Use `/tpahereall`.
- `greentpa.admin.tpo`: Use `/tpo`.
- `greentpa.admin.tpohere`: Use `/tpohere`.
- `greentpa.admin.reload`: Use `/tpareload`.
- `greentpa.admin.nocooldown`: Bypass teleport cooldowns.

## Configuration

The plugin uses `config.yml` for settings and `messages.yml` for all chat messages, supporting MiniMessage formatting.
